package com.patronite.service.battle;

public class LogEntry {
    private final int round;
    private final String content;
    private final String targetId;
    private boolean delivered;

    public LogEntry(int round, String content, boolean isDelivered, String targetId) {
        this.round = round;
        this.content = content;
        this.targetId = targetId;
        delivered = isDelivered;
    }

    public LogEntry(int round, String content, boolean isDelivered) {
        this(round, content, isDelivered, null);
    }

    public int getRound() {
        return round;
    }

    public String getContent() {
        return content;
    }

    public String getTargetId() {
        return targetId;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "round=" + round +
                ", content='" + content + '\'' +
                ", targetId='" + targetId + '\'' +
                ", delivered=" + delivered +
                '}';
    }
}
