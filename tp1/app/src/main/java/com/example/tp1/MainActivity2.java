package com.example.tp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    private TextView txtGreeting;
    private EditText editAge;
    private Button btNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        txtGreeting = findViewById(R.id.txt_greeting);
        editAge = findViewById(R.id.edit_age);
        btNext = findViewById(R.id.bt_next2);

        final String name = getIntent().getStringExtra("EXTRA_NAME");
        txtGreeting.setText("Bonjour " + name + ", quel âge as-tu ?");

        btNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String age = editAge.getText().toString();

                Intent intent = new Intent(MainActivity2.this, MainActivity3.class);
                intent.putExtra("EXTRA_NAME", name);
                intent.putExtra("EXTRA_AGE", age);
                startActivity(intent);
            }
        });
    }
}