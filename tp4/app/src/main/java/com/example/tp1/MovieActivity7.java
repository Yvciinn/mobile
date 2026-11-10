package com.example.tp1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

// Activity B : reçoit les deux notes, calcule la moyenne et le verdict,
// affiche les données reçues et renvoie le résultat à MovieActivity6.
public class MovieActivity7 extends AppCompatActivity implements View.OnClickListener {

    private TextView dataReceived;
    private Button btDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie7);

        dataReceived = findViewById(R.id.txt_data_received);
        btDone = findViewById(R.id.bt_done);
        btDone.setOnClickListener(this);

        Intent myLocalIntent = getIntent();
        Bundle myBundle = myLocalIntent.getExtras();

        Double n1 = myBundle.getDouble(MovieActivity6.KEY_NOTE_SCENARIO);
        Double n2 = myBundle.getDouble(MovieActivity6.KEY_NOTE_REALISATION);

        Double moyenne = (n1 + n2) / 2;

        String verdict;
        if (moyenne >= 8) {
            verdict = "Chef-d'oeuvre ";
        } else if (moyenne >= 6) {
            verdict = "Bon film ";
        } else if (moyenne >= 4) {
            verdict = "Film moyen ";
        } else {
            verdict = "Film décevant ";
        }

        dataReceived.setText("Données reçues :\n" + "Scénario = " + n1 + "\nRéalisation = " + n2 + "\n\nNote finale = " + moyenne + "\nVerdict : " + verdict);

        myBundle.putDouble(MovieActivity6.KEY_NOTE_FINALE, moyenne);
        myBundle.putString(MovieActivity6.KEY_VERDICT, verdict);
        myLocalIntent.putExtras(myBundle);

        setResult(Activity.RESULT_OK, myLocalIntent);
    }

    @Override
    public void onClick(View v) {

        finish();
    }
}