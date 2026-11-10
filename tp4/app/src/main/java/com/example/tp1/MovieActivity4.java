package com.example.tp1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.tp1.services.AnalyseService;
import com.example.tp1.services.EnregistrementService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MovieActivity4 extends AppCompatActivity {

    public static final String EXTRA_BUNDLE = "movie_bundle";
    public static final String KEY_TITLE = "movie_title";
    public static final String KEY_GENRE = "movie_genre";
    public static final String KEY_REVIEW = "movie_review";

    /** true : micro exclusif (dictée interdite pendant l'enregistrement). Mettre false pour la capture dumpsys. */
    private static final boolean EXCLUSION_MICRO = true;

    private enum Action { ENREGISTRER, DICTER }

    private EditText editTitle, editReview;
    private RadioGroup radioGroupGenre;
    private Button btPublish, btRecord, btDictate;
    private TextView txtAudioStatus;
    private Action pendingAction;

    private final ActivityResultLauncher<String[]> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> onPermissionsResult());

    /** Reçoit les broadcasts LOCAUX des services (non exporté). */
    private final BroadcastReceiver localReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (EnregistrementService.ACTION_ENREGISTREMENT_TERMINE.equals(action)) {
                boolean ok = intent.getBooleanExtra(EnregistrementService.EXTRA_SUCCES, false);
                String path = intent.getStringExtra(EnregistrementService.EXTRA_CHEMIN);
                txtAudioStatus.setText(ok && path != null
                        ? "Note vocale enregistrée : " + new File(path).getName()
                        : "Échec de l'enregistrement");
                setBusy(false);
            } else if (AnalyseService.ACTION_ANALYSE_LOCALE.equals(action)) {
                String erreur = intent.getStringExtra(AnalyseService.KEY_ERREUR);
                if (erreur != null) {
                    txtAudioStatus.setText(erreur);
                } else {
                    editReview.setText(intent.getStringExtra(AnalyseService.KEY_TEXTE));
                    selectionnerGenre(intent.getStringExtra(AnalyseService.KEY_GENRE));
                    txtAudioStatus.setText("Sentiment détecté : "
                            + intent.getStringExtra(AnalyseService.KEY_SENTIMENT));
                }
                setBusy(false);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie4);

        editTitle = findViewById(R.id.edit_movie_title);
        editReview = findViewById(R.id.edit_review);
        radioGroupGenre = findViewById(R.id.radio_group_genre);
        btPublish = findViewById(R.id.bt_publish);
        btRecord = findViewById(R.id.bt_record);
        btDictate = findViewById(R.id.bt_dictate);
        txtAudioStatus = findViewById(R.id.txt_audio_status);

        IntentFilter filter = new IntentFilter();
        filter.addAction(EnregistrementService.ACTION_ENREGISTREMENT_TERMINE);
        filter.addAction(AnalyseService.ACTION_ANALYSE_LOCALE);
        ContextCompat.registerReceiver(this, localReceiver, filter,
                ContextCompat.RECEIVER_NOT_EXPORTED);

        btRecord.setOnClickListener(v -> demander(Action.ENREGISTRER));
        btDictate.setOnClickListener(v -> demander(Action.DICTER));

        btPublish.setOnClickListener(v -> {
            String title = editTitle.getText().toString().trim();
            String review = editReview.getText().toString().trim();
            int selectedId = radioGroupGenre.getCheckedRadioButtonId();

            if (title.isEmpty() || selectedId == -1 || review.isEmpty()) {
                Toast.makeText(this, "merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }
            String genre = ((RadioButton) findViewById(selectedId)).getText().toString();

            Bundle b = new Bundle();
            b.putString(KEY_TITLE, title);
            b.putString(KEY_GENRE, genre);
            b.putString(KEY_REVIEW, review);

            Intent intent = new Intent(this, MovieActivity5.class);
            intent.putExtra(EXTRA_BUNDLE, b);
            startActivity(intent);
        });
    }

    // ------------------------------------------------------------------ permissions

    private String[] permissionsRequises() {
        List<String> p = new ArrayList<>();
        p.add(Manifest.permission.RECORD_AUDIO);                       // bloquante
        if (Build.VERSION.SDK_INT >= 33) {
            p.add(Manifest.permission.POST_NOTIFICATIONS);             // non bloquante
            p.add(Manifest.permission.READ_MEDIA_AUDIO);               // non bloquante
        } else {
            p.add(Manifest.permission.READ_EXTERNAL_STORAGE);          // non bloquante
        }
        return p.toArray(new String[0]);
    }

    private boolean accordee(String permission) {
        return ContextCompat.checkSelfPermission(this, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void demander(Action action) {
        pendingAction = action;
        boolean toutOk = true;
        for (String p : permissionsRequises()) toutOk &= accordee(p);
        if (toutOk) lancer(action);
        else permissionLauncher.launch(permissionsRequises());
    }

    /** Gère les 3 cas : refus simple, refus définitif, refus partiel. */
    private void onPermissionsResult() {
        if (!accordee(Manifest.permission.RECORD_AUDIO)) {
            boolean definitif = !ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.RECORD_AUDIO);
            if (definitif) {
                // Cas 2 : « Ne plus demander » → seul recours : les réglages
                new AlertDialog.Builder(this)
                        .setTitle("Micro désactivé")
                        .setMessage("Activez l'autorisation Micro dans les réglages de l'application.")
                        .setPositiveButton("Ouvrir les réglages", (d, w) -> startActivity(
                                new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null))))
                        .setNegativeButton("Annuler", null)
                        .show();
            } else {
                // Cas 1 : refus simple → on explique, l'utilisateur pourra réessayer
                Toast.makeText(this, "Le micro est nécessaire pour cette fonction",
                        Toast.LENGTH_LONG).show();
            }
            return;
        }
        // Cas 3 : refus partiel (micro OK, mais notification / lecture audio refusées)
        boolean partiel = false;
        for (String p : permissionsRequises()) partiel |= !accordee(p);
        if (partiel) {
            Toast.makeText(this, "Fonction limitée : notification ou accès audio refusé",
                    Toast.LENGTH_LONG).show();
        }
        lancer(pendingAction);
    }

    // ------------------------------------------------------------------ lancement des services

    private void lancer(Action action) {
        if (action == Action.ENREGISTRER) {
            ContextCompat.startForegroundService(this,
                    new Intent(this, EnregistrementService.class));
            txtAudioStatus.setText("Enregistrement en cours (15 s)…");
            setBusy(true);
        } else {
            startService(new Intent(this, AnalyseService.class));
            txtAudioStatus.setText("Parlez maintenant…");
            setBusy(true);
        }
    }

    private void setBusy(boolean busy) {
        btRecord.setEnabled(!busy);
        btDictate.setEnabled(!(busy && EXCLUSION_MICRO));
        if (!busy) btDictate.setEnabled(true);
    }

    private void selectionnerGenre(String genre) {
        if ("Action".equals(genre)) radioGroupGenre.check(R.id.genre_action);
        else if ("Comédie".equals(genre)) radioGroupGenre.check(R.id.genre_comedie);
        else if ("Drame".equals(genre)) radioGroupGenre.check(R.id.genre_drame);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(localReceiver);
        super.onDestroy();
    }
}