package com.dalmirdasilva.androidmessagingprotocol.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;

public class LowEnergyScanner {

    private static final String TAG = "LowEnergyScanner";

    private BluetoothAdapter adapter;
    private ScanCallback scanCallback;

    public LowEnergyScanner(BluetoothAdapter adapter) {
        this.adapter = adapter;
    }

    public void startScan(final ScanListener listener) {
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        if (scanCallback == null) {
            scanCallback = new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    BluetoothDevice device = result.getDevice();
                    listener.onDeviceFound(device);
                }
            };
            scanner.startScan(scanCallback);
        }
    }

    public void stopScan() {
        BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();
        scanner.stopScan(scanCallback);
        scanCallback = null;
    }

    public interface ScanListener {
        void onDeviceFound(BluetoothDevice device);
        void onDiscoveryFinished();
    }
}
