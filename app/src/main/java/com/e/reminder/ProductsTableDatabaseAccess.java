package com.e.reminder;

import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.document.ScanOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Search;
import com.amazonaws.mobileconnectors.dynamodbv2.document.Table;
import com.amazonaws.mobileconnectors.dynamodbv2.document.UpdateItemOperationConfig;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductsTableDatabaseAccess {
    private static final String COGNITO_POOL_ID = "us-east-1:f25770b9-bed5-472a-b377-29635e14af4e";
    private static final Regions MY_REGION = Regions.US_EAST_1;
    private AmazonDynamoDBClient dbClient;
    private AmazonDynamoDBClient dbClient2;
    private Table dbTable;
    private Table dbTable_user;
    private Context context;
    private final String DYNAMODB_TABLE = "products";
    private final String DYNAMODB_TABLE_user = "Users";

    CognitoCachingCredentialsProvider credentialsProvider;

    private static volatile ProductsTableDatabaseAccess instance;

    private ProductsTableDatabaseAccess(Context context) {
        this.context = context;
        credentialsProvider = new CognitoCachingCredentialsProvider(context, COGNITO_POOL_ID, MY_REGION);
        dbClient = new AmazonDynamoDBClient(credentialsProvider);
        dbClient2 = new AmazonDynamoDBClient(credentialsProvider);
        dbClient.setRegion(Region.getRegion(Regions.US_EAST_1));
        dbClient2.setRegion(Region.getRegion(Regions.US_EAST_1));
        dbTable = Table.loadTable(dbClient, DYNAMODB_TABLE);
        dbTable_user = Table.loadTable(dbClient2, DYNAMODB_TABLE_user);
    }

    public static synchronized ProductsTableDatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new ProductsTableDatabaseAccess(context);
        }
        return instance;
    }



    // sample db APIs. Write necessary APIs here.
    public Product getItem(String user_id) {
        Document result = dbTable.getItem(new Primitive(credentialsProvider.getCachedIdentityId()), new Primitive(user_id));
        return new Product(result);
    }

    public List<Product> getAllItems() {
        List<Product> result = new ArrayList<>();
        for (Document doc : dbTable.query(new Primitive("9988")).getAllResults()) {
            result.add(new Product(doc));
        }
        return result;
    }

    public ArrayList<Product> getAllContents() {
        ScanOperationConfig scanConfig = new ScanOperationConfig();
        List<String> attributeList = new ArrayList<>();
        attributeList.add("item_name");
        attributeList.add("item_id");
        attributeList.add("category");
        attributeList.add("expiry_date");
        scanConfig.withAttributesToGet(attributeList);
        System.out.println("In get all contents");
        Search searchResult = dbTable.scan(scanConfig);
        ArrayList<Product> result = new ArrayList<>();
        for (Document doc : searchResult.getAllResults()) {
            result.add(new Product(doc));
        }
        Collections.sort(result,Collections.reverseOrder());
        int size = result.size();
        System.out.println(size);
        if (size<=10)
        {
            return result;
        }
        else
        {
            ArrayList<Product> res = new ArrayList<Product>(result.subList(0, 10));
            return res;

        }
    }

    public User getUser(String key) {
        Document result = dbTable_user.getItem(new Primitive(credentialsProvider.getCachedIdentityId()), new Primitive(key));
        return new User(result);
    }

    public void CreateProduct(Product product) {
        Map<String, AttributeValue> hashMap = new HashMap<>();

        hashMap.put("item_name", new AttributeValue().withS(product.getName()));
        hashMap.put("item_id", new AttributeValue().withS(product.getItemid()));
        hashMap.put("category", new AttributeValue().withS(product.getCategory()));
        hashMap.put("expiry_date", new AttributeValue().withS(product.getExpiryDate()));

        Document document = Document.fromAttributeMap(hashMap);
        dbTable.putItem(document);
    }

    public void create_user(User user) {
        System.out.println(user);
        Map<String, AttributeValue> hashMap = new HashMap<>();

        hashMap.put("user_name", new AttributeValue().withS(user.getUsername()));
        hashMap.put("password", new AttributeValue().withS(user.getPassword()));
        hashMap.put("email_id", new AttributeValue().withS(user.getEmail()));
        hashMap.put("number", new AttributeValue().withS(user.getNumber()));

        Document document = Document.fromAttributeMap(hashMap);
        dbTable_user.putItem(document);

//        Document newuser = new Document();
//        newuser.put("email_id",user.get("email_id"));
//        newuser.put("user_name",user.get("user_name"));
//        newuser.put("password",user.get("password"));
//        newuser.put("number", user.get("number"));
//        System.out.println(newuser);
//        dbTable_user.putItem(newuser);

    }

    public void update(Product product) {
        Map<String, AttributeValue> hashMap = new HashMap<>();

        hashMap.put("item_name", new AttributeValue().withS(product.getName()));
        hashMap.put("item_id", new AttributeValue().withS(product.getItemid()));
        hashMap.put("category", new AttributeValue().withS(product.getCategory()));
        hashMap.put("expiry_date", new AttributeValue().withS(product.getExpiryDate()));

        Document document = Document.fromAttributeMap(hashMap);
        dbTable.deleteItem(
                document.get("item_id").asPrimitive(),
                document.get("category").asPrimitive());   // The Partition Key
        dbTable.putItem(document);

//        Document updatedBook = dbTable.updateItem(document, new UpdateItemOperationConfig().withReturnValues(ReturnValue.ALL_NEW));
    }

    public void delete(Product product) {
        Map<String, AttributeValue> hashMap = new HashMap<>();
        hashMap.put("item_id", new AttributeValue().withS(product.getItemid()));
        hashMap.put("category",new AttributeValue().withS(product.getCategory()));
        Document document = Document.fromAttributeMap(hashMap);
        dbTable.deleteItem(
                document.get("item_id").asPrimitive(),
                document.get("category").asPrimitive());  // The Hash Key);   // The Partition Key
    }
}
