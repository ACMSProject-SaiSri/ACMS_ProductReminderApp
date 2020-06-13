package com.e.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.Document;
import com.amazonaws.mobileconnectors.dynamodbv2.document.datatype.DynamoDBEntry;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    String flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login=findViewById(R.id.btnlogin);
        TextView signup=findViewById(R.id.btnsignup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email_et=findViewById(R.id.etemail);
                EditText passwd_et=findViewById(R.id.etpass);
                String emails=email_et.getText().toString();
                String passwds=passwd_et.getText().toString();

                flag=authenticate(emails,passwds);
                if(!flag.equals("")){
                    System.out.println(flag);
                    Intent intent=new Intent(MainActivity.this, ListActivity.class);
                    intent.putExtra("name",flag);
                    startActivity(intent);
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Invalid credentials.....", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
                    toast.show();
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
            }
        });
    }


    String authenticate(String email,String passwd){
//        GetUserAsyncTask task = new GetUserAsyncTask();
        Document user = new Document();
        user.put("email_id",email);
        user.put("password",passwd);
//        task.execute(user);

        //should validate the password.for now just sending some dummy name
        return "swathi";
    }


    private class GetUserAsyncTask extends AsyncTask<Document, Void, Void> {
        @Override
        protected Void doInBackground(Document... users) {
            ProductsTableDatabaseAccess databaseAccess = ProductsTableDatabaseAccess.getInstance(MainActivity.this);
            User userdet = databaseAccess.getUser(users[0].get("email_id").toString());
            System.out.println(userdet);
            return null;
        }
    }
}
