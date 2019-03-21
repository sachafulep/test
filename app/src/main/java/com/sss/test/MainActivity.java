package com.sss.test;

import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    BluetoothManager btManager;
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
        setContentView(R.layout.activity_main);
        btManager = new BluetoothManager(MainActivity.this);
        btManager.getLocationPermission();

        if (!btManager.bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BluetoothManager.REQUEST_ENABLE_BT);
        } else {
            if (!btManager.checkPairedDevices()) {
                searchForBluetoothDevices();
            }
        }

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

    private SeekBar.OnSeekBarChangeListener sbListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            colorView.setBackgroundPaint(
                    sbRed.getProgress(),
                    sbGreen.getProgress(),
                    sbBlue.getProgress()
            );
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    void searchForBluetoothDevices() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(btManager.receiver, filter);
        btManager.bluetoothAdapter.startDiscovery();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            Log.d(BluetoothManager.TAG, "Bluetooth enabled on the server.");
            if (!btManager.checkPairedDevices()) {
                searchForBluetoothDevices();
            }
        } else if (requestCode == 1 && resultCode == 0) {
            Log.d(BluetoothManager.TAG, "Bluetooth not enabled on the server.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(btManager.receiver);
    }
}