import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.Optional;

public class mainClass {
    public static void main(String[] args) {
        OCRProcessor processor = new OCRProcessor(5);
        File image = new File("src/twoEcts/src/files/text.jpg");
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CompletionService<Optional<String>> completionService = new ExecutorCompletionService<>(executor);

        List<Future<Optional<String>>> futures = new ArrayList<>();
        long[] startTimes = new long[5];
        for (int i = 0; i < 5; i++) {
            futures.add(completionService.submit(() -> processor.process(image).get()));
            long start = System.currentTimeMillis();
            System.out.println("Thread #" + (i + 1) + " started at " + start);
            startTimes[i] = start;
        }

        for (int i = 0; i < 5; i++) {
            try {
                Future<Optional<String>> future = completionService.take();
                long end = System.currentTimeMillis();
                int index = futures.indexOf(future) + 1;
                future.get().ifPresent(value -> System.out.println("Thread #" + index + " done at " + end + "\n\tTook " + (end - startTimes[index - 1]) + "ms"));
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        processor.close();
    }
}