package com.zhao.bank.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhao.bank.R;
import com.zhao.bank.bean.TodoOrdersBean;

import java.util.List;

public class TodoOrdersAdapter extends RecyclerView.Adapter<TodoOrdersAdapter.ViewHolder> {
    private Context context;
    private List<TodoOrdersBean.DataBean> dataBeanList;

    private OnItemClickListener onItemClickListener;

    public TodoOrdersAdapter(Context context) {
        this.context = context;
    }

    public void setDataBeanList(List<TodoOrdersBean.DataBean> dataBeanList) {
        this.dataBeanList = dataBeanList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public TodoOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_ticket_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoOrdersAdapter.ViewHolder viewHolder, int position) {
        TodoOrdersBean.DataBean dataBean = dataBeanList.get(position);

        viewHolder.ticketNumberTv.setText("工单编号:" + dataBean.getWorkId());
        viewHolder.pendingTv.setVisibility(View.VISIBLE);
        viewHolder.lockBodyNumberTv.setText("锁体编号:" + dataBean.getLock().getHexUid());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(dataBean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataBeanList == null ? 0 : dataBeanList.size();
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
        void onItemClick(TodoOrdersBean.DataBean dataBean);
    }
}
