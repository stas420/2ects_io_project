package audio_capturing;

import speech_to_text.GoogleSpeech;
import timestamping.TimestampManager;

import javax.sound.sampled.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
/*
    TODO:
    - add scheduled audio capturing
 */

public class AudioCaptureManager {

    // public usage
    public static boolean isActive() {
        return active.get();
    }

    public static void Activate() {
        for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
            System.out.println(mi);
            if (mi.getName().contains("efault")) {
                info = mi;
            }
        }

        if (info == null) {
            System.out.println("AudioCaptureManager::Activate | No mixer found");
            return;
        }

        try {
            targetDataLine = AudioSystem.getTargetDataLine(audioFormat, info);
            audioInputStream = new AudioInputStream(targetDataLine);
        } catch (Exception e) {
            System.out.println("AudioCaptureManager::Activate | Error opening target data line and fetching audio input stream");
            e.printStackTrace();
            return;
        }

        audioCaptures = new ArrayList<>();
        googleSpeech = new GoogleSpeech();

        if (AudioCaptureManager.getInstance().isActive()) {
            capturing = new Thread(() -> {
                while (AudioCaptureManager.isActive()) {
                    AudioCaptureManager.getInstance().captureAudio();
                }
            });

            capturing.start();
        }


        active.set(true);
    }

    public static void Deactivate() {
        try {
            active.set(false);

            while (capturing.isAlive()) {
                capturing.interrupt();
                capturing.join();
            }

            audioInputStream.close();
            googleSpeech.close();
        } catch (Exception e) {
            System.out.println("AudioCaptureManager::Deactivate | Error closing audio input stream or Google Speech");
            e.printStackTrace();
        }

        targetDataLine.close();

        audioInputStream = null;
        targetDataLine = null;
        info = null;
        googleSpeech = null;
    }

    public static Optional<AudioCapture> popTheOldestAudioCapture() {
        if (audioCaptures == null || audioCaptures.isEmpty()) {
            return Optional.empty();
        }

        Optional<AudioCapture> _out = Optional.of(audioCaptures.getFirst());
        long _timestamp = audioCaptures.getFirst().getTimestamp();
        int _index = 0;

        for (int i = 1; i < audioCaptures.size(); i++) {
            if (audioCaptures.get(i).getTimestamp() < _timestamp) {
                _index = i;
                _timestamp = audioCaptures.get(i).getTimestamp();
                _out = Optional.of(audioCaptures.get(i));
            }
        }

        audioCaptures.remove(_index);
        return _out;
    }

    // necessary private fields
    private static final AtomicBoolean active = new AtomicBoolean(false);
    private static Mixer.Info info = null;
    private static TargetDataLine targetDataLine = null;
    private static AudioInputStream audioInputStream = null;
    private static final AudioFormat audioFormat = new AudioFormat(44100, 16, 2,
            true, false);

    private static Thread capturing = null;


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
    private static final long recordingPeriod = 10000000; //< in nanoseconds = 10 ms
    private static final long waitingStep = 1000; //< in milliseconds
    private static final String langCode = "en-US";
    private static GoogleSpeech googleSpeech = null;
    private static List<AudioCapture> audioCaptures = null;

    private static void captureAudio() {
        if (!AudioCaptureManager.active.get()) {
            System.out.println("AudioCaptureManager::captureAudio | Manager not activated");
            return;
        }

        try {
            long ts = TimestampManager.getInstance().getTimestamp();
            Optional<List<String>> transcription = googleSpeech.transcribe(audioInputStream, targetDataLine, recordingPeriod, langCode).get();

            if (transcription.isPresent()) {
                String text = String.join("\n", transcription.get());
                AudioCaptureManager.audioCaptures.add(new AudioCapture(ts, text));
                System.out.println("AudioCaptureManager::captureAudio | new one added, timestamp: " + ts);
            }
            else {
                System.out.println("AudioCaptureManager::captureAudio | No transcription found");
            }

        } catch (Exception e) {
            System.out.println("AudioCaptureManager::captureAudio | transcription error");
            e.printStackTrace();
        }
    }

    /*
    public static void main(String[] args) {



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
            Thread.sleep(4000);
            t.interrupt();

            targetDataLine.stop();
            targetDataLine.close();
            System.out.println("Audio captured");

            byte[] audioBytes = Files.readAllBytes(Paths.get(path));
            GoogleSpeech gs = new GoogleSpeech();
            Future<Optional<String>> tts = gs.transcribe(audioBytes, "en-US");
            Optional<String> result = tts.get();

            if (result.isPresent()) {
                System.out.println(result.get());
            }
            else {
                System.out.println("Audio capture failed");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        / *
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
        * /
    }

    */
}
