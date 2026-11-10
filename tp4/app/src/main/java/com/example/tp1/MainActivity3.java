package com.example.tp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity3 extends BaseTimerActivity {

    private EditText editEmail, editPassword;
    private TextView txtResult;
    private TextView txtWelcomeName;

    private String expectedPassword; // mot de passe recu depuis l'inscription

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initTimer();

        editEmail = findViewById(R.id.edit_login_email);
        editPassword = findViewById(R.id.edit_login_password);
        txtResult = findViewById(R.id.txt_result3);
        txtWelcomeName = findViewById(R.id.txt_welcome_name);

        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(MainActivity2.EXTRA_BUNDLE);

        if (bundle != null) {
            String name = bundle.getString(MainActivity2.KEY_NAME, "");
            String email = bundle.getString(MainActivity2.KEY_EMAIL, "");
            expectedPassword = bundle.getString(MainActivity2.KEY_PASSWORD, "");

            txtWelcomeName.setText("Bienvenue " + name + ", connecte-toi !");
            editEmail.setText(email);
        }

        Button btLogin = findViewById(R.id.bt_do_login);
        btLogin.setOnClickListener(v -> {
            String enteredPassword = editPassword.getText().toString();

            if (expectedPassword == null || expectedPassword.isEmpty()) {
                txtResult.setText("Aucun compte trouvé, inscris-toi d'abord.");
            } else if (enteredPassword.equals(expectedPassword)) {
                txtResult.setText("bienvenue");

                Intent movieIntent = new Intent(MainActivity3.this, MovieActivity4.class);
                startActivity(movieIntent);
            } else {
                editPassword.setError("Mot de passe incorrect");
                txtResult.setText("");
            }
        });

        Button btGoMovie = findViewById(R.id.bt_go_movie);
        btGoMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent movieIntent = new Intent(MainActivity3.this, MovieActivity4.class);
                startActivity(movieIntent);
            }
        });
    }
}