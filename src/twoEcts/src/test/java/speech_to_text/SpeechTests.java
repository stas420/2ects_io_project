package speech_to_text;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
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
