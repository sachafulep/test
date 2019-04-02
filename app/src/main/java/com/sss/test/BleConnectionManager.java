package com.sss.test;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;

import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.UUID;

public class BleConnectionManager {
    public static final int BLE_SEARCHING   = 0;
    public static final int BLE_CONNECTING  = 1;
    public static final int BLE_CONNECTED   = 2;
    public static final int BLE_ERROR       = 3;
    public static final int BLE_STOPPEDSCAN = 4;
    private static final int BLE_WRITING = 5;
    private static final int BLE_DISCONNECTED = 6;


    public static int       BLE_STATUS;

    private final static String DEVICE_NAME = "SSS-Wearable";
    private final static String SERVICE_UUID =  "000046fa-0000-1000-8000-00805f9b34fb";
    private final static String COLOR_UUID =     "000046fb-0000-1000-8000-00805f9b34fb";
    private final static int NUM_LEDS = 20;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    public BluetoothGattService colorService;
    public BluetoothGattCharacteristic colorCharacteristic;
    private Context applicationContext;
    private int mColor;


    public BleConnectionManager(Context applicationContext, BluetoothManager bluetoothManager) {
        this.applicationContext = applicationContext;
        this.bluetoothManager = bluetoothManager;
        this.bluetoothAdapter = bluetoothManager.getAdapter();
        this.bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    static BluetoothManager bluetoothManager;
    static BluetoothAdapter bluetoothAdapter;
    static BluetoothLeScanner bluetoothLeScanner;
    static BluetoothGatt bluetoothGatt;

    @TargetApi(Build.VERSION_CODES.M)
    private BluetoothGattCallback gattCb = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            System.out.println("Switching state..");
            super.onConnectionStateChange(gatt, status, newState);
            System.out.println("New state: " + newState);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    // When device connected look for services
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    //Why'd i disconnect tho?
                    System.out.println("*********************************************************************** disconnected");
                    bluetoothGatt.close();
                    updateStatus(BLE_DISCONNECTED);
//                    bluetoothGatt = null;
                    break;

            }
        }

        // Callback function when service has been discovered.
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            System.out.println("Discovered service BLE");

            super.onServicesDiscovered(gatt, status);
            if(status == BluetoothGatt.GATT_SUCCESS) {
                updateStatus(2);
                for(BluetoothGattService bluetoothGattService : gatt.getServices()){
                    // Check if found service is environmental sensing service
                    if (bluetoothGattService.getUuid().toString().equals(SERVICE_UUID)) {
                        colorService = bluetoothGattService;

                        for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
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
                updateStatus(3);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            System.out.println("************************************************************************\r\n Write result "+ status
                    + "\r\n************************************************************************");
            if(status == 0)
            updateStatus(BLE_CONNECTED);

            if(status == 133){
                //Cancer goes here
                bluetoothGatt.disconnect();
                bluetoothGatt.connect();
                System.out.println("************************************************************************\r\n Tried reconnecting.. Write again? "+ status
                        + "\r\n************************************************************************");

                updateStatus(BLE_CONNECTED);
            }
        }


        // Callback function when characteristic has been read.
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            //TODO: Read array of integers from BLE
            for (BluetoothGattService bluetoothGattService : gatt.getServices()) {
                if (bluetoothGattService.getUuid().toString().equals(SERVICE_UUID)) {
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : bluetoothGattService.getCharacteristics()) {
                        if (bluetoothGattCharacteristic.getUuid().toString().equals(COLOR_UUID)) {
                            colorCharacteristic = bluetoothGattCharacteristic;
                            byte[] b = characteristic.getValue();
                            if(b != null){
                                int[] intArray = new int[b.length /4];
                                int index = intArray.length -1;
                                for (int i = b.length -1; i >= 0 ; i -= 4){ //Reading from back-to-front because of endianism
                                    intArray[index] = b[i] << 24 | (b[i-1] & 0xff) << 16 | (b[i-2] & 0xff) << 8| (b[i-3] & 0xff);
                                    index--;
                                };

                                System.out.println(Arrays.toString(intArray));
                            }

                        }
                    }
                }
            }
        }
    };


    // Callback if bluetooth scan found a device
    @TargetApi(Build.VERSION_CODES.M)
    private ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if(result.getDevice().getName() != null) {
            System.out.println("Found device " + result.getDevice().getName());

                // Check if found device is our nRF52-DK device
                if (result.getDevice().getName().equals(DEVICE_NAME)) {
                    System.out.println("*************************************************************************************************");
                    updateStatus(BLE_CONNECTING);
                    // Connect device
                    bluetoothGatt = result.getDevice().connectGatt(applicationContext,true, gattCb);

                    // Stop scanning for new devices
                    bluetoothLeScanner.stopScan(scanCallback);
                    System.out.println("Stopping scan, i'm connected to NRF");
                }
            }
        }
    };


    // Async function for scanning bluetooth devices
    @TargetApi(Build.VERSION_CODES.M)
    public void startScan() {
        System.out.println("start scanning");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                updateStatus(BLE_SEARCHING);
                // Start scanning for bluetooth devices, when found proceed to scanCallback function
                bluetoothLeScanner.startScan(scanCallback);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void stopScan() {
        // Stop scanning for bluetooth devices
        bluetoothLeScanner.stopScan(scanCallback);
        updateStatus(BLE_STOPPEDSCAN);
    }

    public void updateStatus(int stat){
        BLE_STATUS = stat;
    }

    public boolean writeAsync(){
        //check mBluetoothGatt is available
        if (bluetoothGatt == null) {
            System.out.println("lost connection");
            return false;
        }
//        BluetoothGattService Service = bluetoothGatt.getService(colorService);
        if (colorService == null) {
            System.out.println("service not found!");
            return false;
        }
        if (colorCharacteristic == null) {
            System.out.println("char not found!");
            return false;
        }
        System.out.println(BLE_STATUS);
        if(BLE_STATUS == BLE_DISCONNECTED){
            System.out.println("Looks like i've disconnected, let's retry!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            bluetoothGatt.connect();
            bluetoothGatt.discoverServices();
        }
        if(BLE_STATUS == BLE_WRITING && colorCharacteristic.getWriteType() == BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE){
            //If
            System.out.println("********************* You can't write yet, the previous write has not yet finished bro.");
//            return false;
        }
//        bluetoothGatt.requestMtu(512);  //

//        colorCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        colorCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        //tChar.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        updateStatus(BLE_WRITING);


        byte[] dest = new byte[NUM_LEDS * 4];  //An integer uses 4 bytes
        for(int i = 0; i < NUM_LEDS; i++){
            int x = mColor;
            int j = i << 2;
            System.out.println(Integer.valueOf(String.valueOf(mColor), 16));
            dest[j++] = 00;
            dest[j++] = (byte) ((x >>> 16) & 0xff);
            dest[j++] = (byte) ((x >>> 8) & 0xff);
            dest[j++] = (byte) ((x >>> 0) & 0xff);

        }
        System.out.println(Arrays.toString(dest));


        boolean status = false;
        colorCharacteristic.setValue(dest);
        try{
            status = bluetoothGatt.writeCharacteristic(colorCharacteristic);

        }catch(Exception ex){
            System.out.println("error when writing...");
        }
        System.out.println("*********************************** Done writing \r\n Status " + status);

        return status;
    }
    public void writeCharacteristic(int color){
        mColor = color;
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                writeAsync();
            }
        });
    }

}
