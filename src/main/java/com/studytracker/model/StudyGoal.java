package com.studytracker.model;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.text.DecimalFormat;



//Subclass 3: Represents a study goal for a subject.
//Extends TrackerItem and overrides calculateScore() for goal completion %.

public class StudyGoal extends TrackerItem {

    private String subjectName;
    private double targetHours;    // hours targeted
    private double completedHours; // hours actually studied

    public StudyGoal() {}

    public StudyGoal(String subjectName, double targetHours) {
        super(subjectName + " Goal", LocalDate.now().toString());
        this.subjectName = subjectName;
        this.targetHours = targetHours;
        this.completedHours = 0;
    }

    public String getSubjectName() { return subjectName; }





    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public double getTargetHours() { 
        return targetHours; 
    }
    public void setTargetHours(double targetHours) { 
        this.targetHours = targetHours; 
    }

    public double getCompletedHours() {
         return completedHours; 
    }


    public void setCompletedHours(double completedHours) { 
        this.completedHours = completedHours; 
    }

    ///Method overriding: WE WILL USE THIS for calculating goal completion percentage (0–100).   (PAM)
    
    @Override
    public double calculateScore() {
        if (targetHours <= 0)
             return 0;
        
        double percent = (completedHours / targetHours) * 100;


        return Math.min(100, Math.round(percent * 10.0) / 10.0);
    }

    @Override
    public String getSummary() {

        DecimalFormat df = new DecimalFormat("0.00");
        
        return describe("Goal") + " | " + df.format(completedHours) + "h / " + targetHours + "h (" + calculateScore() + "%)";
    }
}