package com.sss.test;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class AcceptThread extends Thread {
    private final BluetoothServerSocket mmServerSocket;
    BluetoothManager btManager = BluetoothManager.getInstance();

    public AcceptThread() {
        BluetoothServerSocket tmp = null;

        try {
            tmp = btManager.bluetoothAdapter.listenUsingRfcommWithServiceRecord(
                    BluetoothManager.NAME,
                    BluetoothManager.MY_UUID
            );
        } catch (IOException e) {
            Log.e(BluetoothManager.TAG, "Socket's listen() method failed", e);
        }

        mmServerSocket = tmp;
    }

    public void run() {
        BluetoothSocket socket = null;
        while (true) {
            try {
                socket = mmServerSocket.accept();
            } catch (IOException e) {
                Log.e(BluetoothManager.TAG, "Socket's accept() method failed", e);
                break;
            }

            if (socket != null) {
                System.out.println("success");
//                manageMyConnectedSocket(socket);
                try {
                    mmServerSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(BluetoothManager.TAG, "Could not close the connect socket", e);
        }
    }
}
