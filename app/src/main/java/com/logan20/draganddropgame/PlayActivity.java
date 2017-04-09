package com.logan20.draganddropgame;


/*Outside codes were used in this project. jstjoy creation*/

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.game.Game;
import com.shephertz.app42.paas.sdk.android.game.GameService;
import com.shephertz.app42.paas.sdk.android.game.ScoreBoardService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.Arrays;

public class PlayActivity extends AppCompatActivity {

    private final int MARGINSIZE = 3;
    private int currLevel;
    private JSONArray data;
    private GridLayout gl;
    private LinearLayout ll;
    private int w,h;
    private int currY;
    private int currX;
    private int destY;
    private int destX;
    private SoundPool sp;
    private int[]sounds;
    private MediaPlayer mp;
    private Chronometer chronometer;
    private String userID;
    private int level;
    private int stage;
    private String username;
    private int timeGrace;
    private int multiplier;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        currLevel = -1;
        gl = (GridLayout)findViewById(R.id.mainPanel);
        ll =((LinearLayout)findViewById(R.id.viewPanel));
        mp = MediaPlayer.create(this,R.raw.bkg);
        mp.setLooping(true);
        mp.start();
        userID = getIntent().getStringExtra("id");
        username = getIntent().getStringExtra("username");
        loadData();
        loadSounds();
        loadNextLevel();
        setDragAndDrop((ImageView)findViewById(R.id.arrow_left));
        setDragAndDrop((ImageView)findViewById(R.id.arrow_right));
        setDragAndDrop((ImageView)findViewById(R.id.arrow_up));
        setDragAndDrop((ImageView)findViewById(R.id.arrow_down));
        setDragAccept(findViewById(R.id.hsv_panel));
    }

    @Override
    protected void onDestroy() {
        sp.release();
        mp.stop();
        super.onDestroy();
    }

    private void loadSounds() {
        //for compatibility, use the proper API
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes attrs = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            sp = new SoundPool.Builder()
                    .setMaxStreams(10)
                    .setAudioAttributes(attrs)
                    .build();
        }
        else{
            sp = new SoundPool(10, AudioManager.STREAM_MUSIC,0);
        }

        //sounds for in game
        sounds = new int[]{
                sp.load(this,R.raw.action_fail,1),
                sp.load(this,R.raw.action_1,1),
                sp.load(this,R.raw.action_2,1),
                sp.load(this,R.raw.action_success,1)
        };
    }

    private void setDragAndDrop(ImageView iv) {
        //sets the drag and drop on the view
        iv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String direction = view.getTag().toString();
                ClipData.Item item = new ClipData.Item(direction);
                ClipData data = new ClipData(direction,new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN},item);
                View.DragShadowBuilder shadow = new DragShadow((ImageView)view);
                w=view.getWidth();
                h= view.getHeight();

                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
                    view.startDragAndDrop(data,shadow,null,0);
                }
                else{
                    view.startDrag(data,shadow,null,0);
                }
                sp.play(sounds[1],1.0f,1.0f,1,0,1.0f);
                return true;

            }
        });
    }

    private void loadNextLevel() {
        gl.removeAllViews();//clear the maze
        ll.removeAllViews();//clear the user's answers
        currLevel++;
        if (currLevel<data.length()){
            try {
                //restart the timer
                chronometer = new Chronometer(this);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();

                //load data and get necessary variables
                JSONObject obj = data.getJSONObject(currLevel);
                currY = obj.getInt("startRow");
                currX = obj.getInt("startCol");
                destX = obj.getInt("endCol");
                destY = obj.getInt("endRow");
                level = obj.getInt("level");
                stage = obj.getInt("stage");
                timeGrace = obj.getInt("timeGrace");
                multiplier = obj.getInt("multiplier");
                final int rowCount = obj.getInt("numRow");
                final int colCount = obj.getInt("numCol");
                final String levelLayout = obj.getString("data");
                gl.setRowCount(rowCount);
                gl.setColumnCount(colCount);

                //prompt the user of the level and stage
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PlayActivity.this, "Level: "+level+" Stage: "+stage, Toast.LENGTH_SHORT).show();
                    }
                });
                //this will change the background if the level is 2
                changeBackgroundIfNecessary(level);

                //build the map
                gl.post(new Runnable() {
                    @Override
                    public void run() {
                        int glWidth = gl.getWidth();
                        int glHeight = gl.getHeight();
                        for (int a=0;a<rowCount;a++){
                            for(int b=0;b<colCount;b++){
                                ImageView v = new ImageView(PlayActivity.this);
                                v.setBackgroundColor(Color.TRANSPARENT);
                                if (levelLayout.charAt(a*colCount+b)=='1'){
                                    v.setBackgroundColor(Color.WHITE);
                                }
                                if (a*colCount+b ==currY*colCount+currX){
                                    v.setBackgroundColor(Color.RED);
                                }
                                if (a*colCount+b ==destY*colCount+destX){
                                    v.setBackgroundColor(Color.GREEN);
                                }
                                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                                params.width = glWidth/colCount - 2*MARGINSIZE;
                                params.height = glHeight / rowCount - 2*MARGINSIZE;
                                params.setMargins(MARGINSIZE,MARGINSIZE,MARGINSIZE,MARGINSIZE);

                                gl.addView(v,params);
                            }
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            //when there are no more levels, exit the game
            Toast.makeText(this, "Game completed! No more levels", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void changeBackgroundIfNecessary(int level) {
        //changes the default background once the level is 2
        if (level==2){
            final View v =findViewById(R.id.bkg_panel);
            v.post(new Runnable() {
                @Override
                public void run() {
                    v.setBackgroundResource(R.drawable.game_background);
                }
            });
        }
    }

    private void loadData() {
        //loads information from JSON file stored in assets
        try {
            BufferedReader in= new BufferedReader(new InputStreamReader(getAssets().open("levels.json")));
            StringBuilder sb = new StringBuilder();
            String line = in.readLine();
            while(line!=null){
                sb.append(line);
                line = in.readLine();
            }
            in.close();
            data =new JSONArray(sb.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        //verify the user wants to exit before exiting
        new AlertDialog.Builder(PlayActivity.this)
            .setTitle("Exit?")
            .setMessage("Are you sure you want to exit?")
            .setNegativeButton(android.R.string.no,null)
            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    PlayActivity.super.onBackPressed();
                }
            })
            .show();
    }

    public void setDragAccept(final View dragAccept) {
        //sets the view as a drag "acceptor"
        dragAccept.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {
                switch (dragEvent.getAction()){
                    case DragEvent.ACTION_DRAG_STARTED:
                        if (dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)){
                            view.setBackgroundColor(Color.parseColor("#88ffffff"));
                            view.invalidate();
                            return true;
                        }
                        return false;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        view.setBackgroundColor(Color.parseColor("#55ffffff"));
                        view.invalidate();
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        view.setBackgroundColor(Color.parseColor("#88ffffff"));
                        view.invalidate();
                        return true;
                    case DragEvent.ACTION_DROP:
                        ClipData.Item item = dragEvent.getClipData().getItemAt(0);
                        Item i = new Item(PlayActivity.this);
                        i.setTag(item.getText().toString());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(w,h);
                        params.setMargins(5,5,5,5);
                        switch(i.getTag().toString()){
                            case "up":
                                i.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrow_up));
                                break;
                            case "down":
                                i.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrow_down));
                                break;
                            case "left":
                                i.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrow_left));
                                break;
                            case "right":
                                i.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.arrow_right));
                                break;
                        }
                        ll.addView(i,params);
                        view.invalidate();
                        sp.play(sounds[2],1.0f,1.0f,1,0,1.0f);
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        view.setBackgroundColor(Color.parseColor("#88ffffff"));
                        view.invalidate();
                        return true;
                    default:
                        break;

                }
                return false;
            }
        });
    }

    public void checkSol(View view) {
        //this executes when the user presses the 'play' button
        final ImageView iv = (ImageView) gl.getChildAt(currY*gl.getColumnCount() + currX);
        if (iv==null){//fail if the view doesn't exist
            failure();
            return;
        }
        //once view exists, set it's color to blue to simulate a person walking
        iv.post(new Runnable() {
            @Override
            public void run() {
                iv.setBackgroundColor(Color.BLUE);
            }
        });

        //if the user reaches the end, call the success function
        if (currY*gl.getColumnCount() + currX == destY*gl.getColumnCount()+destX){
            success();
            return;
        }

        Item item = (Item)ll.getChildAt(0);
        if (item==null){//fail if the view doesn't exist
            failure();
            return;
        }
        //calculate the next direction from data given
        String direction = item.getTag().toString();
        int nX=currX,nY=currY;
        switch (direction){
            case "up":
                nY--;
                break;
            case "down":
                nY++;
                break;
            case "left":
                nX--;
                break;
            case "right":
                nX++;
                break;
        }

        //get the next position
        ImageView next = (ImageView) gl.getChildAt(nY*gl.getColumnCount() + nX);

        //if next is a valid path, proceed
        if (next!=null){
            int nextColor = ((ColorDrawable)next.getBackground()).getColor();
            if (nextColor ==Color.WHITE || nextColor==Color.GREEN){//only proceed if the color is white or green
                currX=nX;
                currY=nY;
            }
            else{
                //if the path is invalid, remove the first child in the layout
                ll.post(new Runnable() {
                    @Override
                    public void run() {
                        ll.removeViewAt(0);
                    }
                });
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(200);//thread sleep for animation purposes
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkSol(null);
                }
            }).start();
        }
        else{
            //run the "failure" method if the user goes on an "invalid" block
            failure();
        }
    }

    private void success() {
        sp.play(sounds[3],1f,1f,1,0,1f);//play a sound if the user completes the puzzle

        //stop the timer and calculate elapsed time
        final long elapsed = ((SystemClock.elapsedRealtime() - chronometer.getBase())/1000);
        chronometer.stop();

        setHighScoreIfNecessary(elapsed);

        App42API.initialize(this,getString(R.string.apiKey),getString(R.string.secretKey));

        String gameName = "Assignment4_"+level+"_"+stage;
        String description = "Game description for the assignment";

        GameService gameService = App42API.buildGameService();
        gameService.createGame(gameName, description, new App42CallBack() {
            public void onSuccess(Object response)
            {
            }
            public void onException(Exception ex)
            {
                ex.printStackTrace();
            }
        });

        BigDecimal score = new BigDecimal((timeGrace-elapsed)*multiplier);

        if (score.intValue()>0){
            ScoreBoardService scoreBoardService = App42API.buildScoreBoardService();
            scoreBoardService.saveUserScore(gameName, username, score, new App42CallBack() {
                @Override
                public void onSuccess(Object o) {
                    Game game = (Game)o;
                    System.out.println("Game Name is : "+game.getName());
                    for(int i = 0;i<game.getScoreList().size();i++)
                    {
                        System.out.println("userName is : " + game.getScoreList().get(i).getUserName());
                        System.out.println("score is : " + game.getScoreList().get(i).getValue());
                        System.out.println("scoreId is : " + game.getScoreList().get(i).getScoreId());
                        System.out.println("Created On is  :"+game.getScoreList().get(i).getCreatedOn());
                    }
                }

                @Override
                public void onException(Exception e) {

                }
            });
        }



        //prompt the user that level's complete and give option to exit or proceed
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(PlayActivity.this)
                        .setTitle("Success")
                        .setMessage("On to the next level! You took "+elapsed+" seconds")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadNextLevel();
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }

    private void setHighScoreIfNecessary(long elapsed) {
        //store the user's highscore in shared preferences
        final int arr[] = new int[]{9999,9999,9999,9999,9999,9999};
        SharedPreferences sp = getSharedPreferences(getString(R.string.app_name),MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        String userData = sp.getString(userID,null);

        if (userData!=null){
            String[]tokens =userData.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
            for (int a=0;a<arr.length;a++){
                arr[a] = Integer.parseInt(tokens[a]);
            }
        }
        int currIdx = (((level-1) * 3)+stage)-1;
        if (elapsed<arr[currIdx]){
            arr[currIdx] =(int) elapsed;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PlayActivity.this, "Congrats! New highscore", Toast.LENGTH_SHORT).show();
                }
            });
        }

        edit.putString(userID, Arrays.toString(arr));
        edit.apply();

    }

    private void failure() {
        sp.play(sounds[0],1.0f,1.0f,1,0,1.0f);//play sound
        chronometer.stop();//stop timer

        //prompt user that hey failed level and give option to retry or exit
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(PlayActivity.this)
                        .setTitle("Fail")
                        .setMessage("Level failed")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                currLevel--;
                                loadNextLevel();
                            }
                        })
                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        });
    }
}
