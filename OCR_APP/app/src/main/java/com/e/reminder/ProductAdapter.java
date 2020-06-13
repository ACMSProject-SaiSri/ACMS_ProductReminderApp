package com.e.reminder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {
    private ArrayList<Product> productsList;
    private ArrayList<Product> filteredList;
    private ProductsFilter productsFilter;

    public ProductAdapter(@NonNull Context context, @NonNull ArrayList<Product> products) {
        super(context, 0, products);
        this.productsList = products;
        this.filteredList = products;

        getFilter();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Product getItem(int i) {
        return filteredList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Product product = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_layout, parent, false);
        }
        // Lookup view for data population
        TextView itemId = convertView.findViewById(R.id.itemid);
        TextView name = convertView.findViewById(R.id.name);
        TextView category = convertView.findViewById(R.id.category);
        TextView expiry = convertView.findViewById(R.id.expiry);
        // Populate the data into the template view using the data object
        itemId.setText(product.getItemid());
        name.setText(product.getName());
        category.setText(product.getCategory());
        expiry.setText(product.getExpiryDate());

        return convertView;
    }

    /**
     * Get custom filter
     *
     * @return filter
     */
    @Override
    public Filter getFilter() {
        if (productsFilter == null) {
            productsFilter = new ProductsFilter();
        }

        return productsFilter;
    }

    /**
     * Custom filter for friend list
     * Filter content in friend list according to the search text
     */
    private class ProductsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Product> tempList = new ArrayList<>();

                // search content in friend list
                for (Product product : productsList) {
                    if (product.filterBasedOnString(constraint)) {
                        tempList.add(product);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = productsList.size();
                filterResults.values = productsList;
            }

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredList = (ArrayList<Product>) results.values;
            notifyDataSetChanged();
        }
    }
}
