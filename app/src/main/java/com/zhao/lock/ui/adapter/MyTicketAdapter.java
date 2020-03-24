package com.zhao.lock.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.bean.MyTicketBean;

import java.util.List;

public class MyTicketAdapter extends RecyclerView.Adapter<MyTicketAdapter.ViewHolder> {
    private Context context;
    private List<MyTicketBean> myTicketBeanList;

    private OnItemClickListener onItemClickListener;

    public MyTicketAdapter(Context context) {
        this.context = context;
    }

    public void setMyTicketBeanList(List<MyTicketBean> myTicketBeanList) {
        this.myTicketBeanList = myTicketBeanList;
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
    public MyTicketAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_my_ticket_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyTicketAdapter.ViewHolder viewHolder, int position) {
        MyTicketBean myTicketBean = myTicketBeanList.get(position);

        viewHolder.ticketNumberTv.setText("工单编号:" + myTicketBean.getTicNb());
        viewHolder.pendingTv.setVisibility(myTicketBean.isP() ? View.VISIBLE : View.INVISIBLE);
        viewHolder.lockBodyNumberTv.setText("锁体编号:" + myTicketBean.getLocNb());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        return myTicketBeanList == null ? 0 : myTicketBeanList.size();
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
        void onItemClick();
    }
}
