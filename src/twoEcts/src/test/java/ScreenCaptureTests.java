import org.junit.jupiter.api.Test;
import java.awt.image.BufferedImage;
import static org.junit.jupiter.api.Assertions.*;
import screen_capture.ScreenCapture;

public class ScreenCaptureTests {

    @Test
    public void testScreenCaptureCreation() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        long timeStamp = System.currentTimeMillis();
        ScreenCapture screenCapture = new ScreenCapture(image, timeStamp);

        assertEquals(timeStamp, screenCapture.getTimeStamp());
        assertEquals(image, screenCapture.getImage());
    }
}