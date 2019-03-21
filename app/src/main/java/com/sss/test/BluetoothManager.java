package com.sss.test;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class BluetoothManager {
    private static BluetoothManager instance;
    private Context context = null;
    static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_ACCESS_COURSE_LOCATION = 2;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    static final String TAG = "Bluetooth";
    static final String NAME = "server";
    static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

    BluetoothManager(Context context) {
        this.context = context;
        instance = this;

        if (bluetoothAdapter == null) {
            Log.d("Bluetooth", "Bluetooth adapter was not found.");
        }
    }

    static BluetoothManager getInstance() {
        return instance;
    }

    void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    (Activity) context,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_ACCESS_COURSE_LOCATION);
        }
    }

    boolean checkPairedDevices() {
        for (BluetoothDevice device : pairedDevices) {
            String deviceName = device.getName();

            if (deviceName.equals("JBL GO")) {
                ConnectThread thread = new ConnectThread(device);
//                thread.run();
                return true;
            }
        }

        return false;
    }

    final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress();
                Log.d(TAG, "Device found: " + deviceName + " " + deviceHardwareAddress);

                if (deviceName != null && deviceName.equals("JBL GO")) {
                    Method method = null;
                    try {
                        method = device.getClass().getMethod("createBond");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (method != null) {
                            method.invoke(device);
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG, "Bluetooth device discovery started.");
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "Bluetooth device discovery finished.");
            }
        }
    };
}
