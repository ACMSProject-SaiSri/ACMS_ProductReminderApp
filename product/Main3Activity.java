package com.example.producte;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Main3Activity extends AppCompatActivity {

    TextView productname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        productname = (TextView) findViewById(R.id.prod_name);
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null)
        {
            productname.setText(bundle.getString("productname"));
        }


    }
}
