package com.example.cw1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class SongScreen extends AppCompatActivity {

    private Float playback_speed;

    TextView progress;

    SeekBar progressBar;

    Handler h = new Handler(Looper.getMainLooper());

    MP3Player mp3Player = PlayService.mp3Player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_screen);

        //Makes sure the activity doesn't change on rotation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String songUri = getIntent().getExtras().getString("songName");
        String background_colour = getIntent().getExtras().getString("background_colour");
        playback_speed = getIntent().getExtras().getFloat("playback_speed");
        progressBar =findViewById(R.id.progressBar);

        progress = findViewById(R.id.progressNum);
        ConstraintLayout main = findViewById(R.id.SongLayout);
        main.setBackgroundColor(Color.parseColor(background_colour));

        //displays current song playing and the playback speed
        TextView name = findViewById(R.id.songName);
        name.setText(songUri);
        TextView playback = findViewById(R.id.speedLabel);
        playback.setText(String.valueOf(playback_speed) + "x");

        //shows current playback / progress of the song that is currently playing
        if(songUri.equals(mp3Player.getFilePath())) {
            h.post(updateRunnable);
        }else {
            progress.setText("xx:xx");
            progressBar.setProgress(0);
        }

        Button exit = findViewById(R.id.sc_back);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //starts mp3 player and playback display
        ImageButton plays = findViewById(R.id.playButton);
        plays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongScreen.this,PlayService.class);
                Bundle bundle = new Bundle();
                bundle.putString("songName",songUri);
                bundle.putFloat("playback_speed",playback_speed);
                bundle.putString("action","play");
                intent.putExtras(bundle);
                startService(intent);
                h.post(updateRunnable);

            }
        });

        ImageButton pauses = findViewById(R.id.pauseButton);
        pauses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongScreen.this,PlayService.class);
                Bundle bundle = new Bundle();
                bundle.putString("action","pause");
                intent.putExtras(bundle);
                startService(intent);
            }
        });

        ImageButton stops = findViewById(R.id.stopButton);
        stops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SongScreen.this,PlayService.class);
                stopService(intent);
            }
        });

    }

    // used to display playback via numbers and seekbar
    private Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            //converting to ms to seconds
            int pg = (mp3Player.getProgress() / 1000);
            int dn = (mp3Player.getDuration() / 1000);

            // very tedious way of displaying 3:08 instead of 3:8
            if(pg%60 < 10 && dn%60 < 10){
                progress.setText(String.valueOf(pg/60) + ":0" +String.valueOf(pg%60) + "/" +String.valueOf(dn/60) + ":0" +String.valueOf(dn%60));
            }else if(dn%60 < 10){
                progress.setText(String.valueOf(pg/60) + ":" +String.valueOf(pg%60) + "/" +String.valueOf(dn/60) + ":0" +String.valueOf(dn%60));
            }else if(pg%60 < 10){
                progress.setText(String.valueOf(pg/60) + ":0" +String.valueOf(pg%60) + "/" +String.valueOf(dn/60) + ":" +String.valueOf(dn%60));
            }else{
                progress.setText(String.valueOf(pg/60) + ":" +String.valueOf(pg%60) + "/" +String.valueOf(dn/60) + ":" +String.valueOf(dn%60));
            }

            // updating seek bar to display progress
            progressBar.setMax(mp3Player.getDuration());
            progressBar.setProgress(mp3Player.getProgress());

                // Scheduling itself to run again after a certain amount of time based on playback speed.
            h.postDelayed(this, (long) (1000 / playback_speed.floatValue()));
            }

    };




}