package com.zhao.lock.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.bean.BleBean;

import java.util.List;

import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class BleScanAdapter extends RecyclerView.Adapter<BleScanAdapter.ViewHolder> {
    private Context context;
    private List<BleBean> bleBeanList;

    private OnItemClickListener onItemClickListener;

    public BleScanAdapter(Context context) {
        this.context = context;
    }

    public void setBleBeanList(List<BleBean> bleBeanList) {
        this.bleBeanList = bleBeanList;
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
        BleBean bleBean = bleBeanList.get(position);

        viewHolder.titleTv.setText(bleBean.getBleDevice().getBleName());
        viewHolder.contentTv.setText(bleBean.getBleDevice().getBleAddress());
        viewHolder.signalTv.setText(bleBean.getRssi() + "dBm");
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(bleBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bleBeanList == null ? 0 : bleBeanList.size();
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
        void onItemClick(BleBean bleBean);
    }
}
