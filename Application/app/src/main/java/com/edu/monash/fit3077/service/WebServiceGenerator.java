package com.edu.monash.fit3077.service;

import com.edu.monash.fit3077.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;

public class WebServiceGenerator {
    private static final String BASE_URL = "https://fit3077.com/api/v2/";
    private static final String API_KEY = BuildConfig.API_KEY;

    private static OkHttpClient httpClient = new OkHttpClient.Builder()
                                        .addInterceptor(new AuthorizationInterceptor())
                                        .build();

    private static Retrofit retrofitClient = new Retrofit.Builder()
                                                    .baseUrl(BASE_URL)
                                                    .client(httpClient)
                                                    .build();

    public static <S> S createService(Class<S> serviceClass) {
        return retrofitClient.create(serviceClass);
    }

    static class AuthorizationInterceptor implements Interceptor {

        @NotNull
        @Override
        public Response intercept(@NotNull Chain chain) throws IOException {
            Request.Builder requestBuilder = chain.request().newBuilder();
            requestBuilder.header("Authorization", API_KEY);
            return chain.proceed(requestBuilder.build());
        }
    }

}
