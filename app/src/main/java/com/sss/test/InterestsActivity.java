package com.sss.test;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.GridView;

import java.util.Arrays;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setTitle("");
        GridView gvInterests = findViewById(R.id.gvInterests);

        List<String> interests = Arrays.asList(
                "Cooking", "Programming", "Animals", "Languages",
                "Gaming", "Traveling", "Fashion", "Sports", "Fitness", "Fishing", "Sailing",
                "Swimming", "Going out", "Netflix", "Music", "Food", "Nature", "Beauty", "Dancing",
                "Design", "Shopping", "Technology", "Gardening", "Football", "Gymnastics",
                "Volleyball", "Pole dancing", "Skating", "Skateboarding", "Surfing", "Winter sports",
                "Extreme sports", "Board games", "Reading", "Anime"
        );

        gvInterests.setAdapter(new InterestsAdapter(interests, InterestsActivity.this));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
