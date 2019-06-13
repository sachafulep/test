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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.sss.wearable.Classes.BleConnectionManager;
import com.sss.wearable.Classes.Database;

public class OverviewActivity extends AppCompatActivity {
    BleConnectionManager bleConnectionManager;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final int REQUEST_ACCESS_COURSE_LOCATION = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    TextView tvLoading;
    TextView tvWearable;
    FrameLayout buttonContainer;
    public static Handler handler;
    Database database;
    boolean connected = false;
    RelativeLayout loadingPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);
        database = Database.getMainInstance(OverviewActivity.this);
        tvLoading = findViewById(R.id.tvLoading);
        tvWearable = findViewById(R.id.tvWearable);
        loadingPanel = findViewById(R.id.loadingPanel);
        buttonContainer = findViewById(R.id.buttonContainer);
        final ImageView ivWearable = findViewById(R.id.ivWearable);
        getLocationPermission();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

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
                        ivWearable.setVisibility(View.VISIBLE);
                        tvLoading.setVisibility(View.GONE);
                        loadingPanel.setVisibility(View.GONE);
                        break;
                    case 4:
                        if (!connected) {
                            tvLoading.setText(getString(R.string.status_not_found));
                            loadingPanel.setVisibility(View.GONE);
                        }
                        break;
                    case 6:
                        connected = false;
                        tvLoading.setText(R.string.status_searching);
                        ivWearable.setVisibility(View.GONE);
                        tvLoading.setVisibility(View.VISIBLE);
                        loadingPanel.setVisibility(View.VISIBLE);
                        searchForBluetoothDevices();
                }
            }
        };

        if (bluetoothAdapter.isEnabled()) {
//            searchForBluetoothDevices();
            tvWearable.setVisibility(View.VISIBLE);
            ivWearable.setVisibility(View.VISIBLE);
            buttonContainer.setVisibility(View.VISIBLE);
            findViewById(R.id.loadingPanel).setVisibility(View.INVISIBLE);
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
                intent = new Intent(OverviewActivity.this, InterestsActivity.class);
            } else {
                intent = new Intent(OverviewActivity.this, CustomActivity.class);
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
        setScanTimer(999999);
    }

    void setScanTimer(int seconds) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bleConnectionManager.stopScan();
            }
        }, seconds * 1000);
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