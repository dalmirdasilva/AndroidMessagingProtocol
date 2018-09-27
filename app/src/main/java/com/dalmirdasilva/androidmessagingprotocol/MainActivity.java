package com.dalmirdasilva.androidmessagingprotocol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.dalmirdasilva.androidmessagingprotocol.device.DeviceConnState;
import com.dalmirdasilva.androidmessagingprotocol.device.DeviceListener;
import com.dalmirdasilva.androidmessagingprotocol.device.DeviceManager;
import com.dalmirdasilva.androidmessagingprotocol.device.message.AckMessage;
import com.dalmirdasilva.androidmessagingprotocol.device.message.DataMessage;
import com.dalmirdasilva.androidmessagingprotocol.device.message.Message;
import com.dalmirdasilva.androidmessagingprotocol.device.message.PingMessage;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements DeviceListener {

  private static final String TAG = "MainActivity";
  private static final int SETUP_DEVICE_REQUEST_CODE = 1;

  private TextView deviceNameTextView;
  private TextView deviceAddressTextView;
  private Button sendMessageButton;
  private ToggleButton toggleButton;
  private Timer timer;

  private DeviceManager manager;
  private BluetoothDevice device;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    initialize();
    selectDevice();
  }

  @Override
  protected void onPause() {
    super.onPause();
    System.out.println("Disconnecting...");
    disconnect();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (manager != null && device != null) {
//      manager.setupDevice(device);
    }
  }

  private void disconnect() {
    if (manager != null) {
      manager.disconnect();
    }
  }

  private void initialize() {
    initializeView();
    initializeViewListeners();
  }

  private void initializeView() {
    deviceNameTextView = (TextView) findViewById(R.id.view_device_name);
    deviceAddressTextView = (TextView) findViewById(R.id.view_device_address);
    sendMessageButton = (Button) findViewById(R.id.send_message);
    toggleButton = (ToggleButton) findViewById(R.id.toggle_button);
  }

  private void initializeViewListeners() {
    sendMessageButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        Message message = new PingMessage();
        manager.sendMessage(message);
      }
    });
    toggleButton.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        if (timer == null) {
          startSendingMessages();
        } else {
          stopSendingMessages();
        }
      }
    });
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
          device = (BluetoothDevice) extras.get(SetupDeviceActivity.DEVICE_ADDRESS_RESULT);
          setupDevice();
        }
        break;
    }
  }

  private void setupDevice() {
    if (device != null) {
      Log.d(TAG, "Connecting to device: " + device.getAddress());
      deviceNameTextView.setText(device.getName());
      deviceAddressTextView.setText(device.getAddress());
      manager = new DeviceManager(this);
      manager.setupDevice(device);
    }
  }

  @Override
  public void onMessageReceived(Message message) {
    Log.d(TAG, "Message received: " + message.toString());
  }

  @Override
  public void onDeviceConnStateChange(DeviceConnState state) {
    Log.d(TAG, "Device state changed: " + state.toString());
  }

  private void stopSendingMessages() {
    if (timer != null) {
      timer.cancel();
      timer = null;
    }
  }

  protected void startSendingMessages() {
    stopSendingMessages();
    TimerTask task = new TimerTask() {

      public void run() {
        System.out.println("Task performed on: " + new Date() + "n" + "Thread's name: " + Thread.currentThread().getName());
        Message message = new DataMessage(new Byte[] {0x44, 0x55});
        manager.sendMessage(message);
      }
    };
    timer = new Timer("Timer");
    long interval = 100L;

    timer.scheduleAtFixedRate(task, 0, interval);
  }
}
