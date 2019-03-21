package com.sss.test;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class ConnectThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    BluetoothManager btManager = BluetoothManager.getInstance();

    public ConnectThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        mmDevice = device;

        try {
            tmp = device.createRfcommSocketToServiceRecord(BluetoothManager.MY_UUID);
        } catch (IOException e) {
            Log.e(BluetoothManager.TAG, "Socket's create() method failed", e);
        }

        mmSocket = tmp;
    }

    public void run() {
        btManager.bluetoothAdapter.cancelDiscovery();

        try {
            mmSocket.connect();
        } catch (IOException connectException) {
            try {
                connectException.printStackTrace();
                mmSocket.close();
            } catch (IOException closeException) {
                Log.e(BluetoothManager.TAG, "Could not close the client socket", closeException);
            }
            return;
        }

        System.out.println("nice nice nice nice");
    }

    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(BluetoothManager.TAG, "Could not close the client socket", e);
        }
    }
}

