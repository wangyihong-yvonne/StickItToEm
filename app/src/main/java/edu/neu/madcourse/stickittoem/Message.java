package edu.neu.madcourse.stickittoem;

public class Message {
    private String content;
    private Long timestamp;

    public Message() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Message(String content, Long timestamp) {
        this.content = content;
        this.timestamp = timestamp;
    }
}
