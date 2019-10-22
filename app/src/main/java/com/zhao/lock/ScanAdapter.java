package com.zhao.lock;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class ScanAdapter extends RecyclerView.Adapter<ScanAdapter.ViewHolder> {
    private Context context;
    private List<BleDevice> dataList;
    private OnItemClickListener onItemClickListener;


    public ScanAdapter(Context context) {
        this.context = context;
    }

    public void setDataList(List<BleDevice> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onOpenClick(BleDevice bleDevice);

        void onCloseClick(BleDevice bleDevice);

        void onDisconnectClick(BleDevice bleDevice);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan_list, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final BleDevice bleDevice = dataList.get(position);
        viewHolder.deviceNameTv.setText(bleDevice.getBleName());
        viewHolder.openTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onOpenClick(bleDevice);
            }
        });
        viewHolder.closeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onCloseClick(bleDevice);
            }
        });
        viewHolder.disconnectTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onDisconnectClick(bleDevice);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceNameTv;
        TextView openTv;
        TextView closeTv;
        TextView disconnectTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceNameTv = itemView.findViewById(R.id.device_name_tv);
            openTv = itemView.findViewById(R.id.open_tv);
            closeTv = itemView.findViewById(R.id.close_tv);
            disconnectTv = itemView.findViewById(R.id.disconnect_tv);
        }
    }
}
