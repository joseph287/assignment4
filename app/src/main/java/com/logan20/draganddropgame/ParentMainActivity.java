package com.logan20.draganddropgame;


/*Outside codes were used in this project. jstjoy creation*/

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ParentMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_main);
        String fname = getIntent().getStringExtra("fname");
        String lname = getIntent().getStringExtra("lname");
        Toast.makeText(this, "Welcome "+fname+" "+lname, Toast.LENGTH_SHORT).show();

    }

    public void checkStatictics(View view) {
        startActivity(new Intent(ParentMainActivity.this, ParentViewStatistics.class));
    }

    public void exit(View view) {
        new AlertDialog.Builder(ParentMainActivity.this)
                .setTitle("Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no,null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {
        exit(null);
    }
}
