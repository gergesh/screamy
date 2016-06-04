package com.screamy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RecordSoundFragment.OnFragmentInteractionListener {
    MediaRecorder mediaRecorder;
    private ImageButton toggleButton;
    private boolean isRecording = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.container, new RecordSoundFragment(), "RecordSoundFragment");
            fragmentTransaction.commit();
        }
        mediaRecorder = new MediaRecorder();
        /*toggleButton = (ImageButton) findViewById(R.id.toggle_image_button);
        if (toggleButton != null) {
            toggleButton.setOnClickListener(this);
        }
        if (isScreamyServiceRunning()) {
            toggleButton.setImageResource(R.drawable.stop_button);
        } else {
            toggleButton.setImageResource(R.drawable.play_arrow);
        }*/
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toggle_image_button:
                if (isScreamyServiceRunning()) {
                    stopService(new Intent(this, ScreamyService.class));
                    toggleButton.setImageResource(R.drawable.play_arrow);

                } else {
                    startService(new Intent(this, ScreamyService.class));
                    toggleButton.setImageResource(R.drawable.stop_button);
                }
                break;
        }
    }

    private boolean isScreamyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.screamy.ScreamyService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRecordSoundButtonPress() {
        mediaRecorder = null;
        mediaRecorder = new MediaRecorder();
        isRecording = true;
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/falling_sound.3gp");


            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.start();
    }

    @Override
    public void onRecordSoundButtonRelease() {
        if (isRecording)
        {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
        } else {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    protected boolean hasMicrophone() {
        PackageManager pmanager = this.getPackageManager();
        return pmanager.hasSystemFeature(
                PackageManager.FEATURE_MICROPHONE);
    }
}
