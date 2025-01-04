package ScreenCapturing;

/*
    This class implements the following functionalities:
    - capturing screen contents;
    - making screenshots every 5 seconds;
    - checking whether the contents are changed or not:
        - based on the above save the image file, or ignore it;
        - make use of ScreenCapture class.
*/

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// 'enum' instead of class ensures thread-safety for singleton
public enum ScreenCaptureManager {

    // singleton implementation
    INSTANCE();

    private ScreenCaptureManager() {}

    public static ScreenCaptureManager getInstance() {
        return INSTANCE;
    }


    // functional utilities - Activate() and Deactivate()
    public static volatile boolean isActivated = false;
    private static final String dateTimeFormatPattern = "yyyy-MM-dd-HH-mm-ss"; //< TODO may be replaced with TimeManager class
    private static String outputDirectory = "";
    private static Robot robot = null;
    private static ScheduledExecutorService scheduler = null;
    private static final Integer screenshotPeriod = 5;

    public static void Activate() {
        try {
            robot = new Robot();
            outputDirectory = System.getProperty("user.home"); //< TODO we need some "tmp" directory

            isActivated = true;
        }
        catch (Exception e) {
            System.out.println("ScreenCaptureManager::Activate | Method call failed, stack trace: ");
            e.printStackTrace();

            isActivated = false;
            return;
        }

        if (isActivated) {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                if (isActivated) {
                    captureScreenshot();
                }
            }, 0, screenshotPeriod, TimeUnit.SECONDS);
        }
        else {
            scheduler = null;
        }
    }

    public static void Deactivate() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();

            try {
                scheduler.awaitTermination(screenshotPeriod, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("ScreenCaptureManager::Deactivate | awaitTermination failed, stack trace: ");
                e.printStackTrace();
            }
        }

        robot = null;
        outputDirectory = "";
        scheduler = null;
        isActivated = false;

        System.out.println("ScreenCaptureManager::Deactivate");
    }

    // actual screenshotting
    private static void captureScreenshot() {
        if (!isActivated) {
            System.out.println("ScreenCaptureManager::captureScreenshot | Invalid method call, it's not activated");
            return;
        }

        try {
            // TODO temporary implementation
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage capturedImage = robot.createScreenCapture(screenRect);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormatPattern));//< TODO should be obtained from a global manager
            File outputFile = new File(outputDirectory + File.separator + "screenshot_" + timestamp + ".png");
            ImageIO.write(capturedImage, "png", outputFile);

            System.out.println("ScreenCaptureManager::captureScreenshot | done and saved at: " + outputFile.getAbsolutePath());
        }
        catch (Exception e) {
            System.out.println("ScreenCaptureManager::captureScreenshot | Method call failed, stack trace: ");
            e.printStackTrace();
        }
    }

    // storing metadata
    

    // test main
    public static void main(String[] args) {
        ScheduledExecutorService _scheduler = Executors.newSingleThreadScheduledExecutor();

        try {
            Activate();

            if (isActivated) {
                Runnable task = () -> {
                    Deactivate();
                    _scheduler.shutdownNow();
                };
                _scheduler.schedule(task, 3 * screenshotPeriod, TimeUnit.SECONDS);
            }

        } catch (Exception e) {

            Deactivate();
            _scheduler.shutdownNow();

            throw new RuntimeException(e);
        }
    }
}
