package com.screamy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ImageButton toggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleButton = (ImageButton) findViewById(R.id.toggle_image_button);
        toggleButton.setOnClickListener(this);
        if (isScreamyServiceRunning()) {
            toggleButton.setImageResource(R.drawable.stop_button);
        } else {
            toggleButton.setImageResource(R.drawable.play_arrow);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
}
