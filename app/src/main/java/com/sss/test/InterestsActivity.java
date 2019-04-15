package com.sss.test;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Handler;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {
    static Handler handler;
    private InterestsAdapter adapter;
    TextView tvCounter;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }

        tvCounter = findViewById(R.id.tvCounter);
        tvCounter.setText(getString(R.string.counter, counter));
        GridView gvInterests = findViewById(R.id.gvInterests);

        List<String> interests = Arrays.asList(
                "Cooking", "Programming", "Animals", "Languages",
                "Gaming", "Traveling", "Fashion", "Sports", "Fitness", "Fishing", "Sailing",
                "Swimming", "Going out", "Netflix", "Music", "Food", "Nature", "Beauty", "Dancing",
                "Design", "Shopping", "Technology", "Gardening", "Football", "Gymnastics",
                "Volleyball", "Pole dancing", "Skating", "Skateboarding", "Surfing", "Winter sports",
                "Extreme sports", "Board games", "Reading", "Anime"
        );

        adapter = new InterestsAdapter(
                interests,
                InterestsActivity.this,
                getSupportFragmentManager()
        );

        gvInterests.setAdapter(adapter);
        setupMsgHandler();
    }

    void setupMsgHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                counter++;
                Bundle data = msg.getData();
                int position = data.getInt("position");
                int color = data.getInt("color");
                Button button = adapter.getItem(position);
                adapter.colors.put(position, color);
                button.setBackgroundColor(color);
                adapter.notifyDataSetChanged();
                tvCounter.setText(getString(R.string.counter, counter));
            }
        };
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}