package com.studytracker.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Subclass 2: Represents a single study session.
 * Extends TrackerItem and overrides calculateScore() for XP/bonus calculation.
 */
public class StudySession extends TrackerItem {

    private String subject;
    private long durationSeconds;
    private String timestamp;

    private static final DateTimeFormatter FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StudySession() {}

    public StudySession(String subject, long durationSeconds, LocalDateTime timestamp) {
        super(subject + " session", timestamp.toLocalDate().toString());
        this.subject = subject;
        this.durationSeconds = durationSeconds;
        this.timestamp = timestamp.format(FORMATTER);
    }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public long getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(long durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    /**
     * Method overriding: calculates XP earned for this session.
     * Base: 10 XP per minute. Bonus: +50% if session >= 1 hour.
     */
    @Override
    public double calculateScore() {
        double minutes = durationSeconds / 60.0;
        double xp = minutes * 10;
        if (durationSeconds >= 3600) {
            xp *= 1.5;  // 50% bonus for sessions >= 1 hour
        }
        return Math.round(xp * 100.0) / 100.0;
    }

    @Override
    public String getSummary() {
        return describe("Session") + " | " + getFormattedDuration() + " | XP: " + (int)calculateScore();
    }

    public String getFormattedDuration() {
        long h = durationSeconds / 3600;
        long m = (durationSeconds % 3600) / 60;
        long s = durationSeconds % 60;
        return String.format("%02d:%02d:%02d", h, m, s);
    }

    public String getDateOnly() {
        if (timestamp == null) return "";
        return timestamp.length() >= 10 ? timestamp.substring(0, 10) : timestamp;
    }

    public LocalDateTime getParsedTimestamp() {
        try {
            return LocalDateTime.parse(timestamp, FORMATTER);
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
