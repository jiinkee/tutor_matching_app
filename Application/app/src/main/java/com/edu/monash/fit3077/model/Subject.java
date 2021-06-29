package com.edu.monash.fit3077.model;

import java.io.Serializable;

public class Subject implements Serializable {
    private String id;
    private String name; // subject name
    private String description; // subject topic

    public Subject(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // GETTER methods
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // get the string representation of Subject
    @Override
    public String toString() {
        return name + ": " + description;
    }
}
