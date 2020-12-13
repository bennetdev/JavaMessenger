package client.data;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private String from;
    private String to;
    private String text;
    private LocalDateTime timeSend;

    public Message(String from, String to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
        setTimeSend(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Text: \"" + getText() + "\" From: " + getFrom() + " To: " + getTo();
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimeSend() {
        return timeSend;
    }

    public void setTimeSend(LocalDateTime timeSend) {
        this.timeSend = timeSend;
    }
}
