package com.zhao.bank.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.zhao.bank.R;
import com.zhao.bank.base.BaseActivity;

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
                Intent intent = new Intent(this, NewTicketActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
