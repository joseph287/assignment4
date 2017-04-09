package com.logan20.draganddropgame;


/*Outside codes were used in this project. jstjoy creation*/

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.UserService;

public class RegisterActivity extends AppCompatActivity {
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = new DatabaseHelper(this).getWritableDatabase();
    }

    public void register(View view) {
        //get the necessary information
        String fName = ((EditText)findViewById(R.id.et_fName)).getText().toString();
        String lName = ((EditText)findViewById(R.id.et_lName)).getText().toString();
        String email = ((EditText)findViewById(R.id.et_email)).getText().toString().toLowerCase();
        String pwd = ((EditText)findViewById(R.id.et_password)).getText().toString();
        String pwdConf = ((EditText)findViewById(R.id.et_passwordConfirm)).getText().toString();
        int type =((RadioButton)findViewById(R.id.rb_parent)).isChecked()?1:2;

        //data validation
        if (fName.isEmpty() || lName.isEmpty() || email.isEmpty() || pwd.isEmpty() || pwdConf.isEmpty()){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        //password match validation
        if (!pwd.equals(pwdConf)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        //email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        //validation to make sure a user with that email doesn't exist
        String[] projection = {DatabaseHelper.DatabaseUserEntry.FIRST_NAME};
        String selection= DatabaseHelper.DatabaseUserEntry.EMAIL + "= ?";
        Cursor cursor = db.query(DatabaseHelper.DatabaseUserEntry.TABLE_NAME,projection,selection, new String[]{email},null,null,null);
        if(cursor.getCount()>0){
            Toast.makeText(this, "A user already exists with this email", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }

        //store values in database once all criteria is met
        ContentValues cv  = new ContentValues();
        cv.put(DatabaseHelper.DatabaseUserEntry.FIRST_NAME,fName);
        cv.put(DatabaseHelper.DatabaseUserEntry.LAST_NAME,lName);
        cv.put(DatabaseHelper.DatabaseUserEntry.EMAIL,email);
        cv.put(DatabaseHelper.DatabaseUserEntry.PASSWORD,pwd);
        cv.put(DatabaseHelper.DatabaseUserEntry.TYPE,String.valueOf(type));

        long id = db.insert(DatabaseHelper.DatabaseUserEntry.TABLE_NAME,null,cv);

        //if unsuccessful, prompt user
        if (id==-1){
            Toast.makeText(this, "An error has occured. Please try again later!", Toast.LENGTH_SHORT).show();
            return;
        }
        UserService userService = App42API.buildUserService();
        userService.createUser( fName+lName, pwd, email, new App42CallBack() {
            public void onSuccess(Object response){
                User user = (User)response;
                System.out.println("userName is " + user.getUserName());
                System.out.println("emailId is " + user.getEmail());
            }
            public void onException(Exception ex){
                System.out.println("Exception Message"+ex.getMessage());
            }});


        //prompt user if successful
        Toast.makeText(this, "Registration Success!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();//close database
    }
}
