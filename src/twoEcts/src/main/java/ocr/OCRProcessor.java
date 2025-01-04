package ocr;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * A processor that performs OCR on images concurrently using a pool of Tesseract instances and a thread pool.
 */
public class OCRProcessor {
    private final ExecutorService executorService;
    private final ITesseractPool tesseractPool;

    private volatile boolean closed = false;

    public OCRProcessor(int poolSize) {
        this.executorService = Executors.newFixedThreadPool(poolSize);
        this.tesseractPool = new ITesseractPool(poolSize);
    }

    /**
     * Process an image file and return the OCR result. Can throw an IllegalStateException if the processor is closed.
     * @param image the image file to process
     * @return a Future containing the OCR result
     */
    public Future<Optional<String>> process(File image) {
        if (closed) {
            throw new IllegalStateException("Processor is closed and cannot be used");
        }
        return executorService.submit(new OCRWorker(image, tesseractPool));
    }

    /**
     * Close the processor and release all resources.
     */
    public void close() {
        closed = true;
        executorService.shutdown();
        tesseractPool.close();
    }
}
