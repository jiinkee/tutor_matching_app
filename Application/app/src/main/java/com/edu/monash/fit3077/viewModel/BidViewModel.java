package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.edu.monash.fit3077.model.BidOffer;
import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.model.Competency;
import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.PendingContract;
import com.edu.monash.fit3077.model.Subject;
import com.edu.monash.fit3077.model.Tutor;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.repository.BidRepository;
import com.edu.monash.fit3077.service.repository.ContractRepository;

import java.time.Instant;
import java.util.ArrayList;

/**
 * View Model class which contains all methods to close down a bid request.
 */
public abstract class BidViewModel extends BaseViewModel {
    protected BidRepository bidRepository;
    protected ContractRepository contractRepository;

    /**
     * observable live data for bid request close down and contract creation
     */
    private MediatorLiveData<MyResponse<String>> buyOutBid;
    private MediatorLiveData<MyResponse<String>> closeDownBidRequest;

    private String bidCloseDownInitiator;
    public final static String SYSTEM_CLOSE_DOWN = "bid closed down by system automatically";
    public final static String USER_CLOSE_DOWN = "bid closed down through user action";

    public BidViewModel() {
        bidRepository = new BidRepository();
        contractRepository = new ContractRepository();
        buyOutBid = new MediatorLiveData<>();
        closeDownBidRequest = new MediatorLiveData<>();
    }

    /**
     * Function that checks if the logged in user can make a bid offer to the selected bid request
     */
    public boolean checkCanMakeBidOffer(BidRequest selectedBidRequest) {
        Subject bidSubject = selectedBidRequest.getSubject();
        int preferredTutorCompetency = selectedBidRequest.getPreferredTutorCompetencyLevel();

        Competency tutorCompetency = super.getLoggedInUser().getCompetencyForSubject(bidSubject.getId());

        boolean canMakeBidOffer = false;
        if (tutorCompetency != null) {
            if (tutorCompetency.getLevel() >= (preferredTutorCompetency + 2)) {
                canMakeBidOffer = true;
            }
        }
        return canMakeBidOffer;
    }

    /**
     * FUNCTION FOR BIDDER TO BUY OUT A BID REQUEST
     * It creates a new bid offer based on the bid request, set this bid offer as the winner and close down the bid with contract creation
     */
    public LiveData<MyResponse<String>> buyOutBidRequest(BidRequest bidRequest) {
        // create new bid offer where the bid offer details fully matches with the bid request
        Tutor loggedInUser = (Tutor) super.getLoggedInUser();
        BidOffer tutorBidOffer = new BidOffer(loggedInUser, bidRequest.getRequiredLessonDetails());
        bidRequest.addBidOffer(tutorBidOffer);

        buyOutBid.addSource(bidRepository.updateBidRequest(bidRequest), updateBid -> {
            switch (updateBid.status) {
                case SUCCESS:
                    userCloseDownBidRequest(bidRequest, tutorBidOffer);
                    break;
                case ERROR:
                    buyOutBid.setValue(MyResponse.errorResponse("Buy out bid fails.", null));
                    break;
            }
        });

        return buyOutBid;
    }

    /**
     * Function that will be called when the bid close down is due to user action
     */
    public void userCloseDownBidRequest(BidRequest bidRequest, BidOffer winnerBidOffer) {
        closeDownBidWithContractCreation(bidRequest, winnerBidOffer);
        bidCloseDownInitiator = USER_CLOSE_DOWN;
    }

    /**
     * Function that will be called when the bid is closed down due to time-out, i.e. system automatically
     * close down the bid
     */
    public void systemAutoCloseDownBidRequest(BidRequest bidRequest) {

        ArrayList<BidOffer> allBidOffers = bidRequest.getTutorBidOffers();
        if (bidRequest.getType() == BidRequestType.OPEN && allBidOffers != null && !allBidOffers.isEmpty()) {
            // if the bid is an open bid and has bid offer, system selects the last bid offer as the winner & close down the bid with contract creation
            BidOffer lastBidOffer = allBidOffers.get(allBidOffers.size() - 1);
            // if system auto closed down, the duration for lesson and contract are set as 6 months
            lastBidOffer.setLessonEndDate(6);
            // close down bid with contract creation
            closeDownBidWithContractCreation(bidRequest, lastBidOffer);

        } else {
            // otherwise, system will just close down the bid w/o any contract creation
            closeDownBidRequest.addSource(closeDownBid(bidRequest), closeDownBidResponse -> {
                switch (closeDownBidResponse.status) {
                    case ERROR:
                        closeDownBidRequest.setValue(MyResponse.errorResponse("ERROR: Bid close down fails", null));
                        break;
                    case SUCCESS:
                        closeDownBidRequest.setValue(MyResponse.successResponse("Bid close down successfully"));
                        break;
                }
            });
        }
        bidCloseDownInitiator = SYSTEM_CLOSE_DOWN;
    }

    /**
     * Observed by bid list fragment and bid details fragment to see if there is any bid request been closed down
     */
    public LiveData<MyResponse<String>> getCloseDownBidResponse() {
        return closeDownBidRequest;
    }

    public String getBidCloseDownInitiator()  { return this.bidCloseDownInitiator; }


    /**
     * COMMON FUNCTIONS USED IN BOTH BID CLOSE DOWN BY USER & BID CLOSE DOWN BY SYSTEM
     */
    private LiveData<MyResponse<String>> closeDownBidWithContractCreation(BidRequest bidRequest, BidOffer winnerBidOffer) {
        // 1. update the winner bid offer of the bid request
        closeDownBidRequest.addSource(setWinnerBidOffer(bidRequest, winnerBidOffer), updatedResponse -> {
            if (updatedResponse.status == MyResponse.ResponseStatus.SUCCESS) {
                BidRequest updatedBidRequest = updatedResponse.data;
                // 2. close down bid request
                closeDownBidRequest.addSource(closeDownBid(updatedBidRequest), closeDownResponse -> {
                    if (closeDownResponse.status == MyResponse.ResponseStatus.SUCCESS) {
                        // 3. create a new contract based on the bid request & winner bid offer
                        closeDownBidRequest.addSource(createNewContract(updatedBidRequest, winnerBidOffer), createContractResponse -> {
                            if (createContractResponse.status == MyResponse.ResponseStatus.SUCCESS) {
                                closeDownBidRequest.setValue(MyResponse.successResponse("Bid close down successfully!"));
                            } else {
                                closeDownBidRequest.setValue(MyResponse.errorResponse("ERROR: contract creation fails", null));
                            }
                        });
                    } else {
                        closeDownBidRequest.setValue(MyResponse.errorResponse("ERROR: Bid close down fails", null));
                    }
                });
            } else {
                closeDownBidRequest.setValue(MyResponse.errorResponse("ERROR: Bid winner update fails", null));
            }
        });

        return closeDownBidRequest;
    }

    private LiveData<MyResponse<BidRequest>> setWinnerBidOffer(BidRequest bidRequest, BidOffer winnerBidOffer) {
        bidRequest.setWinnerBidOffer(winnerBidOffer);
        return bidRepository.updateBidRequest(bidRequest);
    }

    private LiveData<MyResponse<BidRequest>> closeDownBid(BidRequest bidRequest) {
        return bidRepository.closeDownBidRequest(bidRequest, Instant.now());
    }

    private LiveData<MyResponse<Contract>> createNewContract(BidRequest bidRequest, BidOffer winnerBidOffer) {
        PendingContract newContract = new PendingContract(bidRequest.getInitiator(), winnerBidOffer.getBidder(), winnerBidOffer.getLessonInfo());

        return contractRepository.createContract(newContract);
    }

}
