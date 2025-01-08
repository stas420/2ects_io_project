package AudioCapturing;

import javax.sound.sampled.*;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AudioCaptureManager {

    // singleton implementation
    private static AudioCaptureManager instance = null;

    private AudioCaptureManager() {}

    public static AudioCaptureManager getInstance() {
        if (instance == null) {
            instance = new AudioCaptureManager();
        }
        return instance;
    }

    // audio capture utilities
    private static final AudioFormat format = new AudioFormat(44100, 16, 2,
                                                        true, false);

    private static final long recordingPeriod = 10000; //< in milliseconds
    private static final long waitingStep = 1000; //< in milliseconds
    private static final String path = System.getProperty("user.home") + File.separator + "audio.wav"; //< TODO change it!


    public static void main(String[] args) {


        Mixer.Info info = null;

        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            System.out.println(mi);
            if (mi.getName().contains("efault")) {
                info = mi;
            }
        }

        if (info == null) {
            System.out.println("No mixer found");
            return;
        }

        try {
            TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(format, info);
            File file = new File(path);
            AudioInputStream inputStream = new AudioInputStream(targetDataLine);

            targetDataLine.open(format);
            targetDataLine.start();
            Thread t = new Thread (() -> {
                try {
                    AudioSystem.write(inputStream, AudioFileFormat.Type.WAVE, file);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });

            t.start();
            Thread.sleep(2000);
            t.interrupt();

            targetDataLine.stop();
            targetDataLine.close();
            System.out.println("Audio captured");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        /*
        try {
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
            String path = System.getProperty("user.home") + File.separator + "audio.wav";
            File outputFile = new File(path);
            AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;
            AudioInputStream ais = new AudioInputStream(line);

            System.out.println("Starting audio capture");
            line.open(format);
            line.start();
            AudioSystem.write(ais, type, outputFile);

            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

            Runnable task = () -> {
                System.out.println("Stopping audio capture");
                line.stop();
                line.close();
            };

            executor.schedule(task, 2000, TimeUnit.SECONDS);

            System.out.println("Audio captured");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
