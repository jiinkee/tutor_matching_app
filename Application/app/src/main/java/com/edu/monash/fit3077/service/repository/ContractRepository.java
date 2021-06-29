package com.edu.monash.fit3077.service.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.ContractStatus;
import com.edu.monash.fit3077.model.PendingContract;
import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.model.UserRole;
import com.edu.monash.fit3077.service.ContractAPIService;
import com.edu.monash.fit3077.service.converter.ContractConverter;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.Util;
import com.edu.monash.fit3077.service.WebServiceGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractRepository {
    private ContractAPIService contractAPI;
    private ContractConverter contractConverter;

    public ContractRepository() {
        contractAPI = WebServiceGenerator.createService(ContractAPIService.class);
        contractConverter = new ContractConverter();
    }

    // get a list of all contracts available
    public LiveData<MyResponse<ArrayList<Contract>>> getContracts(ContractStatus status) {
        final MutableLiveData<MyResponse<ArrayList<Contract>>> _contractResponse = new MutableLiveData<>();
        Call<ResponseBody> contractCall = contractAPI.getContracts();
        contractCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        ArrayList<Contract> contracts = filterContracts(responseString, status);
                        _contractResponse.setValue(MyResponse.successResponse(contracts));
                    } catch (Exception e) {
                        e.printStackTrace();
                        _contractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        _contractResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        _contractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _contractResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return _contractResponse;
    }

    // filter the obtained contracts based on the given status
    // also filter the contracts such that only logged in user's contracts are remained
    private ArrayList<Contract> filterContracts(String contractsString, ContractStatus status) {
        // get user id and role
        String userId =  UserRepository.getInstance().getLoggedInUser().getId();
        User loggedInUser = UserRepository.getInstance().getLoggedInUser();

        JsonArray contractJsonArray = new Gson().fromJson(contractsString, JsonArray.class);
        ArrayList<JsonObject> filteredContracts = new ArrayList<>();

        // set the party of contract based on user role
        String party = (loggedInUser.getRolePermissions().contains(UserRole.CONTRACT_FIRST_PARTY)) ? "firstParty" : "secondParty";

        for (JsonElement jsonObj: contractJsonArray) {
            JsonObject contract = jsonObj.getAsJsonObject();
            JsonObject user =  contract.get(party).getAsJsonObject();
            String id = user.get("id").getAsString();
            // check if the contract retrieved is user's contract
            if (id.equals(userId)) {
                // determine the status of the user's contract
                ContractStatus contractStatus;

                // contract has expired when the current date time >= the contract's expiry date time
                Instant expiryDate = Instant.parse(contract.get("expiryDate").getAsString());
                if (Instant.now().compareTo(expiryDate) >= 0) {
                    contractStatus = ContractStatus.EXPIRED;
                } else {
                    // contract does not have sign date means it is still pending
                    // contract which has sign date & no termination date means it is alive and valid
                    contractStatus = (contract.get("dateSigned").isJsonNull()) ? ContractStatus.PENDING : ContractStatus.VALID;
                }

                // get user contract with the specified status
                if (contractStatus == status) {
                    filteredContracts.add(contract);
                }
            }
        }

        // convert from list of Json object to list of contracts
        return contractConverter.fromJsonStringToObjects(filteredContracts.toString());
    }

    public LiveData<MyResponse<ArrayList<Contract>>> getLatestFiveSignedContracts() {
        final MutableLiveData<MyResponse<ArrayList<Contract>>> _contractResponse = new MutableLiveData<>();
        Call<ResponseBody> contractCall = contractAPI.getContracts();
        contractCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();

                        JsonArray allContracts = new Gson().fromJson(responseString, JsonArray.class);
                        ArrayList<JsonObject> signedContracts = new ArrayList<>();

                        for(JsonElement contractElem: allContracts) {
                            JsonObject contractObj = contractElem.getAsJsonObject();
                            // find all signed contracts
                            if (!contractObj.get("dateSigned").isJsonNull()) {
                                signedContracts.add(contractObj);
                            }
                        }

                        if (!signedContracts.isEmpty()) {
                            // sort all signed contracts by creation date in descending order
                            Collections.sort(signedContracts, new Comparator<JsonObject>() {
                                @Override
                                public int compare(JsonObject o1, JsonObject o2) {
                                    return Instant.parse(o2.get("dateCreated").getAsString()).compareTo(Instant.parse(o1.get("dateCreated").getAsString()));
                                }
                            });

                            // get the first 5 contracts
                            ArrayList<Contract> latestFiveContracts = new ArrayList<>();
                            if (signedContracts.size() <= 5) {
                                for (JsonObject contractObj: signedContracts) {
                                    latestFiveContracts.add(contractConverter.fromJsonObjectToObject(contractObj));
                                }
                            } else {
                                for (int i = 0; i < 5; i++) {
                                    Contract contract = contractConverter.fromJsonObjectToObject(signedContracts.get(i));
                                    latestFiveContracts.add(contract);
                                }
                            }

                            _contractResponse.setValue(MyResponse.successResponse(latestFiveContracts));

                        } else {
                            // there is no signed contract
                            _contractResponse.setValue(MyResponse.successResponse(new ArrayList<Contract>()));
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        _contractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        _contractResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        _contractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                _contractResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return _contractResponse;
    }

    // get the details of a specific contract based on the given contract ID
    public LiveData<MyResponse<Contract>> getContract(String contractId) {
        final MutableLiveData<MyResponse<Contract>> contractResponse = new MutableLiveData<>();

        Call<ResponseBody> contractCall = contractAPI.getContract(contractId);
        contractCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        JsonObject contractJson = new Gson().fromJson(responseString, JsonObject.class);
                        Contract contract = contractConverter.fromJsonStringToObject(contractJson.toString());
                        contractResponse.setValue(MyResponse.successResponse(contract));
                    } catch (Exception e) {
                        e.printStackTrace();
                        contractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        contractResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        contractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                contractResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return contractResponse;
    }

    // only Pending contracts can be updated
    public LiveData<MyResponse<Contract>> updateContract(PendingContract contract) {
        final MutableLiveData<MyResponse<Contract>> updateContractResponse = new MutableLiveData<>();

        // prepare request body
        String contractString = contractConverter.fromObjectToJsonString(contract);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), contractString);

        Call<ResponseBody> updateContractCall = contractAPI.updateContract(contract.getId(), body);
        updateContractCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        Contract updatedContract = contractConverter.fromJsonStringToObject(responseString);
                        updateContractResponse.setValue(MyResponse.successResponse(updatedContract));
                    } catch (Exception e) {
                        e.printStackTrace();
                        updateContractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        updateContractResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        updateContractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                updateContractResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return updateContractResponse;

    }

    // close contract, this function will be called when both parties have signed on the pending contract
    // hence the dateSigned attribute of the contract will be updated, and the contract will become valid
    public LiveData<MyResponse<Contract>> closeContract(Contract contract, Instant signDate) {
        final MutableLiveData<MyResponse<Contract>> closeContractResponse = new MutableLiveData<>();

        // prepare request body
        JsonObject dateSigned = new JsonObject();
        dateSigned.addProperty("dateSigned", signDate.toString());
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), dateSigned.toString());

        Call<ResponseBody> updateContractCall = contractAPI.signContract(contract.getId(), body);
        updateContractCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    contract.setSignDate(signDate);
                    closeContractResponse.setValue(MyResponse.successResponse(contract));
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        closeContractResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        closeContractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                closeContractResponse.setValue(MyResponse.errorResponse(t.getMessage(), null));
            }
        });

        return closeContractResponse;

    }

    // create new contract
    public LiveData<MyResponse<Contract>> createContract(Contract newContract) {
        final MutableLiveData<MyResponse<Contract>> createContractResponse = new MutableLiveData<>();

        // prepare request body
        String contractString = contractConverter.fromObjectToJsonString(newContract);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), contractString);

        Call<ResponseBody> contractCall = contractAPI.createContract(body);
        contractCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseString = response.body().string();
                        Contract contract = contractConverter.fromJsonStringToObject(responseString);
                        createContractResponse.setValue(MyResponse.successResponse(contract));
                    } catch (Exception e) {
                        createContractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        String errorMsg = Util.extractResponseErrorBodyMessage(errorBody);
                        createContractResponse.setValue(MyResponse.errorResponse(errorMsg, null));
                    } catch (Exception e) {
                        createContractResponse.setValue(MyResponse.errorResponse(e.getMessage(), null));
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                String errorMsg = t.getMessage();
                createContractResponse.setValue(MyResponse.errorResponse(errorMsg, null));
            }
        });

        return createContractResponse;
    }
}
