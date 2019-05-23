package com.sss.wearable;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.sss.wearable.Classes.BleConnectionManager;
import com.sss.wearable.Views.ColorView;

public class CustomActivity extends AppCompatActivity {
    BleConnectionManager bleConnectionManager;
    ColorView colorView;
    SeekBar sbRed;
    SeekBar sbGreen;
    SeekBar sbBlue;
    Button btnBlink;
    Button btnPulse;
    Button btnRainbow;

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

        bleConnectionManager = new BleConnectionManager(getApplicationContext(),
                (android.bluetooth.BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE));

        colorView = findViewById(R.id.colorView);
        sbRed = findViewById(R.id.sbRed);
        sbGreen = findViewById(R.id.sbGreen);
        sbBlue = findViewById(R.id.sbBlue);
        btnBlink = findViewById(R.id.btnBlink);
        btnPulse = findViewById(R.id.btnPulse);
        btnRainbow = findViewById(R.id.btnRainbow);
        sbRed.setOnSeekBarChangeListener(sbListener);
        sbGreen.setOnSeekBarChangeListener(sbListener);
        sbBlue.setOnSeekBarChangeListener(sbListener);
        btnBlink.setOnClickListener(btnListener);
        btnPulse.setOnClickListener(btnListener);
        btnRainbow.setOnClickListener(btnListener);
    }

    private SeekBar.OnSeekBarChangeListener sbListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            colorView.setBackgroundPaint(Color.rgb(
                    sbRed.getProgress(),
                    sbGreen.getProgress(),
                    sbBlue.getProgress()
            ));

            bleConnectionManager.writeCharacteristic(colorView.getPaintColor());
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

    private Button.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(btnBlink)) {
                ValueAnimator animator = ValueAnimator.ofInt(0, 10);
                animator.setDuration(3000);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        colorView.blink(value);
                    }
                });

                animator.start();
            }

            if (v.equals(btnPulse)) {
                ValueAnimator animator = ValueAnimator.ofInt(255, 0);
                animator.setDuration(2000);
                animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setRepeatCount(3);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        colorView.pulse(value);
                    }
                });

                animator.start();
            }

            if (v.equals(btnRainbow)) {
                final ValueAnimator animator = ValueAnimator.ofInt(100, 175);
                animator.setDuration(2000);
                animator.setRepeatCount(2);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int value = (int) animation.getAnimatedValue();
                        colorView.rainbow(animation.getCurrentPlayTime(), value);
                    }
                });

                animator.start();
            }
        }
    };
}


