package com.example.jyalarm;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class AlarmReceiverActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.alarm);

        Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
        stopAlarm.setOnTouchListener(new OnTouchListener() {
            
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Global.mMediaPlayer.stop();
                finish();
				return false;
			}
        });

        playSound(this, getAlarmUri());
        
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(myIntent);
    }

    private void playSound(Context context, Uri alert) 
    {
    	Global.mMediaPlayer = new MediaPlayer();
        try 
        {
        	Global.mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) 
            {
            	Global.mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            	Global.mMediaPlayer.prepare();
            	Global.mMediaPlayer.setLooping(true);
            	Global.mMediaPlayer.start();           	
            }
        } 
        catch (IOException e) 
        {
            System.out.println("OOPS");
        }
    }

    //Get an alarm sound. Try for an alarm. If none set, try notification, 
    //Otherwise, ringtone.
    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) 
        {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null)
            {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }
}
