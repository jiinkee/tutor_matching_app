package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestStatus;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.model.Tutor;
import com.edu.monash.fit3077.model.UserRole;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.repository.UserRepository;

import java.util.ArrayList;

/**
 * View Model class which handles the retrieval of a particular bid request: all its details & associated bid offers, as well as handling bid subscription
 */
public class BidDetailsViewModel extends BidViewModel {
    private UserRepository userRepository;

    private MediatorLiveData<BidRequest> selectedBidRequest; // observed by fragments to obtain the latest selected bid request data
    private MutableLiveData<ArrayList<BidOffer>> displayBidOffers; // this live data stores a list of bid offers that can be viewed by the logged in user
    private MutableLiveData<BidOffer> loggedInUserBidOffer; // this live data stores the bid offer made by the logged in bidder(tutor)

    private MediatorLiveData<MyResponse<String>> bidSubscription;

    public BidDetailsViewModel() {
        super();
        userRepository = UserRepository.getInstance();
        selectedBidRequest = new MediatorLiveData<>();
        displayBidOffers = new MutableLiveData<>();
        loggedInUserBidOffer = new MutableLiveData<>();
        bidSubscription = new MediatorLiveData<>();
    }

    /**
     * FUNCTIONS THAT RETRIEVE A SPECIFIC BID REQUEST & ITS BID OFFERS THAT CAN BE DISPLAYED TO THE LOGGED IN USER
     */
    public void getBidRequestDetails(String bidRequestId) {
        selectedBidRequest.addSource(bidRepository.getBidRequest(bidRequestId), bidRequestResponse -> {
            if (bidRequestResponse.status == MyResponse.ResponseStatus.SUCCESS) {
                selectedBidRequest.setValue(bidRequestResponse.data);
                determineBidOfferToBeDisplayed(bidRequestResponse.data);
                if (super.getLoggedInUser().getRolePermissions().contains(UserRole.BIDDER)) {
                    determineLoggedInBidderBidOffer(bidRequestResponse.data);
                } else {
                    loggedInUserBidOffer.setValue(null);
                }
            }
        });
    }

    private void determineBidOfferToBeDisplayed(BidRequest bidRequest) {
        // decide the bid offers to be displayed depending on the status of selected bid request
        ArrayList<BidOffer> bidOffersToBeDisplayed = new ArrayList<>();
        if (bidRequest.getStatus() == BidRequestStatus.CLOSED_DOWN) {
            // show only the winner bid when the bid request has already been closed down
            // closed down bid will always have a winner bid, this is just to keep fail safe
            if (bidRequest.getWinnerBidOffer() != null) {
                bidOffersToBeDisplayed.add(bidRequest.getWinnerBidOffer());
            }
        } else if (super.getLoggedInUser().getRolePermissions().contains(UserRole.BIDDER) && bidRequest.getType() == BidRequestType.CLOSE) {
            // if user is bidder & bid request is CLOSE type, show the bidder's bid offer only
            for (BidOffer bidOffer : bidRequest.getTutorBidOffers()) {
                if (bidOffer.getBidder().getId().equals(super.getLoggedInUser().getId())) {
                    bidOffersToBeDisplayed.add(bidOffer);
                }
            }
        } else {
            // in other cases, show all bid offers
            bidOffersToBeDisplayed = bidRequest.getTutorBidOffers();
        }
        displayBidOffers.setValue(bidOffersToBeDisplayed);
    }

    public void determineLoggedInBidderBidOffer(BidRequest bidRequest) {
        String loggedInBidderId = super.getLoggedInUser().getId();

        boolean hasMatchBidOffer = false;
        for (BidOffer offer : bidRequest.getTutorBidOffers()) {
            if (offer.getBidder().getId().equals(loggedInBidderId)) {
                loggedInUserBidOffer.setValue(offer);
                hasMatchBidOffer = true;
            }
        }
        if (!hasMatchBidOffer) {
            loggedInUserBidOffer.setValue(null);
        }
    }

    public LiveData<BidRequest> getSelectedBidRequest() {
        return selectedBidRequest;
    }

    public LiveData<ArrayList<BidOffer>> getDisplayBidOffers() {
        return displayBidOffers;
    }

    public LiveData<BidOffer> getLoggedInUserBidOffer() {
        return loggedInUserBidOffer;
    }

    /**
     * FUNCTIONS THAT ALLOW A TUTOR TO SUBSCRIBE & MONITOR AN OPEN BID REQUEST
     */
    // a method that notifies view whether the logged in tutor has subscribed to a certain bid
    public boolean hasSubscribedToBid(String bidRequestId) {
        Tutor loggedInTutor = (Tutor) super.getLoggedInUser();
        return loggedInTutor.getSubscribedBidsId().contains(bidRequestId);
    }

    public LiveData<MyResponse<String>> subscribeToBidRequest(String bidRequestId) {
        Tutor loggedInTutor = (Tutor) super.getLoggedInUser();
        // add a new bid request subscription to the logged in tutor
        loggedInTutor.addNewBidSubscription(bidRequestId);
        // make HTTP request to update in remote database too
        bidSubscription.addSource(userRepository.updateUser(), subscription -> {
            bidSubscription.setValue(subscription);
        });

        return bidSubscription;
    }

    public LiveData<MyResponse<String>> unsubscribeBidRequest(String bidRequestId) {
        Tutor loggedInTutor = (Tutor) super.getLoggedInUser();

        if (loggedInTutor.getSubscribedBidsId().contains(bidRequestId)) {
            loggedInTutor.removeBidSubscription(bidRequestId);
            bidSubscription.addSource(userRepository.updateUser(), unsubscribe -> {
                bidSubscription.setValue(unsubscribe);
            });
        } else {
            bidSubscription.setValue(MyResponse.errorResponse("You cannot unsubscribe a bid you never subscribed to", null));
        }

        return bidSubscription;
    }

}
