package audio_capturing;

import timestamping.TimestampManager;

// more of a struct representing timestamped audio transcription
public class AudioCapture {

    public AudioCapture(long timestamp, String content) {
        this.Timestamp = timestamp;
        this.Content = content;
    }

    public long getTimestamp() {
        return this.Timestamp;
    }

    public String getContent() {
        return this.Content;
    }

    private long Timestamp = -1;
    private String Content = null;
}
