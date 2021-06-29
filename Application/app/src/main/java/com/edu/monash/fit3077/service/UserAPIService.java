package com.edu.monash.fit3077.service;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserAPIService {
    @FormUrlEncoded
    @POST("user/login?jwt=true")
    Call<ResponseBody> login(@Field("userName") String userName, @Field("password") String password);

    @GET("user/{userId}?fields=competencies&fields=competencies.subject&fields=qualifications")
    Call<ResponseBody> getUser(@Path("userId") String userId);

    @PATCH("user/{userId}")
    Call<ResponseBody> updateUser(@Path("userId")String userId, @Body RequestBody body);
}
