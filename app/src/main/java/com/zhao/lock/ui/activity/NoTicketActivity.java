package com.zhao.lock.ui.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.core.constant.Constants;

import butterknife.BindView;
import butterknife.OnClick;

public class NoTicketActivity extends BaseActivity {
    @BindView(R.id.title_left_rl)
    RelativeLayout titleLeftRl;
    @BindView(R.id.title_left_iv)
    ImageView titleLeftIv;
    @BindView(R.id.title_line_View)
    View titleLineView;
    @BindView(R.id.new_ticket_ly)
    LinearLayout newTicketLy;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_no_ticket;
    }

    @Override
    protected void initView() {
        titleLeftIv.setVisibility(View.VISIBLE);
        titleLineView.setVisibility(View.GONE);
    }

    @OnClick({R.id.title_left_rl, R.id.new_ticket_ly})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_left_rl:
                finish();
                break;
            case R.id.new_ticket_ly:
                setResult(Constants.NO_TICKET);
                finish();
                break;
        }
    }
}
