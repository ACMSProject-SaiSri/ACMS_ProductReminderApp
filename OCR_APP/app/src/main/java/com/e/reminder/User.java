package com.e.reminder;

import androidx.annotation.NonNull;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.util.StringUtils;

import java.util.Map;

public class User {

    private String username;
    private String password;
    private String number;
    private String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email;
    }

    public User(String username, String password, String number, String email) {
        this.username = username;
        this.password = password;
        this.number = number;
        this.email = email;
    }

    public User(Document document) {
        if (document != null) {
            Map<String, AttributeValue> attributes = document.toAttributeMap();
            if (attributes.containsKey("username")) {
                this.username = attributes.get("username").getS();
            }

            if (attributes.containsKey("password")) {
                this.password = attributes.get("password").getS();
            }

            if (attributes.containsKey("number")) {
                this.number = attributes.get("number").getS();
            }

            if (attributes.containsKey("email")) {
                this.email = attributes.get("email").getS();
            }
        }
    }

    @Override
    @NonNull
    public String toString() {
        return "username='" + username + "\'\n" +
                ", password='" + password + "\'\n" +
                ", email='" + email + "\'\n" +
                ", number='" + number + "\'\n";
    }
}
