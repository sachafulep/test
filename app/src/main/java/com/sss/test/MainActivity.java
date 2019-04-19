package com.sss.test;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    LocationManager locationManager;
    ServiceView svInternet;
    ServiceView svBluetooth;
    ServiceView svLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OverviewActivity.class);
                startActivity(intent);
            }
        });

        svInternet = findViewById(R.id.svInternet);
        svBluetooth = findViewById(R.id.svBluetooth);
        svLocation = findViewById(R.id.svLocation);

        svBluetooth.setState(bluetoothAdapter.isEnabled());
        svInternet.setState(hasInternetConnection());
        svLocation.setState(hasLocationEnabled());

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(LocationManager.MODE_CHANGED_ACTION);
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");


        registerReceiver(getReceiver(), filter);
    }

    private boolean hasLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean hasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    private BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR);
                    switch (state) {
                        case BluetoothAdapter.STATE_OFF:
                            svBluetooth.setState(false);
                            break;
                        case BluetoothAdapter.STATE_ON:
                            svBluetooth.setState(true);
                            break;
                    }
                }

                if (action.equals(LocationManager.MODE_CHANGED_ACTION)) {
                    svLocation.setState(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
                }
            }
        };
    }
}