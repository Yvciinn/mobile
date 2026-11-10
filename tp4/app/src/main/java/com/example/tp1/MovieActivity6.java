package com.example.tp1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class MovieActivity6 extends AppCompatActivity {

    public static final int REQUEST_RATING = 101;


    public static final String KEY_NOTE_SCENARIO = "note_scenario";
    public static final String KEY_NOTE_REALISATION = "note_realisation";
    public static final String KEY_NOTE_FINALE = "note_finale";
    public static final String KEY_VERDICT = "verdict";

    private EditText editScenario;
    private EditText editRealisation;
    private TextView txtResult;
    private Button btCalculate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie6);

        editScenario = findViewById(R.id.edit_note_scenario);
        editRealisation = findViewById(R.id.edit_note_realisation);
        txtResult = findViewById(R.id.txt_final_result);
        btCalculate = findViewById(R.id.bt_calculate);

        btCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Double n1 = Double.parseDouble(editScenario.getText().toString());
                    Double n2 = Double.parseDouble(editRealisation.getText().toString());

                    if (n1 < 0 || n1 > 10 || n2 < 0 || n2 > 10) {
                        Toast.makeText(MovieActivity6.this,
                                "Les notes doivent être entre 0 et 10",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(MovieActivity6.this, MovieActivity7.class);

                    Bundle myData = new Bundle();
                    myData.putDouble(KEY_NOTE_SCENARIO, n1);
                    myData.putDouble(KEY_NOTE_REALISATION, n2);
                    intent.putExtras(myData);

                    startActivityForResult(intent, REQUEST_RATING);

                } catch (NumberFormatException e) {
                    Toast.makeText(MovieActivity6.this,
                            "Merci de saisir les deux notes", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if ((requestCode == REQUEST_RATING) && (resultCode == Activity.RESULT_OK)) {
                Bundle myResults = data.getExtras();
                Double finale = myResults.getDouble(KEY_NOTE_FINALE);
                String verdict = myResults.getString(KEY_VERDICT, "");
                txtResult.setText("Note finale : " + finale + " / 10\n" + verdict);
            }
        } catch (Exception e) {
            txtResult.setText("Problèmes : " + requestCode + " " + resultCode);
        }
    }
}