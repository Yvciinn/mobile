package com.example.tp1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity2 extends BaseTimerActivity {

    // Cles du Bundle
    public static final String EXTRA_BUNDLE = "signup_bundle";
    public static final String KEY_NAME = "signup_name";
    public static final String KEY_EMAIL = "signup_email";
    public static final String KEY_PASSWORD = "signup_password";

    private EditText editName, editEmail, editPassword, editConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initTimer();

        editName = findViewById(R.id.edit_name);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        editConfirm = findViewById(R.id.edit_confirm);
        Button btSignUp = findViewById(R.id.bt_register);

        btSignUp.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString();
            String confirm = editConfirm.getText().toString();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                toast("remplir tous les champs svp");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.setError("Email invalide");
            } else if (password.length() < 6) {
                editPassword.setError("6 caracteres minimum");
            } else if (!password.equals(confirm)) {
                editConfirm.setError("les mots de passe sont differents");
            } else {
                toast("Compte créé !");

                // 1) Creer le Bundle
                Bundle b = new Bundle();
                b.putString(KEY_NAME, name);
                b.putString(KEY_EMAIL, email);
                b.putString(KEY_PASSWORD, password);

                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);

                intent.putExtra(EXTRA_BUNDLE, b);

                startActivity(intent);
                finish();
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}