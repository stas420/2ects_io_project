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

    public Future<Optional<List<String>>> transcribe(AudioInputStream audioInputStream, TargetDataLine targetDataLine, int durationInMillis,
                                               String languageCode) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> results = new ArrayList<>();

            ResponseObserver<StreamingRecognizeResponse> responseObserver = new ResponseObserver<>() {
                @Override
                public void onStart(StreamController streamController) {}
                @Override
                public void onResponse(StreamingRecognizeResponse streamingRecognizeResponse) {
                    streamingRecognizeResponse.getResultsList().stream()
                            .map(response -> response.getAlternativesList().getFirst().getTranscript())
                            .forEach(results::add);
                    System.out.println("ArrayList size: " + results.size());
                }
                @Override
                public void onError(Throwable throwable) {
                    throwable.printStackTrace();
                }
                @Override
                public void onComplete() {}
            };

            ClientStream<StreamingRecognizeRequest> clientStream =
                    speechClient.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setSampleRateHertz(44100)
                    .setEnableAutomaticPunctuation(true)
                    .setMaxAlternatives(1)
                    .setLanguageCode(languageCode)
                    .build();

            StreamingRecognitionConfig streamingRecognitionConfig =
                    StreamingRecognitionConfig.newBuilder().setConfig(config).build();

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingRecognitionConfig)
                            .build(); // The first request in a streaming call has to be a config
            clientStream.send(request);

            System.out.println("Transcription started");
            long startTime = System.nanoTime();
            try {
                ByteString byteString;
                while (true) {
                    long estimatedTime = System.currentTimeMillis() - startTime;
                    byte[] data = new byte[64000];
                    if (audioInputStream.read(data) < 0) {
                        System.out.println("no audio read");
                    }

                    if (estimatedTime > durationInMillis) { // 60 seconds
                        System.out.println("Stop speaking.");
                        targetDataLine.stop();
                        targetDataLine.close();
                        break;
                    }

                    byteString = ByteString.copyFrom(data);

                    request =
                            StreamingRecognizeRequest.newBuilder()
                                    .setAudioContent(byteString)
                                    .build();
                    clientStream.send(request);
                    System.gc();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }
            System.out.println("Transcription ended");
            return Optional.of(results);
        });

    }

    @Override
    public void close() throws Exception {
        speechClient.close();
    }
}
