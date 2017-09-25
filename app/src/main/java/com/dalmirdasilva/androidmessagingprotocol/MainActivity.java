package com.dalmirdasilva.androidmessagingprotocol;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.dalmirdasilva.androidmessagingprotocol.device.DeviceAdapter;
import com.dalmirdasilva.androidmessagingprotocol.device.DeviceListener;
import com.dalmirdasilva.androidmessagingprotocol.device.DeviceState;
import com.dalmirdasilva.androidmessagingprotocol.device.message.Message;

public class MainActivity extends AppCompatActivity implements DeviceListener {

    private static final String TAG = "MainActivity";
    private static final int SETUP_DEVICE_REQUEST_CODE = 1;

    private TextView deviceNameTextView;
    private TextView deviceAddressTextView;
    private TextView deviceRssiTextView;

    private DeviceAdapter adatapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        selectDevice();
    }

    private void initialize() {
        deviceNameTextView = (TextView) findViewById(R.id.view_device_name);
        deviceAddressTextView = (TextView) findViewById(R.id.view_device_address);
        deviceRssiTextView = (TextView) findViewById(R.id.view_device_rssi);
    }

    private void selectDevice() {
        Intent setupIntent = new Intent(this, SetupDeviceActivity.class);
        startActivityForResult(setupIntent, SETUP_DEVICE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SETUP_DEVICE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    BluetoothDevice device = (BluetoothDevice) extras.get(SetupDeviceActivity.DEVICE_ADDRESS_RESULT);
                    setupDevice(device);
                }
                break;
        }
    }

    private void setupDevice(BluetoothDevice device) {
        if (device != null) {
            Log.d(TAG, "Connecting to device: " + device.getAddress());
            deviceNameTextView.setText(device.getName());
            deviceAddressTextView.setText(device.getAddress());
            adatapter = new DeviceAdapter(this, device);
            adatapter.connect();
        }
    }

    @Override
    public void onMessageReceived(Message message) {
        Log.d(TAG, "Message received: " + message.toString());
    }

    @Override
    public void onDeviceStateChange(DeviceState state) {
        Log.d(TAG, "Device state changed: " + state.toString());
    }
}
