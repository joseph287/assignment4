package com.logan20.draganddropgame;

/*Outside codes were used in this project. jstjoy creation*/

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.game.Game;
import com.shephertz.app42.paas.sdk.android.game.ScoreBoardService;

import java.util.ArrayList;
import java.util.List;

public class ViewHighscoreChart extends AppCompatActivity {

    private TextView tv;
    private int level;
    private int stage;
    private String username;
    private LineChart lc;
    private ProgressBar load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_highscore_chart);
        App42API.initialize(this,getString(R.string.apiKey),getString(R.string.secretKey));
        tv = ((TextView)findViewById(R.id.tv_gameName));
        level =1;
        stage = 1;
        username = getIntent().getStringExtra("username");
        lc = (LineChart)findViewById(R.id.lc_chart);
        load = (ProgressBar)findViewById(R.id.pb_load);
        loadLeaderboard();
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

    private void loadLeaderboard() {
        tv.setText("Level: "+level + " Stage: "+stage);
        String gameName = "Assignment4_"+level+"_"+stage;
        lc.setVisibility(View.INVISIBLE);
        load.setVisibility(View.VISIBLE);
        ScoreBoardService scoreBoardService = App42API.buildScoreBoardService();
        scoreBoardService.getScoresByUser(gameName,username, new App42CallBack() {
            public void onSuccess(Object response)
            {
                Game game = (Game)response;

                List<Entry> entryList = new ArrayList<>();

                for(int i = 0;i<game.getScoreList().size();i++)
                {
                    entryList.add(new Entry(i+1,game.getScoreList().get(i).getValue().floatValue()));
                }
                final LineDataSet lineDataSet = new LineDataSet(entryList,"Scores");
                lc.post(new Runnable() {
                    @Override
                    public void run() {
                        lc.setData(new LineData(lineDataSet));
                        lc.invalidate();
                        lc.setVisibility(View.VISIBLE);
                        load.setVisibility(View.INVISIBLE);
                    }
                });

            }
            public void onException(Exception ex)
            {
                System.out.println("Exception Message"+ex.getMessage());
            }
        });


    }

}
