package com.sss.test;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;

import java.util.logging.Handler;

class BleConnectionManager {
    static final String TAG = "Bluetooth";
    private static final int BLE_SEARCHING = 0;
    private static final int BLE_CONNECTING = 1;
    private static final int BLE_CONNECTED = 2;
    private static final int BLE_ERROR = 3;
    private static final int BLE_STOPPED_SCAN = 4;
    private static final int BLE_WRITING = 5;
    private static final int BLE_DISCONNECTED = 6;
    private static int BLE_STATUS;
    private final static String DEVICE_NAME = "SSS-Wearable";
    private final static String SERVICE_UUID = "000046fa-0000-1000-8000-00805f9b34fb";
    private final static String COLOR_UUID = "000046fb-0000-1000-8000-00805f9b34fb";
    private final static int NUM_LEDS = 20;
    private BluetoothGattService colorService;
    private BluetoothGattCharacteristic colorCharacteristic;
    private static BluetoothLeScanner bluetoothLeScanner;
    private static BluetoothGatt bluetoothGatt;
    private Context applicationContext;
    private int mColor;
    private Message msg = Message.obtain();
    private Bundle bdl = new Bundle();

    BleConnectionManager(Context applicationContext, BluetoothManager bluetoothManager) {
        this.applicationContext = applicationContext;
        bluetoothLeScanner = bluetoothManager.getAdapter().getBluetoothLeScanner();
    }

    @TargetApi(Build.VERSION_CODES.M)
    void startScan() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                BLE_STATUS = BLE_SEARCHING;
                bluetoothLeScanner.startScan(scanCallback);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    void stopScan() {
        bluetoothLeScanner.stopScan(scanCallback);
        BLE_STATUS = BLE_STOPPED_SCAN;
        sendBleStatus();
    }

    private void sendBleStatus() {
        bdl.putInt("status", BLE_STATUS);
        msg.setData(bdl);
        MainActivity.handler.sendMessage(msg);
    }

    void writeCharacteristic(int color) {
        mColor = color;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                writeAsync();
            }
        });
    }

    private void writeAsync() {
        if (bluetoothGatt == null) {
            return;
        }

        if (colorService == null) {
            return;
        }

        if (colorCharacteristic == null) {
            return;
        }

        if (BLE_STATUS == BLE_DISCONNECTED) {
            bluetoothGatt.connect();
            bluetoothGatt.discoverServices();
        }

        if (BLE_STATUS == BLE_WRITING && colorCharacteristic.getWriteType() ==
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE) {
            colorCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            BLE_STATUS = BLE_WRITING;

            byte[] dest = new byte[NUM_LEDS * 4];
            for (int i = 0; i < NUM_LEDS; i++) {
                int x = mColor;
                int j = i << 2;
                dest[j++] = 00;
                dest[j++] = (byte) ((x >>> 16) & 0xff);
                dest[j++] = (byte) ((x >>> 8) & 0xff);
                dest[j++] = (byte) ((x) & 0xff);
            }

            colorCharacteristic.setValue(dest);
            try {
                bluetoothGatt.writeCharacteristic(colorCharacteristic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getDevice().getName() != null) {
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
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    bluetoothGatt.close();
                    BLE_STATUS = BLE_DISCONNECTED;
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                BLE_STATUS = BLE_CONNECTED;
                for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                    // Check if found service is environmental sensing service
                    if (bluetoothGattService.getUuid().toString().equals(SERVICE_UUID)) {
                        colorService = bluetoothGattService;

                        for (BluetoothGattCharacteristic bluetoothGattCharacteristic :
                                bluetoothGattService.getCharacteristics()) {
                            // Check if found characteristic is Temperature characteristic
                            if (bluetoothGattCharacteristic.getUuid().toString().equals(COLOR_UUID)) {
                                colorCharacteristic = bluetoothGattCharacteristic;
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
                                          BluetoothGattCharacteristic characteristic, int status) {
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
                                         BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                if (bluetoothGattService.getUuid().toString().equals(SERVICE_UUID)) {
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic :
                            bluetoothGattService.getCharacteristics()) {
                        if (bluetoothGattCharacteristic.getUuid().toString().equals(COLOR_UUID)) {
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
}
