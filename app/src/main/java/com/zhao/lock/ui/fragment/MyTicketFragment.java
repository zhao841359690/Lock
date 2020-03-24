package com.zhao.lock.ui.fragment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhao.lock.R;
import com.zhao.lock.base.BaseFragment;
import com.zhao.lock.bean.MyTicketBean;
import com.zhao.lock.ui.activity.LockActivity;
import com.zhao.lock.ui.adapter.MyTicketAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyTicketFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, MyTicketAdapter.OnItemClickListener {

    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.my_ticket_rv)
    RecyclerView mRecyclerView;

    private MyTicketAdapter mAdapter;
    private List<MyTicketBean> myTicketBeanList;

    private int currentPage = 0;
    private boolean isRefresh = true;

    public static MyTicketFragment newInstance() {
        return new MyTicketFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_ticket;
    }

    @Override
    protected void initView() {
        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);

        mAdapter = new MyTicketAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);
        initMyTicketData(currentPage);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isRefresh = true;
        currentPage = 0;
        initMyTicketData(currentPage);
        refreshLayout.finishRefresh();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isRefresh = false;
        currentPage++;
        initMyTicketData(currentPage);
        refreshLayout.finishLoadMore();
    }

    @Override
    public void onItemClick() {
        Intent intent = new Intent(getActivity(), LockActivity.class);
        startActivity(intent);
    }

    private void initMyTicketData(int pageNum) {
        myTicketBeanList = new ArrayList<>();

        MyTicketBean myTicketBean = new MyTicketBean();
        myTicketBean.setTicNb("ABC800214");
        myTicketBean.setP(true);
        myTicketBean.setLocNb("SQB112356-1025");
        myTicketBeanList.add(myTicketBean);

        MyTicketBean myTicketBean1 = new MyTicketBean();
        myTicketBean1.setTicNb("ABC800214");
        myTicketBean1.setP(false);
        myTicketBean1.setLocNb("SQB112356-1025");
        myTicketBeanList.add(myTicketBean1);

        MyTicketBean myTicketBean2 = new MyTicketBean();
        myTicketBean2.setTicNb("ABC800214");
        myTicketBean2.setP(false);
        myTicketBean2.setLocNb("SQB112356-1025");
        myTicketBeanList.add(myTicketBean2);

        MyTicketBean myTicketBean3 = new MyTicketBean();
        myTicketBean3.setTicNb("ABC800214");
        myTicketBean3.setP(false);
        myTicketBean3.setLocNb("SQB112356-1025");
        myTicketBeanList.add(myTicketBean3);

        mAdapter.setMyTicketBeanList(myTicketBeanList);
    }
}
