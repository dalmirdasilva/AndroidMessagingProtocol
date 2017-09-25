package com.dalmirdasilva.androidmessagingprotocol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dalmirdasilva.androidmessagingprotocol.adapters.DeviceAdapter;
import com.dalmirdasilva.androidmessagingprotocol.bluetooth.LowEnergyScanner;

import java.util.ArrayList;

public class SetupDeviceActivity extends AppCompatActivity {

    private static final int ENABLE_DEVICE_REQUEST_CODE = 1;
    private static final String TAG = "SetupDeviceActivity";
    public static final String DEVICE_ADDRESS_RESULT = "DEVICE_ADDRESS_RESULT";

    private LowEnergyScanner lowEnergyScanner;
    private LowEnergyScanner.ScanListener scanListener;
    private DeviceAdapter deviceAdapter;
    private ListView devicesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_device);
        initialize();
        enableDevice();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ENABLE_DEVICE_REQUEST_CODE:
                onEnableDeviceRequestResult(resultCode, data);
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initialize() {
        lowEnergyScanner = new LowEnergyScanner(BluetoothAdapter.getDefaultAdapter());
        scanListener = new LowEnergyScanner.ScanListener() {

            @Override
            public void onDeviceFound(BluetoothDevice device) {
                Log.d(TAG, "Low energy device found: address: " + device.getAddress() + ", name: " + device.getName());
                handleFoundDevice(device);
            }

            @Override
            public void onDiscoveryFinished() {
                Log.d(TAG, "Low energy discovery finished.");
            }
        };
        deviceAdapter = new DeviceAdapter(this, new ArrayList<BluetoothDevice>());
        devicesListView = (ListView) findViewById(R.id.device_list);
        devicesListView.setAdapter(deviceAdapter);
        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = deviceAdapter.getItem(position);
                stopScanDevice();
                sendResult(device);
            }
        });
    }

    void enableDevice() {
        Intent permissionIntent = new Intent(this, EnableDeviceActivity.class);
        startActivityForResult(permissionIntent, ENABLE_DEVICE_REQUEST_CODE);
    }

    private void handleFoundDevice(BluetoothDevice device) {
        deviceAdapter.add(device);
    }


    private void scanDevices() {
        Log.d(TAG, "Starting scanning bluetooth devices.");
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                lowEnergyScanner.startScan(scanListener);
                Log.d(TAG, "Scanner start async task died.");
            }
        });
    }

    private void onEnableDeviceRequestResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            EnableDeviceActivity.EnableResult result = (EnableDeviceActivity.EnableResult) extras.get(EnableDeviceActivity.ENABLE_DEVICE_RESULT);
            if (result.equals(EnableDeviceActivity.EnableResult.SUCCESS)) {
                scanDevices();
            } else {
                Log.d(TAG, "Enable device not successful." + result);
            }
        }
    }

    private void stopScanDevice() {
        Log.d(TAG, "Stop scanning bluetooth devices.");
        AsyncTask.execute(new Runnable() {

            @Override
            public void run() {
                lowEnergyScanner.stopScan();
                Log.d(TAG, "Scanner stop async task died.");
            }
        });
    }

    void sendResult(BluetoothDevice device) {
        Intent intent = new Intent();
        intent.putExtra(DEVICE_ADDRESS_RESULT, device);
        setResult(RESULT_OK, intent);
        finish();
    }
}
