package com.dalmirdasilva.androidmessagingprotocol.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalmirdasilva.androidmessagingprotocol.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {

    private List<String> addressesInList;

    public DeviceListAdapter(@NonNull Context context, List<BluetoothDevice> devices) {
        super(context, 0, devices);
        addressesInList = new ArrayList<>();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothDevice device = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_device, parent, false);
        }
        TextView nameView = (TextView) convertView.findViewById(R.id.name);
        TextView addressView = (TextView) convertView.findViewById(R.id.address);
        nameView.setText(device.getName());
        addressView.setText(device.getAddress());
        return convertView;
    }

    @Override
    public void add(@Nullable BluetoothDevice device) {
        if (!addressesInList.contains(device.getAddress())) {
            addressesInList.add(device.getAddress());
            super.add(device);
        }
    }
}
