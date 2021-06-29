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

public interface ContractAPIService {

    @GET("contract")
    Call<ResponseBody> getContracts();

    @GET("contract/{contractId}")
    Call<ResponseBody> getContract(@Path("contractId") String contractId);

    @POST("contract")
    Call<ResponseBody> createContract(@Body RequestBody body);

    @PATCH("contract/{contractId}")
    Call<ResponseBody> updateContract(@Path("contractId") String contractId, @Body RequestBody body);

    @POST("contract/{contractId}/sign")
    Call<ResponseBody> signContract(@Path("contractId") String contractId, @Body RequestBody body);

}
