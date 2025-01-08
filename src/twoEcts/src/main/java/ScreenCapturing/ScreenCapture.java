package ScreenCapturing;

/*
    This class is a representation of a done screenshot:
    - it contains path to the screenshot file;
    - it contains the metadata, such as time taken;
    - it may return a file? << TODO discuss
    - well, that's it I guess.
*/

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

// simple class, which is more of a "struct", representing a timestamped-screenshot
public class ScreenCapture {

    private BufferedImage image = null;
    private long timeStamp = 0;

    public ScreenCapture(BufferedImage image, long timeStamp) {
        this.image = image;
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public BufferedImage getImage() {
        return image;
    }
}
