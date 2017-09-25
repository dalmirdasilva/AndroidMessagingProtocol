package com.dalmirdasilva.androidmessagingprotocol;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.dalmirdasilva.androidmessagingprotocol.adapters.CharacteristicAdapter;
import com.dalmirdasilva.androidmessagingprotocol.adapters.DescriptorAdapter;
import com.dalmirdasilva.androidmessagingprotocol.adapters.ServiceAdapter;

import java.util.ArrayList;
import java.util.List;

//                    readCharacteristic(gatt);
//                        Parcel parcel = Parcel.obtain();
//                        parcel.writeByte((byte) 'd');
//                        parcel.writeByte((byte) 'a');
//                        parcel.writeByte((byte) 'l');
//                        parcel.writeByte((byte) 'm');
//                        characteristic.writeToParcel(parcel, 0);

/**
 * ********************************************************************
 * Command             Description			           *
 * ---------------------------------------------------------------- *
 * AT                  Check if the command terminal work normally  *
 * AT+RESET            Software reboot				   *
 * AT+VERSION          Get firmware, bluetooth, HCI and LMP version *
 * AT+HELP             List all the commands		           *
 * AT+NAME             Get/Set local device name                    *
 * AT+PIN              Get/Set pin code for pairing                 *
 * AT+PASS             Get/Set pin code for pairing                 *
 * AT+BAUD             Get/Set baud rate		                   *
 * AT+LADDR            Get local bluetooth address		   *
 * AT+ADDR             Get local bluetooth address		   *
 * AT+DEFAULT          Restore factory default			   *
 * AT+RENEW            Restore factory default			   *
 * AT+STATE            Get current state				   *
 * AT+PWRM             Get/Set power on mode(low power) 		   *
 * AT+POWE             Get/Set RF transmit power 		   *
 * AT+SLEEP            Sleep mode 		                   *
 * AT+ROLE             Get/Set current role.	                   *
 * AT+PARI             Get/Set UART parity bit.                     *
 * AT+STOP             Get/Set UART stop bit.                       *
 * AT+START            System start working.			   *
 * AT+IMME             System wait for command when power on.	   *
 * AT+IBEA             Switch iBeacon mode.	                   *
 * AT+IBE0             Set iBeacon UUID 0.            	           *
 * AT+IBE1             Set iBeacon UUID 1.            	           *
 * AT+IBE2             Set iBeacon UUID 2.            	           *
 * AT+IBE3             Set iBeacon UUID 3.            	           *
 * AT+MARJ             Set iBeacon MARJ .            	           *
 * AT+MINO             Set iBeacon MINO .            	           *
 * AT+MEA              Set iBeacon MEA .            	           *
 * AT+NOTI             Notify connection event .                    *
 * AT+UUID             Get/Set system SERVER_UUID .            	   *
 * AT+CHAR             Get/Set system CHAR_UUID .            	   *
 * -----------------------------------------------------------------*
 * Note: (M) = The command support slave mode only. 		   *
 * For more information, please visit http://www.bolutek.com        *
 * Copyright@2013 www.bolutek.com. All rights reserved.		   *
 * *******************************************************************
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int SETUP_DEVICE_REQUEST_CODE = 1;

    private BluetoothGatt bluetoothGatt;
    private TextView deviceNameTextView;
    private TextView deviceAddressTextView;
    private TextView deviceRssiTextView;

    private ServiceAdapter servicesAdapter;
    private CharacteristicAdapter characteristicsAdapter;
    private DescriptorAdapter descriptorsAdapter;

    private ListView servicesListView;
    private ListView characteristicsListView;
    private ListView descriptorsListView;

    private TextView characteristicUuidView;
    private TextView characteristicDataLengthView;
    private TextView characteristicDataView;
    private TextView characteristicHowManyDescriptors;

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
        servicesListView = (ListView) findViewById(R.id.device_services_list);
        characteristicUuidView = (TextView) findViewById(R.id.characteristic_uuid);
        characteristicDataLengthView = (TextView) findViewById(R.id.characteristic_data_length);
        characteristicDataView = (TextView) findViewById(R.id.characteristic_data);
        characteristicHowManyDescriptors = (TextView) findViewById(R.id.characteristic_how_namy_descriptors);
        servicesAdapter = new ServiceAdapter(this, new ArrayList<BluetoothGattService>());
        servicesListView.setAdapter(servicesAdapter);
        servicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothGattService service = servicesAdapter.getItem(position);
                List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                characteristicsAdapter.clear();
                for (BluetoothGattCharacteristic characteristic : characteristics) {
                    characteristicsAdapter.add(characteristic);
                }
            }
        });
        characteristicsListView = (ListView) findViewById(R.id.characteristics_list);
        characteristicsAdapter = new CharacteristicAdapter(this, new ArrayList<BluetoothGattCharacteristic>());
        characteristicsListView.setAdapter(characteristicsAdapter);
        characteristicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothGattCharacteristic characteristic = characteristicsAdapter.getItem(position);
                bluetoothGatt.readCharacteristic(characteristic);
            }
        });

        descriptorsListView = (ListView) findViewById(R.id.descriptors_list);
        descriptorsAdapter = new DescriptorAdapter(this, new ArrayList<BluetoothGattDescriptor>());
        descriptorsListView.setAdapter(descriptorsAdapter);
        descriptorsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothGattDescriptor descriptor = descriptorsAdapter.getItem(position);
                bluetoothGatt.readDescriptor(descriptor);
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
                    BluetoothDevice device = (BluetoothDevice) extras.get(SetupDeviceActivity.DEVICE_ADDRESS_RESULT);
                    setupDevice(device);
                }
                break;
        }
    }

    private void setupDevice(BluetoothDevice device) {

        if (device != null) {
            Log.d(TAG, "Connecting GATT to device: " + device.getAddress());
            deviceNameTextView.setText(device.getName());
            deviceAddressTextView.setText(device.getAddress());
            bluetoothGatt = device.connectGatt(this, false, new BluetoothGattCallback() {

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    Log.d(TAG, "Connection State Changed: status: " + status + ", newState: " + newState);

                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        gatt.readRemoteRssi();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    Log.d(TAG, "Services Discovered: status: " + status);
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        List<BluetoothGattService> services = gatt.getServices();
                        Log.d(TAG, "How many services: " + services.size());
                        for (final BluetoothGattService service : services) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    servicesAdapter.add(service);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                    Log.d(TAG, "onCharacteristicRead: status: " + status);
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            byte[] characteristicsBytes = characteristic.getValue();
                            characteristicUuidView.setText(characteristic.getUuid().toString());
                            characteristicDataLengthView.setText(String.valueOf(characteristicsBytes.length));
                            characteristicDataView.setText(characteristic.getStringValue(0));
                            List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
                            characteristicHowManyDescriptors.setText(String.valueOf(descriptors.size()));
                            descriptorsAdapter.clear();
                            for (BluetoothGattDescriptor descriptor : descriptors) {
                                descriptorsAdapter.add(descriptor);
                            }
                        }
                    });
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    Log.d(TAG, "onCharacteristicWrite:: " + gatt.toString() + " characteristic: " + characteristic.toString() + " statue:" + status);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    Log.d(TAG, "onCharacteristicChanged:: " + gatt.toString() + " characteristic: " + characteristic.toString());

                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);
                    Log.d(TAG, "onDescriptorRead:: " + gatt.toString() + " descriptor: " + descriptor.toString() + " statue:" + status);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                    Log.d(TAG, "onDescriptorWrite:: " + gatt.toString() + " descriptor: " + descriptor.toString() + " statue:" + status);
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);
                    Log.d(TAG, "onReliableWriteCompleted:: " + gatt.toString() + " statue:" + status);
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, final int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                    Log.d(TAG, "onReadRemoteRssi:: " + gatt.toString() + " rssi: " + rssi + " statue:" + status);
                    MainActivity.this.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            deviceRssiTextView.setText(Integer.toString(rssi));
                        }
                    });
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                    Log.d(TAG, "onMtuChanged:: " + gatt.toString() + " mtu: " + mtu + " statue:" + status);
                }
            });
        }
    }

    public void listServicesOnClick(View view) {
        bluetoothGatt.discoverServices();
    }
}
