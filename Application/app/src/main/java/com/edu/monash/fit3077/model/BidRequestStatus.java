package com.edu.monash.fit3077.model;

public enum BidRequestStatus {
    ALIVE,
    CLOSED_DOWN;

    public static String statusToString(BidRequestStatus status) {
        String statusString = "";
        switch (status) {
            case ALIVE:
                statusString = "ALIVE";
                break;
            case CLOSED_DOWN:
                statusString = "CLOSED DOWN";
                break;
        }
        return statusString;
    }
}
