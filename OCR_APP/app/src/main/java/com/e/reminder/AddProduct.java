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
import android.view.View;
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

import java.util.ArrayList;
import java.util.PropertyPermission;
import java.util.Calendar;
import java.util.UUID;

public class AddProduct extends AppCompatActivity {
    EditText scanResult;
    ImageView previewImage;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;

    String cameraPermission[];
    String storagePermission[];

    Uri image_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        scanResult = findViewById(R.id.expiry_date_et);
        previewImage = findViewById(R.id.image_preview);

        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


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

                EditText editText=findViewById(R.id.name_et);
                String name=editText.getText().toString();
                editText = findViewById(R.id.ctgy_et);
                String category=editText.getText().toString();
                editText = findViewById(R.id.expiry_date_et);
                String expiry_date=editText.getText().toString();
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
                System.out.println("sending intnet"+cal);
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
                        sb.append("\n");
                    }

                    System.out.println(sb.toString());
                    String full_date=sb.toString();
                    String[] split = full_date.split("/");
                    String day=split[0];
                    String month=split[1];
                    String year=split[2];
                    System.out.println("day"+day);
                    System.out.println("month"+month);
                    System.out.println("year"+year);
                    scanResult.setText(sb.toString());

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
