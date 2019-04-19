package com.sss.test;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    LocationManager locationManager;
    ServiceView svInternet;
    ServiceView svBluetooth;
    ServiceView svLocation;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = new Intent(MainActivity.this,
                OverviewActivity.class);
        locationManager = (LocationManager) MainActivity.this.getSystemService(
                Context.LOCATION_SERVICE);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        svInternet = findViewById(R.id.svInternet);
        svBluetooth = findViewById(R.id.svBluetooth);
        svLocation = findViewById(R.id.svLocation);

        svBluetooth.setState(bluetoothAdapter.isEnabled());
        hasInternetConnection();
        svLocation.setState(hasLocationEnabled());

        if (svBluetooth.getState() && svInternet.getState() && svLocation.getState()) {
            startActivity(intent);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(LocationManager.MODE_CHANGED_ACTION);

        registerReceiver(getReceiver(), filter);
    }

    private void changeServiceState(String service, boolean state) {
        switch (service) {
            case "internet":
                svInternet.setState(state);
                break;

            case "bluetooth":
                svBluetooth.setState(state);
                break;

            case "location":
                svLocation.setState(state);
                break;
        }

        if (svBluetooth.getState() && svInternet.getState() && svLocation.getState()) {
            startActivity(intent);
        }
    }

    private boolean hasLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void hasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        connectivityManager.requestNetwork(
                new NetworkRequest.Builder().build(),
                new Callback()
        );
    }

    private class Callback extends ConnectivityManager.NetworkCallback {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            changeServiceState("internet", true);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            changeServiceState("internet", false);
        }
    }

    private BroadcastReceiver getReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null) {
                    if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                                BluetoothAdapter.ERROR);
                        switch (state) {
                            case BluetoothAdapter.STATE_OFF:
                                changeServiceState("bluetooth", false);
                                break;
                            case BluetoothAdapter.STATE_ON:
                                changeServiceState("bluetooth", true);
                                break;
                        }
                    }

                    if (action.equals(LocationManager.MODE_CHANGED_ACTION)) {
                        changeServiceState(
                                "location",
                                locationManager.isProviderEnabled(
                                        LocationManager.GPS_PROVIDER
                                )
                        );
                    }
                }
            }
        };
    }
}