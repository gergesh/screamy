package com.screamy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;

import java.io.IOException;

public class ScreamyService extends Service implements SensorEventListener, MediaPlayer.OnCompletionListener {
    private SensorManager senSensorManager;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;


    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private int userVolume;
    private AudioManager audioManager;
    private SharedPreferences sharedPreferences;

    public ScreamyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        senSensorManager.unregisterListener(this);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER && sharedPreferences.getBoolean("isDetectFall", true)) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                //float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;
                if (y <= 1.5 && y >= -1.5) {
                    if (x <= 1.5 && x >= -1.5) {
                        if (z <= 1.5 && z >= -1.5) {
                            if (!isPlaying) {
                                isPlaying = true;
                                if (sharedPreferences.getBoolean("isRecordedSound", false)) {
                                    try {
                                        mediaPlayer = new MediaPlayer();
                                        mediaPlayer.setDataSource(sharedPreferences.getString("recordPath", ""));
                                        mediaPlayer.prepare();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    mediaPlayer = MediaPlayer.create(this, R.raw.screamwilhm);
                                }
                                if (sharedPreferences.getBoolean("hearAlways", false)) {
                                    audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                                    userVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                                    if (userVolume < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2 + 1) {
                                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2 + 1, 0);
                                    }
                                }
                                mediaPlayer.start();
                                mediaPlayer.setOnCompletionListener(this);
                            }
                        }
                    }
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (sharedPreferences.getBoolean("hearAlways", false)) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, userVolume, 0);
        }
        isPlaying = false;
        mp.release();
        mp = null;
    }

}
