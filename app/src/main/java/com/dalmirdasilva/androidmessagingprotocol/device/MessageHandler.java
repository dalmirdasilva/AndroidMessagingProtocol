package com.dalmirdasilva.androidmessagingprotocol.device;

import android.util.Log;

import com.dalmirdasilva.androidmessagingprotocol.device.message.Message;
import com.dalmirdasilva.androidmessagingprotocol.device.message.MessageParser;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * Processes incoming bytes from the device.
 * <p>
 * The @See{DeviceAdapter} is responsible to manage the communication thread, which eventually will
 * receive bytes from the device. The @See{DeviceAdapter} than sends the data to
 * the @See{MessageHandler} which is responsible to parse the bytes using a @See{MessageParser}.
 * </p>
 */
public class MessageHandler extends android.os.Handler {

  private static final String TAG = "MessageHandler";

  private final WeakReference<DeviceListener> deviceListener;
  private final MessageParser parser;

  public MessageHandler(DeviceListener deviceListener) {
    this.deviceListener = new WeakReference<>(deviceListener);
    this.parser = new MessageParser();
  }

  /**
   * Receives a byte array from and feed @See{MessageParser}.
   * <p>
   * It needs to deal with:
   * </p>
   *
   * @param buffer
   */
  private void processIncomingBytes(byte[] buffer) {
    Log.d(TAG, "Processing incoming bytes: " + Util.formatHex(buffer));
    int from = 0;
    int to = buffer.length;
    while (from < to) {
      byte[] copy = Arrays.copyOfRange(buffer, from, to);
      Log.d(TAG, "Copy of the buffer: " + Util.formatHex(copy));
      int parsed = parser.parse(copy);
      Log.d(TAG, "Parsed: " + parsed);

      // Collect all decoded messages if any
      while (parser.wasMessageDecoded()) {
        Log.d(TAG, "A message was decoded. Collecting it and sending it to the device DeviceListener");
        Message message = parser.collectDecodedMessage();
        deviceListener.get().onMessageReceived(message);
      }

      // If not data was parsed, just discard that first byte and try again,
      // Sometime we will ingest something.
      if (parsed == 0) {
        Log.e(TAG, "No decoded data. Incrementing... We will try again.");
        from++;
      } else if (parsed != copy.length) {
        Log.e(TAG, "Decoded bytes shorter than buffer length and none message decoded. It should not happen!");
        parser.resetState();
      }
      from += parsed;
    }
  }

  @Override
  public void handleMessage(android.os.Message osMessage) {
    switch (osMessage.what) {
      case DeviceAdapter.MESSAGE_STATE_CHANGE:
        deviceListener.get().onDeviceConnStateChange((DeviceConnState) osMessage.obj);
        break;
      case DeviceAdapter.MESSAGE_RECEIVE:
        Log.d(TAG, "Received message data.");
        byte[] buffer = (byte[]) osMessage.obj;
        processIncomingBytes(buffer);
        break;
    }
  }
}
