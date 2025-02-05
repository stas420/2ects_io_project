package ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;
import java.nio.file.Path;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A pool of Tesseract instances. Used to avoid creating a new Tesseract instance for each image.
 */
class ITesseractPool {
    private final static String tessdataPath = "tessdata";
    private final BlockingQueue<ITesseract> pool;

    public ITesseractPool(int poolSize) {
        pool = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath(tessdataPath);
            Path path = new File(tessdataPath).toPath();
            System.out.println("Tessdata path: " + path.toAbsolutePath());
            pool.add(tesseract);
        }
    }

    /**
     * Borrow a Tesseract instance from the pool. This method will block if no instance is available.
     * @return a free Tesseract instance from the pool
     * @throws InterruptedException
     */
    public ITesseract borrow() throws InterruptedException {
        return pool.take();
    }

    /**
     * Return a Tesseract instance to the pool.
     * @param tesseract the Tesseract instance to return
     */
    public void release(ITesseract tesseract) {
        pool.add(tesseract);
    }
    /**
     * Close the pool and release all Tesseract instances.
     */
    public void close() {
        pool.clear();
    }
}
