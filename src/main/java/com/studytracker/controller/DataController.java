package com.studytracker.controller;

import com.studytracker.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DataController manages all data operations.
 * Uses plain TXT files for persistence (no JSON/Gson).
 * Format: TYPE|field1|field2|...
 */
public class DataController {

    private static final String DATA_FILE = System.getProperty("user.home") + "/study_tracker_data.txt";

    private static DataController instance;
    private AppData appData;

    private DataController() {
        appData = loadData();
    }

    public static DataController getInstance() {
        if (instance == null) {
            instance = new DataController();
        }
        return instance;
    }

    // ── Subjects ──────────────────────────────────────────────────────────────

    public List<Subject> getSubjects() {
        return appData.getSubjects();
    }

    public void addSubject(String name) throws InvalidStudyDataException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidStudyDataException("Subject name cannot be empty.");
        }
        if (name.contains("|")) {
            throw new InvalidStudyDataException("Subject name cannot contain the '|' character.");
        }
        boolean exists = appData.getSubjects().stream()
            .anyMatch(s -> s.getName().equalsIgnoreCase(name));
        if (!exists) {
            appData.getSubjects().add(new Subject(name));
            saveData();
        }
    }

    public void removeSubject(Subject subject) {
        appData.getSubjects().remove(subject);
        saveData();
    }

    // ── Sessions ──────────────────────────────────────────────────────────────

    public List<StudySession> getSessions() {
        return appData.getSessions();
    }

    public void addSession(StudySession session) throws InvalidStudyDataException {
        if (session.getDurationSeconds() <= 0) {
            throw new InvalidStudyDataException("Session duration must be greater than zero.");
        }
        appData.getSessions().add(session);
        saveData();
    }

    // ── Stats ─────────────────────────────────────────────────────────────────

    public long getTotalStudySeconds() {
        return appData.getSessions().stream()
            .mapToLong(StudySession::getDurationSeconds)
            .sum();
    }

    public int getTotalSessionCount() {
        return appData.getSessions().size();
    }

    public double getTotalXP() {
        return appData.getSessions().stream()
            .mapToDouble(StudySession::calculateScore)
            .sum();
    }

    public int getCurrentStreak() {
        List<StudySession> sessions = appData.getSessions();
        if (sessions.isEmpty()) return 0;

        Set<LocalDate> studyDays = sessions.stream()
            .map(s -> s.getParsedTimestamp().toLocalDate())
            .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        int streak = 0;
        LocalDate check = today;

        if (!studyDays.contains(today)) {
            check = today.minusDays(1);
        }

        while (studyDays.contains(check)) {
            streak++;
            check = check.minusDays(1);
        }

        return streak;
    }

    public Map<String, Long> getTimePerSubject() {
        Map<String, Long> map = new LinkedHashMap<>();
        for (StudySession session : appData.getSessions()) {
            map.merge(session.getSubject(), session.getDurationSeconds(), Long::sum);
        }
        return map;
    }

    // ── Goals ─────────────────────────────────────────────────────────────────

public List<StudyGoal> getGoals() {
    return appData.getGoals();
}

public void addGoal(StudyGoal goal) throws InvalidStudyDataException {
    if (goal.getSubjectName() == null || goal.getSubjectName().trim().isEmpty()) {
        throw new InvalidStudyDataException("Goal must have a subject.");
    }
    if (goal.getTargetHours() <= 0) {
        throw new InvalidStudyDataException("Target hours must be greater than zero.");
    }
    appData.getGoals().add(goal);
    saveData();
}

public void removeGoal(StudyGoal goal) {
    appData.getGoals().remove(goal);
    saveData();
}

public void updateGoalProgress(StudyGoal goal) {
    // Calculate actual completed hours from sessions for this subject
    long totalSeconds = appData.getSessions().stream()
        .filter(s -> s.getSubject().equalsIgnoreCase(goal.getSubjectName()))
        .mapToLong(StudySession::getDurationSeconds)
        .sum();
    goal.setCompletedHours(totalSeconds / 3600.0);
}

    // ── TXT Persistence ───────────────────────────────────────────────────────
    // Format per line:
    //   SUBJECT|name|weeklyGoalHours|createdDate
    //   SESSION|subject|durationSeconds|timestamp|createdDate
    //   GOAL|subjectName|targetHours|createdDate


    private AppData loadData() {
        AppData data = new AppData();
        File file = new File(DATA_FILE);
        if (!file.exists()) return data;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split("\\|", -1);
                if (parts.length < 2) continue;

                if (parts[0].equals("SUBJECT") && parts.length >= 4) {
                    Subject s = new Subject();
                    s.setName(parts[1]);
                    try { s.setWeeklyGoalHours(Integer.parseInt(parts[2])); }
                    catch (NumberFormatException e) { s.setWeeklyGoalHours(2); }
                    s.setCreatedDate(parts[3]);
                    data.getSubjects().add(s);

                } else if (parts[0].equals("SESSION") && parts.length >= 5) {
                    StudySession session = new StudySession();
                    session.setSubject(parts[1]);
                    try { session.setDurationSeconds(Long.parseLong(parts[2])); }
                    catch (NumberFormatException e) { session.setDurationSeconds(0); }
                    session.setTimestamp(parts[3]);
                    session.setCreatedDate(parts[4]);
                    session.setName(parts[1] + " session");
                    data.getSessions().add(session);
                }  else if (parts[0].equals("GOAL") && parts.length >= 4) {
                   StudyGoal g = new StudyGoal();
                   g.setSubjectName(parts[1]);
                   try { g.setTargetHours(Double.parseDouble(parts[2])); }
                   catch (NumberFormatException e) { g.setTargetHours(1); }
                   g.setCreatedDate(parts[3]);
                   g.setName(parts[1] + " Goal");
                   data.getGoals().add(g);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load data: " + e.getMessage());
        }
        return data;
    }

    public void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE))) {
            for (Subject s : appData.getSubjects()) {
                writer.println("SUBJECT|" + s.getName() + "|" + s.getWeeklyGoalHours() + "|" + s.getCreatedDate());
            }
            for (StudySession session : appData.getSessions()) {
                writer.println("SESSION|" + session.getSubject() + "|" + session.getDurationSeconds()
                    + "|" + session.getTimestamp() + "|" + session.getCreatedDate());
            }
            for (StudyGoal g : appData.getGoals()) {
            writer.println("GOAL|" + g.getSubjectName() + "|" + g.getTargetHours() + "|" + g.getCreatedDate());
}
        } catch (IOException e) {
            System.err.println("Failed to save data: " + e.getMessage());
        }
    }
}
