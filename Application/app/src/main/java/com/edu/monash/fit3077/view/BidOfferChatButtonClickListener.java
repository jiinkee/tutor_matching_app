package com.edu.monash.fit3077.view;

import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.model.Tutor;

public interface BidOfferChatButtonClickListener {
    void onChatButtonClicked (String bidRequestId, Student student, Tutor tutor);
}
