package com.logan20.draganddropgame;

/*Outside codes were used in this project. jstjoy creation*/

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.game.Game;
import com.shephertz.app42.paas.sdk.android.game.ScoreBoardService;

import java.util.ArrayList;
import java.util.Locale;

//This activity is used to load the leaderboards
public class LeaderboardsActivity extends AppCompatActivity {

    private int level;
    private int stage;
    private TextView tv;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboards);
        App42API.initialize(this,getString(R.string.apiKey),getString(R.string.secretKey));
        level =1;
        stage =1;
        tv = ((TextView)findViewById(R.id.tv_gameName));
        lv  = ((ListView)findViewById(R.id.lv_leaderboard));
        loadLeaderboard();
    }

    private void loadLeaderboard() {
        lv.setVisibility(View.INVISIBLE);
        tv.setText("Level: "+level + " Stage: "+stage);
        String gameName = "Assignment4_"+level+"_"+stage;
        final ArrayList<String> data = new ArrayList<>();

        ScoreBoardService scoreBoardService = App42API.buildScoreBoardService();
        scoreBoardService.getTopRankings(gameName,new App42CallBack() {
            public void onSuccess(Object response)
            {
                Game game = (Game)response;
                ArrayList<Game.Score> scores = game.getScoreList();
                System.out.println("Game Name is : "+game.getName());
                for(int i = 0;i<game.getScoreList().size();i++)
                {
                    String format = String.format(Locale.getDefault(),"Score: %8s %10s Player: %s",String.valueOf(scores.get(i).getValue()),"",scores.get(i).getUserName());
                    data.add(format);
                }
                lv.post(new Runnable() {
                    @Override
                    public void run() {
                        lv.setVisibility(View.VISIBLE);
                        lv.setAdapter(new ArrayAdapter<>(LeaderboardsActivity.this,android.R.layout.simple_list_item_1,data));
                    }
                });
            }
            public void onException(Exception ex)
            {
                System.out.println("Exception Message"+ex.getMessage());
            }
        });

    }

    public void moveLeft(View view) {
        stage--;
        if (stage<1){
            stage=3;
            if (level==1){
                level=2;
            }
            else{
                level=1;
            }
        }
        loadLeaderboard();
    }

    public void moveRight(View view) {
        stage++;
        if (stage>3){
            stage=1;
            if (level==2){
                level=1;
            }
            else{
                level=2;
            }
        }
        loadLeaderboard();
    }
}
