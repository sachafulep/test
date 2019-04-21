package com.sss.wearable;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sss.wearable.Adapters.InterestsAdapter;
import com.sss.wearable.Classes.Database;
import com.sss.wearable.Classes.Interest;

import java.util.Arrays;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {
    public static Handler handler;
    private InterestsAdapter adapter;
    GridView gvInterests;
    TextView tvCounter;
    Button btnSave;
    int counter;
    Database database;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        database = Database.getInstance();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }

        tvCounter = findViewById(R.id.tvCounter);
        tvCounter.setText(getString(R.string.counter, counter));
        gvInterests = findViewById(R.id.gvInterests);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (Interest interest : adapter.interests) {
                    database.interestDao().updateInterest(interest);
                }

                finish();
            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                final List<String> interests = Arrays.asList(
                        "Cooking", "Programming", "Animals", "Languages", "Gaming", "Traveling",
                        "Fashion", "Sports", "Fitness", "Fishing", "Sailing", "Swimming",
                        "Going out", "Netflix", "Music", "Food", "Nature", "Beauty", "Dancing",
                        "Design", "Shopping", "Technology", "Gardening", "Football", "Gymnastics",
                        "Volleyball", "Pole dancing", "Skating", "Skateboarding", "Surfing",
                        "Winter sports", "Extreme sports", "Board games", "Reading", "Anime"
                );

                if (database.interestDao().getCount() == 0) {
                    for (String interest : interests) {
                        database.interestDao().insert(new Interest(id, interest, 0));
                        id++;
                    }
                }

                adapter = new InterestsAdapter(
                        database.interestDao().getAll(),
                        InterestsActivity.this,
                        getSupportFragmentManager()
                );

                for (Interest interest : adapter.interests) {
                    if (interest.getColor() != 0) {
                        counter++;
                        tvCounter.setText(getString(R.string.counter, counter));
                    }
                }

                // tell UI thread to update gvInterests with the loaded interests from database
                handler.sendEmptyMessage(0);
            }
        });

        setupMsgHandler();
    }

    void setupMsgHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                if (!data.isEmpty()) {
                    int position = data.getInt("position");
                    int color = data.getInt("color");
                    Button button = adapter.getItem(position);
                    adapter.setInterestColor(position, color);
                    adapter.notifyDataSetChanged();

                    if (color == 0) {
                        button.setBackgroundResource(android.R.drawable.btn_default);
                        counter--;
                    } else {
                        button.setBackgroundColor(color);
                        counter++;
                    }

                    tvCounter.setText(getString(R.string.counter, counter));
                } else {
                    gvInterests.setAdapter(adapter);
                }
            }
        };
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}