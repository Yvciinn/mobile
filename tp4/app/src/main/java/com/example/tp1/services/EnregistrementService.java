package com.example.tp1.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EnregistrementService extends Service {
    public static final String ACTION_ENREGISTREMENT_TERMINE = "com.example.tp1.ENREGISTREMENT_TERMINE";
    public static final String EXTRA_SUCCES = "succes";
    public static final String EXTRA_CHEMIN = "chemin_fichier";

    private static final String TAG = "EnregistrementService";
    private static final String CHANNEL_ID = "service_audio";
    private static final int NOTIF_ID = 1;
    private static final long DUREE_MS = 15000;

    private MediaRecorder mRecorder;   // accédé uniquement depuis mExecutor
    private File mAudioFile;
    private Handler mHandler;
    private ExecutorService mExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        mExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        demarrerForeground();                               // rapide, OK sur le main
        mExecutor.execute(this::demarrerEnregistrement);    // prepare()/start() hors main
        // NOT_STICKY : une note de 15 s ne doit pas redémarrer toute seule
        // (Android 12+ interdit de relancer un foreground service micro depuis l'arrière-plan).
        return START_NOT_STICKY;
    }

    private void demarrerEnregistrement() {
        try {
            mAudioFile = new File(getExternalFilesDir(null),
                    "note_" + System.currentTimeMillis() + ".3gp");
            mRecorder = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    ? new MediaRecorder(this) : new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(mAudioFile.getAbsolutePath());

            mRecorder.prepare();   // bloquant, mais sur le thread secondaire
            mRecorder.start();

            mHandler.postDelayed(this::terminer, DUREE_MS);  // callback sur le main
        } catch (IOException | RuntimeException e) {          // RuntimeException : micro occupé, etc.
            Log.e(TAG, "Erreur MediaRecorder", e);
            arreterEnregistrement();
            notifierFin(false);
            mHandler.post(this::stopSelf);
        }
    }

    private void terminer() {
        mExecutor.execute(() -> {
            arreterEnregistrement();
            notifierFin(true);
            mHandler.post(this::stopSelf);
        });
    }

    private void arreterEnregistrement() {
        if (mRecorder != null) {
            try { mRecorder.stop(); } catch (RuntimeException ignored) {}
            mRecorder.release();
            mRecorder = null;
        }
    }

    /** Broadcast LOCAL (setPackage = notre propre appli) pour prévenir l'Activity. */
    private void notifierFin(boolean succes) {
        Intent i = new Intent(ACTION_ENREGISTREMENT_TERMINE);
        i.setPackage(getPackageName());
        i.putExtra(EXTRA_SUCCES, succes);
        if (succes && mAudioFile != null) i.putExtra(EXTRA_CHEMIN, mAudioFile.getAbsolutePath());
        sendBroadcast(i);
    }

    private void demarrerForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Enregistrement audio", NotificationManager.IMPORTANCE_LOW);
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
        Notification notif = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Enregistrement en cours")
                .setContentText("Note vocale CineBlog…")
                .setSmallIcon(android.R.drawable.ic_btn_speak_now)
                .setOngoing(true)
                .build();

        // Le type "microphone" n'existe qu'à partir de l'API 30 (R), pas 29.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            startForeground(NOTIF_ID, notif, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
        } else {
            startForeground(NOTIF_ID, notif);
        }
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mExecutor.execute(this::arreterEnregistrement);
        mExecutor.shutdown();
        super.onDestroy();
    }
}