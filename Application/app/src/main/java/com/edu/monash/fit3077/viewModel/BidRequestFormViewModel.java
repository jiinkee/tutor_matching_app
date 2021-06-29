package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.model.CloseBidRequest;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.model.OpenBidRequest;
import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.model.Subject;
import com.edu.monash.fit3077.service.MyResponse;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Subclass of FormViewModel responsible for bid request form
 */
public class BidRequestFormViewModel extends FormViewModel {

    // mediator live data for create bid request response
    private MediatorLiveData<MyResponse<String>> createBidRequestResponse;

    /**
     * Constructor
     */
    public BidRequestFormViewModel() {
        super();
        createBidRequestResponse = new MediatorLiveData<>();
    }

    // GETTER for bid request based on selected bid form options
    private BidRequest getCreatedBidRequest() throws Exception{
        Student initiator = (Student) super.getLoggedInUser();
        LessonInformation selectedLessonInformation = getPreferredLessonInformation();

        if (preferredBidRequestType == null) {
            throw new Exception("All fields in this form must be filled up correctly.");
        }

        // create bid request based on selected bid type and other form options
        BidRequest selectedBidRequestInfo = null;
        String preferredBidRequestTypeString = preferredBidRequestType.toString();
        if (preferredBidRequestTypeString.equals(BidRequestType.OPEN.toString())) {
            selectedBidRequestInfo = new OpenBidRequest(initiator, selectedLessonInformation);
        } else if (preferredBidRequestTypeString.equals(BidRequestType.CLOSE.toString())) {
            selectedBidRequestInfo = new CloseBidRequest(initiator, selectedLessonInformation);
        }
        return selectedBidRequestInfo;
    }

    /**
     * Create new bid request based on current bid form options recorded in view model
     * @return Response of create bid request API call
     */
    public LiveData<MyResponse<String>> createBidRequest() {
        try {
            BidRequest selectedBidRequest = getCreatedBidRequest();

            createBidRequestResponse.addSource(bidRepository.createNewBidRequest(selectedBidRequest), response -> {
                createBidRequestResponse.setValue(response);
            });
        } catch (Exception e) {
            e.printStackTrace();
            MutableLiveData<MyResponse<String>> errorResponse = new MutableLiveData<>();
            errorResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
            createBidRequestResponse.setValue(errorResponse.getValue());
        }

        return createBidRequestResponse;
    }

}
