package main_process;

import audio_capturing.AudioCapture;
import audio_capturing.AudioCaptureManager;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Paragraph;
import ocr.OCRProcessor;
import screen_capture.ScreenCapture;
import screen_capture.ScreenCaptureManager;
import timestamping.TimestampManager;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

import javax.imageio.ImageIO;

public class MainProcessManager {

    public static void Run() {
        if (!MainProcessManager.getInstance().Init()) {
            System.out.println("MainProcessManager failed to initialize, try once again");

            if (!MainProcessManager.getInstance().Init()) {
                System.out.println("MainProcessManager failed to initialize second time, abort");
                return;
            }
        }

        MainProcessManager.getInstance().prepImgDirectory();

        if (!MainProcessManager.getInstance().prepareFile()) {
            System.out.println("MainProcessManager failed to prepare file, abort");
            return;
        }


        runningThread.submit(new Thread(() -> {
            Optional<ScreenCapture> sc = Optional.empty();
            Optional<AudioCapture> ac = Optional.empty();

            Optional<String> ocrText = Optional.empty();
            OCRResult ocrResult = null;
            OCRProcessor ocrProcessor = new OCRProcessor(5);

            sc = ScreenCaptureManager.getInstance().popTheOldestScreenshot();
            ac = AudioCaptureManager.getInstance().popTheOldestAudioCapture();

            while (sc.isPresent() || ac.isPresent()) {
                try {
                    if (sc.isPresent()) {
                        ocrText = ocrProcessor.process(sc.get().getImage()).get();

                        if (ocrText.isPresent()) {
                            ocrResult = new OCRResult(sc.get().getTimeStamp(), ocrText.get());
                        }
                    }

                } catch (Exception e) {
                    System.out.println("MainProcessManager::Run | OCR processor exception");
                    e.printStackTrace();
                    continue;
                }

                if (document != null && ocrResult != null) {
                    // FIXME this won't run if ocrResult == null, see line 65
                    if (ac.isPresent() && ocrResult.getTimestamp() < ac.get().getTimestamp()) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();

                        try {
                            if (sc.isPresent()) {
                                ImageIO.write(sc.get().getImage(), "PNG", baos);
                            } else {

                            }
                        } catch (IOException e) {
                            System.out.println("MainProcessManager::Run | ImageIO exception");
                        }

                        byte[] imgBytes = baos.toByteArray();
                        document.add(new Image(ImageDataFactory.create(imgBytes)));

                        document.add(new Paragraph(ocrResult.getContent()));
                        document.add(new Paragraph(ac.get().getContent()));
                    } else {
                        ac.ifPresent(audio -> document.add(new Paragraph(audio.getContent())));
                        document.add(new Paragraph(ocrResult.getContent()));
                    }

                }

                sc = ScreenCaptureManager.getInstance().popTheOldestScreenshot();
                ac = AudioCaptureManager.getInstance().popTheOldestAudioCapture();
            }
        }));
        // deactiv

        MainProcessManager.getInstance().Deactivate();
    }

    // image saving
    private static String imgDirectory = "";

    private static void prepImgDirectory() {
        imgDirectory = System.getProperty("user.dir") + File.separator + "notes" + File.separator;
        File imgDir = new File(imgDirectory);
        if (!imgDir.exists()) {
            imgDir.mkdirs();
        }
    }

    // pdf handling
    private static String currentPdfFilePath = "";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmm");
    private static PdfWriter pdfWriter = null;
    private static PdfDocument pdfDoc = null;
    private static Document document = null;
    private static boolean isActive = false;

    private static boolean prepareFile() {
        try {
            currentPdfFilePath = System.getProperty("user.dir") + File.separator + "notes" + File.separator + "note-" + sdf.format(new Date()) + ".pdf";
            pdfWriter = new PdfWriter(currentPdfFilePath);
            pdfDoc = new PdfDocument(pdfWriter);
            document = new Document(pdfDoc);
        } catch (Exception e) {
            System.err.println("MainProcessManager::prepareFile | some exception lol");
            e.printStackTrace();
            return false;
        }

        isActive = true;
        return true;
    }

    // utilities
    private static ExecutorService runningThread = Executors.newSingleThreadExecutor();

    private static boolean Init() {
        TimestampManager.getInstance().Activate();

        if (!TimestampManager.getInstance().isActivated()) {
            System.err.println("MainProcessManager | TimestampManager is not activated");
            return false;
        }

        AudioCaptureManager.getInstance().Activate();
        ScreenCaptureManager.getInstance().Activate();

        if (!ScreenCaptureManager.getInstance().isActivated() || !AudioCaptureManager.getInstance().isActive()) {
            System.err.println("MainProcessManager | ScreenCaptureManager or AudioCaptureManager is not activated");
            return false;
        }
        return true;
    }

    // singleton implementation
    public static MainProcessManager getInstance() {
        if (instance == null) {
            instance = new MainProcessManager();
        }
        return instance;
    }

    public boolean Deactivate() {
        if (!isActive) {
            return false;
        }
        try {
            isActive = false;
            document.close();
            document = null;
            pdfDoc.close();
            pdfDoc = null;
            pdfWriter.close();
            pdfWriter = null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Deactivate managers that generate audio and screen captures.
     * Without them the running thread will stop after processing all remaining.
     * Blocks thread until runningThread has stopped
     * @return if the deactivation was a success
     */
    public boolean DeferredDeactivate(long timeoutInSeconds) {
        AudioCaptureManager.Deactivate();
        ScreenCaptureManager.getInstance().Deactivate();

        //TimestampManager.getInstance().Deactivate();
        while(true) {
            if(AudioCaptureManager.getInstance().isActive() || ScreenCaptureManager.getInstance().isActivated()) {
                continue;
            }

            TimestampManager.getInstance().Deactivate();
            try {
                return runningThread.awaitTermination(timeoutInSeconds, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    private static MainProcessManager instance = null;

    private MainProcessManager() {
    }

    public static void main(String[] args) throws Exception {
        Run();
        System.out.println("Passed Run(), waiting 10 seconds");
        Thread.sleep(10000);
        System.out.println("Waiting over, exiting");
        getInstance().DeferredDeactivate(10);
    }
}
