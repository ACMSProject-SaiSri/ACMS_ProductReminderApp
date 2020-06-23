package com.e.reminder;

import androidx.annotation.NonNull;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.util.StringUtils;

import java.util.Map;

public class User {

    private String user_name;
    private String password;
    private String number;
    private String email_id;

    public String getUsername() {
        return user_name;
    }

    public String getPassword() {
        return password;
    }

    public String getNumber() {
        return number;
    }

    public String getEmail() {
        return email_id;
    }

    public User(String username, String password, String number, String email) {
        this.user_name = username;
        this.password = password;
        this.number = number;
        this.email_id = email;
    }

    public User(Document document) {
        if (document != null) {
            Map<String, AttributeValue> attributes = document.toAttributeMap();
            if (attributes.containsKey("user_name")) {
                this.user_name = attributes.get("user_name").getS();
            }

            if (attributes.containsKey("password")) {
                this.password = attributes.get("password").getS();
            }

            if (attributes.containsKey("number")) {
                this.number = attributes.get("number").getS();
            }

            if (attributes.containsKey("email_id")) {
                this.email_id = attributes.get("email_id").getS();
            }
        }
    }

    @Override
    @NonNull
    public String toString() {
        return "username='" + user_name + "\'\n" +
                ", password='" + password + "\'\n" +
                ", email='" + email_id + "\'\n" +
                ", number='" + number + "\'\n";
    }
}
