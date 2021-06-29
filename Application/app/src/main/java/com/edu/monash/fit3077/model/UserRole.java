package com.edu.monash.fit3077.model;

import java.io.Serializable;

public enum UserRole implements Serializable {
    BID_INITIATOR,
    BIDDER,
    CHAT_PARTICIPANT,
    CONTRACT_FIRST_PARTY,
    CONTRACT_SECOND_PARTY
}
