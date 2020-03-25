package com.zhao.lock.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.core.constant.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewTicketActivity extends BaseActivity {
    @BindView(R.id.title_left_rl)
    RelativeLayout titleLeftRl;
    @BindView(R.id.title_left_tv)
    TextView titleLeftTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.title_right_tv)
    TextView titleRightTv;

    @BindView(R.id.cash_drawer_number_et)
    EditText cashDrawerNumberEt;
    @BindView(R.id.cash_drawer_number_iv)
    ImageView cashDrawerNumberIv;
    @BindView(R.id.lock_body_number_et)
    EditText lockBodyNumberEt;
    @BindView(R.id.lock_body_number_iv)
    ImageView lockBodyNumberIv;
    @BindView(R.id.time_ly)
    LinearLayout timeLy;
    @BindView(R.id.type_ly)
    LinearLayout typeLy;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_ticket;
    }

    @Override
    protected void initView() {
        titleLeftTv.setVisibility(View.VISIBLE);
        titleLeftTv.setText("取消");
        titleTv.setVisibility(View.VISIBLE);
        titleTv.setText("填写工单");
        titleRightTv.setVisibility(View.VISIBLE);
        titleRightTv.setText("提交");
    }

    @OnClick({R.id.title_left_rl, R.id.title_right_tv, R.id.cash_drawer_number_iv, R.id.lock_body_number_iv, R.id.time_ly, R.id.type_ly})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_left_rl:
                finish();
                break;
            case R.id.title_right_tv:
                setResult(Constants.NEW_TICKET);
                finish();
                break;
            case R.id.cash_drawer_number_iv:
                break;
            case R.id.lock_body_number_iv:
                break;
            case R.id.time_ly:
                break;
            case R.id.type_ly:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
