package com.e.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.UUID;

public class UpdateProduct extends AppCompatActivity {
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_product);

        EditText tv1= findViewById(R.id.name_et);
        EditText tv2= findViewById(R.id.ctgy_et);
        EditText tv3= findViewById(R.id.expiry_date_et);
        Bundle b = getIntent().getExtras();
        if(b!=null)
        {
            String str1=b.getString("name");
            String str2=b.getString("category");
            String str3=b.getString("expiry_date");
            id = b.getString("id");

            tv1.setText(str1);
            tv2.setText(str2);
            tv3.setText(str3);
        }

        Button buttonUpdateProduct = findViewById(R.id.btn_save);

        buttonUpdateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked");
                Intent intent = new Intent(UpdateProduct.this, ListActivity.class);

                EditText editText=findViewById(R.id.name_et);
                String name=editText.getText().toString();
                editText = findViewById(R.id.ctgy_et);
                String category=editText.getText().toString();
                editText = findViewById(R.id.expiry_date_et);
                String expiry_date=editText.getText().toString();

                Product newProduct = new Product(id,name,category,expiry_date);

                UpdateItemAsyncTask task = new UpdateItemAsyncTask();
                task.execute(newProduct);
                startActivity(intent);

            }
        });

    }
    private class UpdateItemAsyncTask extends AsyncTask<Product, Void, Void> {
        @Override
        protected Void doInBackground(Product... products) {
            ProductsTableDatabaseAccess databaseAccess = ProductsTableDatabaseAccess.getInstance(UpdateProduct.this);
            databaseAccess.update(products[0]);
            return null;
        }
    }
}
