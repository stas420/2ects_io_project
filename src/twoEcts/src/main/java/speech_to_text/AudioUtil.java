package speech_to_text;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

public abstract class AudioUtil {
    public static Optional<byte[]> fileToBytes(File file) {
        try {
            return Optional.of(Files.readAllBytes(file.toPath()));
        } catch (Exception _) {
            return Optional.empty();
        }
    }
}
