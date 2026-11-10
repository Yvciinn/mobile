package com.example.tp1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MovieActivity5 extends AppCompatActivity {

    private TextView txtTitle;
    private TextView txtGenre;
    private TextView txtReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie5);

        txtTitle = findViewById(R.id.txt_movie_title);
        txtGenre = findViewById(R.id.txt_movie_genre);
        txtReview = findViewById(R.id.txt_movie_review);

        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(MovieActivity4.EXTRA_BUNDLE);

        if (bundle != null) {
            String title = bundle.getString(MovieActivity4.KEY_TITLE, "");
            String genre = bundle.getString(MovieActivity4.KEY_GENRE, "");
            String review = bundle.getString(MovieActivity4.KEY_REVIEW, "");

            txtTitle.setText(title);
            txtGenre.setText(genre);
            txtReview.setText(review);
        } else {
            txtTitle.setText("Aucune donnée reçue");
        }

        // NOUVEAU : bouton "Noter ce film" -> MovieActivity6
        Button btGoRating = findViewById(R.id.bt_go_rating);
        btGoRating.setOnClickListener(v ->
                startActivity(new Intent(MovieActivity5.this, MovieActivity6.class)));
    }
}