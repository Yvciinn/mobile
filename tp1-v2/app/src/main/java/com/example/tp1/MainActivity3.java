package com.example.tp1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity3 extends BaseTimerActivity {

    private EditText editEmail, editPassword;
    private TextView txtResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        initTimer();

        editEmail = findViewById(R.id.edit_login_email);
        editPassword = findViewById(R.id.edit_login_password);
        txtResult = findViewById(R.id.txt_result3);
        Button btLogin = findViewById(R.id.bt_do_login);
        btLogin.setOnClickListener(v -> txtResult.setText("bienvenue"));
    }
}