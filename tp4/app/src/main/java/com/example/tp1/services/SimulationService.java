package com.example.tp1.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SimulationService extends Service {
    private static final String TAG = "SimulationService";
    private Thread workerThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null; // service non lié
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service démarré");
        if (workerThread != null && workerThread.isAlive()) {
            return START_STICKY; // synchronisation déjà en cours
        }
        workerThread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                Log.d(TAG, "Synchronisation des critiques : " + (i + 1) + "/10");
                try { Thread.sleep(1000); }
                catch (InterruptedException e) { break; }
            }
            Log.d(TAG, "Synchronisation terminée");
            stopSelf();
        });
        workerThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (workerThread != null) workerThread.interrupt();
        super.onDestroy();
    }
}