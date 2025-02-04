import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import speech_to_text.AudioUtil;
import speech_to_text.GoogleSpeech;
import speech_to_text.SpeechToText;

import javax.sound.sampled.*;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;


public class SpeechTests {
    @Test
    public void alwaysPasses() {
        Assertions.assertEquals(1, 1);
    }

    @Test
    public void alwaysFails() {
        Assertions.assertThrows(AssertionError.class, () -> {
            Assertions.assertEquals(1, 2);
        });
    }

    @Test
    public void testFileToBytes() {
        File file = new File(getClass().getClassLoader().getResource("test.wav").getFile());
        Optional<byte[]> audioData = AudioUtil.fileToBytes(file);
        Assertions.assertTrue(audioData.isPresent());
    }

    @Test
    public void testFileToGoogleSpeech() {
        File file = new File(getClass().getClassLoader().getResource("test.wav").getFile());
        Optional<byte[]> audioData = AudioUtil.fileToBytes(file);
        Assertions.assertTrue(audioData.isPresent());
        SpeechToText googleSpeech = new GoogleSpeech();
        Future<Optional<String>> transcriptionFuture = googleSpeech.transcribe(audioData.get(), "en-US");
        try {
            Optional<String> transcription = transcriptionFuture.get();
            transcription.ifPresent(System.out::println);
            Assertions.assertTrue(transcription.isPresent());
        } catch (Exception e) {
            Assertions.fail("Failed to transcribe audio file", e);
        }
    }

    @Test
    public void testStreamingGoogleSpeech() {
        try (GoogleSpeech googleSpeech = new GoogleSpeech()) {
            Mixer.Info info = null;

            for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
                System.out.println(mi.getName());
                if (mi.getName().contains("efault")) {
                    info = mi;
                }
            }
            AudioFormat audioFormat = new AudioFormat(44100,16,2,true,false);
            TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat, info);
            AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);

            Optional<List<String>> results = googleSpeech.transcribe(audioInputStream, targetDataLine, (long) 1e10, "en-US").get();
            results.ifPresent(strings -> strings.forEach(System.out::println));
            Assertions.assertTrue(results.isPresent());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to play audio file", e);
        }
    }

    @Test
    public void testGoogleAccuracy() {
        final String expected = ("shall I compare thee to a summer's day Thou Art more lovely and more temperate rough " +
                "winds do shake the darling butts of May and Summers these have all too short a date sometime too hot " +
                "the eye of Heaven shines and often in is his gold complexion dimmed and the Very and every fair from " +
                "Fair sometime declines by chance or Nature's changing course untrimmed but thy eternal Summer shall not " +
                "fade nor lose possession of that fair thou Hast nor shall death brag the wonders in his shade when in Eternal " +
                "lines to time thou gross so long as men can breathe or eyes can see so long lives this and this gives life to thee").toLowerCase();
        File file = new File(getClass().getClassLoader().getResource("test.wav").getFile());
        Optional<byte[]> audioData = AudioUtil.fileToBytes(file);
        Assertions.assertTrue(audioData.isPresent());
        GoogleSpeech googleSpeech = new GoogleSpeech();
        Future<Optional<String>> transcriptionFuture = googleSpeech.transcribe(audioData.get(), "en-US");
        try {
            Optional<String> transcription = transcriptionFuture.get();
            transcription.ifPresent(System.out::println);
            Assertions.assertTrue(transcription.isPresent());
            Assertions.assertTrue(transcription.get().toLowerCase().equals(expected));
        } catch (Exception e) {
            Assertions.fail("Failed to transcribe audio file", e);
        }
    }
}
