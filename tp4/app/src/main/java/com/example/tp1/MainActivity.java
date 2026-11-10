package com.example.tp1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends BaseTimerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTimer();

        Button btSignUp = findViewById(R.id.bt_signup);
        Button btLogin = findViewById(R.id.bt_login);

        btSignUp.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MainActivity2.class)));

        btLogin.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, MainActivity3.class)));
    }
}