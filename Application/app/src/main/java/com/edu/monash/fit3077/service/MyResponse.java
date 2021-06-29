package com.edu.monash.fit3077.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

public class MyResponse<T> {

    @NonNull public final ResponseStatus status;
    @Nullable public final T data;
    @Nullable public final String errorMsg;

    private MyResponse(@NonNull ResponseStatus status, @Nullable T data, @Nullable String errorMsg) {
        this.status = status;
        this.data = data;
        this.errorMsg = errorMsg;
    }

    public static <T> MyResponse<T> successResponse(@NonNull T data) {
        return new MyResponse<>(ResponseStatus.SUCCESS, data, null);
    }

    public static <T> MyResponse<T> errorResponse(@NonNull String errorMsg, @Nullable T data) {
        return new MyResponse<>(ResponseStatus.ERROR, data, errorMsg);
    }

    public enum ResponseStatus {
        SUCCESS,
        ERROR,
    }
}
