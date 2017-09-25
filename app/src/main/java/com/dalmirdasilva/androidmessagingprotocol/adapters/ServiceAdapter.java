package com.dalmirdasilva.androidmessagingprotocol.adapters;

import android.bluetooth.BluetoothGattService;
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

public class ServiceAdapter extends ArrayAdapter<BluetoothGattService> {

    public ServiceAdapter(@NonNull Context context, List<BluetoothGattService> services) {
        super(context, 0, services);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BluetoothGattService service = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_service, parent, false);
        }
        TextView uuidView = (TextView) convertView.findViewById(R.id.uuid);
        uuidView.setText(service.getUuid().toString());
        return convertView;
    }
}
