package screen_capture;

/*
    This class implements the following functionalities:
    - capturing screen contents;
    - making screenshots every 5 seconds;
    - checking whether the contents are changed or not:
        - based on the above save the image file, or ignore it;
        - make use of ScreenCapture class.
*/

import timestamping.TimestampManager;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

// could be an 'enum' instead of class, which "ensures thread-safety for singleton"
// but so far so good
// -------------------------------------
// the following class needs to be used in some kind of *app's main thread*
// where there is called .getInstance().Activate() at the beginning
// and .getInstance().Deactivate() at the end of the thread (ideally, in "finally" block)
public class ScreenCaptureManager {

    // singleton implementation
    private static ScreenCaptureManager INSTANCE = null;

    private ScreenCaptureManager() {}

    public static ScreenCaptureManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ScreenCaptureManager();
        }

        return INSTANCE;
    }

    // functional utilities - isActivated(), Activate() and Deactivate()
    private AtomicBoolean isActivated = new AtomicBoolean(false);
    private Robot robot = null;
    private ScheduledExecutorService scheduler = null;
    private static final Integer screenshotPeriod = 5;

    public boolean isActivated() {
        return isActivated.get();
    }

    public void Activate() {
        try {
            this.robot = new Robot();
            this.Screenshots = new ArrayList<ScreenCapture>();

            this.isActivated.set(true);
        }
        catch (Exception e) {
            System.out.println("ScreenCaptureManager::Activate | Method call failed, stack trace: ");
            e.printStackTrace();

            isActivated.set(false);
            return;
        }

        if (isActivated.get()) {
            scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                if (isActivated.get()) {
                    captureScreenshot();
                }
            }, 0, screenshotPeriod, TimeUnit.SECONDS);

            System.out.println("ScreenCaptureManager::Activate | successfully activated");
        }
        else {
            scheduler = null;
        }
    }

    public void Deactivate() {
        this.isActivated.set(false);

        if (this.scheduler != null && !this.scheduler.isShutdown()) {
            this.scheduler.shutdownNow();

            try {
                this.scheduler.awaitTermination(screenshotPeriod, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("ScreenCaptureManager::Deactivate | awaitTermination failed, stack trace: ");
                e.printStackTrace();
            }
        }

        this.robot = null;
        this.scheduler = null;
        if (this.Screenshots != null) {
            this.Screenshots.clear();
            this.Screenshots = null;
        }

        System.out.println("ScreenCaptureManager::Deactivate");
    }




    // actual screenshotting
    private void captureScreenshot() {
        if (!this.isActivated.get()) {
            System.out.println("ScreenCaptureManager::captureScreenshot | Invalid method call, it's not activated");
            return;
        }

        try {
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            ScreenCapture sc = new ScreenCapture(
                    this.robot.createScreenCapture(screenRect),
                    TimestampManager.getInstance().getTimestamp()
            );

            if (this.Screenshots.isEmpty() || !this.compareImages(sc.getImage(), this.Screenshots.getLast().getImage())) {
                this.Screenshots.add(sc);
                System.out.println("ScreenCaptureManager::captureScreenshot | new one done and saved, at: " + sc.getTimeStamp());
            }
            else {
                System.out.println("ScreenCaptureManager::captureScreenshot | no valid screenshot added to list");
            }
        }
        catch (Exception e) {
            System.out.println("ScreenCaptureManager::captureScreenshot | Method call failed, stack trace: ");
            e.printStackTrace();
        }
    }

    // storing ScreenCaptures
    private ArrayList<ScreenCapture> Screenshots = null;

    // return Optional of ScreenCapture object with the lowest .getTimestamp() output,
    // and then *removes it* from the private list (hence why it's "pop")
    public Optional<ScreenCapture> popTheOldestScreenshot() {
        if ((!this.isActivated.get()) || this.Screenshots == null || Screenshots.isEmpty()) {
            return Optional.empty();
        }


        Optional<ScreenCapture> _out = Optional.of(this.Screenshots.getFirst());
        long _timestamp = this.Screenshots.getFirst().getTimeStamp();
        int _index = 0;

        for (int i = 1; i < this.Screenshots.size(); i++) {
            if (this.Screenshots.get(i).getTimeStamp() < _timestamp) {
                _index = i;
                _timestamp = this.Screenshots.get(i).getTimeStamp();
                _out = Optional.of(this.Screenshots.get(i));
            }
        }

        this.Screenshots.remove(_index);
        return _out;
    }

    // SSIM images comparison constant of acceptable difference
    private final double acceptableSSIMDifference = 0.9;

    // returns true if images are "similar enough"
    // (i.e. result of SSIM is greater-or-equal than acceptable value *acceptableSSIMDifference*)
    public boolean compareImages(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        int width = img1.getWidth();
        int height = img1.getHeight();

        double C1 = Math.pow(0.01 * 255, 2);
        double C2 = Math.pow(0.03 * 255, 2);

        double meanX = 0;
        double meanY = 0;
        double varianceX = 0;
        double varianceY = 0;
        double covariance = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                double grayX = (rgb1 >> 16 & 0xff) * 0.299 + (rgb1 >> 8 & 0xff) * 0.587 + (rgb1 & 0xff) * 0.114;
                double grayY = (rgb2 >> 16 & 0xff) * 0.299 + (rgb2 >> 8 & 0xff) * 0.587 + (rgb2 & 0xff) * 0.114;

                meanX += grayX;
                meanY += grayY;
            }
        }

        meanX /= (width * height);
        meanY /= (width * height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb1 = img1.getRGB(x, y);
                int rgb2 = img2.getRGB(x, y);

                double grayX = (rgb1 >> 16 & 0xff) * 0.299 + (rgb1 >> 8 & 0xff) * 0.587 + (rgb1 & 0xff) * 0.114;
                double grayY = (rgb2 >> 16 & 0xff) * 0.299 + (rgb2 >> 8 & 0xff) * 0.587 + (rgb2 & 0xff) * 0.114;

                varianceX += Math.pow(grayX - meanX, 2);
                varianceY += Math.pow(grayY - meanY, 2);
                covariance += (grayX - meanX) * (grayY - meanY);
            }
        }

        varianceX /= (width * height - 1);
        varianceY /= (width * height - 1);
        covariance /= (width * height - 1);

        double numerator = (2 * meanX * meanY + C1) * (2 * covariance + C2);
        double denominator = (Math.pow(meanX, 2) + Math.pow(meanY, 2) + C1) * (varianceX + varianceY + C2);

        return (numerator / denominator) >= ScreenCaptureManager.getInstance().acceptableSSIMDifference;
    }

    // simple test main - not to be used in release!
    public static void main(String[] args) {
        ScheduledExecutorService _scheduler = Executors.newSingleThreadScheduledExecutor();

        try {
            ScreenCaptureManager.getInstance().Activate();
            TimestampManager.getInstance().Activate();

            if (ScreenCaptureManager.getInstance().isActivated()) {
                Runnable task = () -> {
                    boolean _v = ScreenCaptureManager.getInstance().compareImages(
                      ScreenCaptureManager.getInstance().popTheOldestScreenshot().get().getImage(),
                      ScreenCaptureManager.getInstance().popTheOldestScreenshot().get().getImage()
                    );

                    if (_v)
                        System.out.println("The images are similar enough");
                    else
                        System.out.println("The images are NOT similar enough");

                    TimestampManager.getInstance().Deactivate();
                    ScreenCaptureManager.getInstance().Deactivate();
                    _scheduler.shutdownNow();
                };
                _scheduler.schedule(task, 3 * screenshotPeriod, TimeUnit.SECONDS);
            }

        } catch (Exception e) {

            ScreenCaptureManager.getInstance().Deactivate();
            _scheduler.shutdownNow();

            throw new RuntimeException(e);
        }
    }
}
