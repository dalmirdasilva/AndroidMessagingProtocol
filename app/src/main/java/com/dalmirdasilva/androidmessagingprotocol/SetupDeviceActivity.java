package com.dalmirdasilva.androidmessagingprotocol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dalmirdasilva.androidmessagingprotocol.adapters.DeviceListAdapter;

import java.util.ArrayList;

public class SetupDeviceActivity extends AppCompatActivity {

  private static final int ENABLE_DEVICE_REQUEST_CODE = 1;
  private static final String TAG = "SetupDeviceActivity";
  public static final String DEVICE_ADDRESS_RESULT = "DEVICE_ADDRESS_RESULT";

  private DeviceListAdapter deviceListAdapter;
  private ListView devicesListView;
  private BroadcastReceiver receiver;

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
    IntentFilter filter = new IntentFilter();
    filter.addAction(BluetoothDevice.ACTION_FOUND);
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
    receiver = new BroadcastReceiver() {

      @Override
      public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {

        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
          BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          handleFoundDevice(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(device.getAddress()));
        }
      }
    };
    registerReceiver(receiver, filter);
    BluetoothAdapter.getDefaultAdapter().startDiscovery();
    deviceListAdapter = new DeviceListAdapter(this, new ArrayList<BluetoothDevice>());
    devicesListView = (ListView) findViewById(R.id.device_list);
    devicesListView.setAdapter(deviceListAdapter);
    devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = deviceListAdapter.getItem(position);
        stopScanDevice();
        sendResult(device);
      }
    });
  }

  @Override
  protected void onStop() {
    if (receiver != null) {
      unregisterReceiver(receiver);
    }
    super.onStop();
  }

  void enableDevice() {
    Intent permissionIntent = new Intent(this, EnableDeviceActivity.class);
    startActivityForResult(permissionIntent, ENABLE_DEVICE_REQUEST_CODE);
  }

  private void handleFoundDevice(BluetoothDevice device) {
    Log.d(TAG, "Device found: address: " + device.getAddress() + ", name: " + device.getName());
    deviceListAdapter.add(device);
  }


  private void scanDevices() {
    Log.d(TAG, "Starting scanning bluetooth devices.");
    BluetoothAdapter.getDefaultAdapter().startDiscovery();
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
    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
  }

  void sendResult(BluetoothDevice device) {
    Intent intent = new Intent();
    intent.putExtra(DEVICE_ADDRESS_RESULT, device);
    setResult(RESULT_OK, intent);
    finish();
  }
}
