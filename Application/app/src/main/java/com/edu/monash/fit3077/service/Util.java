package com.edu.monash.fit3077.service;

import com.edu.monash.fit3077.model.Student;
import com.edu.monash.fit3077.model.Tutor;
import com.edu.monash.fit3077.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

public class Util {
    public static User decodeJWTToUser(String jwtJson) throws JSONException {

        String jwt = new JSONObject(jwtJson).getString("jwt");
        String[] chunks = jwt.split("\\.");
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(chunks[1]));

        JSONObject payloadJson = new JSONObject(payload);
        String userId = payloadJson.getString("sub");
        String userName = payloadJson.getString("username");
        String givenName = payloadJson.getString("givenName");
        String familyName = payloadJson.getString("familyName");
        boolean isStudent = payloadJson.getBoolean("isStudent");
        boolean isTutor = payloadJson.getBoolean("isTutor");

        User user = null;
        if (isStudent) {
            user = new Student(userId, userName, givenName, familyName);
        } else if (isTutor) {
            user = new Tutor(userId, userName, givenName, familyName);
        }
        return user;
    }

    public static String extractResponseErrorBodyMessage(String errorBody) throws JSONException {
        return new JSONObject(errorBody).getString("message");
    }
}
