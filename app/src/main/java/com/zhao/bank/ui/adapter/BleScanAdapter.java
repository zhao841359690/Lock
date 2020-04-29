package com.zhao.bank.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.zhao.bank.R;

import java.util.List;

public class BleScanAdapter extends RecyclerView.Adapter<BleScanAdapter.ViewHolder> {
    private Context context;
    private List<BleDevice> bleDeviceList;

    private OnItemClickListener onItemClickListener;

    public BleScanAdapter(Context context) {
        this.context = context;
    }

    public void setBleBeanList(List<BleDevice> bleDeviceList) {
        this.bleDeviceList = bleDeviceList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public BleScanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ble_scan_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BleScanAdapter.ViewHolder viewHolder, int position) {
        final int i = position;
        BleDevice bleDevice = bleDeviceList.get(position);

        viewHolder.titleTv.setText(bleDevice.getName());
        viewHolder.contentTv.setText(bleDevice.getMac());
        viewHolder.signalTv.setText(bleDevice.getRssi() + "dBm");
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(bleDevice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bleDeviceList == null ? 0 : bleDeviceList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView contentTv;
        TextView signalTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title_tv);
            contentTv = itemView.findViewById(R.id.content_tv);
            signalTv = itemView.findViewById(R.id.signal_tv);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(BleDevice bleDevice);
    }
}
