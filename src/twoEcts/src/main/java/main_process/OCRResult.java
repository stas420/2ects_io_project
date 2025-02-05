package main_process;

public class OCRResult {

    public OCRResult(long timestamp, String content) {
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
