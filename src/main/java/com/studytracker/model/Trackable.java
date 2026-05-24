package com.studytracker.model;



// Shanto : Keep two methods in the interface that defines trackable items in the study tracker.
//Fulfilling the Abstraction (interface) requirement
public interface Trackable {
    String getSummary();
    double calculateScore();  // used for mathematical calculations (bonus/score)
}
