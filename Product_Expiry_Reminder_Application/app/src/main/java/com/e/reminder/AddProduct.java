package com.e.reminder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.PropertyPermission;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddProduct extends AppCompatActivity {
    EditText scanResult;
    ImageView previewImage;
    EditText predicResult;
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    private static final String MODEL_NAME = "file:///android_asset/regression_model.pb";
    private static final String INPUT_NODE = "dense_input:0";
    private static final String OUTPUT_NODE = "dense_2/Relu:0";
    private static final long[] INPUT_SHAPE = {1,25};
    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        scanResult = findViewById(R.id.expiry_date_et);
        predicResult = findViewById(R.id.expiry_date_et);
        previewImage = findViewById(R.id.image_preview);

        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        final HashMap<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("Baby food",1);
        mapping.put("Breads & Cereal",2);
        mapping.put("Baking and Cooking",3);
        mapping.put("Beverages",5);
        mapping.put("Condiments",6);
        mapping.put("Dairy Products & Eggs",7);
        mapping.put("Fruit",8);
        mapping.put("Grains, Beans & Pasta",9);
        mapping.put("Fresh Meat",10);
        mapping.put("Shelf Meats",11);
        mapping.put("Processed Meat",12);
        mapping.put("Stuffed Meat",13);
        mapping.put("Poultry Cooked ",14);
        mapping.put("Poultry Fresh",15);
        mapping.put("Fresh Fruits",18);
        mapping.put("Fresh Vegetables",19);
        mapping.put("Seafood Fresh",20);
        mapping.put("Seafood Shellfish",21);
        mapping.put("Seafood Smoked",22);
        mapping.put("Snacks/shelf staple foods",23);
        mapping.put("Vegetarian Proteins",24);
        mapping.put("Pizza/Prepared Foods",25);
        mapping.put("Legumes",27);
        mapping.put("Desserts/candy",28);

        // Category drop down
        ArrayList<String> categorylist = new ArrayList<>(mapping.keySet());
        Spinner dropdown = findViewById(R.id.ctgy_et);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, categorylist);
        dropdown.setAdapter(adapter);



        Button predict = findViewById(R.id.btn_predict);
        predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked Load model");
                Spinner dropDowncat = findViewById(R.id.ctgy_et);
                String category=dropDowncat.getSelectedItem().toString();
                loadModel(category,mapping);


            }
        });

        Button ScanDate = findViewById(R.id.btn_scan_date);

        ScanDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked");
                showImageImportDialog();


            }
        });



        Button AddProductToDb = findViewById(R.id.btn_add);

        AddProductToDb.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                System.out.println("clicked");
                Intent intent_to_hp = new Intent(AddProduct.this, ListActivity.class);

                EditText editTextn=findViewById(R.id.name_et);
                String name=editTextn.getText().toString();
                Spinner dropDowncat = findViewById(R.id.ctgy_et);
                String category=dropDowncat.getSelectedItem().toString();
                EditText editTextexp = findViewById(R.id.expiry_date_et);
                String expiry_date=editTextexp.getText().toString();
                String uniqueID = UUID.randomUUID().toString();

                Product newProduct = new Product(uniqueID,name,category,expiry_date);


                String[] expdate=expiry_date.split("/");
                String date=expdate[0];
                int edate = Integer.parseInt(date);
                String month=expdate[1];
                int emonth = Integer.parseInt(month);
                String year=expdate[2];
                int eyear = Integer.parseInt(year);

                Calendar cal = Calendar.getInstance();
                int curyear=cal.get(Calendar.YEAR);
                int curdate =cal.get(Calendar.DATE);
                int curmonth=cal.get(Calendar.MONTH)+1;

                int diffy=eyear-curyear;
                int diffm=emonth-curmonth;
                int diffd=edate-curdate;

                cal.add(Calendar.YEAR,diffy);
                cal.add(Calendar.MONTH,diffm);
                cal.add(Calendar.DATE,diffd);
                cal.add(Calendar.HOUR_OF_DAY,00);
                cal.add(Calendar.MINUTE,00);
                cal.add(Calendar.SECOND,10);
                System.out.println("sending intent_"+cal);
                Intent intent = new Intent(getApplicationContext(),AlarmReceiver.class);
                intent.putExtra("item_name",name);

                PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

                CreateItemAsyncTask task = new CreateItemAsyncTask();
                task.execute(newProduct);

                startActivity(intent_to_hp);

            }
        });

    }

    private float[] ohencode(Integer id){
        System.out.println(id);
        float[] en_res={0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        if(id<=3) {
            en_res[id - 1] = 1;
        }else if(id<=15){
            en_res[id - 2] = 1;
        }else{
            en_res[id - 4] = 1;
        }

        return en_res;


    }





    private void loadModel(String Category_model,HashMap<String, Integer> mapping)
    {
        try{
            TensorFlowInferenceInterface tensorFlowInferenceInterface = new TensorFlowInferenceInterface(getAssets(), MODEL_NAME);

            int id= mapping.get(Category_model);
            float[] floatArray = ohencode(id);

            System.out.println(Arrays.toString(floatArray));
            tensorFlowInferenceInterface.feed(INPUT_NODE, floatArray, INPUT_SHAPE);
            tensorFlowInferenceInterface.run(new String[] {OUTPUT_NODE});
            float[] results = {0.0f};
            tensorFlowInferenceInterface.fetch(OUTPUT_NODE, results);
//            System.out.println("Model loa");
            System.out.println(results[0]);

            Toast predic=Toast.makeText(getApplicationContext(),"This product is predicted to expire in "+results[0],Toast.LENGTH_SHORT);
            predic.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
            predic.show();


            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = Calendar.getInstance();

            System.out.println("Current Date: "+sdf.format(cal.getTime()));
            cal.add(Calendar.DAY_OF_MONTH,Math.round(results[0]));
            String newDate = sdf.format(cal.getTime());
            System.out.println("Date after Addition: "+newDate);
            predicResult.setText(newDate);

        }
        catch (IllegalArgumentException e){
            System.out.println(e);
        }
    }
    private void showImageImportDialog() {
        String[] items = {"Camera","Gallery"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Select Image");
        dialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which ==0)
                {
                    if (!checkCameraPermission()){
                        //camera permission not allowed, request for perm
                        requestCameraPermission();
                    }
                    else{
                        //permission allowed
                        pickCamera();
                    }
                }
                if(which == 1)
                {
                    if (!checkStoragePermission()){
                        //camera permission not allowed, request for perm
                        requestStoragePermission();
                    }
                    else{
                        //permission allowed
                        pickGallery();
                    }
                }
            }
        });
        dialog.create().show();
    }

    private void pickGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"NewPic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image to Text");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE);
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermission,CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }
    // handle permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:
                if(grantResults.length>0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted)
                    {
                        pickCamera();
                    }
                    else {
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:
                if(grantResults.length>0)
                {
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted)
                    {
                        pickGallery();
                    }
                    else {
                        Toast.makeText(this,"permission denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    //handle image result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // got image from camera
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                CropImage.activity(data.getData()).setGuidelines(CropImageView.Guidelines.ON).start(this); //Enable image guidelines

            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                CropImage.activity(image_uri).setGuidelines(CropImageView.Guidelines.ON).start(this); //Enable image guidelines
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();//get image uri
                //set image to image view
                previewImage.setImageURI(resultUri);
                // get drawable bitmap for text recognition;
                BitmapDrawable bitmapDrawable = (BitmapDrawable) previewImage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

                if (!textRecognizer.isOperational()) {
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
                } else {
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> items = textRecognizer.detect(frame);
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < items.size(); i++) {
                        TextBlock myItem = items.valueAt(i);
                        sb.append(myItem.getValue());

                    }

                    System.out.println("txt_recog_result"+sb.toString());
                    String pls =sb.toString();
                    String final1="";
                    Matcher m = Pattern.compile("Expiry Date:\\s*(\\d{1,2}\\/\\d{1,2}\\/\\d{4}|\\d{1,2}\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(pls);
                    while (m.find()) {
                        System.out.println("in first_regex"+m.group(1));
                        final1=final1+m.group(1);
                    }
                    System.out.println("exp_regex: "+final1);
                    String final2 = "";
                    Matcher m2 = Pattern.compile("(\\d{1,2}\\/\\d{1,2}\\/\\d{4}|\\d{1,2}\\d{1,2})", Pattern.CASE_INSENSITIVE).matcher(final1);
                    while (m2.find()) {
                        System.out.println(m2.group(1));
                        final2=final2+m2.group(1);
                    }
                    System.out.println("Final_regex"+final2);
                    //System.out.println(final1);
//                    String[] split = final1.split("/");
//                    String day=split[0];
//                    String month=split[1];
//                    String year=split[2];
//                    System.out.println("day "+day);
//                    System.out.println("month "+month);
//                    System.out.println("year "+year);

                    scanResult.setText(final2);

                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                System.out.println("Error");
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class CreateItemAsyncTask extends AsyncTask<Product, Void, Void> {
        @Override
        protected Void doInBackground(Product... products) {
            ProductsTableDatabaseAccess databaseAccess = ProductsTableDatabaseAccess.getInstance(AddProduct.this);
            databaseAccess.CreateProduct(products[0]);
            return null;
        }
    }
}