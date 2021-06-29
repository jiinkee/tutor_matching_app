package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.model.Tutor;
import com.edu.monash.fit3077.service.MyResponse;

/**
 * Subclass of FormViewModel responsible for bid offer form
 */
public class BidOfferFormViewModel extends FormViewModel {

    // Mutable live data storing create bid offer response and edit bid offer response
    private MediatorLiveData<MyResponse<String>> createBidOfferResponse;
    private MediatorLiveData<MyResponse<String>> editBidOfferResponse;
    // store the selected bid request to make an offer to
    private BidRequest predefinedBidRequest;

    /**
     * Constructor
     */
    public BidOfferFormViewModel() {
        super();
        createBidOfferResponse = new MediatorLiveData<>();
        editBidOfferResponse = new MediatorLiveData<>();
    }

    /**
     * Preset bid form options based on information in bid request
     * @param bidRequest bid request to set its information to bid form option
     */
    public void setPredefinedBidRequest(BidRequest bidRequest) {
        predefinedBidRequest = bidRequest;
        // set the non-editable part of bid offer form based on selected bid request information
        preferredBidRequestType = bidRequest.getType();
        preferredLessonInformation.setSubject(bidRequest.getSubject());
        preferredLessonInformation.setPreferredTutorCompetencyLevel(bidRequest.getPreferredTutorCompetencyLevel());
    }

    /**
     * Preset bid form options based on information in bid offer
     * @param bidOffer bid offer to set its information to bid form option
     */
    public void setPredefinedBidOffer(BidOffer bidOffer) {
        // populate selected bid offer information for edit bid offer form
        preferredLessonInformation = bidOffer.getLessonInfo();
        preferredLessonDuration = bidOffer.getLessonInfo().getDuration();
    }

    // get bid offer based on latest bid form options
    private BidOffer getCreatedBidOffer() throws Exception{
        Tutor bidder = (Tutor) super.getLoggedInUser();
        LessonInformation selectedBidOfferLessonInfo = getPreferredLessonInformation();

        if (bidder == null || selectedBidOfferLessonInfo == null) {
            throw new Exception("All fields in this form must be filled up correctly.");
        }

        BidOffer bidOffer = new BidOffer(bidder, selectedBidOfferLessonInfo);
        return bidOffer;
    }

    // get bid request with updated bid offer
    private BidRequest getBidRequestWithUpdatedBidOffer() throws Exception{
        if (predefinedBidRequest == null) {
            throw new Exception("Update bid offer failed.");
        }
        BidOffer updatedBidOffer = getCreatedBidOffer();
        predefinedBidRequest.updateBidOffer(updatedBidOffer);
        return predefinedBidRequest;
    }

    // get bid request with newly added bid offer
    private BidRequest getBidRequestWithCreatedBidOffer() throws Exception{
        if (predefinedBidRequest == null) {
            throw new Exception("Update bid offer failed.");
        }
        BidOffer createdBidOffer = getCreatedBidOffer();
        predefinedBidRequest.addBidOffer(createdBidOffer);
        return predefinedBidRequest;
    }

    /**
     * Create new bid offer based on current bid form options recorded in view model
     * @return Response of create bid offer API call
     */
    public LiveData<MyResponse<String>> createBidOffer() {
        try {
            BidRequest updatedBidRequest = getBidRequestWithCreatedBidOffer();

            createBidOfferResponse.addSource(bidRepository.updateBidRequest(updatedBidRequest), response -> {
                if (response.status == MyResponse.ResponseStatus.SUCCESS) {
                    String responseString = response.data.toString();
                    createBidOfferResponse.setValue(MyResponse.successResponse(responseString));
                } else {
                    createBidOfferResponse.setValue(MyResponse.errorResponse(response.errorMsg, null));
                }
            });
        } catch (Exception e) {
            MutableLiveData<MyResponse<String>> errorResponse = new MutableLiveData<>();
            errorResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
            createBidOfferResponse.setValue(errorResponse.getValue());
        }

        return createBidOfferResponse;
    }

    /**
     * Edit bid offer based on current bid form options recorded in view model
     * @return Response of edit bid offer API call
     */
    public LiveData<MyResponse<String>> editBidOffer() {
        try {
            BidRequest updatedBidRequest = getBidRequestWithUpdatedBidOffer();

            editBidOfferResponse.addSource(bidRepository.updateBidRequest(updatedBidRequest), response -> {
                if (response.status == MyResponse.ResponseStatus.SUCCESS) {
                    String responseString = response.data.toString();
                    editBidOfferResponse.setValue(MyResponse.successResponse(responseString));
                } else {
                    editBidOfferResponse.setValue(MyResponse.errorResponse(response.errorMsg, null));
                }
            });
        } catch (Exception e) {
            MutableLiveData<MyResponse<String>> errorResponse = new MutableLiveData<>();
            errorResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
            editBidOfferResponse.setValue(errorResponse.getValue());
        }

        return editBidOfferResponse;
    }
}
