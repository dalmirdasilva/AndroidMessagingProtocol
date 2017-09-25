package com.dalmirdasilva.androidmessagingprotocol.device;

import android.bluetooth.BluetoothProfile;

public enum DeviceState {

    STATE_INITIAL(0, "Device not connected"),
    STATE_CONNECTING(1, "Connecting device"),
    STATE_CONNECTED(2, "Device connected"),
    STATE_CONNECTION_FAILED(3, "Connection fail"),
    STATE_CONNECTION_LOST(4, "Connection lost");

    private final int state;
    private final String description;

    DeviceState(int state, String description) {
        this.state = state;
        this.description = description;
    }

    public int getState() {
        return state;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return description;
    }
}
