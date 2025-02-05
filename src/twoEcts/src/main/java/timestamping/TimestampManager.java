package timestamping;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimestampManager {

    // mx long in Java 9,223,372,036,854,775,807 = c.a. 2*10^12 hours
    // actual Timestamping
    public long getTimestamp() {
        if (startTime == null) {
            return -1;
        }

        LocalDateTime now = LocalDateTime.now();
        return ChronoUnit.MILLIS.between(startTime, now);
    }

    // utilities
    private LocalDateTime startTime = null;
    private boolean isActivated = false;

    public boolean isActivated() {
        return isActivated;
    }

    public void Activate() {
        startTime = LocalDateTime.now();
        isActivated = true;
    }

    public void Deactivate() {
        startTime = null;
        isActivated = false;
    }

    // singleton implementation
    private static TimestampManager INSTANCE = null;

    private TimestampManager() {}

    public static TimestampManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TimestampManager();
        }

        return INSTANCE;
    }
}
