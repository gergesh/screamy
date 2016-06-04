package com.screamy;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    private ImageButton toggleButton;
    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    TextView speedTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        speedTextView = (TextView) findViewById(R.id.speed_text_view);
        toggleButton = (ImageButton) findViewById(R.id.toggle_image_button);
        if (toggleButton != null) {
            toggleButton.setOnClickListener(this);
        }
        if (isScreamyServiceRunning()) {
            toggleButton.setImageResource(R.drawable.stop_button);
        } else {
            toggleButton.setImageResource(R.drawable.play_arrow);
        }
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        senSensorManager.unregisterListener(this);
        super.onPause();
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
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                speedTextView.append("" + speed);
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
