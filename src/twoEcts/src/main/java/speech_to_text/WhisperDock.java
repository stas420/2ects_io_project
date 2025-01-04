package speech_to_text;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
/**
 *
 */
@Deprecated
public class WhisperDock {

    // === Public methods to use ===

    // as WhisperDock should have one instance to communicate
    // with the running app, we should use: getInstance().doSomething()
    // >> note that getInstance() also initializes it, so carefully plan the timing
    public static WhisperDock getInstance() {
        if (instance == null) {
            instance = new WhisperDock();
        }

        return instance;
    }

//    public static String submitTranscriptionTask() {
//      // TODO
//    }

    // --- Singleton implementation ---
    private static WhisperDock instance;

    private WhisperDock() {
        /*
            Here insert any startup tasks
        */
        // preparing HTTP
        try {
            whisperdockUrl = new URL("http://localhost:5000/transcribe");
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException: " + e.getMessage());
        }

        // preparing thread
        Thread mainThread = new Thread(() -> {
            while (true) {
                try {
                    Runnable task = tasks.take();
                    executor.submit(task);
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("WhisperDock class - InterruptedException thrown, stack trace: ");
                    e.printStackTrace();
                }
            }
        });

        mainThread.setDaemon(true);
        mainThread.start();
    }

    // Method to submit transcription task
    public String submitTranscriptionTask(String filePath) {
        try {
            tasks.put(() -> {
                Optional<String> result = askWhisperDock(filePath);
                result.ifPresentOrElse(
                        transcription -> System.out.println("Transcription: " + transcription),
                        () -> System.err.println("Failed to transcribe audio.")
                );
            });
            return "Task submitted successfully.";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: Unable to submit task. " + e.getMessage();
        }
    }

    // --- HTTP client implementation
    private static HttpURLConnection connection = null;
    private static URL whisperdockUrl = null; //< TODO set this little guy

    // Method to make a POST request to WhisperDock for transcription
    private Optional<String> askWhisperDock(String filePath) {
        File audioFile = new File(filePath);
        if (!audioFile.exists()) {
            System.err.println("Error: File not found - " + filePath);
            return Optional.empty();
        }

        try {
            // Prepare the connection
            connection = (HttpURLConnection) whisperdockUrl.openConnection();
            connection.setRequestMethod("POST");
            //connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            // Prepare output stream to send the file
            try (OutputStream outputStream = connection.getOutputStream();
                 DataOutputStream writer = new DataOutputStream(outputStream)) {

                // Get the response code
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read the response from the server
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        return Optional.of(response.toString());
                    }
                } else {
                    System.err.println("Error: Server responded with code " + responseCode);
                    return Optional.empty();
                }
            }

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    // --- Task queueing and constraining ---
    private final int taskQueueSize = 1000;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>(taskQueueSize);
}
