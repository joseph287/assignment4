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

public class ParentViewStatistics extends AppCompatActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view_statistics);
        db = new DatabaseHelper(this).getReadableDatabase();

    }

    public void search(View view) {
        String email = ((EditText)findViewById(R.id.et_email)).getText().toString();
        String fname = ((EditText)findViewById(R.id.et_fName)).getText().toString();
        String lname = ((EditText)findViewById(R.id.et_lName)).getText().toString();

        if (email.isEmpty() || fname.isEmpty()|| lname.isEmpty()){
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalid Email Address", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] projection = {DatabaseHelper.DatabaseUserEntry.FIRST_NAME, DatabaseHelper.DatabaseUserEntry.LAST_NAME,DatabaseHelper.DatabaseUserEntry._ID, DatabaseHelper.DatabaseUserEntry.TYPE};
        String selection= DatabaseHelper.DatabaseUserEntry.EMAIL + "= ? AND "+ DatabaseHelper.DatabaseUserEntry.FIRST_NAME+" = ? AND "+ DatabaseHelper.DatabaseUserEntry.LAST_NAME+" =?";
        Cursor cursor = db.query(DatabaseHelper.DatabaseUserEntry.TABLE_NAME,projection,selection, new String[]{email,fname,lname},null,null,null);
        if(cursor.getCount()<1){
            Toast.makeText(this, "No user exists with the given credentials", Toast.LENGTH_SHORT).show();
            cursor.close();
        }else{
            cursor.moveToFirst();
            int type = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseUserEntry.TYPE)));
            if (type==1){
                Toast.makeText(this, "The specified user is not a student!", Toast.LENGTH_SHORT).show();
                return;
            }

            String ID = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseUserEntry._ID));
            String username = cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseUserEntry.FIRST_NAME))+cursor.getString(cursor.getColumnIndex(DatabaseHelper.DatabaseUserEntry.LAST_NAME));
            //get the user's info from sharedpreferences

            String data = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE).getString(ID,null);
            if (data==null){
                Toast.makeText(this, "User does not have any data to display", Toast.LENGTH_SHORT).show();
            }
            else{
                startActivity(new Intent(ParentViewStatistics.this,ViewHighscoreChart.class).putExtra("username",username));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
