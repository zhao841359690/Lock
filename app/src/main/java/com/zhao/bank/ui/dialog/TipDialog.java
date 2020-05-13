package com.zhao.bank.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhao.bank.R;
import com.zhao.bank.core.constant.Constants;


public class TipDialog extends Dialog implements View.OnClickListener {
    private TextView titleTv;
    private ImageView lockIv;
    private TextView contentTv;
    private TextView openCloseTv;
    private TextView cancelTv;

    private boolean openOrClose;
    private boolean click;

    private OnTipDialogClickListener onTipDialogClickListener;


    public TipDialog(@NonNull Context context, OnTipDialogClickListener onTargetDiaLogCyclesListener) {
        super(context);
        this.onTipDialogClickListener = onTargetDiaLogCyclesListener;
    }

    public void setOpenOrClose(boolean openOrClose) {
        this.openOrClose = openOrClose;
    }

    public void setClick(boolean click) {
        this.click = click;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_tip);
        setCanceledOnTouchOutside(false);

        titleTv = findViewById(R.id.title_tv);
        titleTv.setText(openOrClose ? "开锁" : "关锁" + "提醒");

        lockIv = findViewById(R.id.lock_iv);
        lockIv.setImageResource(R.drawable.open_lock_icon);

        contentTv = findViewById(R.id.content_tv);

        openCloseTv = findViewById(R.id.open_close_tv);
        openCloseTv.setText(openOrClose ? "开锁" : "关锁");

        if (click) {
            openCloseTv.setBackgroundResource(R.drawable.shape_gradation_blue);
            openCloseTv.setOnClickListener(this);
        } else {
            openCloseTv.setBackgroundResource(R.drawable.shape_gray);
            openCloseTv.setOnClickListener(null);
        }
        cancelTv = findViewById(R.id.cancel_tv);
        cancelTv.setOnClickListener(this);

        contentTv.setText(Html.fromHtml("编号:<font color='#0E5EAB'>ADC123456</font>锁体已连接,<br/>请确认" + (openOrClose ? "开锁" : "关锁") + "!"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_close_tv:
                onTipDialogClickListener.onOpenCloseClick(openOrClose ? Constants.OPEN : Constants.CLOSE);
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
