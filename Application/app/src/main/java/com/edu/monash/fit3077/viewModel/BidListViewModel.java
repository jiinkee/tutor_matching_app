package com.edu.monash.fit3077.viewModel;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestStatus;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.model.Tutor;
import com.edu.monash.fit3077.service.MyResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * ViewModel that handles the retrieval of a list of bid requests.
 */
public class BidListViewModel extends BidViewModel {

    /**
     * observable live data for bid requests and bid offer retrieval
     */
    private MediatorLiveData<MyResponse<ArrayList<BidRequest>>> bidRequests;


    public BidListViewModel() {
        super();
        bidRequests = new MediatorLiveData<>();
    }


    /**
     * FUNCTIONS THAT RETRIEVE A LIST OF BID REQUESTS
     */
    private void getBidRequestsData(BidRequestStatus status, @Nullable BidRequestType type) {
        bidRequests.addSource(bidRepository.getBidRequests(status, type), new Observer<MyResponse<ArrayList<BidRequest>>>() {
            @Override
            public void onChanged(MyResponse<ArrayList<BidRequest>> response) {
                switch (response.status) {
                    case ERROR:
                        bidRequests.setValue(response);
                        break;
                    case SUCCESS:
                        ArrayList<BidRequest> retrievedBidRequests = response.data;
                        ArrayList<BidRequest> aliveBidRequests = closeDownExpiredBids(retrievedBidRequests);
                        ArrayList<BidRequest> sortedAliveBidRequests = sortBidRequestsData(aliveBidRequests);
                        bidRequests.setValue(MyResponse.successResponse(sortedAliveBidRequests));
                        break;
                }
            }
        });
    }

    public void getOpenBidRequests() {
        this.getBidRequestsData(BidRequestStatus.ALIVE, BidRequestType.OPEN);
    }

    public void getCloseBidRequests() {
        this.getBidRequestsData(BidRequestStatus.ALIVE, BidRequestType.CLOSE);
    }

    public void getClosedDownBidRequests() {
        this.getBidRequestsData(BidRequestStatus.CLOSED_DOWN, null);
    }

    public void getSubscribedOpenBidRequests() {
        Tutor loggedInTutor = (Tutor) super.getLoggedInUser();

        if (!loggedInTutor.getSubscribedBidsId().isEmpty() && !(loggedInTutor.getSubscribedBidsId() == null)) {
            bidRequests.addSource(bidRepository.getSubscribedBidRequests(loggedInTutor.getSubscribedBidsId()), response -> {
                switch(response.status) {
                    case ERROR:
                        bidRequests.setValue(response);
                        break;
                    case SUCCESS:
                        ArrayList<BidRequest> allSubscribedBids = response.data;
                        // close down any expired open bids & filter them out of the subscribed bid list
                        ArrayList<BidRequest> aliveSubscribedBids = closeDownExpiredBids(allSubscribedBids);
                        ArrayList<BidRequest> sortedAliveSubscribedBids = sortBidRequestsData(aliveSubscribedBids);
                        bidRequests.setValue(MyResponse.successResponse(sortedAliveSubscribedBids));
                        break;
                }
            });
        } else {
            bidRequests.setValue(MyResponse.successResponse(new ArrayList<>()));
        }
    }

    public LiveData<MyResponse<ArrayList<BidRequest>>> getBidRequestsResponse() {
        return bidRequests;
    }

    /**
     * AUXILIARY METHODS
     */
    // identify all bids that have expired from the retrieved bid requests data and close down each of the expired bid
    private ArrayList<BidRequest> closeDownExpiredBids(ArrayList<BidRequest> retrievedBidRequests) {
        ArrayList<BidRequest> expiredBidRequests = new ArrayList<>();

        // need to check for those bids that have already expired, but are still stored as ALIVE in ur system
        // this situation is normal because we did not update the bid status when app is offline
        for (BidRequest bidRequest : retrievedBidRequests) {
            if (!bidRequest.isStillAlive() && bidRequest.getStatus() == BidRequestStatus.ALIVE) {
                expiredBidRequests.add(bidRequest);
            }
        }
        // remove from retrievedBidRequests separately to prevent ConcurrentModificationException
        retrievedBidRequests.removeIf(bid -> (expiredBidRequests.contains(bid)));

        // for each expired bid requests, system will automatically close down them depending on the type and its bid offer
        for (BidRequest expiredBid : expiredBidRequests) {
            systemAutoCloseDownBidRequest(expiredBid);
        }

        // return the remaining alive bids
        return retrievedBidRequests;
    }

    // Sort the retrieved bid requests data using the bid creation date in descending order
    private ArrayList<BidRequest> sortBidRequestsData(ArrayList<BidRequest> bidRequests) {
        if (!bidRequests.isEmpty()) {
            Collections.sort(bidRequests, new Comparator<BidRequest>() {
                @Override
                public int compare(BidRequest bid1, BidRequest bid2) {
                    return bid2.getCreationDate().compareTo(bid1.getCreationDate());
                }
            });
        }

        return bidRequests;
    }



}
