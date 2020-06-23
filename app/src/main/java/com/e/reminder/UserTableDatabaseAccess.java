package com.e.reminder;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

public class UserTableDatabaseAccess {
    private static final String COGNITO_POOL_ID = "us-east-1:f25770b9-bed5-472a-b377-29635e14af4e";
    private static final Regions MY_REGION = Regions.US_EAST_1;
    private AmazonDynamoDBClient dbClient;
    private Table dbTable;
    private Context context;
    private final String DYNAMODB_TABLE = "Users";

    CognitoCachingCredentialsProvider credentialsProvider;

    private static volatile UserTableDatabaseAccess instance;
    private UserTableDatabaseAccess(Context context) {
        this.context = context;
        credentialsProvider = new CognitoCachingCredentialsProvider(context, COGNITO_POOL_ID, MY_REGION);
        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_EAST_1));
        dbTable = Table.loadTable(dbClient, DYNAMODB_TABLE);
    }
    public static synchronized UserTableDatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new UserTableDatabaseAccess(context);
        }
        return instance;
    }

    // sample db APIs. Write necessary APIs here.
    public User getUser(String user_id) {
        Document result=dbTable.getItem(new Primitive(user_id));
        System.out.println(result);
        //Document result = dbTable.getItem(new Primitive(credentialsProvider.getCachedIdentityId()), new Primitive("abc@gmail.com"));
        return new User(result);
    }

    public void create_user(User user) {
        System.out.println(user);
        Map<String, AttributeValue> hashMap = new HashMap<>();

        hashMap.put("user_name", new AttributeValue().withS(user.getUsername()));
        hashMap.put("password", new AttributeValue().withS(user.getPassword()));
        hashMap.put("email_id", new AttributeValue().withS(user.getEmail()));
        hashMap.put("number", new AttributeValue().withS(user.getNumber()));

        Document document = Document.fromAttributeMap(hashMap);
        dbTable.putItem(document);
//        Document newuser = new Document();
//        newuser.put("email_id",user.get("email_id"));
//        newuser.put("user_name",user.get("user_name"));
//        newuser.put("password",user.get("password"));
//        newuser.put("number", user.get("number"));
//        System.out.println(newuser);
//        dbTable_user.putItem(newuser);

    }

}
