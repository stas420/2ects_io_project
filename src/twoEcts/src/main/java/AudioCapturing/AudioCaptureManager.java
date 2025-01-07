package AudioCapturing;

import javax.sound.sampled.*;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AudioCaptureManager {
    public static void main(String[] args) {
        final AudioFormat format = new AudioFormat(44100, 16, 2, true, false);

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        
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
    }
}
