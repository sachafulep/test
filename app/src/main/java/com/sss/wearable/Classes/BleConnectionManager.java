package com.sss.wearable.Classes;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.sss.wearable.OverviewActivity;

import java.util.List;

public class BleConnectionManager {
    public static final String TAG = "Bluetooth";
    private static final int BLE_SEARCHING = 0;
    private static final int BLE_CONNECTING = 1;
    private static final int BLE_CONNECTED = 2;
    private static final int BLE_ERROR = 3;
    private static final int BLE_STOPPED_SCAN = 4;
    private static final int BLE_WRITING = 5;
    private static final int BLE_DISCONNECTED = 6;
    private static int BLE_STATUS;
    private final static String DEVICE_NAME = "SSS BLE";
    private final static String SERVICE_UUID = "000006fa-0000-1000-8000-00805f9b34fb";
    private final static String COLOR_UUID = "000006fb-0000-1000-8000-00805f9b34fb";
    private final static String INTEREST_SERVICE_UUID = "000001a0-0000-1000-8000-00805f9b34fb";
    private final static String INTEREST_UUID = "000001a2-0000-1000-8000-00805f9b34fb";

    private final static int NUM_LEDS = 20;
    private BluetoothGattService colorService;
    private BluetoothGattService interestService;
    private BluetoothGattCharacteristic colorCharacteristic;
    private BluetoothGattCharacteristic interestCharacteristic;
    private static BluetoothLeScanner bluetoothLeScanner;
    private static BluetoothGatt bluetoothGatt;
    private Context applicationContext;
    private int mColor;
    private Message msg;
    private Bundle bdl = new Bundle();
    private static BleConnectionManager instance = null;

    public static BleConnectionManager getInstance() {
        return instance;
    }

    public BleConnectionManager(Context applicationContext, BluetoothManager bluetoothManager) {
        this.applicationContext = applicationContext;
        bluetoothLeScanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
        instance = this;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void startScan() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                BLE_STATUS = BLE_SEARCHING;
                bluetoothLeScanner.startScan(scanCallback);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void stopScan() {
        bluetoothLeScanner.stopScan(scanCallback);
        BLE_STATUS = BLE_STOPPED_SCAN;
        sendBleStatus();
    }

    private void sendBleStatus() {
        msg = Message.obtain();
        bdl.putInt("status", BLE_STATUS);
        msg.setData(bdl);
        OverviewActivity.handler.sendMessage(msg);
    }

    public void writeCharacteristic(int color) {
        mColor = color;
        writeAsync();
    }

    private void writeAsync() {
        if (bluetoothGatt == null) {
            Log.d(TAG, "BluetoothGatt is null");
            return;
        }

        if (colorService == null) {
            Log.d(TAG, "colorService is null");
            return;
        }

        if (colorCharacteristic == null) {
            Log.d(TAG, "colorCharacteristic is null");
            return;
        }

        if (BLE_STATUS == BLE_DISCONNECTED) {
            Log.d(TAG, "BLE is disconnected");
            bluetoothGatt.connect();
            bluetoothGatt.discoverServices();
        }
        Log.d(TAG, "All is well, go send");
        if (colorCharacteristic.getWriteType() ==
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) {
            colorCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }

        byte[] dest = new byte[4];
        dest[0] = (byte) ((mColor >>> 16) & 0xff);
        dest[1] = (byte) ((mColor >>> 8) & 0xff);
        dest[2] = (byte) ((mColor) & 0xff);
        colorCharacteristic.setValue(dest);
        Log.d(TAG, "Wrote color to controller");
        try {
            if(BLE_STATUS != BLE_WRITING) {
                BLE_STATUS = BLE_WRITING;
                boolean res = bluetoothGatt.writeCharacteristic(colorCharacteristic);
                Log.d(TAG, res + "");
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        }

        BLE_STATUS = BLE_CONNECTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getDevice().getName() != null) {
//                Log.d(TAG, result.getDevice().getName());
                if (result.getDevice().getName().equals(DEVICE_NAME)) {
                    bluetoothLeScanner.stopScan(scanCallback);
                    BLE_STATUS = BLE_CONNECTING;
                    bluetoothGatt = result.getDevice().connectGatt(applicationContext,
                            true, btGattCallback);
                    sendBleStatus();
                }
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.M)
    private BluetoothGattCallback btGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    gatt.discoverServices();
                    Log.d(TAG, "Connected to wearable");
                    stopScan();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    bluetoothGatt.close();
                    BLE_STATUS = BLE_DISCONNECTED;
                    Log.d(TAG, "Disconnected from wearable");
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BLE_STATUS = BLE_CONNECTED;
                sendBleStatus();
                for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                    // Check if found service is environmental sensing service
                    if (bluetoothGattService.getUuid().toString().equals(SERVICE_UUID)) {
                        colorService = bluetoothGattService;
                        Log.d(TAG, "Color service found");

                        for (BluetoothGattCharacteristic bluetoothGattCharacteristic :
                                bluetoothGattService.getCharacteristics()) {
                            // Check if found characteristic is Temperature characteristic
                            Log.d(TAG, "Color char found");

                            if (bluetoothGattCharacteristic.getUuid().toString()
                                    .equals(COLOR_UUID)) {
                                colorCharacteristic = bluetoothGattCharacteristic;
                                // Read Temperature characteristic
                                gatt.readCharacteristic(bluetoothGattCharacteristic);
                            }
                        }
                    }

                    Log.d(TAG, "service UUIDs are equal: " + bluetoothGattService.getUuid().toString().equals(INTEREST_SERVICE_UUID));

                    if (bluetoothGattService.getUuid().toString().equals(INTEREST_SERVICE_UUID)) {

                        interestService = bluetoothGattService;

                        for (BluetoothGattCharacteristic bluetoothGattCharacteristic :
                                bluetoothGattService.getCharacteristics()) {

                            Log.d(TAG, "UUID: " + bluetoothGattCharacteristic.getUuid().toString());

                            Log.d(TAG, "UUIDs are equal: " + bluetoothGattCharacteristic.getUuid().toString().equals(INTEREST_UUID));

                            // Check if found characteristic is Temperature characteristic
                            if (bluetoothGattCharacteristic.getUuid().toString()
                                    .equals(INTEREST_UUID)) {
                                interestCharacteristic = bluetoothGattCharacteristic;
                                // Read Temperature characteristic
                                gatt.readCharacteristic(bluetoothGattCharacteristic);
                            }
                        }
                    }
                }
            } else {
                BLE_STATUS = BLE_ERROR;
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic,
                                          int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == 0)
                BLE_STATUS = BLE_CONNECTED;
            else if (status == 133) {
                bluetoothGatt.disconnect();
                bluetoothGatt.connect();
                BLE_STATUS = BLE_CONNECTED;
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                if (bluetoothGattService.getUuid().toString().equals(SERVICE_UUID)) {
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic :
                            bluetoothGattService.getCharacteristics()) {
                        if (bluetoothGattCharacteristic.getUuid().toString()
                                .equals(COLOR_UUID)) {
                            colorCharacteristic = bluetoothGattCharacteristic;
                            byte[] b = characteristic.getValue();
                            if (b != null) {
                                int[] intArray = new int[b.length / 4];
                                int index = intArray.length - 1;
                                for (int i = b.length - 1; i >= 0; i -= 4) {
                                    intArray[index] = b[i] << 24 | (b[i - 1] & 0xff) << 16 |
                                            (b[i - 2] & 0xff) << 8 | (b[i - 3] & 0xff);
                                    index--;
                                }

                            }
                        }
                    }
                }
            }
        }
    };

    public void writeInterest(List<Interest> selectedInterests) {
        Log.d(TAG, "Start writing to wearable");
        writeAsync2(selectedInterests);
    }

    private void writeAsync2(List<Interest> selectedInterests) {
        if (bluetoothGatt == null) {
            Log.d(TAG, "BluetoothGatt is null");
            return;
        }

        if (interestService == null) {
            Log.d(TAG, "interestService is null");
            return;
        }

        if (interestCharacteristic == null) {
            Log.d(TAG, "interestCharacteristic is null");
            return;
        }

        if (BLE_STATUS == BLE_DISCONNECTED) {
            Log.d(TAG, "BLE is disconnected");
            bluetoothGatt.connect();
            bluetoothGatt.discoverServices();
        }

        Log.d(TAG, "write type: " + interestCharacteristic.getWriteType());

        if (interestCharacteristic.getWriteType() ==
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) {
            interestCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }

        BLE_STATUS = BLE_WRITING;

        Log.d(TAG, "no errors");

        byte[] dest = new byte[20];
        for (int i = 0; i < selectedInterests.size() * 4; i += 4) {
            Interest interest = selectedInterests.get(i / 4);
            int x = interest.getColor();
            byte red = (byte) Color.red(interest.getColor());
            byte green = (byte) Color.green(interest.getColor());
            byte blue = (byte) Color.blue(interest.getColor());
            int j = i;
            int id = interest.getId() + 1;
            dest[j] = (byte) id;          //This int's value is never bigger than a byte(255) so the parse should be safe
            dest[++j] = red;//(byte) ((x >>> 16) & 0xff);
            dest[++j] = green;//(byte) ((x >>> 8) & 0xff);
            dest[++j] = blue;//(byte) ((x) & 0xff);

            Log.d(TAG, "bleh " + (byte) interest.getId() + " " + red + " " + green + " " + blue);
        }

        interestCharacteristic.setValue(dest);
        try {
            bluetoothGatt.writeCharacteristic(interestCharacteristic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
