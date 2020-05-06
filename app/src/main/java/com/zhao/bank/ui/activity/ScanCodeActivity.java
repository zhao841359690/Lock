package com.zhao.bank.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.king.zxing.CaptureHelper;
import com.king.zxing.OnCaptureCallback;
import com.king.zxing.ViewfinderView;
import com.zhao.bank.R;
import com.zhao.bank.app.BaseApp;
import com.zhao.bank.base.BaseActivity;
import com.zhao.bank.bean.TodoOrdersBean;
import com.zhao.bank.core.constant.Constants;
import com.zhao.bank.util.SharedPreferencesUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

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

    private int fromWhere;
    private CaptureHelper mCaptureHelper;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scan_code;
    }

    @Override
    protected void initView() {
        fromWhere = getIntent().getIntExtra("fromWhere", Constants.SCAN_CODE);

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
        if (fromWhere != Constants.SCAN_CODE) {
            Intent intent = new Intent();
            intent.putExtra("result", result);
            setResult(fromWhere, intent);
            finish();
        } else {
            String reg = "^([0-9a-f]{2})(([:][0-9a-f]{2}){5})$";
            if (!Pattern.compile(reg).matcher(result).find()) {
                Toast.makeText(this, "二维码错误", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                RxHttp.get("/app/todoOrders")
                        .add("token", SharedPreferencesUtils.getInstance().getToken())
                        .asClass(TodoOrdersBean.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(todoOrdersBean -> {
                            if (todoOrdersBean.getCode() == 200) {
                                if (todoOrdersBean.getData() != null && todoOrdersBean.getData().size() > 0) {
                                    boolean canFind = false;
                                    List<TodoOrdersBean.DataBean> dataBeanList = new ArrayList<>();

                                    for (TodoOrdersBean.DataBean datum : todoOrdersBean.getData()) {
                                        if (datum.getLock().getBleMac().equals(result)) {
                                            canFind = true;
                                            dataBeanList.add(datum);
                                        }
                                    }
                                    if (canFind) {
                                        Intent intent = new Intent(this, TodoOrdersActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable("todo", (Serializable) dataBeanList);
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(this, NoTicketActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(BaseApp.getContext(), "无法访问该设备!您可以换个设备试试", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            } else {
                                Toast.makeText(BaseApp.getContext(), todoOrdersBean.getMsg(), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, throwable -> {
                            Toast.makeText(BaseApp.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        });
            }
        }
        return true;
    }
}
