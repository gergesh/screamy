package com.screamy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements RecordSoundFragment.OnFragmentInteractionListener {
    MediaRecorder mediaRecorder;
    private ImageButton toggleButton;
    private boolean isRecording = false;
    SharedPreferences sharedPreferences;


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
        if (!isScreamyServiceRunning()) {
            startService(new Intent(this, ScreamyService.class));
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRecordSoundButtonPress() {
        Log.d("wnfjne", "start");
        mediaRecorder = null;
        mediaRecorder = new MediaRecorder();
        isRecording = true;
        try {
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("recordPath", Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/falling_sound.3gp");
            editor.commit();
            mediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/falling_sound.3gp");
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRecordSoundButtonRelease() {
        Log.d("wnfjne", "end");
        if (isRecording)
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isRecordedSound", true);
            editor.commit();
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
