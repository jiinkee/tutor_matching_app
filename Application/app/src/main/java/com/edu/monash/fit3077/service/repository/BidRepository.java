package com.edu.monash.fit3077.service.repository;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.monash.fit3077.model.BidRequest;
import com.edu.monash.fit3077.model.BidRequestStatus;
import com.edu.monash.fit3077.model.BidRequestType;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.model.UserRole;
import com.edu.monash.fit3077.service.BidAPIService;
import com.edu.monash.fit3077.service.Util;
import com.edu.monash.fit3077.service.converter.BidRequestConverter;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.WebServiceGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidRepository {
    private BidAPIService bidAPI;
    private BidRequestConverter bidRequestConverter;

    public BidRepository() {
        bidAPI = WebServiceGenerator.createService(BidAPIService.class);
        bidRequestConverter = new BidRequestConverter();
    }

    // get a list of bid requests based on the given bid status and type
    public LiveData<MyResponse<ArrayList<BidRequest>>> getBidRequests(BidRequestStatus bidStatus, @Nullable BidRequestType bidType) {
        final MutableLiveData<MyResponse<ArrayList<BidRequest>>> bidRequestsResponse = new MutableLiveData<>();

        Call<ResponseBody> bidRequestCall = bidAPI.getAllBidRequests();
        bidRequestCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // check if response is successful (HTTP code start with 2)
                if (response.isSuccessful()) {
                    try {
                        String responseBodyString = response.body().string();
                        ArrayList<BidRequest> filteredBidRequestsResponse = filterBidRequestData(responseBodyString, bidStatus, bidType);
                        // set mutable live data
                        bidRequestsResponse.setValue(MyResponse.successResponse(filteredBidRequestsResponse));

                    } catch (IOException e) {
                        bidRequestsResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        bidRequestsResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        bidRequestsResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                bidRequestsResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return bidRequestsResponse;
    }

    // filter the obtained bid requests list such that only the bid requests that can be viewed by the logged in user remain
    private ArrayList<BidRequest> filterBidRequestData(String bidRequestString, BidRequestStatus bidStatus, @Nullable BidRequestType bidType) {
        ArrayList<JsonObject> filteredBidRequests = new ArrayList<>();

        // transform response string to Json array
        JsonArray bidRequestsArray = new Gson().fromJson(bidRequestString, JsonArray.class);

        // filter based on bid request status
        if (bidStatus == BidRequestStatus.CLOSED_DOWN) {
            for (JsonElement bidRequestJsonElem : bidRequestsArray) {
                JsonObject bidRequest = bidRequestJsonElem.getAsJsonObject();
                // bid request is closed down when dateClosedDown is not null
                JsonElement dateClosedDown = bidRequest.get("dateClosedDown");
                if (!dateClosedDown.isJsonNull()) {
                    filteredBidRequests.add(bidRequest);
                }
            }
        } else {
            // filter based on bid request type
            for (JsonElement bidRequestJsonElem : bidRequestsArray) {
                JsonObject bidRequest = bidRequestJsonElem.getAsJsonObject();
                String bidRequestBidType = bidRequest.get("type").getAsString();
                BidRequestStatus status = (bidRequest.get("dateClosedDown").isJsonNull()) ? BidRequestStatus.ALIVE : BidRequestStatus.CLOSED_DOWN;
                if (status == bidStatus && BidRequestType.stringToBidType(bidRequestBidType) == bidType) {
                    filteredBidRequests.add(bidRequest);
                }
            }
        }

        // further filter by user role
        // A bid initiator can only see his/her own bid requests but a bidder can see all bid requests
        User loggedInUser = UserRepository.getInstance().getLoggedInUser();
        if (loggedInUser.getRolePermissions().contains(UserRole.BID_INITIATOR)) {
            filteredBidRequests.removeIf(bid -> (!bid.getAsJsonObject("initiator").get("id").getAsString().equals(loggedInUser.getId())));
        }

        // convert a list of Json Object (Json String) to Bid Request objects
        return bidRequestConverter.fromJsonStringToObjects(filteredBidRequests.toString());
    }

    // get a list of bid requests that were subscribed by the logged in user
    public LiveData<MyResponse<ArrayList<BidRequest>>> getSubscribedBidRequests(ArrayList<String> subscribedBidsId) {
        final MutableLiveData<MyResponse<ArrayList<BidRequest>>> subscribedBidsResponse = new MutableLiveData<>();

        // first get a list of all bid requests
        Call<ResponseBody> bidRequestCall = bidAPI.getAllBidRequests();
        bidRequestCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // check if response is successful (HTTP code start with 2)
                if (response.isSuccessful()) {
                    try {
                        String allBidRequestsString = response.body().string();
                        JsonArray allBidRequestsArray = new Gson().fromJson(allBidRequestsString, JsonArray.class);
                        ArrayList<BidRequest> subscribedBids = new ArrayList<>();

                        if (allBidRequestsArray.size() > 0) {
                            for (JsonElement bidElem: allBidRequestsArray) {
                                JsonObject bidObj = bidElem.getAsJsonObject();

                                // filter response, retain only those bid requests which have matching ID as the subscribed bids
                                if (subscribedBidsId.contains(bidObj.get("id").getAsString())) {
                                    // further remove subscribed bid that have already been closed down
                                    if (bidObj.get("dateClosedDown").isJsonNull()) {
                                        BidRequest bidRequest = bidRequestConverter.fromJsonObjectToObject(bidObj);
                                        subscribedBids.add(bidRequest);
                                    }
                                }
                            }
                        }
                        // set mutable live data
                        subscribedBidsResponse.setValue(MyResponse.successResponse(subscribedBids));

                    } catch (IOException e) {
                        subscribedBidsResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        subscribedBidsResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        subscribedBidsResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                subscribedBidsResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return subscribedBidsResponse;
    }


    // get a specific bid request based on the given ID
    public LiveData<MyResponse<BidRequest>> getBidRequest(String bidRequestId) {
        final MutableLiveData<MyResponse<BidRequest>> bidRequestResponse = new MutableLiveData<>();

        Call<ResponseBody> bidRequestCall = bidAPI.getBidRequest(bidRequestId);

        bidRequestCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                // check if response is successful (HTTP code start with 2)
                if (response.isSuccessful()) {
                    try {
                        String responseBodyString = response.body().string();
                        BidRequest bidRequest = bidRequestConverter.fromJsonStringToObject(responseBodyString);
                        // set mutable live data
                        bidRequestResponse.setValue(MyResponse.successResponse(bidRequest));

                    } catch (IOException e) {
                        bidRequestResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        bidRequestResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        bidRequestResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                bidRequestResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });
        return bidRequestResponse;
    }

    // create a new bid request
    public LiveData<MyResponse<String>> createNewBidRequest(BidRequest bidRequest) {
        final MutableLiveData<MyResponse<String>> createBidRequestResponse = new MutableLiveData<>();

        String bidRequestString = (String) bidRequestConverter.fromObjectToJsonString(bidRequest);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), bidRequestString);

        Call<ResponseBody> bidRequestCall = bidAPI.createNewBidRequest(body);
        bidRequestCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBodyString = response.body().string();
                        createBidRequestResponse.setValue(MyResponse.successResponse(responseBodyString));
                    } catch (IOException e) {
                        createBidRequestResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        createBidRequestResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        createBidRequestResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                createBidRequestResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return createBidRequestResponse;
    }

    // update an existing bid request
    public LiveData<MyResponse<BidRequest>> updateBidRequest(BidRequest bidRequest) {
        final MutableLiveData<MyResponse<BidRequest>> updateBidRequestResponse = new MutableLiveData<>();

        // prepare request body (additional info)
        JsonObject additionalInfoObject =  BidRequestConverter.fromAdditionalInfoToJsonObject(bidRequest);
        JsonObject bodyObject = new JsonObject();
        bodyObject.add("additionalInfo", additionalInfoObject);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), bodyObject.toString());

        Call<ResponseBody> updateBidCall = bidAPI.updateBidRequest(bidRequest.getId(), body);
        updateBidCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    updateBidRequestResponse.setValue(MyResponse.successResponse(bidRequest));
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        updateBidRequestResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        updateBidRequestResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                updateBidRequestResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return updateBidRequestResponse;
    }

    // close down a bid request
    public LiveData<MyResponse<BidRequest>> closeDownBidRequest(BidRequest bidRequest, Instant closeDownDateTime) {
        final MutableLiveData<MyResponse<BidRequest>> closedDownBidResponse = new MutableLiveData<>();

        // prepare request body
        JsonObject dateSigned = new JsonObject();
        dateSigned.addProperty("dateClosedDown", closeDownDateTime.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), dateSigned.toString());

        Call<ResponseBody> closeDownBidCall = bidAPI.closeDownBidRequest(bidRequest.getId(), body);
        closeDownBidCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    closedDownBidResponse.setValue(MyResponse.successResponse(bidRequest));
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        closedDownBidResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        closedDownBidResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                closedDownBidResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return closedDownBidResponse;
    }

}
