package com.logan20.draganddropgame;

/*Outside codes were used in this project. jstjoy creation*/

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.App42API;


public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        App42API.initialize(this,"6f6e482c6fd43c228758d796d456b3c731ece71940516d82ed50b8aaa268e1f5","2c76d5c4a15bd468e6bcff655c5388dd1343a7b9ae17f416d84628b7b9cc9648");

    }

    public void login(View view) {
        String emailAddress= ((EditText)findViewById(R.id.et_email)).getText().toString().toLowerCase(); //gets the email
        String password= ((EditText)findViewById(R.id.et_password)).getText().toString();//gets the password

        //this if statement validates input
        if (emailAddress.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()|| password.isEmpty()){
            Toast.makeText(this, "Invalid email/password", Toast.LENGTH_SHORT).show();
            return;
        }

        //get the first name, last name and ID of the user
        String[] projection = {DatabaseHelper.DatabaseUserEntry.FIRST_NAME,DatabaseHelper.DatabaseUserEntry.LAST_NAME, DatabaseHelper.DatabaseUserEntry.TYPE, DatabaseHelper.DatabaseUserEntry._ID};

        //criteria necessary to select the correct user
        String selection= DatabaseHelper.DatabaseUserEntry.EMAIL + "= ? AND "+ DatabaseHelper.DatabaseUserEntry.PASSWORD+" = ?";

        //load the database
        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();

        //Check to see if user exists, if user doesn't exist, prompt that the credentials are incorrect
        Cursor cursor = db.query(DatabaseHelper.DatabaseUserEntry.TABLE_NAME,projection,selection, new String[]{emailAddress,password},null,null,null);
        if(cursor.getCount()<1){
            Toast.makeText(this, "Invalid email/password", Toast.LENGTH_SHORT).show();
            return;
        }

        while (cursor.moveToNext()){
            //get the first name
            String fname  = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseUserEntry.FIRST_NAME));

            //get the last name
            String lname  = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseUserEntry.LAST_NAME));

            //get the ID
            String ID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseUserEntry._ID));

            //get the user type (necessary to login a student or a parent)
            int type = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseUserEntry.TYPE)));

            switch (type){
                case 1://a parent is logging in
                    startActivity(new Intent(LoginActivity.this,ParentMainActivity.class).putExtra("fname",fname).putExtra("lname",lname).putExtra("id",ID));
                    break;
                case 2://a student is logging in
                    startActivity(new Intent(LoginActivity.this,MainActivity.class).putExtra("fname",fname).putExtra("lname",lname).putExtra("id",ID));
                    break;
            }
        }
        cursor.close();
    }

    public void register(View view) {
        //open the registration activity if a new user wants to register
        startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
    }
}
