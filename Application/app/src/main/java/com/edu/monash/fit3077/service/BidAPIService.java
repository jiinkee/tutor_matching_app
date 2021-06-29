package com.edu.monash.fit3077.service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BidAPIService {
    @GET("bid")
    Call<ResponseBody> getAllBidRequests();

    @GET("bid/{bidId}")
    Call<ResponseBody> getBidRequest(@Path("bidId") String bidId);

    @POST("bid")
    Call<ResponseBody> createNewBidRequest(@Body RequestBody body);

    @PATCH("bid/{bidId}")
    Call<ResponseBody> updateBidRequest(@Path("bidId") String bidId, @Body RequestBody body);

    @POST("bid/{bidId}/close-down")
    Call<ResponseBody> closeDownBidRequest(@Path("bidId") String bidId, @Body RequestBody body);
}
