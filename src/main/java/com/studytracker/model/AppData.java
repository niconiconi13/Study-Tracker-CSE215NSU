package com.studytracker.model;

import java.util.ArrayList;
import java.util.List;

public class AppData {
    private List<Subject> subjects = new ArrayList<>();
    private List<StudySession> sessions = new ArrayList<>();
    private List<StudyGoal> goals = new ArrayList<>();

    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { this.subjects = subjects; }

    public List<StudySession> getSessions() { return sessions; }
    public void setSessions(List<StudySession> sessions) { this.sessions = sessions; }

    public List<StudyGoal> getGoals() { return goals; }
    public void setGoals(List<StudyGoal> goals) { this.goals = goals; }
}