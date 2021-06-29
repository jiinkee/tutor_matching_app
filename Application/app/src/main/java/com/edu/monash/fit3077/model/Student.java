package com.edu.monash.fit3077.model;

import java.util.ArrayList;

public class Student extends User{

    // constructor for student complete profile data retrieval
    public Student(String uId, String uName, String gName, String fName, ArrayList<Competency> competencies) {
        super(uId, uName, gName, fName,competencies);
    }

    // constructor for student basic profile data retrieval
    public Student(String uId, String uName, String gName, String fName) {
        super(uId, uName, gName, fName);
    }

    // GETTER method
    @Override
    public String getRoleString() {
        return "STUDENT";
    }

    // SETTER method
    @Override
    protected void setRolePermissions() {
        rolePermissions.add(UserRole.BID_INITIATOR);
        rolePermissions.add(UserRole.CONTRACT_FIRST_PARTY);
        rolePermissions.add(UserRole.CHAT_PARTICIPANT);
    }

}
