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

public class DeviceAdapter {

    private static final String TAG = "DeviceAdapter";

    public static final int MESSAGE_STATE_CHANGE = 0;
    public static final int MESSAGE_RECEIVE = 1;

    private final BluetoothDevice device;
    private final Handler handler;
    private ConnectionThread connectionThread;
    private CommunicationThread communicationThread;

    private DeviceState state;

    public DeviceAdapter(DeviceListener deviceListener, BluetoothDevice device) {
        this.device = device;
        this.handler = new MessageHandler(deviceListener);
        state = DeviceState.STATE_INITIAL;
    }

    private synchronized void setState(DeviceState state) {
        this.state = state;
        handler.obtainMessage(MESSAGE_STATE_CHANGE, state).sendToTarget();
    }

    public synchronized void connect() {
        closeThreads();
        connectionThread = new ConnectionThread();
        connectionThread.start();
        setState(DeviceState.STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket) {
        closeThreads();
        communicationThread = new CommunicationThread(socket);
        communicationThread.start();
        setState(DeviceState.STATE_CONNECTED);
    }

    public synchronized boolean isConnected() {
        return state == DeviceState.STATE_CONNECTED;
    }

    public synchronized void close() {
        closeThreads();
        setState(DeviceState.STATE_INITIAL);
    }

    public void write(byte[] out) {
        CommunicationThread communicationThread;
        synchronized (this) {
            if (state != DeviceState.STATE_CONNECTED) {
                return;
            }
            communicationThread = this.communicationThread;
        }
        communicationThread.write(out);
    }

    private void connectionFailed() {
        closeThreads();
        setState(DeviceState.STATE_CONNECTION_FAILED);
    }

    private void connectionLost() {
        setState(DeviceState.STATE_CONNECTION_LOST);
    }

    private void closeThreads() {
        if (connectionThread != null) {
            connectionThread.close();
            connectionThread = null;
        }
        if (communicationThread != null) {
            communicationThread.close();
            communicationThread = null;
        }
    }

    private class ConnectionThread extends Thread {

        private final BluetoothSocket socket;

        public ConnectionThread() {
            BluetoothSocket socket = null;
            Log.d(TAG, device.fetchUuidsWithSdp() ? "TRUE" : "FALSE");
            if (device.fetchUuidsWithSdp()) {
                ParcelUuid[] uuids = device.getUuids();
                Log.d(TAG, uuids!=null ? uuids.toString():"NULL");
                if (uuids != null && uuids.length > 0) {
                    Log.d(TAG, "Trying...");
                    try {
                        socket = device.createInsecureRfcommSocketToServiceRecord(uuids[0].getUuid());
                        Log.d(TAG, socket.toString());
                    } catch (IOException e) {
                        Log.e(TAG, "Failed while trying to create the socket.", e);
                    }
                }
            }
            this.socket = socket;
        }

        public void run() {
            Log.i(TAG, "Running connectionThread.");
            setName("ConnectionThread");
            if (this.socket != null) {
                try {
                    socket.connect();
                } catch (IOException e) {
                    Log.e(TAG, "Unable to connect socket.", e);
                    try {
                        socket.close();
                    } catch (IOException ee) {
                        Log.e(TAG, "Unable to close socket during connection failure.", ee);
                    }
                    connectionFailed();
                    return;
                }
                synchronized (DeviceAdapter.this) {
                    connectionThread = null;
                }
                connected(socket);
            } else {
                connectionFailed();
                Log.d(TAG, "Socket is null, cannot connect.");
            }
        }

        public void close() {
            try {
                socket.close();
            } catch (NullPointerException | IOException e) {
                Log.e(TAG, "Close connected socket failed.", e);
            }
        }
    }

    private class CommunicationThread extends Thread {

        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public CommunicationThread(BluetoothSocket socket) {
            Log.d(TAG, "Creating CommunicationThread");
            this.socket = socket;
            InputStream is = null;
            OutputStream os = null;
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "I/O streams not created.", e);
            }
            inputStream = is;
            outputStream = os;
        }

        public void run() {
            Log.i(TAG, "Running CommunicationThread.");
            setName("CommunicationThread");
            byte[] buffer = new byte[1024];
            while (true) {
                try {
                    int bytes = inputStream.read(buffer);
                    byte[] copy = Arrays.copyOfRange(buffer, 0, bytes);
                    Log.d(TAG, "Received raw: " + Util.formatHex(copy));
                    handler.obtainMessage(MESSAGE_RECEIVE, bytes, -1, copy).sendToTarget();
                } catch (IOException e) {
                    Log.e(TAG, "Disconnected.", e);
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                Log.e(TAG, "Exception during write.", e);
            }
        }

        public void close() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed.", e);
            }
        }
    }
}
