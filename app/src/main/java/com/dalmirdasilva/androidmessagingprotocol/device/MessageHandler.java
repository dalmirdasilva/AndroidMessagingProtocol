package com.dalmirdasilva.androidmessagingprotocol.device;

import android.util.Log;

import com.dalmirdasilva.androidmessagingprotocol.device.message.Message;
import com.dalmirdasilva.androidmessagingprotocol.device.message.MessageParser;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class MessageHandler extends android.os.Handler {

    private static final String TAG = "MessageHandler";

    private final WeakReference<DeviceListener> deviceListener;
    private final MessageParser parser;

    public MessageHandler(DeviceListener deviceListener) {
        this.deviceListener = new WeakReference<>(deviceListener);
        this.parser = new MessageParser();
    }

    private void processIncomingBytes(byte[] buffer) {
        int from = 0;
        int to = buffer.length - 1;
        while(from < to) {
            byte[] copy = Arrays.copyOfRange(buffer, from, to);
            int parsed = parser.parse(copy);
            if (parser.wasMessageDecoded()) {
                Message decodedMessage = parser.collectDecodedMessage();
                deviceListener.get().onMessageReceived(decodedMessage);
            } else if (parsed != copy.length) {
                Log.e(TAG, "Decoded bytes shorter than buffer length and none message decoded. It should not happen!");
                parser.reset();
            }
            from += parsed;
        }
    }

    @Override
    public void handleMessage(android.os.Message osMessage) {
        switch (osMessage.what) {
            case DeviceAdapter.MESSAGE_STATE_CHANGE:
                deviceListener.get().onDeviceStateChange((DeviceState) osMessage.obj);
                break;
            case DeviceAdapter.MESSAGE_RECEIVE:
                Log.d(TAG, "Message received.");
                byte[] buffer = (byte[]) osMessage.obj;
                processIncomingBytes(buffer);
                break;
        }
    }
}
