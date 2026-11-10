package com.example.tp1.services;

import android.content.Context;

import java.util.Locale;

/** Classe un texte (positive / negative) avec un lexique de mots-clés embarqué. */
public class SentimentClassifier implements AutoCloseable {

    private static final String[] POSITIVE = {
            "excellent", "super", "génial", "genial", "magnifique", "superbe", "adoré", "adore",
            "aimé", "aime", "bon", "bien", "incroyable", "parfait", "meilleur",
            "great", "good", "amazing", "love", "loved", "awesome", "best", "brilliant", "fantastic"
    };

    private static final String[] NEGATIVE = {
            "nul", "mauvais", "ennuyeux", "décevant", "decevant", "horrible", "affreux", "pire",
            "déteste", "deteste", "lent", "long", "raté", "rate", "médiocre", "mediocre",
            "bad", "boring", "awful", "terrible", "worst", "hate", "hated", "disappointing"
    };

    public SentimentClassifier(Context context) {
        // Rien à charger.
    }

    public String classify(String text) {
        if (text == null || text.trim().isEmpty()) return "inconnu";
        String t = text.toLowerCase(Locale.ROOT);

        int score = 0;
        for (String w : POSITIVE) if (t.contains(w)) score++;
        for (String w : NEGATIVE) if (t.contains(w)) score--;

        if (score > 0) return "positive";
        if (score < 0) return "negative";
        return "inconnu";
    }

    @Override
    public void close() {
        // Rien à libérer.
    }
}