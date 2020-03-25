package com.zhao.lock.ui.activity;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class LockActivity extends BaseActivity {
    @BindView(R.id.title_left_rl)
    RelativeLayout titleLeftRl;
    @BindView(R.id.title_left_iv)
    ImageView titleLeftIv;
    @BindView(R.id.title_line_View)
    View titleLineView;
    @BindView(R.id.pending_tv)
    TextView pendingTv;
    @BindView(R.id.ticket_number_tv)
    TextView ticketNumberTv;
    @BindView(R.id.lock_body_number_tv)
    TextView lockBodyNumberTv;
    @BindView(R.id.cabinet_number)
    TextView cabinetNumber;
    @BindView(R.id.time_tv)
    TextView timeTv;
    @BindView(R.id.type_tv)
    TextView typeTv;
    @BindView(R.id.lock_ly)
    LinearLayout lockLy;
    @BindView(R.id.lock_iv)
    ImageView lockIv;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock;
    }

    @Override
    protected void initView() {
        titleLeftIv.setVisibility(View.VISIBLE);
        titleLineView.setVisibility(View.GONE);

        pendingTv.setText(Html.fromHtml("您有一条<font color='#0E5EAB'>[待操作]</font>的订单"));
        ticketNumberTv.setText("工单编号：ABC800214");
        lockBodyNumberTv.setText("锁体编号：NB885607");
        cabinetNumber.setText("箱体编号：AB123545");
        timeTv.setText("2020.03.02 16:00 - 2020.03.02 17:30 ");
        typeTv.setText(Html.fromHtml("操作类型：<font color='#0E5EAB'>开锁 - 关锁</font>"));
    }

    @OnClick({R.id.title_left_rl, R.id.lock_ly})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_left_rl:
                finish();
                break;
            case R.id.lock_ly:
                break;
        }
    }
}
