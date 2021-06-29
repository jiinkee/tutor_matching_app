package com.edu.monash.fit3077.service.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.UserAPIService;
import com.edu.monash.fit3077.service.converter.UserConverter;
import com.edu.monash.fit3077.service.WebServiceGenerator;
import com.edu.monash.fit3077.service.Util;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// UserRepository is a singleton class because we want to make the logged in user instance available globally
public class UserRepository {
    private static UserRepository repo = null;
    private User loggedInUser = null;
    private UserAPIService userAPI;
    private UserConverter userConverter;

    private UserRepository() {
        userAPI = WebServiceGenerator.createService(UserAPIService.class);
        userConverter = new UserConverter();
    }

    // allow other classes to instantiate/retrieve the single instance of UserRepository
    public static UserRepository getInstance() {
        if (repo == null) {
            repo = new UserRepository();
        }
        return repo;
    }

    // allow other classes to retrieve the single instanced loggedInUser
    public User getLoggedInUser() {
        return loggedInUser;
    }

    // allow user to login to the system
    public LiveData<MyResponse<String>> login(String userName, String password) {
        final MutableLiveData<MyResponse<String>> loginResponse = new MutableLiveData<>();
        Call<ResponseBody> loginCall = userAPI.login(userName, password);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        // retrieve JWT token from the response body
                        String jwt = response.body().string();
                        // decode JWT token string to get the details of the logged in user
                        loggedInUser = Util.decodeJWTToUser(jwt);
                        // the User instance we retrieved from JWT token has only got the basic information of the user
                        // hence we call this method to retrieve a full profile of the user
                        getUserFullProfile();
                        loginResponse.setValue(MyResponse.successResponse(jwt));

                    } catch (Exception e) {
                        loginResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        loginResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        loginResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                loginResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return loginResponse;
    }

    // retrieve a full profile of the logged in user
    // full profile of a user includes his/her competencies, subjects & qualifications
    // we choose to pre-fetch all these data because these information will be widely used in bid request/bid offer creation part
    private LiveData<MyResponse<String>> getUserFullProfile() {
        final MutableLiveData<MyResponse<String>> updateLoggedInUserResponse = new MutableLiveData<>();

        Call<ResponseBody> loginCall = userAPI.getUser(loggedInUser.getId());
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String userInfo = response.body().string();
                        loggedInUser = userConverter.fromJsonStringToObject(userInfo);
                        updateLoggedInUserResponse.setValue(MyResponse.successResponse(null));
                    } catch (Exception e) {
                        updateLoggedInUserResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        updateLoggedInUserResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        updateLoggedInUserResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                updateLoggedInUserResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return updateLoggedInUserResponse;
    }

    public LiveData<MyResponse<String>> updateUser() {
        final MutableLiveData<MyResponse<String>> updateUserResponse = new MutableLiveData<>();

        // prepare request body
        JsonObject userObject =  userConverter.fromObjectToJsonObject(loggedInUser);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), userObject.toString());

        Call<ResponseBody> updateUserCall = userAPI.updateUser(loggedInUser.getId(), body);
        updateUserCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    updateUserResponse.setValue(MyResponse.successResponse("Success."));
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        updateUserResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        updateUserResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                updateUserResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return updateUserResponse;
    }

    // get user profile based on user id
    public LiveData<MyResponse<User>> getUserProfile(String userId) {
        final MutableLiveData<MyResponse<User>> getUserProfileResponse = new MutableLiveData<>();

        Call<ResponseBody> loginCall = userAPI.getUser(userId);
        loginCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String userInfo = response.body().string();
                        User userProfile = userConverter.fromJsonStringToObject(userInfo);
                        getUserProfileResponse.setValue(MyResponse.successResponse(userProfile));
                    } catch (Exception e) {
                        getUserProfileResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        getUserProfileResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        getUserProfileResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                getUserProfileResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return getUserProfileResponse;
    }
}
