package com.sss.test;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

public class ServicesThread implements Runnable {
    private Context context;
    LocationManager locationManager;

    ServicesThread(Context context, LocationManager locationManager) {
        this.context = context;
        this.locationManager = locationManager;
    }

    @Override
    public void run() {

    }

    private void sendMessage(String key, boolean value) {
        Message msg = Message.obtain();
        Bundle bdl = new Bundle();
        bdl.putBoolean(key, value);
        msg.setData(bdl);
//        MainActivity.handler.sendMessage(msg);
    }
}
