package ocr;

import net.sourceforge.tess4j.ITesseract;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * A worker that performs OCR on an image file.
 */
class FileOCRWorker implements Callable<Optional<String>> {
    private final File image;

    private final ITesseractPool tesseractPool;
    public FileOCRWorker(File image, ITesseractPool pool) {
        this.image = image;
        this.tesseractPool = pool;
    }

    @Override
    public Optional<String> call() {
        ITesseract tesseract = null;
        try {
            tesseract = tesseractPool.borrow();
            return Optional.of(tesseract.doOCR(image));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        } finally {
            if (tesseract != null) {
                tesseractPool.release(tesseract);
            }
        }
    }
}
