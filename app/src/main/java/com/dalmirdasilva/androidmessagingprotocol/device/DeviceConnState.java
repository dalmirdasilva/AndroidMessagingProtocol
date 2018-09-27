package com.dalmirdasilva.androidmessagingprotocol.device;

/**
 * DeviceConnState represents the connection state of the remote device
 */
public enum DeviceConnState {

    DISCONNECTED(0, "Device not connected"),
    CONNECTING(1, "Connecting device"),
    CONNECTED(2, "Device connected"),
    CONNECTION_FAILED(3, "Connection fail"),
    CONNECTION_LOST(4, "Connection lost");

    private final int state;
    private final String description;

    /**
     * DeviceConnState
     *
     * @param state Internal state number
     * @param description   Human readable description.
     */
    DeviceConnState(int state, String description) {
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
