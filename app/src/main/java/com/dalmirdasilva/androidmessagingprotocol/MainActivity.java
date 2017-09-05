package com.dalmirdasilva.androidmessagingprotocol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int SETUP_DEVICE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        selectDevice();
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
                    String address = extras.getString(SetupDeviceActivity.DEVICE_ADDRESS_RESULT);
                    setupDevice(address);
                }
                break;
        }
    }

    private void setupDevice(String address) {
        Log.d(TAG, "THE ADDRESS IS: " + address);
//        deviceManager = new DeviceManager(this);
//        deviceManager.setupDevice(address);
    }
}
