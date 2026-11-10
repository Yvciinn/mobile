package com.example.tp1.work;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SyncWorker extends Worker {
    public static final String UNIQUE_NAME = "sync_critiques";
    public static final String KEY_PROGRESS = "progress";
    private static final String TAG = "SyncWorker";

    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {          // déjà exécuté sur un thread d'arrière-plan
        for (int i = 0; i < 10; i++) {
            if (isStopped()) return Result.failure();
            Log.d(TAG, "Synchronisation des critiques : " + (i + 1) + "/10");
            setProgressAsync(new Data.Builder().putInt(KEY_PROGRESS, (i + 1) * 10).build());
            try { Thread.sleep(1000); }
            catch (InterruptedException e) { return Result.failure(); }
        }
        return Result.success();
    }
}