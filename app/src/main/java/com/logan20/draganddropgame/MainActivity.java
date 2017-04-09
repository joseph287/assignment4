package com.logan20.draganddropgame;


/*Outside codes were used in this project. jstjoy creation*/

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String id;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String fname = getIntent().getStringExtra("fname");//first name
        String lname = getIntent().getStringExtra("lname");//last name
        username = fname+lname;
        id = getIntent().getStringExtra("id");//id of the user (needed for log writing)

        Toast.makeText(this, "Welcome "+fname+" "+lname, Toast.LENGTH_SHORT).show();
    }
    public void play(View view) {
        startActivity(new Intent(MainActivity.this, PlayActivity.class).putExtra("id",id).putExtra("username",username));
    }

    public void exit(View view) {
        //verify from user before exiting
        new AlertDialog.Builder(MainActivity.this)
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

    public void instructions(View view) {
        //method used for viewing gameplay instructions
        startActivity(new Intent(MainActivity.this,InstructionActivity.class));
    }

    public void leaderboards(View view) {
        //method used for viewing leaderboards
        startActivity(new Intent(MainActivity.this,LeaderboardsActivity.class));
    }
}
