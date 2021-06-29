package com.edu.monash.fit3077.model;

import java.util.ArrayList;

public class Tutor extends User{
    private ArrayList<Qualification> qualifications;
    private ArrayList<String> subscribedBidsId;

    // constructor for tutor complete profile data retrieval
    public Tutor(String uId, String uName, String gName, String fName, ArrayList<Competency> competencies, ArrayList<Qualification> qualifications,
                 ArrayList<String> subscribedBidsId) {
        super(uId, uName, gName, fName, competencies);
        this.qualifications = qualifications;

        if (subscribedBidsId == null) {
            this.subscribedBidsId = new ArrayList<>();
        } else {
            this.subscribedBidsId = subscribedBidsId;
        }
    }

    // constructor for tutor basic profile data retrieval
    public Tutor(String uId, String uName, String gName, String fName) {
        super(uId, uName, gName, fName);
    }

    // GETTER method
    @Override
    public String getRoleString() {
        return "TUTOR";
    }

    public ArrayList<Qualification> getQualifications() {
        return qualifications;
    }

    // this method combines all the user's qualifications into one string
    public String getQualificationsString() {
        if (qualifications!=null) {
            StringBuilder strRepresentation = new StringBuilder();
            for (Qualification qualification : qualifications) {
                strRepresentation.append(qualification.toString()).append("\n");
            }
            return strRepresentation.toString();
        }
        return "";
    }

    public ArrayList<String> getSubscribedBidsId () {
        return this.subscribedBidsId;
    }

    // SETTER method
    @Override
    protected void setRolePermissions() {
        rolePermissions.add(UserRole.BIDDER);
        rolePermissions.add(UserRole.CONTRACT_SECOND_PARTY);
        rolePermissions.add(UserRole.CHAT_PARTICIPANT);
    }

    public void setQualifications(ArrayList<Qualification> userQualification) {
        qualifications = userQualification;
    }

    public void addNewBidSubscription(String bidRequestId) {
        subscribedBidsId.add(bidRequestId);
    }

    public void removeBidSubscription(String bidRequestId) {
        subscribedBidsId.remove(bidRequestId);
    }

}
