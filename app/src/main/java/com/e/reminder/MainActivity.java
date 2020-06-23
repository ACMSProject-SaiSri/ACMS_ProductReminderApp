package com.e.reminder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button login = findViewById(R.id.btnlogin);
        TextView signup = findViewById(R.id.btnsignup);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText email_et = findViewById(R.id.etemail);
                EditText passwd_et = findViewById(R.id.etpass);
                String email = email_et.getText().toString();
                String passwd = passwd_et.getText().toString();

                GetUserAsyncTask task = new GetUserAsyncTask();
                Document user = new Document();
                user.put("email_id", email);
                user.put("password", passwd);
                User user1 = new User(user);
                task.execute(user1);

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
            }
        });
    }


    private class GetUserAsyncTask extends AsyncTask<User, Void, ArrayList<User>> {
        @Override
        protected ArrayList<User> doInBackground(User... users) {
            UserTableDatabaseAccess databaseAccess = UserTableDatabaseAccess.getInstance(MainActivity.this);
            User userdet = databaseAccess.getUser(users[0].getEmail());
            ArrayList<User> comp = new ArrayList();
            comp.add(0,userdet);
            comp.add(1,users[0]);
            return comp;
        }

        @Override
        protected void onPostExecute(ArrayList<User> comp) {
            if (comp.get(0).getPassword().equals(comp.get(1).getPassword())) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                intent.putExtra("name", comp.get(0).getUsername());
                startActivity(intent);
            } else {
                System.out.println("Invalid Credentials");
                AlertDialog diaBox = AskOption();
                diaBox.show();
            }
        }
    }

    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox = new AlertDialog.Builder(this)
                // set message, title, and icon
                .setTitle("Invalid Credentials!")
                .setMessage("Please try again")

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                    }

                })
                .create();

        return myQuittingDialogBox;
    }
}
