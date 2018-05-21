package com.dalmirdasilva.androidmessagingprotocol;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

import java.io.Serializable;

public class EnableDeviceActivity extends AppCompatActivity {

    private static final int BLUETOOTH_ENABLE_REQUEST_CODE = 1;
    public static final String ENABLE_DEVICE_RESULT = "ENABLE_DEVICE_RESULT";
    private static final String TAG = "EnableDeviceActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enable_device);
        enableBluetooth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case BLUETOOTH_ENABLE_REQUEST_CODE:
                onBluetoothEnableRequestResult(resultCode, data);
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            onBluetoothEnabled();
        } else {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, BLUETOOTH_ENABLE_REQUEST_CODE);
        }
    }

    private void onBluetoothEnableRequestResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "Bluetooth enabled.");
            onBluetoothEnabled();
        } else {
            Log.d(TAG, "Bluetooth was not enabled.");
            sendResult(EnableResult.BLUETOOTH_NOT_ENABLED);
        }
    }

    private void onBluetoothEnabled() {
        sendResult(EnableResult.SUCCESS);
    }

    void sendResult(EnableResult result) {
        Intent intent = new Intent();
        intent.putExtra(ENABLE_DEVICE_RESULT, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    public enum EnableResult implements Serializable {
        SUCCESS(),
        BLUETOOTH_NOT_ENABLED();
    }
}
