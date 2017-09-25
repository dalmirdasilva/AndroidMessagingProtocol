package com.dalmirdasilva.androidmessagingprotocol.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
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

public class CharacteristicAdapter extends ArrayAdapter<BluetoothGattCharacteristic> {

    public CharacteristicAdapter(@NonNull Context context, List<BluetoothGattCharacteristic> characteristics) {
        super(context, 0, characteristics);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothGattCharacteristic characteristic = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_characteristic, parent, false);
        }
        TextView uuidView = (TextView) convertView.findViewById(R.id.uuid);
        uuidView.setText(characteristic.getUuid().toString());
        return convertView;
    }
}
