package com.sss.test;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    BleConnectionManager bleConnectionManager;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ACCESS_COURSE_LOCATION = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    TextView tvLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLoading = findViewById(R.id.tvLoading);
        getLocationPermission();

        if (bluetoothAdapter.isEnabled()) {
            searchForBluetoothDevices();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Button btnInterests = findViewById(R.id.btnInterests);
        Button btnCustom = findViewById(R.id.btnCustom);
        btnInterests.setOnClickListener(listener);
        btnCustom.setOnClickListener(listener);
    }

    Button.OnClickListener listener = new Button.OnClickListener() {
        Intent intent;

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnInterests) {
                intent = new Intent(MainActivity.this, InterestsActivity.class);
            } else {
                intent = new Intent(MainActivity.this, CustomActivity.class);
            }

            startActivity(intent);
        }
    };

    void searchForBluetoothDevices() {
        Log.d(BleConnectionManager.TAG, "Looking for BLE device");
        tvLoading.setText(getString(R.string.status_searching));
        bleConnectionManager = new BleConnectionManager(getApplicationContext(),
                (android.bluetooth.BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE));
        bleConnectionManager.startScan();
    }

    void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    (Activity) MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COURSE_LOCATION);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            Log.d(BleConnectionManager.TAG, "Bluetooth enabled on the phone.");
            searchForBluetoothDevices();
        } else if (requestCode == 1 && resultCode == 0) {
            Log.d(BleConnectionManager.TAG, "Bluetooth not enabled on the phone.");
        }
    }
}