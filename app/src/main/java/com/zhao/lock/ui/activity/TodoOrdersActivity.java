package com.zhao.lock.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.bean.TodoOrdersBean;
import com.zhao.lock.ui.adapter.TodoOrdersAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TodoOrdersActivity extends BaseActivity implements TodoOrdersAdapter.OnItemClickListener {
    @BindView(R.id.title_left_tv)
    TextView titleLeftTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.my_ticket_rv)
    RecyclerView mRecyclerView;

    private TodoOrdersAdapter mAdapter;
    private List<TodoOrdersBean.DataBean> dataBeanList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_todo_orders;
    }

    @Override
    protected void initView() {
        dataBeanList = (List<TodoOrdersBean.DataBean>) getIntent().getSerializableExtra("todo");

        titleLeftTv.setText("返回");
        titleLeftTv.setVisibility(View.VISIBLE);
        titleTv.setText("我的工单");
        titleTv.setVisibility(View.VISIBLE);

        mAdapter = new TodoOrdersAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setDataBeanList(dataBeanList);
    }

    @OnClick(R.id.title_left_rl)
    public void onViewClicked(View view) {
        finish();
    }

    @Override
    public void onItemClick(TodoOrdersBean.DataBean dataBean) {
        Intent intent = new Intent(this, LockActivity.class);
        intent.putExtra("showLock", true);
        intent.putExtra("workId", dataBean.getWorkId());
        startActivity(intent);
    }
}
