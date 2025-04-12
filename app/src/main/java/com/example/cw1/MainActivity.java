package com.example.cw1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {


    public static final int SETTING_CODE = 1;

    private String background_colour = "#3787A6";
    private Float playback_speed = 1f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Makes sure the activity doesn't change on rotation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //enumerates music files in a listview
        final ListView lv = findViewById(R.id.listView);
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.IS_MUSIC + "!= 0", null, null);

        lv.setAdapter(new SimpleCursorAdapter(this,R.layout.list_item, cursor,new String[] { MediaStore.Audio.Media.DATA}, new int[] { R.id.Name }));

        //
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() { public
        void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
            Cursor c = (Cursor) lv.getItemAtPosition(myItemInt);
            @SuppressLint("Range") String uri =
                    c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));


            Intent intent = new Intent(MainActivity.this,SongScreen.class);
            Bundle bundle = new Bundle();
            bundle.putString("songName",uri);
            bundle.putFloat("playback_speed", playback_speed);
            bundle.putString("background_colour", background_colour);
            intent.putExtras(bundle);
            startActivity(intent);



        }
        });

        Button toSetting = findViewById(R.id.settingButton);
        toSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                Bundle bundle = new Bundle();
                bundle.putString("background_colour", background_colour);
                bundle.putFloat("playback_speed", playback_speed);
                intent.putExtras(bundle);
                startActivityForResult(intent,SETTING_CODE);
            }
        });


    }

    //if the user exited properly from settings, the main activity appropriately handles it
    @Override
    protected void onActivityResult (int requestCode , int resultCode , @Nullable Intent
            data ) {
        super . onActivityResult ( requestCode , resultCode , data ) ;

        if( requestCode == SETTING_CODE ) {
            if( resultCode == Activity . RESULT_OK ) {
                background_colour = data.getExtras().getString("hex");
                playback_speed = data.getExtras().getFloat("playback_speed");
                RelativeLayout main = findViewById(R.id.ListLayout);
                main.setBackgroundColor(Color.parseColor(background_colour));

            }
        }
    }







}