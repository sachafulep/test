package com.sss.wearable;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sss.wearable.Classes.BleConnectionManager;

public class CustomActivity extends AppCompatActivity {
    BleConnectionManager bleConnectionManager;
    SeekBar sbRed;
    SeekBar sbGreen;
    SeekBar sbBlue;
    ImageView ivWearable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle("");
        }

        getWindow().setStatusBarColor(getColor(R.color.backgroundDark));

        bleConnectionManager = BleConnectionManager.getInstance();
        sbRed = findViewById(R.id.sbRed);
        sbGreen = findViewById(R.id.sbGreen);
        sbBlue = findViewById(R.id.sbBlue);
        ivWearable = findViewById(R.id.ivWearable);
        sbRed.setOnSeekBarChangeListener(sbListener);
        sbGreen.setOnSeekBarChangeListener(sbListener);
        sbBlue.setOnSeekBarChangeListener(sbListener);
    }

    private SeekBar.OnSeekBarChangeListener sbListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int currentColor = Color.rgb(
                    sbRed.getProgress(),
                    sbGreen.getProgress(),
                    sbBlue.getProgress()
            );

            ivWearable.setColorFilter(currentColor, PorterDuff.Mode.SRC_IN);
            bleConnectionManager.writeCharacteristic(currentColor);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}


