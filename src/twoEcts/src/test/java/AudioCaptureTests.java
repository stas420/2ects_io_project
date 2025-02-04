import com.google.api.gax.rpc.ClientStream;
import com.google.api.gax.rpc.ResponseObserver;
import com.google.api.gax.rpc.StreamController;
import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import speech_to_text.AudioUtil;
import speech_to_text.GoogleSpeech;

import javax.sound.sampled.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.Future;

public class AudioCaptureTests {
    @Test
    public void testAudioCaptureTranscription() {
        Mixer.Info info = null;

        // audio capture utilities
        final AudioFormat format = new AudioFormat(44100, 16, 2,true, false);
        final long recordingPeriod = 10000; //< in milliseconds
        final long waitingStep = 1000; //< in milliseconds
        final String path = System.getProperty("user.home") + File.separator + "audio.wav"; //< TODO change it!


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
            Thread.sleep(4000);
            t.interrupt();

            targetDataLine.stop();
            targetDataLine.close();
            System.out.println("Audio captured");

            Optional<byte[]> audioBytes = AudioUtil.fileToBytes(file);
            Assertions.assertTrue(audioBytes.isPresent());



            GoogleSpeech gs = new GoogleSpeech();
            Future<Optional<String>> tts = gs.transcribe(audioBytes.get(), "en-US");
            Optional<String> result = tts.get();

            Assertions.assertTrue(result.isPresent());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Performs microphone streaming speech recognition with a duration of 1 minute. */
    public static void streamingMicRecognize() throws Exception {

        ResponseObserver<StreamingRecognizeResponse> responseObserver = null;
        try (SpeechClient client = SpeechClient.create()) {

            responseObserver =
                    new ResponseObserver<StreamingRecognizeResponse>() {
                        ArrayList<StreamingRecognizeResponse> responses = new ArrayList<>();

                        public void onStart(StreamController controller) {}

                        public void onResponse(StreamingRecognizeResponse response) {
                            responses.add(response);
                        }

                        public void onComplete() {
                            for (StreamingRecognizeResponse response : responses) {
                                StreamingRecognitionResult result = response.getResultsList().get(0);
                                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                                System.out.printf("Transcript : %s\n", alternative.getTranscript());
                            }
                        }

                        public void onError(Throwable t) {
                            System.out.println(t);
                        }
                    };

            ClientStream<StreamingRecognizeRequest> clientStream =
                    client.streamingRecognizeCallable().splitCall(responseObserver);

            RecognitionConfig recognitionConfig =
                    RecognitionConfig.newBuilder()
                            .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                            .setLanguageCode("en-US")
                            .setSampleRateHertz(44100)
                            .build();
            StreamingRecognitionConfig streamingRecognitionConfig =
                    StreamingRecognitionConfig.newBuilder().setConfig(recognitionConfig).build();

            StreamingRecognizeRequest request =
                    StreamingRecognizeRequest.newBuilder()
                            .setStreamingConfig(streamingRecognitionConfig)
                            .build(); // The first request in a streaming call has to be a config

            clientStream.send(request);
            // SampleRate:16000Hz, SampleSizeInBits: 16, Number of channels: 1, Signed: true,
            // bigEndian: false
            AudioFormat audioFormat = new AudioFormat(44100, 16, 1, true, false);

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
            TargetDataLine targetDataLine = AudioSystem.getTargetDataLine(audioFormat, info);
            //AudioInputStream inputStream = new AudioInputStream(targetDataLine);

            DataLine.Info targetInfo =
                    new DataLine.Info(
                            TargetDataLine.class,
                            audioFormat); // Set the system information to read from the microphone audio stream

            if (!AudioSystem.isLineSupported(targetInfo)) {
                System.out.println("Microphone not supported");
                System.exit(0);
            }
            // Target data line captures the audio stream the microphone produces.
            targetDataLine.open(audioFormat);
            targetDataLine.start();
            System.out.println("Start speaking");
            long startTime = System.currentTimeMillis();
            // Audio Input Stream
            AudioInputStream audio = new AudioInputStream(targetDataLine);
            while (true) {
                long estimatedTime = System.currentTimeMillis() - startTime;
                byte[] data = new byte[32000];
                audio.read(data);
                if (estimatedTime > 10_000) { // 60 seconds
                    System.out.println("Stop speaking.");
                    targetDataLine.stop();
                    targetDataLine.close();
                    break;
                }
                request =
                        StreamingRecognizeRequest.newBuilder()
                                .setAudioContent(ByteString.copyFrom(data))
                                .build();
                clientStream.send(request);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        responseObserver.onComplete();
    }

    public static void main(String[] args) throws Exception {
        streamingMicRecognize();
    }
}
