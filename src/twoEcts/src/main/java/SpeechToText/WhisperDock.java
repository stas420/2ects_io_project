package SpeechToText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

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

    // --- HTTP client implementation
    private static HttpURLConnection connection = null;
    private static URL whisperdockUrl = null; //< TODO set this little guy

    private Optional<String> askWhisperDock() {
        /*
            As per readme file on github:
            "To transcribe audio, make a POST request to the /transcribe endpoint
             with the audio file" ~/ErcinDedeoglu/WhisperDock

             so shall I do it
        */
        try {
            // request
            connection = (HttpURLConnection) whisperdockUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // TODO end this shit

            // response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );
        }
        catch (Exception e) {

        };

        return Optional.empty();
    }

    // --- Task queueing and constraining ---
    private final int taskQueueSize = 10;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>(taskQueueSize);
}
