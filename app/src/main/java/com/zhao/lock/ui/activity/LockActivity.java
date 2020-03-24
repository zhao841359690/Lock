package com.zhao.lock.ui.activity;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.zhao.lock.R;
import com.zhao.lock.app.BaseApp;
import com.zhao.lock.base.BaseActivity;

import butterknife.BindView;

public class LockActivity extends BaseActivity {
    @BindView(R.id.title_tv)
    TextView titleTv;
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
        pendingTv.setText(Html.fromHtml("您有一条<font color='#FF0000'>[待操作]</font>的订单"));
        ticketNumberTv.setText("工单编号:ABC800214");
        lockBodyNumberTv.setText("锁体编号:NB885607");
        cabinetNumber.setText("箱体编号:AB123545");
        timeTv.setText("2020.03.02 16:00 - 2020.03.02 17:30 ");
        typeTv.setText(Html.fromHtml("操作类型:<font color='#FF0000'>开锁 - 关锁</font>"));

        Glide.with(BaseApp.getContext()).load(R.mipmap.ic_launcher)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(lockIv);
    }
}
