package com.e.reminder;

import androidx.annotation.NonNull;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.util.StringUtils;

import java.util.Map;

public class Product implements Comparable<Product> {
    private String itemid;
    private String name;
    private String category;
    private String expiryDate;

    public String getItemid() {
        return itemid;
    }

    public String getCategory() {
        return category;
    }

    public String getExpiryDate() {
        return expiryDate;
    }


    public String getName() {
        return name;
    }

    public Product(String itemid, String name, String category, String expiryDate) {
        this.itemid = itemid;
        this.name = name;
        this.category = category;
        this.expiryDate = expiryDate;
    }

    public Product(Document document) {
        if (document != null) {
            Map<String, AttributeValue> attributes = document.toAttributeMap();
            if (attributes.containsKey("item_id")) {
                this.itemid = attributes.get("item_id").getS();
            }

            if (attributes.containsKey("item_name")) {
                this.name = attributes.get("item_name").getS();
            }

            if (attributes.containsKey("category")) {
                this.category = attributes.get("category").getS();
            }

            if (attributes.containsKey("expiry_date")) {
                this.expiryDate = attributes.get("expiry_date").getS();
            }
        }
    }

    public Boolean filterBasedOnString(CharSequence sequence) {
        CharSequence lowerCaseSequence = StringUtils.lowerCase(sequence.toString());

        if (this.itemid != null && StringUtils.lowerCase(this.itemid).contains(lowerCaseSequence)) {
            return true;
        }

        if (this.name != null && StringUtils.lowerCase(this.name).contains(lowerCaseSequence)) {
            return true;
        }

        if (this.category != null && StringUtils.lowerCase(this.category).contains(lowerCaseSequence)) {
            return true;
        }

        return false;
    }


    @Override
    @NonNull
    public String toString() {
        return "item_id='" + itemid + "\'\n" +
                ", item_name='" + name + "\'\n" +
                ", category='" + category + "\'\n" +
                ", expiryDate='" + expiryDate + "\'\n";
    }

    @Override
    public int compareTo(Product u) {
        if (getExpiryDate() == null || u.getExpiryDate() == null) {
            return 0;
        }
        return getExpiryDate().compareTo(u.getExpiryDate());
    }
}
