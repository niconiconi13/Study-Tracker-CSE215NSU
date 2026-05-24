package com.studytracker.model;

import java.time.LocalDate;


// Subclass 1 representing a study subject
// Extends TrackerItem and overrides calculateScore() for goal bonus calculation
public class Subject extends TrackerItem {

    private int weeklyGoalHours;  // target hours per week

    public Subject() {}

    public Subject(String name) {
        super(name, LocalDate.now().toString());
        this.weeklyGoalHours = 2; // default goal
    }

    public Subject(String name, int weeklyGoalHours) {
        super(name, LocalDate.now().toString());
        this.weeklyGoalHours = weeklyGoalHours;
    }

    public int getWeeklyGoalHours() { return weeklyGoalHours; }
    public void setWeeklyGoalHours(int weeklyGoalHours) { this.weeklyGoalHours = weeklyGoalHours; }

    
     // Method overriding: calculates required hours per day to meet weekly goal.
     
    @Override
    public double calculateScore() {
        return weeklyGoalHours / 7.0;
    }

    @Override
    public String getSummary() {
        return describe("Subject") + " | Goal: " + weeklyGoalHours + "h/week";
    }

    @Override
    public String toString() { return getName(); }
}
