package Timestamping;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimestampManager {

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
