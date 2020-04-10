package com.zhao.lock.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.bean.WorkOrdersBean;
import com.zhao.lock.ui.adapter.MyTicketAdapter;
import com.zhao.lock.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class OrdersActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener, MyTicketAdapter.OnItemClickListener {
    @BindView(R.id.title_left_tv)
    TextView titleLeftTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.smart_refresh_layout)
    SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.my_ticket_rv)
    RecyclerView mRecyclerView;

    private MyTicketAdapter mAdapter;
    private List<WorkOrdersBean.DataBean.ContentBean> workOrdersBeanList;

    private int currentPage = 0;
    private boolean isRefresh = true;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_orders;
    }

    @Override
    protected void initView() {
        titleLeftTv.setText("返回");
        titleLeftTv.setVisibility(View.VISIBLE);
        titleTv.setText("我的工单");
        titleTv.setVisibility(View.VISIBLE);

        mRefreshLayout.setOnRefreshListener(this);
        mRefreshLayout.setOnLoadMoreListener(this);

        mAdapter = new MyTicketAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isRefresh = true;
        currentPage = 0;
        initWorkOrdersData(currentPage);
        refreshLayout.finishRefresh();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        isRefresh = false;
        currentPage++;
        initWorkOrdersData(currentPage);
        refreshLayout.finishLoadMore();
    }

    @OnClick(R.id.title_left_rl)
    public void onViewClicked(View view) {
        finish();
    }

    @Override
    public void onItemClick(WorkOrdersBean.DataBean.ContentBean contentBean) {
        Intent intent = new Intent(this, LockActivity.class);
        intent.putExtra("showLock", contentBean.isEffective());
        intent.putExtra("workId", contentBean.getWorkId());
        startActivity(intent);
    }

    private void initWorkOrdersData(int pageNum) {
        RxHttp.get("/app/workOrders")
                .add("token", SharedPreferencesUtils.getInstance().getToken())
                .add("page", pageNum)
                .add("size", 10)
                .asClass(WorkOrdersBean.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workOrdersBean -> {
                    if (isRefresh) {
                        workOrdersBeanList = new ArrayList<>();
                    }
                    workOrdersBeanList.addAll(workOrdersBean.getData().getContent());
                    mAdapter.setWorkOrdersBeanList(workOrdersBeanList);
                }, throwable -> {
                });
    }
}
