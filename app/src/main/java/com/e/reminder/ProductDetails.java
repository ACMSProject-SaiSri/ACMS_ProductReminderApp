package com.e.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.e.reminder.R;

public class ProductDetails extends AppCompatActivity {
    TextView productname,category,details,expiry;
    String productname_val,productcategory_val,productexpdate_val,productid_val;
    Product newProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);



        productname = (TextView) findViewById(R.id.prod_name);
        category = (TextView) findViewById(R.id.prod_category);
        expiry = (TextView) findViewById(R.id.exp_date);

        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            productname_val = bundle.getString("productname");
            productcategory_val = bundle.getString("category");
            productexpdate_val = bundle.getString("expdate");
            productid_val = bundle.getString("id");
            newProduct = new Product(productid_val,productname_val,productcategory_val,productexpdate_val);
            productname.setText(bundle.getString("productname"));
            category.setText(bundle.getString("category"));
            expiry.setText(bundle.getString("expdate"));
        }

        Button buttonUpdateProduct = findViewById(R.id.btn_update);

        buttonUpdateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("clicked");
                Intent intent = new Intent(ProductDetails.this, UpdateProduct.class);
                intent.putExtra("name",productname_val);
                intent.putExtra("category",productcategory_val);
                intent.putExtra("expiry_date",productexpdate_val);
                intent.putExtra("id",productid_val);
                startActivity(intent);

            }
        });
        Button buttonDeleteProduct = findViewById(R.id.btn_delete);

        buttonDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog diaBox = AskOption();
                diaBox.show();

            }
        });


    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to delete "+productname_val)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        DeleteItemAsyncTask task = new DeleteItemAsyncTask();
                        task.execute(newProduct);
                        Intent intent = new Intent(ProductDetails.this, AddProduct.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();

        return myQuittingDialogBox;
    }

    private class DeleteItemAsyncTask extends AsyncTask<Product, Void, Void> {
        @Override
        protected Void doInBackground(Product... products) {
            ProductsTableDatabaseAccess databaseAccess = ProductsTableDatabaseAccess.getInstance(ProductDetails.this);
            databaseAccess.delete(products[0]);
            return null;
        }
    }
}
