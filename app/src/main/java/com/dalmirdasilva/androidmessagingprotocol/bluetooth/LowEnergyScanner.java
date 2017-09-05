package com.dalmirdasilva.androidmessagingprotocol.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.util.Log;

public class LowEnergyScanner {

    private static final String TAG = "LowEnergyScanner";

    private BluetoothAdapter adapter;
    private ScanCallback scanCallback;

    public LowEnergyScanner(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    public void startScan(Context context, final DiscoveryListener callback) {
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        scanCallback = new ScanCallback() {

            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                Log.d(TAG, "onScanResult.");
                BluetoothDevice device = result.getDevice();
                callback.onDeviceFound(device);
            }
        };
        Log.d(TAG, "Starting scan.");
        scanner.startScan(scanCallback);
    }

    public void stopScan() {
        BluetoothLeScanner BLEScanner = adapter.getBluetoothLeScanner();
        BLEScanner.stopScan(scanCallback);
    }
}
