package ocr;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OCRProcessorTests {

    private OCRProcessor ocrProcessor;
    private ITesseractPool tesseractPoolMock;
    private ITesseract tesseractMock;

    @BeforeEach
    void setUp() throws InterruptedException {
        tesseractPoolMock = mock(ITesseractPool.class);
        tesseractMock = mock(ITesseract.class);
        when(tesseractPoolMock.borrow()).thenReturn(tesseractMock);
        ocrProcessor = new OCRProcessor(2);
    }

    @AfterEach
    void tearDown() {
        ocrProcessor.close();
    }

    @Test
    void testProcessFile() throws ExecutionException, InterruptedException, TesseractException {
        File imageFile = new File("test.png");
        when(tesseractMock.doOCR(imageFile)).thenReturn("Test OCR Result");

        Future<Optional<String>> result = ocrProcessor.process(imageFile);
        assertTrue(result.get().isPresent());
        assertEquals("Test OCR Result", result.get().get());
    }

    @Test
    void testProcessBufferedImage() throws ExecutionException, InterruptedException, TesseractException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        when(tesseractMock.doOCR(image)).thenReturn("Test OCR Result");

        Future<Optional<String>> result = ocrProcessor.process(image);
        assertTrue(result.get().isPresent());
        assertEquals("Test OCR Result", result.get().get());
    }

    @Test
    void testProcessAfterClose() {
        ocrProcessor.close();
        File imageFile = new File("test.png");

        assertThrows(IllegalStateException.class, () -> ocrProcessor.process(imageFile));
    }
}