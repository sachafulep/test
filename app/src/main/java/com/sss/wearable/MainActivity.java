package com.sss.wearable;

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
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sss.wearable.Classes.Database;
import com.sss.wearable.Views.ServiceView;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    ServiceView svInternet;
    ServiceView svBluetooth;
    ServiceView svLocation;
    Intent intent;
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        Database.createInstance(MainActivity.this);
        locationManager = (LocationManager) MainActivity.this.getSystemService(
                Context.LOCATION_SERVICE);
        intent = new Intent(MainActivity.this,
                OverviewActivity.class);

        svInternet = findViewById(R.id.svInternet);
        svBluetooth = findViewById(R.id.svBluetooth);
        svLocation = findViewById(R.id.svLocation);

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        svBluetooth.setState(bluetoothAdapter.isEnabled());
        hasInternetConnection();
        svLocation.setState(hasLocationEnabled());

        if (svBluetooth.isActive() && svInternet.isActive() && svLocation.isActive()) {
            unregisterReceiver(receiver);
            startActivity(intent);
            finish();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(LocationManager.MODE_CHANGED_ACTION);

        receiver = getReceiver();
        registerReceiver(receiver, filter);
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

        if (svBluetooth.isActive() && svInternet.isActive() && svLocation.isActive()) {
            unregisterReceiver(receiver);
            startActivity(intent);
            finish();
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
}