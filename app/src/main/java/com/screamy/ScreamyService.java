package com.screamy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ScreamyService extends Service {
    public ScreamyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
