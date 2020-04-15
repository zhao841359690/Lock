package com.zhao.bank.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhao.bank.R;
import com.zhao.bank.bean.WorkOrdersBean;

import java.util.List;

public class MyTicketAdapter extends RecyclerView.Adapter<MyTicketAdapter.ViewHolder> {
    private Context context;
    private List<WorkOrdersBean.DataBean.ContentBean> workOrdersBeanList;

    private OnItemClickListener onItemClickListener;

    public MyTicketAdapter(Context context) {
        this.context = context;
    }

    public void setWorkOrdersBeanList(List<WorkOrdersBean.DataBean.ContentBean> workOrdersBeanList) {
        this.workOrdersBeanList = workOrdersBeanList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyTicketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_ticket_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTicketAdapter.ViewHolder viewHolder, int position) {
        WorkOrdersBean.DataBean.ContentBean contentBean = workOrdersBeanList.get(position);

        viewHolder.ticketNumberTv.setText("工单编号:" + contentBean.getWorkId());
        viewHolder.pendingTv.setVisibility(contentBean.isEffective() ? View.VISIBLE : View.INVISIBLE);
        if (contentBean.getLock() != null) {
            viewHolder.lockBodyNumberTv.setText("锁体编号:" + contentBean.getLock().getUid());
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(contentBean);
            }
        });

    }

    @Override
    public int getItemCount() {
        return workOrdersBeanList == null ? 0 : workOrdersBeanList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView ticketNumberTv;
        TextView pendingTv;
        TextView lockBodyNumberTv;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ticketNumberTv = itemView.findViewById(R.id.ticket_number_tv);
            pendingTv = itemView.findViewById(R.id.pending_tv);
            lockBodyNumberTv = itemView.findViewById(R.id.lock_body_number_tv);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(WorkOrdersBean.DataBean.ContentBean contentBean);
    }
}
