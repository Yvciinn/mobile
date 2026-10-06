package com.example.tp1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity3 extends AppCompatActivity {

    private TextView txtInfo;
    private TextView txtResult;
    private RadioGroup radioGroupStatus;
    private Button btAfficher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        txtInfo = findViewById(R.id.txt_info3);
        txtResult = findViewById(R.id.txt_result3);
        radioGroupStatus = findViewById(R.id.radio_group_status);
        btAfficher = findViewById(R.id.bt_afficher3);

        final String name = getIntent().getStringExtra("EXTRA_NAME");
        final String age = getIntent().getStringExtra("EXTRA_AGE");

        txtInfo.setText("Nom : " + name + "   Âge : " + age);

        btAfficher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroupStatus.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    txtResult.setText("merci de choisir un statut.");
                    return;
                }

                RadioButton selectedRadio = findViewById(selectedId);
                String status = selectedRadio.getText().toString();

                txtResult.setText(name + " a " + age + " ans et est " + status + ".");
            }
        });
    }
}