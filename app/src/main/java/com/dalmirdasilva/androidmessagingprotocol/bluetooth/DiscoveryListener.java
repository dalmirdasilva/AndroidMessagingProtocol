package com.dalmirdasilva.androidmessagingprotocol.bluetooth;

import android.bluetooth.BluetoothDevice;

public interface DiscoveryListener {
    void onDeviceFound(BluetoothDevice device);
    void onDiscoveryFinished();
}
