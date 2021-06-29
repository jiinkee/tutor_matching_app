package com.edu.monash.fit3077.model;

import java.io.Serializable;

public class Qualification implements Serializable {
    private String id;
    private String title;
    private String description;
    private boolean isVerified;

    public Qualification(String id, String title, String description, boolean isVerified) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isVerified = isVerified;
    }

    // GETTER methods
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isVerified() {
        return isVerified;
    }

    // String representation of qualification
    @Override
    public String toString() {
        return title;
    }
}
