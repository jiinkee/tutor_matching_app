package com.edu.monash.fit3077.service.converter;

import com.edu.monash.fit3077.model.Competency;
import com.edu.monash.fit3077.model.Contract;
import com.edu.monash.fit3077.model.ContractPayment;
import com.edu.monash.fit3077.model.ContractSignature;
import com.edu.monash.fit3077.model.ExpiredContract;
import com.edu.monash.fit3077.model.LessonInformation;
import com.edu.monash.fit3077.model.PendingContract;
import com.edu.monash.fit3077.model.Qualification;
import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.model.Subject;
import com.edu.monash.fit3077.model.Tutor;
import com.edu.monash.fit3077.model.ValidContract;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Converter for contract
 */
public class ContractConverter extends Converter<Contract>{

    private static UserConverter userConverter;
    private static LessonInformationConverter lessonInfoConverter;
    private static SubjectConverter subjectConverter;
    private static CompetencyConverter competencyConverter;
    private static QualificationConverter qualificationConverter;

    public ContractConverter() {
        super(ContractConverter::toJson, ContractConverter::fromJson);
        userConverter = new UserConverter();
        lessonInfoConverter = new LessonInformationConverter();
        subjectConverter = new SubjectConverter();
        competencyConverter = new CompetencyConverter();
        qualificationConverter = new QualificationConverter();
    }

    // convert a Contract object to a JSON string
    private static String toJson(Contract contract) {
        String firstPartyId = contract.getFirstParty().getId();
        String secondPartyId = contract.getSecondParty().getId();
        String subjectId = contract.getSubject().getId();
        String dateCreated = contract.getCreationDateString();
        String expiryDate = contract.getExpiryDateString();
        JsonObject lessonInfo = lessonInfoConverter.fromObjectToJsonObject(contract.getLessonInfo());

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("firstPartyId", firstPartyId);
        jsonObject.addProperty("secondPartyId", secondPartyId);
        jsonObject.addProperty("subjectId", subjectId);
        jsonObject.addProperty("dateCreated", dateCreated);
        jsonObject.addProperty("expiryDate", expiryDate);

        JsonObject paymentInfo  = fromPaymentInfoToJsonObject(contract);
        jsonObject.add("paymentInfo", paymentInfo);

        jsonObject.add("lessonInfo", lessonInfo);

        JsonObject additionalInfo = fromAdditionalInfoToJsonObject(contract);
        jsonObject.add("additionalInfo", additionalInfo);

        return jsonObject.toString();
    }

    // convert the payment info part of a contract, i.e. the ContractPayment, into a JSON string
    private static JsonObject fromPaymentInfoToJsonObject(Contract contract) {
        String paymentAmount= contract.getPaymentInfo().getAmountString();

        JsonObject paymentInfo = new JsonObject();
        paymentInfo.addProperty("amount", paymentAmount);

        return paymentInfo;
    }

    // convert the additional info part of the contract into a JSON string
    private static JsonObject fromAdditionalInfoToJsonObject(Contract contract) {

        JsonObject additionalInfo = new JsonObject();

        if (contract.getStudentSignDate() == null) {
            additionalInfo.add("studentSignDate", null);
        } else {
            additionalInfo.addProperty("studentSignDate", contract.getStudentSignDate().toString());
        }

        if (contract.getTutorSignDate() == null) {
            additionalInfo.add("tutorSignDate", null);
        } else {
            additionalInfo.addProperty("tutorSignDate", contract.getTutorSignDate().toString());
        }

        Competency tutorCompetency = contract.getTutorCompetency();
        if (tutorCompetency == null) {
            additionalInfo.add("tutorCompetency", null);
        } else {
            additionalInfo.add("tutorCompetency", competencyConverter.fromObjectToJsonObject(tutorCompetency));
        }

        ArrayList<Qualification> tutorQualifications = contract.getTutorQualification();
        if (tutorQualifications == null) {
            additionalInfo.add("tutorQualifications", null);
        } else {
            additionalInfo.add("tutorQualifications", qualificationConverter.fromObjectsToJsonArray(tutorQualifications));
        }

        return additionalInfo;
    }

    // convert a JSON string into a Contract object
    private static Contract fromJson(String jsonString) {
        JsonObject convertedObject = new Gson().fromJson(jsonString, JsonObject.class);

        // get contract parties and subject information
        String id = convertedObject.get("id").getAsString();

        Student firstParty = (Student) userConverter.fromJsonStringToObject(convertedObject.get("firstParty").toString());
        Tutor secondParty = (Tutor) userConverter.fromJsonStringToObject(convertedObject.get("secondParty").toString());
        Subject subject = subjectConverter.fromJsonStringToObject(convertedObject.get("subject").toString());

        // get contract date related information
        Instant dateCreated = Instant.parse(convertedObject.get("dateCreated").getAsString());
        Instant dateSigned = null;
        if (!convertedObject.get("dateSigned").isJsonNull()) {
            dateSigned = Instant.parse(convertedObject.get("dateSigned").getAsString());
        }
        Instant expiryDate = Instant.parse(convertedObject.get("expiryDate").getAsString());

        // get payment information
        JsonObject paymentInfoObj = convertedObject.get("paymentInfo").getAsJsonObject();
        ContractPayment paymentInfo = new ContractPayment(paymentInfoObj.get("amount").getAsDouble());

        // get lesson information
        LessonInformation lessonInfo = lessonInfoConverter.fromJsonStringToObject(convertedObject.get("lessonInfo").toString(), subject);

        // get student and tutor sign date from additional Info
        JsonObject additionalInfo = convertedObject.get("additionalInfo").getAsJsonObject();

        // student sign date can be null
        Instant studentSignDate = null;
        if (!additionalInfo.get("studentSignDate").isJsonNull()) {
            studentSignDate = Instant.parse(additionalInfo.get("studentSignDate").getAsString());
        }
        Instant tutorSignDate = null;
        if (!additionalInfo.get("tutorSignDate").isJsonNull()) {
            tutorSignDate = Instant.parse(additionalInfo.get("tutorSignDate").getAsString());
        }
        // create contract signature
        ContractSignature contractSignature = new ContractSignature(studentSignDate, tutorSignDate, dateSigned);

        // get and set tutor competency
        if (additionalInfo.get("tutorCompetency")!= null && !additionalInfo.get("tutorCompetency").isJsonNull()) {
            Competency tutorCompetency = competencyConverter.fromJsonObjectToObject(additionalInfo.get("tutorCompetency").getAsJsonObject());
            secondParty.setCompetencies(new ArrayList<>(Arrays.asList(tutorCompetency)));
        }

        // get and set tutor qualification
        if (additionalInfo.get("tutorQualifications")!= null &&!additionalInfo.get("tutorQualifications").isJsonNull()) {
            ArrayList<Qualification> tutorQualifications = qualificationConverter.fromJsonArrayToObjects(additionalInfo.get("tutorQualifications").getAsJsonArray());
            secondParty.setQualifications(tutorQualifications);
        }

        // create contract object
        Contract contract;
        // Create EXPIRED contract, when the current date time >= contract expiry date time
        if (Instant.now().compareTo(expiryDate) >= 0) {
            contract = new ExpiredContract(id, firstParty, secondParty, dateCreated, expiryDate, contractSignature, lessonInfo, paymentInfo);
        }
        // create PENDING contract
        else if (dateSigned == null) {
            contract = new PendingContract(id, firstParty, secondParty, dateCreated, expiryDate, contractSignature, lessonInfo, paymentInfo);
        }
        // create VALID contract
        else {
            contract = new ValidContract(id, firstParty, secondParty, dateCreated, expiryDate, contractSignature, lessonInfo, paymentInfo);
        }
        return contract;
    }
}
