package com.studytracker.model;

/**
 * Custom exception for invalid study tracker data.
 * Fulfills the custom exception requirement.
 */
public class InvalidStudyDataException extends Exception {

    public InvalidStudyDataException(String message) {
        super(message);
    }

    public InvalidStudyDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
