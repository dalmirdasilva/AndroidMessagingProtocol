package com.dalmirdasilva.androidmessagingprotocol.adapters;

import android.bluetooth.BluetoothGattDescriptor;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dalmirdasilva.androidmessagingprotocol.R;

import java.util.List;

public class DescriptorAdapter extends ArrayAdapter<BluetoothGattDescriptor> {

    public DescriptorAdapter(@NonNull Context context, List<BluetoothGattDescriptor> descriptors) {
        super(context, 0, descriptors);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothGattDescriptor descriptor = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_descriptor, parent, false);
        }
        TextView uuidView = (TextView) convertView.findViewById(R.id.uuid);
        uuidView.setText(descriptor.getUuid().toString());
        return convertView;
    }
}
