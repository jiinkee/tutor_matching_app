package com.edu.monash.fit3077.model;

public enum BidRequestType {
    OPEN,
    CLOSE;

    public static BidRequestType stringToBidType(String typeString) {
        BidRequestType bidType = null;
        switch (typeString) {
            case "open":
                bidType = OPEN;
                break;
            case "close":
                bidType = CLOSE;
                break;
        }
        return bidType;
    }

    public static String bidTypeToString(BidRequestType bidType) {
        String typeString = "";
        switch (bidType) {
            case OPEN:
                typeString = "open";
                break;
            case CLOSE:
                typeString = "close";
                break;
        }
        return typeString;
    }
}
