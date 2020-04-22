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
import java.util.UUID;

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

    private Ble<BleDevice> mBle;

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

        initBle();
        scan();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBle.destory(this);
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
        mBle.stopScan();
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

    private void initBle() {
        mBle = Ble.options()//开启配置
                .setLogBleExceptions(true)//设置是否输出打印蓝牙日志（非正式打包请设置为true，以便于调试）
                .setThrowBleException(true)//设置是否抛出蓝牙异常
                .setAutoConnect(true)//设置是否自动连接
                .setFilterScan(true)//设置是否过滤扫描到的设备
                .setConnectFailedRetryCount(3)
                .setConnectTimeout(10 * 1000)//设置连接超时时长（默认10*1000 ms）
                .setScanPeriod(12 * 1000)//设置扫描时长（默认10*1000 ms）
                .setUuidService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"))//主服务的uuid
                .setUuidWriteCha(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"))//可写特征的uuid
                .setUuidReadCha(UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"))//可读特征的uuid
                .setUuidNotify(UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"))
                .create(this);

    }

    private void scan() {
        if (!mBle.isSupportBle(BaseApp.getContext())) {
            Toast.makeText(BaseApp.getContext(), "BLE is not supported", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mBle.isBleEnable()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, Ble.REQUEST_ENABLE_BT);
            return;
        }

        mBle.startScan(new BleScanCallback<BleDevice>() {
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
