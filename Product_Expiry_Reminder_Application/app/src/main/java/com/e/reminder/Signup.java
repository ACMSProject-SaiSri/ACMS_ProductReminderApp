package com.e.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;


import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Primitive;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Signup extends AppCompatActivity {
    EditText email;
    EditText passwd;
    EditText name;
    EditText number;
    String emails;
    String passwds;
    String names;
    String numbers;
    String flag;
    String TAG = "db operations:";

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); commented this line in order to disable back press
        //Write your code here

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Button reg = findViewById(R.id.btnsignupreg);
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = findViewById(R.id.etemailreg);
                passwd = findViewById(R.id.etpassreg);
                name = findViewById(R.id.etname);
                number = findViewById(R.id.etnumber);
                emails = email.getText().toString();
                passwds = passwd.getText().toString();
                names = name.getText().toString();
                numbers = number.getText().toString();
                flag = add_to_db(emails, passwds, names, numbers);
                if (!flag.equals("")) {
                    Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                    intent.putExtra("name", flag);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "oops...something went wrong", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }
            }
        });


    }

    String add_to_db(String email, String passwd, String name, String number) {
        User newUser = new User(name,passwd,number,email);
        CreateUserAsyncTask task = new CreateUserAsyncTask();
        task.execute(newUser);
        return name;
    }


    private class CreateUserAsyncTask extends AsyncTask<User, Void, Void> {
        @Override
        protected Void doInBackground(User... users) {
            UserTableDatabaseAccess databaseAccess = UserTableDatabaseAccess.getInstance(Signup.this);
            Log.d(TAG, "user document conent" + users[0].toString());
            databaseAccess.create_user(users[0]);

            return null;
        }
    }
}
