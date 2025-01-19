package speech_to_text;

import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;

import javax.sound.sampled.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class GoogleSpeech implements SpeechToText, AutoCloseable {
    private final SpeechClient speechClient;

    public GoogleSpeech() {
        try {
            speechClient = SpeechClient.create();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Google Speech client", e);
        }
    }

    @Override
    public Future<Optional<String>> transcribe(byte[] audioData, String languageCode) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ByteString audioBytes = ByteString.copyFrom(audioData);
                RecognitionConfig config = RecognitionConfig.newBuilder()
                        .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                        .setSampleRateHertz(44100)
                        .setLanguageCode(languageCode)
                        .build();
                RecognitionAudio audio = RecognitionAudio.newBuilder()
                        .setContent(audioBytes)
                        .build();
                RecognizeResponse response = speechClient.recognize(config, audio);
                StringBuilder transcription = new StringBuilder();
                response.getResultsList().stream()
                        .map(SpeechRecognitionResult::getAlternativesList)
                        .map(List::getFirst)
                        .map(SpeechRecognitionAlternative::getTranscript)
                        .forEach(transcription::append);

                return Optional.of(transcription.toString());
            } catch (Exception e) {
                return Optional.empty();
            }
        });
    }

    @Override
    public void close() throws Exception {
        speechClient.close();
    }
}
