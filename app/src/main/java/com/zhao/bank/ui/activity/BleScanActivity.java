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

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.zhao.bank.R;

import com.zhao.bank.app.BaseApp;
import com.zhao.bank.base.BaseActivity;

import com.zhao.bank.bean.TodoOrdersBean;
import com.zhao.bank.ui.adapter.BleScanAdapter;
import com.zhao.bank.util.SharedPreferencesUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
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
    private List<BleDevice> bleDeviceList = new ArrayList<>();

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
    public void onItemClick(BleDevice bleDevice) {
        BleManager.getInstance().cancelScan();
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
                                if (datum.getLock().getBleMac().equals(bleDevice.getMac().toLowerCase())) {
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
                            Toast.makeText(BaseApp.getContext(), "无法访问该设备!您可以换个设备试试", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(BaseApp.getContext(), todoOrdersBean.getMsg(), Toast.LENGTH_LONG).show();
                    }
                }, throwable -> {
                    Toast.makeText(BaseApp.getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                });
    }


    private void scan() {
        if (!BleManager.getInstance().isSupportBle()) {
            Toast.makeText(BaseApp.getContext(), "BLE is not supported", Toast.LENGTH_LONG).show();
            return;
        }

        if (!BleManager.getInstance().isBlueEnable()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0x01);
            return;
        }

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (bleDeviceList.size() == 0) {
                    retryTv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScanStarted(boolean success) {
                retryTv.setVisibility(View.GONE);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                if (TextUtils.isEmpty(bleDevice.getName())) {
                    return;
                }
                for (BleDevice device : bleDeviceList) {
                    if (device.getMac().equals(bleDevice.getMac())) {
                        return;
                    }
                }
                bleDeviceList.add(bleDevice);
                mAdapter.setBleBeanList(bleDeviceList);
            }
        });
    }
}
