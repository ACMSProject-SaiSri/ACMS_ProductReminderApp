package com.e.reminder;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;

import timber.log.Timber;

public class ListActivity extends AppCompatActivity {
    private String TAG = "Listing_Page";
    private ListView listView;
    private SearchView searchView;
    private Spinner categorySpinner;
    private String[] distinctCategories;

    private AsyncTask searchTask = new SearchForItemsAsyncTask();

    ProductAdapter adapter;
    ArrayAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listview);

        categorySpinner = findViewById(R.id.categorySpinner);

        searchTask.execute(new String[]{""});

//        Intent intent = getIntent();
//        String name =intent.getStringExtra("name");
//        System.out.println("name: "+name);
//        Toast toast = Toast.makeText(getApplicationContext(), "Welcome.."+name, Toast.LENGTH_SHORT);
//        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
//        toast.show();

        Button buttonAddProduct = findViewById(R.id.btn_add);

        buttonAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked");
                Intent intent = new Intent(ListActivity.this, AddProduct.class);
                startActivity(intent);
            }
        });
    }

    private class SearchForItemsAsyncTask extends AsyncTask<String, Void, ArrayList<Product>> {
        @Override
        protected ArrayList<Product> doInBackground(String... params) {
            ProductsTableDatabaseAccess productsTableDatabaseAccess = ProductsTableDatabaseAccess.getInstance(ListActivity.this);
            Timber.d("databases content : %s", productsTableDatabaseAccess.getAllContents().toString());
            return new ArrayList<>(productsTableDatabaseAccess.getAllContents());
        }


        @Override
        protected void onPostExecute(ArrayList<Product> productList) {
            ListActivity.this.distinctCategories = getDistinctCategories(productList);
            ListActivity.this.adapter = new ProductAdapter(ListActivity.this, productList);
            ListActivity.this.categoryAdapter = new ArrayAdapter<>(ListActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, ListActivity.this.distinctCategories);

            ListActivity.this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Product product = (Product) ListActivity.this.listView.getItemAtPosition(i);
                    Intent intent = new Intent(ListActivity.this, ProductDetails.class);
                    intent.putExtra("productname", product.getName());
                    intent.putExtra("category", product.getCategory());
                    intent.putExtra("expdate", product.getExpiryDate());
                    intent.putExtra("id", product.getItemid());
                    startActivity(intent);
                }
            });

            ListActivity.this.categorySpinner.setAdapter(ListActivity.this.categoryAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long itemID) {
                    if (position == 0) {
                        Timber.d("category reset invoked");
                        ListActivity.this.adapter.doCategoryFilter("", ListActivity.this.searchView.getQuery());
                    } else if (position >= 1 && position < ListActivity.this.distinctCategories.length) {
                        String selectedCategory = ListActivity.this.distinctCategories[position];
                        ListActivity.this.adapter.doCategoryFilter(selectedCategory, ListActivity.this.searchView.getQuery());
                    } else {
                        Toast.makeText(ListActivity.this, "Selected Category Does not Exist!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            ListActivity.this.listView.setAdapter(ListActivity.this.adapter);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    adapter.getFilter().filter(query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
    }

    private String[] getDistinctCategories(ArrayList<Product> productList) {
        HashSet<String> categories = new HashSet<>();
        for (Product product : productList) {
            categories.add(product.getCategory().toLowerCase());
        }

        ArrayList<String> tempList = new ArrayList<>(categories);
        tempList.add(0, "Categories");

        String[] array = new String[tempList.size()];
        tempList.toArray(array);
        return array;
    }
}
