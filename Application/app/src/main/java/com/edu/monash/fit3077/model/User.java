package com.edu.monash.fit3077.model;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class User implements Serializable {
    protected String id, userName, givenName, familyName;
    protected ArrayList<Competency> competencies;
    protected ArrayList<UserRole> rolePermissions = new ArrayList<>();

    // constructor for user complete profile data retrieval
    public User(String uId, String uName, String gName, String fName, ArrayList<Competency> competencies) {
        this.id = uId;
        this.userName = uName;
        this.givenName = gName;
        this.familyName = fName;
        this.competencies = competencies;
        setRolePermissions();
    }

    // constructor for user basic profile data retrieval
    public User(String uId, String uName, String gName, String fName) {
        this.id = uId;
        this.userName = uName;
        this.givenName = gName;
        this.familyName = fName;
        setRolePermissions();
    }

    // GETTER methods
    public String getId(){
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getFullName() {
        return givenName + ' ' + familyName;
    }

    public ArrayList<Competency> getCompetencies() {
        return competencies;
    }

    // this method retrieves the user's competency level for a specific subject
    public Competency getCompetencyForSubject(String subjectId) {
        Competency matchedCompetency = null;
        if (competencies != null) {
            for (Competency competency: competencies) {
                if (competency.getSubject().getId().equals(subjectId)) {
                    matchedCompetency = competency;
                }
            }
        }
        return matchedCompetency;
    }

    // subjects are included in competencies, hence we obtain user's subjects from his/her competencies
    public ArrayList<Subject> getSubjects() {
        ArrayList<Subject> subjects = null;
        if (competencies!=null) {
            subjects = new ArrayList<>();
            for (Competency competency: competencies) {
                subjects.add(competency.getSubject());
            }
        }
        return subjects;
    }

    public ArrayList<UserRole> getRolePermissions() {
        return rolePermissions;
    }

    // SETTER methods
    public void setCompetencies(ArrayList<Competency> userCompetencies) {
        competencies = userCompetencies;
    }

    protected abstract void setRolePermissions();

    public abstract String getRoleString();
}
