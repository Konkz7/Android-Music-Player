package com.example.cw1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class PlayService extends Service {

    private String songUri;
    private String action;
    private Float playback_speed = 1f;

    private Notification notification;

    private static final String CHANNEL_ID = " MusicChannel ";
    private static final int PLAYING_ID = 1;
    private static final int PAUSED_ID = 2;



    public static MP3Player mp3Player = new MP3Player();

    public PlayService() {


    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel () ;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle =  intent.getExtras();
        action = bundle.getString("action");


        // if play button is pressed and theres no error
        if(mp3Player.getState() != MP3Player.MP3PlayerState.ERROR && action.equals("play") ){
            songUri = bundle.getString("songName");
            //if mp3player has been stopped or a different song is chosen, load a new song
            if(mp3Player.getState() == MP3Player.MP3PlayerState.STOPPED || !mp3Player.getFilePath().equals(songUri)) {
                mp3Player.stop();
                playback_speed = intent.getExtras().getFloat("playback_speed");
                mp3Player.load(songUri, playback_speed);


            }
            // resume song
            if(mp3Player.getState() == MP3Player.MP3PlayerState.PAUSED) {
                mp3Player.play();
                mp3Player.setPlaybackSpeed(playback_speed);

            }

            // "playing" notification
            notification = new NotificationCompat. Builder (this ,
                    CHANNEL_ID )
                    . setContentTitle ("MP3Player")
                    . setContentText ("Currently Playing:" + songUri)
                    . setSmallIcon ( R . drawable . note )
                    . build () ;
            startForeground ( PLAYING_ID , notification ) ;


        }
        // if pause button is pressed
        if(mp3Player.getState() == MP3Player.MP3PlayerState.PLAYING && action.equals("pause") ){
            mp3Player.pause();

            // paused notification
            notification = new NotificationCompat. Builder (this ,
                    CHANNEL_ID )
                    . setContentTitle ("MP3Player")
                    . setContentText ("Currently Paused:" + songUri)
                    . setSmallIcon ( R . drawable . note )
                    . build () ;
            startForeground ( PAUSED_ID , notification ) ;
        }
        //everytime playback speed is changed
        if(action.equals("pbChange")){
            playback_speed = intent.getExtras().getFloat("playback_speed");
            if (mp3Player.getState() != MP3Player.MP3PlayerState.PAUSED) {
                mp3Player.setPlaybackSpeed(playback_speed);
            }
        }


        return START_NOT_STICKY;
    }

    private void createNotificationChannel () {
        if ( Build. VERSION . SDK_INT >= Build . VERSION_CODES . O ) {
            CharSequence name = " Music Service ";
            String description = " Used for playing music ";
            int importance = NotificationManager. IMPORTANCE_LOW ;
            NotificationChannel channel = new NotificationChannel ( CHANNEL_ID , name
                    , importance ) ;
            channel . setDescription ( description ) ;
            NotificationManager notificationManager = getSystemService (
                    NotificationManager . class ) ;
            notificationManager . createNotificationChannel ( channel ) ;
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        mp3Player.stop();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


}