package com.zhao.bank.ui.activity;

import android.content.Intent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.king.zxing.CaptureHelper;
import com.king.zxing.OnCaptureCallback;
import com.king.zxing.ViewfinderView;
import com.zhao.bank.R;
import com.zhao.bank.base.BaseActivity;
import com.zhao.bank.core.constant.Constants;

import butterknife.BindView;
import butterknife.OnClick;

public class ScanCodeActivity extends BaseActivity implements OnCaptureCallback {
    @BindView(R.id.title_left_iv)
    ImageView titleLeftIv;
    @BindView(R.id.title_left_rl)
    RelativeLayout titleLeftRl;
    @BindView(R.id.title_line_View)
    View titleLineView;
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.viewfinderView)
    ViewfinderView viewFinderView;
    @BindView(R.id.ivTorch)
    View ivTorch;

    private CaptureHelper mCaptureHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_code;
    }

    @Override
    protected void initView() {
        titleLeftIv.setVisibility(View.VISIBLE);
        titleLineView.setVisibility(View.GONE);

        mCaptureHelper = new CaptureHelper(this, surfaceView, viewFinderView, ivTorch);
        mCaptureHelper.setOnCaptureCallback(this);
        mCaptureHelper.onCreate();
        mCaptureHelper.vibrate(true)
                .fullScreenScan(true)//全屏扫码
                .supportVerticalCode(true)//支持扫垂直条码，建议有此需求时才使用。
                .supportLuminanceInvert(true)//是否支持识别反色码（黑白反色的码），增加识别率
                .continuousScan(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCaptureHelper.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCaptureHelper.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCaptureHelper.onDestroy();
    }

    @OnClick(R.id.title_left_rl)
    public void onViewClicked() {
        finish();
    }

    @Override
    public boolean onResultCallback(String result) {
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(Constants.SCAN_CODE, intent);
        return false;
    }
}
