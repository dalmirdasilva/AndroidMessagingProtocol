package com.dalmirdasilva.androidmessagingprotocol.device;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.dalmirdasilva.androidmessagingprotocol.device.message.Message;
import com.dalmirdasilva.androidmessagingprotocol.device.message.MessageFactory;

public class DeviceManager implements DeviceListener {

    private static final String TAG = "DeviceManager";

    private DeviceListener deviceListener;
    private DeviceAdapter deviceAdapter;

    public DeviceManager(DeviceListener deviceListener) {
        this.deviceListener = deviceListener;
    }

    public void setupDevice(BluetoothDevice device) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(device.getAddress());
            deviceAdapter = new DeviceAdapter(deviceListener, remoteDevice);
            deviceAdapter.connect();
        }
    }

    public void sendMessage(Message message) {
        byte[] array = message.toArray();
        Log.d(TAG, "Sending message: " + message.toString());
        if (deviceAdapter != null && deviceAdapter.isConnected()) {
            deviceAdapter.write(array);
        }
    }

    public void disconnect() {
        if (deviceAdapter != null) {
            deviceAdapter.close();
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        switch (message.getType()) {
            case Message.TYPE_CONNECT:
                sendMessage(MessageFactory.newConnectMessage());
                break;
            case Message.TYPE_EPOCH:
                sendMessage(MessageFactory.newEpochMessage());
                break;
            case Message.TYPE_PING:
                sendMessage(MessageFactory.newAckMessage());
                break;
        }
        Log.d(TAG, "Message received: " + message.toString());
        deviceListener.onMessageReceived(message);
    }

    @Override
    public void onDeviceStateChange(DeviceState state) {
        deviceListener.onDeviceStateChange(state);
    }
}
