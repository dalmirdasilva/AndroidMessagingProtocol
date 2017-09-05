package com.dalmirdasilva.androidmessagingprotocol.device;

import com.dalmirdasilva.androidmessagingprotocol.device.message.Message;

public interface DeviceListener {

    void onMessageReceived(Message message);

    void onDeviceStateChange(DeviceState state);
}
