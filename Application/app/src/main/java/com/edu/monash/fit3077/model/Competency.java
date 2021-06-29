package com.edu.monash.fit3077.model;

import java.io.Serializable;

public class Competency implements Serializable {
    private String id;
    private Subject subject;
    private int level;

    public Competency(String id, Subject subject, int level) {
        this.id = id;
        this.subject = subject;
        this.level = level;
    }

    // GETTER method
    public String getId() {
        return id;
    }

    public Subject getSubject() {
        return subject;
    }

    public int getLevel() {
        return level;
    }

    public String getLevelString() {
        return Integer.toString(level);
    }

    // SETTER method
    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    // Method that returns the string representation of a competency
    @Override
    public String toString() {
        return getSubject().toString();
    }
}
