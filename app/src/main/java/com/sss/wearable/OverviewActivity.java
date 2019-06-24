package com.sss.wearable;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sss.wearable.Classes.BleConnectionManager;

public class OverviewActivity extends AppCompatActivity {
    boolean connected = false;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ACCESS_COURSE_LOCATION = 2;
    public static Handler handler;
    BleConnectionManager bleConnectionManager;
    TextView tvLoading;
    TextView tvWearable;
    FrameLayout buttonContainer;
    RelativeLayout loadingPanel;
    ImageView ivWearable;
    Button btnDebug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        tvLoading = findViewById(R.id.tvLoading);
        tvWearable = findViewById(R.id.tvWearable);
        loadingPanel = findViewById(R.id.loadingPanel);
        buttonContainer = findViewById(R.id.buttonContainer);
        ivWearable = findViewById(R.id.ivWearable);
        btnDebug = findViewById(R.id.btnDebug);
        getLocationPermission();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                int status = data.getInt("status");
                Log.d(BleConnectionManager.TAG, "Message received: status #" + status);

                switch (status) {
                    case 1:
                        tvLoading.setText(R.string.connecting);
                        break;
                    case 2:
                        connected = true;
                        updateUI(true);
                        break;
                    case 4:
                        if (!connected) {
                            tvLoading.setText(getString(R.string.status_scan_ended));
                            loadingPanel.setVisibility(View.GONE);
                        }
                        break;
                    case 6:
                        connected = false;
                        updateUI(false);
                        searchForBluetoothDevices();
                }
            }
        };

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            searchForBluetoothDevices();
        } else {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Button btnInterests = findViewById(R.id.btnInterests);
        Button btnCustom = findViewById(R.id.btnCustom);

        Button.OnClickListener listener = new Button.OnClickListener() {
            Intent intent;

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.btnInterests) {
                    intent = new Intent(OverviewActivity.this, InterestsActivity.class);
                } else {
                    intent = new Intent(OverviewActivity.this, CustomActivity.class);
                }

                startActivity(intent);
            }
        };

        btnInterests.setOnClickListener(listener);
        btnCustom.setOnClickListener(listener);

        btnDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleConnectionManager.stopScan();
                updateUI(true);
            }
        });
    }

    private void updateUI(boolean connected) {
        if (connected) {
            tvLoading.setVisibility(View.GONE);
            loadingPanel.setVisibility(View.GONE);
            btnDebug.setVisibility(View.GONE);

            tvWearable.setVisibility(View.VISIBLE);
            ivWearable.setVisibility(View.VISIBLE);
            buttonContainer.setVisibility(View.VISIBLE);
        } else {
            tvWearable.setVisibility(View.GONE);
            ivWearable.setVisibility(View.GONE);
            buttonContainer.setVisibility(View.GONE);

            tvLoading.setVisibility(View.VISIBLE);
            loadingPanel.setVisibility(View.VISIBLE);
            tvLoading.setText(R.string.status_searching);
        }
    }

    void searchForBluetoothDevices() {
        Log.d(BleConnectionManager.TAG, "Looking for BLE device");
        tvLoading.setText(getString(R.string.status_searching));
        bleConnectionManager = new BleConnectionManager(getApplicationContext(),
                (android.bluetooth.BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE));
        bleConnectionManager.startScan();
        setScanTimer();
    }

    void setScanTimer() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bleConnectionManager.stopScan();
            }
        }, 999999);
    }

    void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                OverviewActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    OverviewActivity.this,
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