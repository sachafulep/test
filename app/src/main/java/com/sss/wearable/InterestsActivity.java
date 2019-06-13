package com.sss.wearable;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sss.wearable.Adapters.InterestsAdapter;
import com.sss.wearable.Classes.BleConnectionManager;
import com.sss.wearable.Classes.Database;
import com.sss.wearable.Classes.Interest;
import com.sss.wearable.Fragments.ColorPickerDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InterestsActivity extends AppCompatActivity {
    public static Handler handler;
    private InterestsAdapter interestsAdapter;
    private InterestsAdapter selectedInterestAdapter;
    private List<Interest> interests;
    private List<Interest> selectedInterests;
    private static final int MAX_INTERESTS = 6;
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
        setContentView(R.layout.activity_interests);
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

        setupMsgHandler();

        tvCounter = findViewById(R.id.tvCounter);
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

        selectedInterests = new ArrayList<>();

        if (database.interestDao().getCount() == 0) {
            fillDatabase();
        } else {
            interests = database.interestDao().getAll();
            for (Interest interest : interests) {
                if (interest.getColor() != 0) {
                    counter++;
                    selectedInterests.add(interest);
                }
            }

            for (Interest interest : selectedInterests) {
                interests.remove(interest);
            }
        }

        // tell UI thread to update interest grids
        handler.sendEmptyMessage(0);
    }

    void setupMsgHandler() {
        handler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                if (!data.isEmpty()) {
                    int color = data.getInt("color");
                    String name = data.getString("name");
                    String mode = data.getString("mode");
                    Interest interest = null;

                    if (mode != null) {
                        if (mode.equals("edit")) {
                            for (Interest i : selectedInterests) {
                                if (i.getName().equals(name)) {
                                    interest = i;
                                    break;
                                }
                            }
                        } else {
                            for (Interest i : interests) {
                                if (i.getName().equals(name)) {
                                    interest = i;
                                    break;
                                }
                            }
                        }
                    }

                    if (interest != null) {
                        interest.setColor(color);
                        Button button = (Button) interest.getView().getChildAt(0);

                        if (color == 0) {
                            button.setBackgroundResource(R.drawable.button_interest);
                            selectedInterests.remove(interest);
                            interests.add(interest.getPosition(), interest);
                            counter--;
                        } else {
                            GradientDrawable shape = new GradientDrawable();
                            shape.setShape(GradientDrawable.RECTANGLE);
                            shape.setCornerRadius(15);
                            shape.setColor(interest.getColor());
                            button.setBackground(shape);
                            button.setTextColor(getTextColor(color));

                            if (mode.equals("set")) {
                                interests.remove(interest);
                                selectedInterests.add(interest);
                                counter++;
                            }
                        }
                    }

                    interestsAdapter.notifyDataSetChanged();
                    selectedInterestAdapter.notifyDataSetChanged();
                    tvCounter.setText(getString(R.string.counter, counter, MAX_INTERESTS));
                } else {
                    fillInterestGrids();
                }
            }
        };
    }

    private void fillInterestGrids() {
        createInterestViews();
        interestsAdapter = new InterestsAdapter(interests);
        selectedInterestAdapter = new InterestsAdapter(selectedInterests);
        gvInterests.setAdapter(interestsAdapter);
        gvSelectedInterest.setAdapter(selectedInterestAdapter);
        tvCounter.setText(getString(R.string.counter, counter, MAX_INTERESTS));
    }

    private void createInterestViews() {
        for (Interest interest : interests) {
            LinearLayout layout = new LinearLayout(InterestsActivity.this);
            Button button = new Button(InterestsActivity.this);
            setInterestButtonStyling(layout, button, interest);
            setButtonClickListener(button, interest);
            interest.setView(layout);
        }

        for (Interest interest : selectedInterests) {
            LinearLayout layout = new LinearLayout(InterestsActivity.this);
            Button button = new Button(InterestsActivity.this);
            setInterestButtonStyling(layout, button, interest);
            setButtonClickListener(button, interest);
            interest.setView(layout);
        }
    }

    private void setInterestButtonStyling(LinearLayout layout, Button button, Interest interest) {
        button.setText(interest.getName());

        if (interest.getColor() == 0) {
            button.setBackgroundResource(R.drawable.button_interest);
        } else {
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(15);
            shape.setColor(interest.getColor());
            button.setBackground(shape);
            button.setTextColor(getTextColor(interest.getColor()));
        }

        button.setAllCaps(false);
        layout.setGravity(Gravity.CENTER);
        layout.addView(button);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        params.setMargins(10, 10, 10, 10);
        button.setLayoutParams(params);
    }

    private void setButtonClickListener(Button button, final Interest interest) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("name", interest.getName());
                bundle.putInt("color", interest.getColor());

                if (interest.getColor() == 0) {
                    bundle.putString("mode", "set");
                } else {
                    bundle.putString("mode", "edit");
                }

                if (counter < MAX_INTERESTS || interest.getColor() != 0) {
                    ColorPickerDialogFragment dialog = new ColorPickerDialogFragment();
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), "ColorPickerDialog");
                } else {
                    // TODO show alert dialog
                }
            }
        });
    }

    private int getTextColor(int color) {
        double temp;

        temp = Color.red(color) / 255.0;
        double red = temp <= 0.03928 ? temp / 12.92 : Math.pow(((temp + 0.055) / 1.055), 2.4);

        temp = Color.green(color) / 255.0;
        double green = temp <= 0.03928 ? temp / 12.92 : Math.pow(((temp + 0.055) / 1.055), 2.4);

        temp = Color.blue(color) / 255.0;
        double blue = temp <= 0.03928 ? temp / 12.92 : Math.pow(((temp + 0.055) / 1.055), 2.4);

        double L = 0.2126 * red + 0.7152 * green + 0.0722 * blue;

        return L > 0.179 ? Color.parseColor("#000000") :
                InterestsActivity.this.getColor(R.color.background);
    }

    private void fillDatabase() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                List<String> interestNames = Arrays.asList(
                        "Cooking", "Programming", "Animals", "Languages", "Gaming", "Traveling",
                        "Fashion", "Sports", "Fitness", "Fishing", "Sailing", "Swimming",
                        "Going out", "Netflix", "Music", "Food", "Nature", "Beauty", "Dancing",
                        "Design", "Shopping", "Technology", "Gardening", "Football", "Gymnastics",
                        "Volleyball", "Pole dancing", "Skating", "Skateboarding", "Surfing",
                        "Winter sports", "Extreme sports", "Board games", "Reading", "Anime"
                );

                interests = new ArrayList<>();

                for (String name : interestNames) {
                    interests.add(new Interest(id, name, 0, interestNames.indexOf(name)));
                    id++;
                }

                database.interestDao().insert(interests);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void sendInterestsToWearable() {
        bleConnectionManager.writeInterest(selectedInterests);
    }
}