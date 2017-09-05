package com.dalmirdasilva.androidmessagingprotocol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.dalmirdasilva.androidmessagingprotocol.bluetooth.DiscoveryListener;
import com.dalmirdasilva.androidmessagingprotocol.bluetooth.LocationEnabledListener;
import com.dalmirdasilva.androidmessagingprotocol.bluetooth.LowEnergyScanner;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;

public class SetupDeviceActivity extends AppCompatActivity implements DiscoveryListener, LocationEnabledListener {

    private static final String TAG = "SetupDeviceActivity";
    public static final String DEVICE_ADDRESS_RESULT = "DEVICE_ADDRESS_RESULT";
    private static final int ENABLE_LOCATION_REQUEST_CODE = 2;
    public static final int FINE_PERMISSIONS_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_device);
        enableBluetooth();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ENABLE_LOCATION_REQUEST_CODE:
                onLocationEnabled(isLocationEnabled());
                break;
            case FINE_PERMISSIONS_REQUEST_CODE:
                onFinePermissionResult(resultCode, data);
        }
    }

    private void onFinePermissionResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            boolean granted = extras.getBoolean(LocationPermissionActivity.PERMISSIONS_REQUEST_RESULT);

            Log.d(TAG, granted ? "PERMISSIONS_REQUEST_RESULT granted" : "PERMISSIONS_REQUEST_RESULT NOT granted");
        }
    }

    private void checkPermissions() {
        Intent permissionIntent = new Intent(this, LocationPermissionActivity.class);
        startActivityForResult(permissionIntent, FINE_PERMISSIONS_REQUEST_CODE);
    }

    private void scanDevices() {
        Log.d(TAG, "Starting scanning  bluetooth devices...");

        new LowEnergyScanner(BluetoothAdapter.getDefaultAdapter()).startScan(this, new DiscoveryListener() {
            @Override
            public void onDeviceFound(BluetoothDevice device) {
                Log.d(TAG, "FOUND: " + device.getAddress() + " name " + device.getName());
            }

            @Override
            public void onDiscoveryFinished() {
                Log.d(TAG, "FINISHED");

            }
        });
    }

    private void enableBluetooth() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            onBluetoothEnabled();
        } else {
            BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(final Context context, final Intent intent) {
                    onBluetoothEnabled();
                    context.unregisterReceiver(this);
                }
            };
            registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            bluetoothAdapter.enable();
        }
    }

    private void enableLocation() {
        if (isLocationEnabled()) {
            onLocationEnabled(true);
        } else {
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
            googleApiClient.connect();
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            LocationRequest request = new LocationRequest();
            request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            builder.addLocationRequest(request);
            builder.build();
            PendingResult result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback() {

                @Override
                public void onResult(@NonNull Result result) {
                    try {
                        result.getStatus().startResolutionForResult(SetupDeviceActivity.this, ENABLE_LOCATION_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        onLocationEnabled(false);
                    }
                }
            });
        }
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void onBluetoothEnabled() {
        enableLocation();
    }

    @Override
    public void onDeviceFound(BluetoothDevice device) {

    }

    @Override
    public void onDiscoveryFinished() {

    }

    @Override
    public void onLocationEnabled(boolean enabled) {
        if (enabled) {
            scanDevices();
        } else {
            sendResult(null);
        }
    }

    void sendResult(String address) {
        Intent intent = new Intent();
        intent.putExtra(DEVICE_ADDRESS_RESULT, address);
        setResult(RESULT_OK, intent);
        finish();
    }
}
