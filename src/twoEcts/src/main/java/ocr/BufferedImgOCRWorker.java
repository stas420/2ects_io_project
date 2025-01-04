package ocr;

import net.sourceforge.tess4j.ITesseract;

import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.concurrent.Callable;

class BufferedImgOCRWorker implements Callable<Optional<String>> {
    private final BufferedImage image;
    private final ITesseractPool tesseractPool;
    public BufferedImgOCRWorker(BufferedImage image, ITesseractPool pool) {
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
