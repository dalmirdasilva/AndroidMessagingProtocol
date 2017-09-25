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
    private static final int ENABLE_LOCATION_REQUEST_CODE = 2;
    public static final int FINE_PERMISSIONS_REQUEST_CODE = 3;
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
            case ENABLE_LOCATION_REQUEST_CODE:
                onLocationEnableRequestResult(resultCode, data);
                break;
            case FINE_PERMISSIONS_REQUEST_CODE:
                onFinePermissionsRequestResult(resultCode, data);
                break;
            case BLUETOOTH_ENABLE_REQUEST_CODE:
                onBluetoothEnableRequestResult(resultCode, data);
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void checkPermissions() {
        Intent permissionIntent = new Intent(this, LocationPermissionActivity.class);
        startActivityForResult(permissionIntent, FINE_PERMISSIONS_REQUEST_CODE);
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

    private void enableLocation() {
        if (isLocationEnabled()) {
            onLocationEnabled();
        } else {
            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
            googleApiClient.connect();
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            LocationRequest request = new LocationRequest();
            request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            builder.addLocationRequest(request);
            LocationSettingsRequest req = builder.build();
            PendingResult result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, req);
            result.setResultCallback(new ResultCallback() {

                @Override
                public void onResult(@NonNull Result result) {
                    try {
                        result.getStatus().startResolutionForResult(EnableDeviceActivity.this, ENABLE_LOCATION_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        Log.d(TAG, "Exception when enabling location." + e.getMessage());
                        sendResult(EnableResult.LOCATION_NOT_ENABLED);
                    }
                }
            });
        }
    }

    public boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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

    private void onLocationEnableRequestResult(int resultCode, Intent data) {
        if (isLocationEnabled()) {
            onLocationEnabled();
        } else {
            Log.d(TAG, "Location wan't enabled.");
            sendResult(EnableResult.LOCATION_NOT_ENABLED);
        }
    }

    private void onFinePermissionsRequestResult(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            boolean granted = extras.getBoolean(LocationPermissionActivity.PERMISSIONS_REQUEST_RESULT);
            if (granted) {
                Log.d(TAG, "Fine location permission was granted.");
                sendResult(EnableResult.SUCCESS);
            } else {
                Log.d(TAG, "Fine location permission was not granted.");
                sendResult(EnableResult.LOCATION_NOT_ENABLED);
            }
        }
    }

    private void onBluetoothEnabled() {
        enableLocation();
    }

    private void onLocationEnabled() {
        checkPermissions();
    }

    void sendResult(EnableResult result) {
        Intent intent = new Intent();
        intent.putExtra(ENABLE_DEVICE_RESULT, result);
        setResult(RESULT_OK, intent);
        finish();
    }

    public enum EnableResult implements Serializable {
        SUCCESS(),
        BLUETOOTH_NOT_ENABLED(),
        LOCATION_NOT_ENABLED();
    }
}
