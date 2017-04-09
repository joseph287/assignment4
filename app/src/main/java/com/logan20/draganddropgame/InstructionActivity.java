package com.logan20.draganddropgame;


/*Outside codes were used in this project. jstjoy creation*/

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//This class is used to show a textview with the instructions to play the game
public class InstructionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
    }

    public void back(View view) {
        super.onBackPressed();
    }
}
