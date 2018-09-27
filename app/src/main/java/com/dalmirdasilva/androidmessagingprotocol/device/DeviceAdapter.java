package com.dalmirdasilva.androidmessagingprotocol.device;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 *
 */
public class DeviceAdapter {

  private static final String TAG = "DeviceListAdapter";

  public static final int MESSAGE_STATE_CHANGE = 0;
  public static final int MESSAGE_RECEIVE = 1;

  private final BluetoothDevice device;
  private final Handler handler;
  private DeviceCommThread connectionThread;

  private DeviceConnState state;

  public DeviceAdapter(DeviceListener deviceListener, BluetoothDevice device) {
    this.device = device;
    this.handler = new MessageHandler(deviceListener);
    state = DeviceConnState.DISCONNECTED;
  }

  private synchronized void setState(DeviceConnState state) {
    this.state = state;
    handler.obtainMessage(MESSAGE_STATE_CHANGE, state).sendToTarget();
  }

  public synchronized void connect() {
    closeCommDeviceThread();
    setState(DeviceConnState.CONNECTING);
    connectionThread = new DeviceCommThread();
    connectionThread.start();
  }

  public synchronized boolean isConnected() {
    return state == DeviceConnState.CONNECTED;
  }

  public synchronized void close() {
    closeCommDeviceThread();
    setState(DeviceConnState.DISCONNECTED);
  }

  public synchronized void write(byte[] out) {
    if (isConnected()) {
      connectionThread.write(out);
    }
  }

  private void closeCommDeviceThread() {
    if (connectionThread != null) {
      connectionThread.close();
      connectionThread = null;
    }
  }

  private class DeviceCommThread extends Thread {

    private BluetoothSocket socket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public DeviceCommThread() {
      setName(TAG);
    }

    private boolean createBluetoothSocket() {
      try {
        ParcelUuid[] parcelUuids = device.getUuids();
        if (parcelUuids.length > 0) {
          UUID firstUuid = parcelUuids[0].getUuid();
          socket = device.createInsecureRfcommSocketToServiceRecord(firstUuid);
          return true;
        }
      } catch (IOException e) {
        Log.e(TAG, "Failed while trying to create socket.", e);
      }
      return false;
    }

    private boolean connectToSocket() {
      Log.i(TAG, "Running connectionThread.");
      if (this.socket != null) {
        try {
          socket.connect();
          setState(DeviceConnState.CONNECTED);
          return true;
        } catch (IOException e) {
          Log.e(TAG, "Unable to connect socket.", e);
          try {
            socket.close();
          } catch (IOException ex) {
            Log.e(TAG, "Unable to close socket during connection failure.", ex);
          }
          setState(DeviceConnState.CONNECTION_FAILED);
          return false;
        }
      }
      return false;
    }

    private boolean createStreams() {
      try {
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        return true;
      } catch (IOException e) {
        Log.e(TAG, "I/O streams not created.", e);
      }
      return false;
    }

    private void startListen() {
      byte[] buffer = new byte[1024];
      while (true) {
        try {
          int bytes = inputStream.read(buffer);
          byte[] copy = Arrays.copyOfRange(buffer, 0, bytes);
          Log.d(TAG, "Received raw: " + Util.formatHex(copy));
          handler.obtainMessage(MESSAGE_RECEIVE, bytes, -1, copy).sendToTarget();
        } catch (IOException e) {
          Log.e(TAG, "Disconnected.", e);
          setState(DeviceConnState.CONNECTION_LOST);
          break;
        }
      }
    }

    public void close() {
      try {
        socket.close();
      } catch (NullPointerException | IOException e) {
        Log.e(TAG, "Close connected socket failed.", e);
        setState(DeviceConnState.CONNECTION_LOST);
      }
    }

    public void write(byte[] buffer) {
      try {
        outputStream.write(buffer);
      } catch (IOException e) {
        Log.e(TAG, "Exception during write.", e);
        setState(DeviceConnState.CONNECTION_LOST);
      }
    }

    @Override
    public void run() {
      if (createBluetoothSocket() && connectToSocket() && createStreams()) {
        startListen();
      }
    }
  }
}
