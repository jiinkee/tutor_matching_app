package com.edu.monash.fit3077.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.edu.monash.fit3077.model.Competency;
import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.model.PendingContract;
import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.model.Subject;
import com.edu.monash.fit3077.model.Tutor;
import com.edu.monash.fit3077.model.User;
import com.edu.monash.fit3077.model.UserRole;
import com.edu.monash.fit3077.service.MyResponse;
import com.edu.monash.fit3077.service.repository.ContractRepository;
import com.edu.monash.fit3077.service.repository.UserRepository;

import java.util.ArrayList;

import static com.edu.monash.fit3077.model.UserRole.BIDDER;

public class ContractRenewalFormViewModel extends FormViewModel {

    // repositories used in view model
    private ContractRepository contractRepository;
    private UserRepository userRepository;
    // variables to keep track of student selected options
    private String preferredSecondPartyUserId;
    private Contract selectedContract;
    // observable live data for contract renewal response
    private MediatorLiveData<MyResponse<String>> renewContractResponse;


    public ContractRenewalFormViewModel() {
        super();
        renewContractResponse = new MediatorLiveData<>();
        contractRepository = new ContractRepository();
        userRepository = UserRepository.getInstance();
    }

    /** SETTER method for contract renewal options **/
    public void setSelectedContract(Contract selectedContract) {
        this.selectedContract = selectedContract;
        preferredLessonInformation = selectedContract.getLessonInfo();
        preferredLessonDuration = selectedContract.getLessonInfo().getDuration();
        // preferred second party id is set to id of tutor in selected contract
        preferredSecondPartyUserId = selectedContract.getSecondPartyId();
    }

    public void setSelectedContractTerms(LessonInformation selectedContractTerms) {
        preferredLessonInformation = selectedContractTerms;
    }

    public void setPreferredSecondPartyUserId(String preferredSecondPartyUserId) {
        this.preferredSecondPartyUserId = preferredSecondPartyUserId;
    }

    /** GETTER method for contract details and options **/
    public Student getFirstParty() {
        Student firstParty = null;
        if (selectedContract != null) {
            firstParty = selectedContract.getFirstParty();
        }
        return firstParty;
    }

    public Tutor getSecondParty() {
        Tutor secondParty = null;
        if (selectedContract != null) {
            secondParty = selectedContract.getSecondParty();
        }
        return secondParty;
    }

    public String getSelectedContractId() {
        String selectedContractId = null;
        if (selectedContract!=null) {
            selectedContractId = selectedContract.getId();
        }
        return selectedContractId;
    }

    // get the preferred tutor selected by student
    private LiveData<MyResponse<Tutor>> getPreferredSecondParty() {
        MediatorLiveData<MyResponse<Tutor>> getSecondPartyResponse = new MediatorLiveData<>();

        // if preferred tutor is same as the one in selected contract (if student choose to renew contract with same tutor),
        //      directly return the tutor profile recorded in the selected contract
        if (preferredSecondPartyUserId.equals(selectedContract.getSecondPartyId())) {
            getSecondPartyResponse.setValue(MyResponse.successResponse(selectedContract.getSecondParty()));
        } else {
            // if student entered a different tutor id, call API to get the tutor profile
            getSecondPartyResponse.addSource(userRepository.getUserProfile(preferredSecondPartyUserId), response -> {
                if (response.status == MyResponse.ResponseStatus.SUCCESS) {
                    // check if the id given is a tutor id
                    if (!(response.data.getRolePermissions().contains(UserRole.CONTRACT_SECOND_PARTY))) {
                        getSecondPartyResponse.setValue(MyResponse.errorResponse("Please enter a valid tutor id", null));
                    } else {
                        Tutor secondPartyProfile = (Tutor) response.data;
                        getSecondPartyResponse.setValue(MyResponse.successResponse(secondPartyProfile));
                    }
                } else {
                    getSecondPartyResponse.setValue(MyResponse.errorResponse(response.errorMsg, null));
                }
            });
        }
        return getSecondPartyResponse;
    }

    // get latest five contracts of Student
    public LiveData<MyResponse<ArrayList<Contract>>> getLatestFiveContract() {
        MediatorLiveData<MyResponse<ArrayList<Contract>>>latestFiveContract = new MediatorLiveData<>();
        latestFiveContract.addSource(contractRepository.getLatestFiveSignedContracts(), latestFiveContracts -> {
            switch (latestFiveContracts.status) {
                case ERROR:
                    latestFiveContract.setValue(latestFiveContracts);
                    break;
                case SUCCESS:
                    latestFiveContract.setValue(MyResponse.successResponse(latestFiveContracts.data));
                    break;
            }
        });

        return latestFiveContract;
    }

    /** HELPER methods **/
    // check if second party has the subject specified and if his competency level for that subject
    // is at least 2 level higher than the competency level specified
    private boolean secondPartyPassedRequirements(Tutor secondParty, LessonInformation contractTerms) {

        Subject requiredSubject = contractTerms.getSubject();
        int requiredCompetencyLevel = contractTerms.getPreferredTutorCompetencyLevel();
        Competency secondPartyCompetencyForSubject = secondParty.getCompetencyForSubject(requiredSubject.getId());

        if (secondPartyCompetencyForSubject != null &&
                secondPartyCompetencyForSubject.getLevel() >= requiredCompetencyLevel + 2) {
            return true;
        } else {
            return false;
        }
    }

    // Perform checking on options chosen by student, create and return a Pending contract based on
    // student selected tutor and contract terms options, if requirements are fulfilled
    private LiveData<MyResponse<Contract>> getCreatedContract() {
        MediatorLiveData<MyResponse<Contract>> getContractResponse = new MediatorLiveData<>();

        // 1. get profile of second party selected by student
        getContractResponse.addSource(getPreferredSecondParty(), response -> {
            if (response.status == MyResponse.ResponseStatus.SUCCESS) {
                Tutor secondParty = response.data;
                LessonInformation lessonInformation = null;
                Contract newContract;

                // 2. get contract terms selected by student
                try {
                    lessonInformation = getPreferredLessonInformation();
                } catch (Exception e) {
                    getContractResponse.setValue(MyResponse.errorResponse("Please make sure the form is filled up correctly.", null));
                }

                // 3. check if the second party selected and contract terms fulfil the requirements and create a contract if valid
                if (lessonInformation != null && secondPartyPassedRequirements(secondParty, lessonInformation)) {
                    newContract = new PendingContract(getFirstParty(), secondParty, lessonInformation);
                    getContractResponse.setValue(MyResponse.successResponse(newContract));
                } else {
                    getContractResponse.setValue(MyResponse.errorResponse(
                            "The specified tutor does not fulfil the requirements for subject or competency level.",
                            null));
                }
            } else {
                getContractResponse.setValue(MyResponse.errorResponse("Failed to get second party information.", null));
            }
        });

        return getContractResponse;
    }

    /**
     * Renew contract based on tutor and contract terms chosen by student
     *
     * @return Response of renew contract API call
     */
    public LiveData<MyResponse<String>> renewContract() {

        renewContractResponse.addSource(getCreatedContract(), getContractResponse -> {
            if (getContractResponse.status == MyResponse.ResponseStatus.SUCCESS) {
                Contract newContract = getContractResponse.data;
                renewContractResponse.addSource(contractRepository.createContract(newContract), createContractResponse -> {
                    if (createContractResponse.status == MyResponse.ResponseStatus.SUCCESS) {
                        renewContractResponse.setValue(MyResponse.successResponse("Renew contract successfully"));
                    } else {
                        renewContractResponse.setValue(MyResponse.errorResponse(createContractResponse.errorMsg, null));
                    }
                });
            } else {
                renewContractResponse.setValue(MyResponse.errorResponse(getContractResponse.errorMsg, null));
            }
        });

        return renewContractResponse;
    }

    // reset the renew contract response status so that application will not show snackbar based on
    //  previous renew contract response
    public void resetRenewContractResponse() {
        renewContractResponse = new MediatorLiveData<>();
    }

}
