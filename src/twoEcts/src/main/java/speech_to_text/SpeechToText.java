package speech_to_text;

import java.util.Optional;
import java.util.concurrent.Future;

public interface SpeechToText {
    Future<Optional<String>> transcribe(byte[] audioData, String languageCode);
}
