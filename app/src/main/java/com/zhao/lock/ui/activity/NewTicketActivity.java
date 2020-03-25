package com.zhao.lock.ui.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class NewTicketActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView titleTv;
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
        titleTv.setText("填写工单");
    }

    @OnClick({R.id.cash_drawer_number_iv, R.id.lock_body_number_iv, R.id.time_ly, R.id.type_ly})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
}
