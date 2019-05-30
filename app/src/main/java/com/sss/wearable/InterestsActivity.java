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
import com.sss.wearable.Classes.BleConnectionManager;
import com.sss.wearable.Classes.Database;
import com.sss.wearable.Classes.Interest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {
    public static Handler handler;
    private InterestsAdapter interestsAdapter;
    private InterestsAdapter selectedInterestAdapter;
    GridView gvInterests;
    GridView gvSelectedInterest;
    TextView tvCounter;
    Button btnSave;
    int counter;
    Database database;
    BleConnectionManager bleConnectionManager;
    int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);
        database = Database.getInstance();
        bleConnectionManager = BleConnectionManager.getInstance();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }

        getWindow().setStatusBarColor(getColor(R.color.backgroundDark));

        tvCounter = findViewById(R.id.tvCounter);
        tvCounter.setText(getString(R.string.counter, counter));
        gvInterests = findViewById(R.id.gvInterests);
        gvSelectedInterest = findViewById(R.id.gvSelectedInterests);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.interestDao().updateInterest(interestsAdapter.interests);
                database.interestDao().updateInterest(selectedInterestAdapter.interests);
                sendInterestsToWearable();
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
                        database.interestDao().insert(new Interest(id, interest, 0, interests.indexOf(interest)));
                        id++;
                    }
                }

                interestsAdapter = new InterestsAdapter(
                        database.interestDao().getAll(),
                        InterestsActivity.this,
                        getSupportFragmentManager()
                );

                List<Interest> selectedInterests = new ArrayList<>();

                for (Interest interest : interestsAdapter.interests) {
                    if (interest.getColor() != 0) {
                        counter++;
                        selectedInterests.add(interest);
                    }
                }

                for (Interest interest : selectedInterests) {
                    interestsAdapter.interests.remove(interest);
                }

                selectedInterestAdapter = new InterestsAdapter(
                        selectedInterests,
                        InterestsActivity.this,
                        getSupportFragmentManager()
                );

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
                    Button button;
                    Interest interest;

                    if (color == 0) {
                        interest = selectedInterestAdapter.interests.get(position);
                        button = selectedInterestAdapter.getItem(position);
                        selectedInterestAdapter.interests.remove(interest);
                        interestsAdapter.interests.add(interest);
                        button.setBackgroundResource(android.R.drawable.btn_default);
                        counter--;
                    } else {
                        interest = interestsAdapter.interests.get(position);
                        interestsAdapter.interests.remove(interest);
                        selectedInterestAdapter.interests.add(interest);
                        selectedInterestAdapter.notifyDataSetChanged();
//                        int index = selectedInterestAdapter.interestButtons.size() - 1;

                        for (Button b : selectedInterestAdapter.interestButtons) {
                            System.out.println(b.getText().toString());
                        }
//                        button = selectedInterestAdapter.interestButtons.get(index);
//                        button.setBackgroundColor(color);
                        counter++;
                    }

                    interestsAdapter.notifyDataSetChanged();
//                    selectedInterestAdapter.notifyDataSetChanged();
                    tvCounter.setText(getString(R.string.counter, counter));
                } else {
                    tvCounter.setText(getString(R.string.counter, counter));
                    gvInterests.setAdapter(interestsAdapter);
                    gvSelectedInterest.setAdapter(selectedInterestAdapter);
                }
            }
        };
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void sendInterestsToWearable() {
//        List<Interest> selectedInterests = new ArrayList<>();
//
//        for (Interest interest : interestsAdapter.interests) {
//            if (interest.getColor() != 0) {
//                selectedInterests.add(interest);
//            }
//        }
//
//        bleConnectionManager.writeInterest(selectedInterests);
    }
}