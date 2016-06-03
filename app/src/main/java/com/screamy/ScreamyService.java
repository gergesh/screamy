package com.screamy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ScreamyService extends Service {
    public ScreamyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
