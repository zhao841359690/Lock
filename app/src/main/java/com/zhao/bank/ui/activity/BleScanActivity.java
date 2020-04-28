package com.zhao.bank.ui.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.bank.R;

import com.zhao.bank.app.BaseApp;
import com.zhao.bank.base.BaseActivity;

import com.zhao.bank.bean.BleBean;
import com.zhao.bank.bean.TodoOrdersBean;
import com.zhao.bank.ui.adapter.BleScanAdapter;
import com.zhao.bank.util.SharedPreferencesUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class BleScanActivity extends BaseActivity implements BleScanAdapter.OnItemClickListener {
    @BindView(R.id.title_left_tv)
    TextView titleLeftTv;
    @BindView(R.id.title_tv)
    TextView titleTv;
    @BindView(R.id.retry_tv)
    TextView retryTv;
    @BindView(R.id.ble_scan_rv)
    RecyclerView mRecyclerView;

    private BleScanAdapter mAdapter;
    private List<BleBean> bleBeanList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ble_scan;
    }

    @Override
    protected void initView() {
        titleLeftTv.setText("返回");
        titleLeftTv.setVisibility(View.VISIBLE);
        titleTv.setText("BLE扫描-结果");
        titleTv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAdapter = new BleScanAdapter(this);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);

        scan();
    }

    @OnClick({R.id.title_left_rl, R.id.retry_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_left_rl:
                finish();
                break;
            case R.id.retry_tv:
                scan();
                break;
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void onItemClick(BleBean bleBean) {
        Ble.getInstance().stopScan();
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
                                if (datum.getLock().getBleMac().equals(bleBean.getBleDevice().getBleAddress().toLowerCase())) {
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
                        }
                    } else {
                        Toast.makeText(BaseApp.getContext(), todoOrdersBean.getMsg(), Toast.LENGTH_SHORT).show();
                    }
                }, throwable -> {
                    Toast.makeText(BaseApp.getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void scan() {
        if (!Ble.getInstance().isSupportBle(BaseApp.getContext())) {
            Toast.makeText(BaseApp.getContext(), "BLE is not supported", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Ble.getInstance().isBleEnable()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Ble.REQUEST_ENABLE_BT);
            return;
        }

        Ble.getInstance().startScan(new BleScanCallback<BleDevice>() {
            @Override
            public void onStart() {
                super.onStart();
                retryTv.setVisibility(View.GONE);
            }

            @Override
            public void onLeScan(BleDevice device, int rssi, byte[] scanRecord) {
                if (TextUtils.isEmpty(device.getBleName())) {
                    return;
                }
                for (BleBean bleBean : bleBeanList) {
                    if (bleBean.getBleDevice().getBleAddress().equals(device.getBleAddress())) {
                        return;
                    }
                }
                BleBean bleBean = new BleBean();
                bleBean.setBleDevice(device);
                bleBean.setRssi(rssi);
                bleBeanList.add(bleBean);
                mAdapter.setBleBeanList(bleBeanList);
            }

            @Override
            public void onStop() {
                super.onStop();
                if (bleBeanList.size() == 0) {
                    retryTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
