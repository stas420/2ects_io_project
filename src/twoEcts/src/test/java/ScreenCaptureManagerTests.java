import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import screen_capture.ScreenCapture;
import screen_capture.ScreenCaptureManager;

public class ScreenCaptureManagerTests {

    private ScreenCaptureManager screenCaptureManager;

    @BeforeEach
    public void setUp() {
        screenCaptureManager = ScreenCaptureManager.getInstance();
        screenCaptureManager.Deactivate();
    }

    @Test
    public void testSingletonInstance() {
        ScreenCaptureManager instance1 = ScreenCaptureManager.getInstance();
        ScreenCaptureManager instance2 = ScreenCaptureManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    public void testActivateDeactivate() {
        screenCaptureManager.Activate();
        assertTrue(screenCaptureManager.isActivated());

        screenCaptureManager.Deactivate();
        assertFalse(screenCaptureManager.isActivated());
    }

    @Test
    public void testCaptureScreenshot() throws Exception {
        Robot robot = mock(Robot.class);
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        when(robot.createScreenCapture(any(Rectangle.class))).thenReturn(image);

        screenCaptureManager.Activate();

        // Use reflection to access the private captureScreenshot method
        Method captureScreenshotMethod = ScreenCaptureManager.class.getDeclaredMethod("captureScreenshot");
        captureScreenshotMethod.setAccessible(true);
        captureScreenshotMethod.invoke(screenCaptureManager);

        Optional<ScreenCapture> screenCapture = screenCaptureManager.popTheOldestScreenshot();
        assertTrue(screenCapture.isPresent());
        assertEquals(image, screenCapture.get().getImage());

        screenCaptureManager.Deactivate();
    }

    @Test
    public void testCompareImages() {
        BufferedImage img1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        BufferedImage img2 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);

        boolean result = screenCaptureManager.compareImages(img1, img2);
        assertTrue(result);
    }
}