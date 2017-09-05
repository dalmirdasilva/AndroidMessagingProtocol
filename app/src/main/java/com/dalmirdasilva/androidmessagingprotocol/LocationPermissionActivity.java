package com.dalmirdasilva.androidmessagingprotocol;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LocationPermissionActivity extends AppCompatActivity {

    public static final int LOCATION_PERMISSIONS_REQUEST = 1;
    public static final String PERMISSIONS_REQUEST_RESULT = "PERMISSIONS_REQUEST_RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        checkLocationPermission();
    }

    public boolean hasPermission() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void checkLocationPermission() {
        if (hasPermission()) {
            sendResult(true);
            return;
        }
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSIONS_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSIONS_REQUEST: {
               sendResult(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
            }
        }
    }

    private void sendResult(boolean granted) {
        Intent intent = new Intent();
        intent.putExtra(PERMISSIONS_REQUEST_RESULT, granted);
        setResult(RESULT_OK, intent);
        finish();
    }
}
