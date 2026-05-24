package com.studytracker.model;



// This is the Abstract Superclass for all trackable items
//Fulfills the superclass and encapsulation requirement
public abstract class TrackerItem implements Trackable {

   
    private String name;
    private String createdDate;

    public TrackerItem() {}

    public TrackerItem(String name, String createdDate) {
        this.name = name;
        this.createdDate = createdDate;
    }

    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    // Method overloading: two versions of describe()
    public String describe() {
        return "Item: " + name;
    }

    public String describe(String prefix) {
        return prefix + ": " + name;
    }

    // Abstract method to be overridden by subclasses
    @Override
    public abstract String getSummary();

    @Override
    public abstract double calculateScore();
}
