package com.edu.monash.fit3077.service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatMessageAPIService {
    @GET("bid/{bidId}?fields=messages")
    Call<ResponseBody> getChatMessages(@Path("bidId") String bidId);

    @POST("message")
    Call<ResponseBody> sendChatMessage(@Body RequestBody message);

}
