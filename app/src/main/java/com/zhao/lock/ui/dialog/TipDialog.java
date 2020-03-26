package com.zhao.lock.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhao.lock.R;
import com.zhao.lock.core.constant.Constants;


public class TipDialog extends Dialog implements View.OnClickListener {
    private TextView titleTv;
    private ImageView lockIv;
    private TextView contentTv;
    private TextView openCloseTv;
    private TextView cancelTv;

    private OnTipDialogClickListener onTipDialogClickListener;


    public TipDialog(@NonNull Context context, OnTipDialogClickListener onTargetDiaLogCyclesListener) {
        super(context);
        this.onTipDialogClickListener = onTargetDiaLogCyclesListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tip);
        setCanceledOnTouchOutside(false);

        titleTv = findViewById(R.id.title_tv);
        titleTv.setText("开锁提醒");

        lockIv = findViewById(R.id.lock_iv);
        lockIv.setImageResource(R.drawable.add_order_active_btn);

        contentTv = findViewById(R.id.content_tv);
        contentTv.setText(Html.fromHtml("编号:<font color='#0E5EAB'>ADC123456</font>锁体已连接,\n请确认开锁!"));

        openCloseTv = findViewById(R.id.open_close_tv);
        openCloseTv.setText("开锁");
        openCloseTv.setOnClickListener(this);
        cancelTv = findViewById(R.id.cancel_tv);
        cancelTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_close_tv:
                onTipDialogClickListener.onOpenCloseClick(Constants.OPEN);
                break;
            case R.id.cancel_tv:
                dismiss();
                break;
        }
    }

    public interface OnTipDialogClickListener {
        void onOpenCloseClick(int type);
    }
}
