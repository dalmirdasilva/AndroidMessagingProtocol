package com.dalmirdasilva.androidmessagingprotocol.device;

import com.dalmirdasilva.androidmessagingprotocol.device.message.Message;

public interface DeviceListener {

    void onMessageReceived(Message message);

    void onDeviceConnStateChange(DeviceConnState state);
}
