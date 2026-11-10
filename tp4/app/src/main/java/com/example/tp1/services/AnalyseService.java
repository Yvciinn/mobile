package com.example.tp1.services;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class AnalyseService extends Service {

    // Broadcast vers l'application réceptrice (externe)
    public static final String ACTION_PREFERENCES = "com.example.PREFERENCES_ANALYSEES";
    public static final String PACKAGE_RECEPTEUR = "com.example.applicationreceptrice";
    public static final String PERMISSION_RECEPTION = "com.example.tp1.permission.RECEIVE_PREFERENCES";

    // Broadcast local vers MovieActivity4
    public static final String ACTION_ANALYSE_LOCALE = "com.example.tp1.ANALYSE_LOCALE";

    public static final String KEY_TEXTE = "texte";
    public static final String KEY_GENRE = "genre_prefere";
    public static final String KEY_SENTIMENT = "sentiment";
    public static final String KEY_ERREUR = "erreur";

    private static final String TAG = "AnalyseService";

    private SpeechRecognizer recognizer;
    private SentimentClassifier sentimentClassifier;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sentimentClassifier = new SentimentClassifier(this);   // bonus TFLite
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        lancerAnalyse();
        return START_NOT_STICKY;
    }

    private void lancerAnalyse() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            terminerAvecErreur("Permission micro manquante");
            return;
        }
        if (!SpeechRecognizer.isRecognitionAvailable(this)) {
            terminerAvecErreur("Reconnaissance vocale indisponible sur cet appareil");
            return;
        }
        if (recognizer != null) return;   // analyse déjà en cours

        recognizer = SpeechRecognizer.createSpeechRecognizer(this);  // thread principal : OK
        recognizer.setRecognitionListener(new ListenerAdapter() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches =
                        results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    Map<String, String> prefs = extrairePreferences(matches.get(0));
                    diffuserInterne(prefs);
                    diffuserExterne(prefs);
                    terminer();
                } else {
                    terminerAvecErreur("Aucun texte reconnu");
                }
            }

            @Override
            public void onError(int error) {
                terminerAvecErreur("Erreur de reconnaissance (code " + error + ")");
            }
        });

        Intent reco = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        reco.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        reco.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR");
        recognizer.startListening(reco);
    }

    /** Extraction simple par mots-clés + sentiment par le modèle TFLite. */
    private Map<String, String> extrairePreferences(String texte) {
        String t = texte.toLowerCase(Locale.FRENCH);
        String genre = "";
        if (t.contains("action") || t.contains("explosion") || t.contains("combat")) genre = "Action";
        else if (t.contains("comédie") || t.contains("drôle") || t.contains("rire")) genre = "Comédie";
        else if (t.contains("drame") || t.contains("émouvant") || t.contains("triste")) genre = "Drame";

        Map<String, String> prefs = new LinkedHashMap<>();
        prefs.put(KEY_TEXTE, texte);
        prefs.put(KEY_GENRE, genre);
        prefs.put(KEY_SENTIMENT, sentimentClassifier.classify(texte));
        return prefs;
    }

    private void diffuserInterne(Map<String, String> prefs) {
        Intent i = new Intent(ACTION_ANALYSE_LOCALE);
        i.setPackage(getPackageName());
        for (Map.Entry<String, String> e : prefs.entrySet()) i.putExtra(e.getKey(), e.getValue());
        sendBroadcast(i);
    }

    /** Seul point de sortie vers l'extérieur : ciblé (setPackage) ET protégé (permission). */
    private void diffuserExterne(Map<String, String> prefs) {
        Intent i = new Intent(ACTION_PREFERENCES);
        for (Map.Entry<String, String> e : prefs.entrySet()) i.putExtra(e.getKey(), e.getValue());
        i.setPackage(PACKAGE_RECEPTEUR);
        sendBroadcast(i, PERMISSION_RECEPTION);
    }

    private void terminerAvecErreur(String message) {
        Log.w(TAG, message);
        Map<String, String> m = new LinkedHashMap<>();
        m.put(KEY_ERREUR, message);
        diffuserInterne(m);          // l'erreur ne sort pas de l'application
        terminer();
    }

    private void terminer() {
        libererRecognizer();
        stopSelf();
    }

    private void libererRecognizer() {
        if (recognizer != null) {
            recognizer.destroy();
            recognizer = null;
        }
    }

    @Override
    public void onDestroy() {
        libererRecognizer();
        if (sentimentClassifier != null) sentimentClassifier.close();
        super.onDestroy();
    }

    /** RecognitionListener impose 9 méthodes : on ne surcharge que celles utiles. */
    private abstract static class ListenerAdapter implements RecognitionListener {
        @Override public void onReadyForSpeech(Bundle params) {}
        @Override public void onBeginningOfSpeech() {}
        @Override public void onRmsChanged(float rmsdB) {}
        @Override public void onBufferReceived(byte[] buffer) {}
        @Override public void onEndOfSpeech() {}
        @Override public void onPartialResults(Bundle partialResults) {}
        @Override public void onEvent(int eventType, Bundle params) {}
    }
}