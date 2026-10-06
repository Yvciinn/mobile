package com.example.tp1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MovieActivity4 extends AppCompatActivity {

    // Cles du Bundle (bonne pratique recommandee dans le guide)
    public static final String EXTRA_BUNDLE = "movie_bundle";
    public static final String KEY_TITLE = "movie_title";
    public static final String KEY_GENRE = "movie_genre";
    public static final String KEY_REVIEW = "movie_review";

    private EditText editTitle;
    private EditText editReview;
    private RadioGroup radioGroupGenre;
    private Button btPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie4);

        editTitle = findViewById(R.id.edit_movie_title);
        editReview = findViewById(R.id.edit_review);
        radioGroupGenre = findViewById(R.id.radio_group_genre);
        btPublish = findViewById(R.id.bt_publish);

        btPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = editTitle.getText().toString().trim();
                String review = editReview.getText().toString().trim();
                int selectedId = radioGroupGenre.getCheckedRadioButtonId();

                if (title.isEmpty() || selectedId == -1 || review.isEmpty()) {
                    Toast.makeText(MovieActivity4.this,
                            "merci de remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton selectedGenre = findViewById(selectedId);
                String genre = selectedGenre.getText().toString();

                Bundle b = new Bundle();
                b.putString(KEY_TITLE, title);
                b.putString(KEY_GENRE, genre);
                b.putString(KEY_REVIEW, review);

                Intent intent = new Intent(MovieActivity4.this, MovieActivity5.class);

                intent.putExtra(EXTRA_BUNDLE, b);


                startActivity(intent);
            }
        });
    }
}