package com.example.cw1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    private int background_colour_index = 0 ;
    private float playback_speed = 1f;
    private float temp_speed = 1f;
    private String result;
    private boolean backed;

    // 2 colour arrays ordered the same so the name and hex value are easy to access
    private final String[] hex_colours = {"#3787A6","#FFD31414", "#FFE8C808","#FFEA0762","#000000" };
    private String[] colours  = {"Blue","Red", "Yellow","Pink","Black" };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Makes sure the activity doesn't change on rotation
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        backed = true;

        ImageView right = findViewById(R.id.right_arr);
        ImageView left = findViewById(R.id.left_arr);
        Button exit = findViewById(R.id.st_back);
        TextView playback_speed_value = findViewById(R.id.ps_value);
        SeekBar playback_speed_bar = findViewById(R.id.psBar);

        playback_speed = getIntent().getExtras().getFloat("playback_speed");
        temp_speed = playback_speed;

        //When returning to settings, playback values would represent the last change.
        playback_speed_bar.setProgress((int)(playback_speed * 10));
        playback_speed_value.setText(String.valueOf(playback_speed) + "x");



        Intent PBintent = new Intent(Settings.this,PlayService.class);


        playback_speed_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // alters playback speed each time thumb is dragged
                temp_speed = progress / 10f;
                playback_speed_value.setText(String.valueOf(temp_speed) + "x");

                Bundle bundle = new Bundle();
                bundle.putString("action","pbChange");
                bundle.putFloat("playback_speed", temp_speed);
                PBintent.putExtras(bundle);
                startService(PBintent);



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // on returning to settings, the setting page assigns the correct bg colour
        ConstraintLayout main = findViewById(R.id.SettingsLayout);
        result = getIntent().getExtras().getString("background_colour");
        for (int i = 0; i < hex_colours.length; i++) {
            if(result.equals(hex_colours[i])){
                background_colour_index = i;
                applyColour();
            }

        }

        // on pressing the save and exit button (not pressing the back button) , the program sends relevant /current settings out
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playback_speed = temp_speed;
                backed = false;


                Intent intent = new Intent(Settings.this,MainActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("hex", hex_colours[background_colour_index]);
                bundle1.putFloat("playback_speed", playback_speed);
                intent.putExtras(bundle1);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }
        });

        // left and right arrows navigate through bg colour list
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(background_colour_index == hex_colours.length - 1){
                    background_colour_index = 0;
                }else{
                  background_colour_index += 1;
                }
                applyColour();
            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(background_colour_index == 0){
                    background_colour_index = hex_colours.length - 1;
                }else{
                    background_colour_index -= 1;
                }
                applyColour();
            }
        });
    }

    // if back is pressed, none of the settings are saved.
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(backed){
            Intent PBintent = new Intent(Settings.this,PlayService.class);
            backed = false;
            Bundle bundle = new Bundle();
            bundle.putString("action","pbChange");
            bundle.putFloat("playback_speed", playback_speed);
            PBintent.putExtras(bundle);
            startService(PBintent);
        }

    }

    public void applyColour(){
        ConstraintLayout main = findViewById(R.id.SettingsLayout);
        TextView bg = findViewById(R.id.bg_value);
        bg.setText(colours[background_colour_index]);
        main.setBackgroundColor(Color.parseColor(hex_colours[background_colour_index]));
    }

}