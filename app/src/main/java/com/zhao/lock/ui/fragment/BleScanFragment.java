package com.zhao.lock.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.lock.R;

import com.zhao.lock.app.BaseApp;
import com.zhao.lock.base.BaseFragment;

import com.zhao.lock.ui.adapter.BleScanAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class BleScanFragment extends BaseFragment implements BleScanAdapter.OnItemClickListener {

    @BindView(R.id.retry_tv)
    TextView retryTv;
    @BindView(R.id.ble_scan_rv)
    RecyclerView mRecyclerView;

    private Ble<BleDevice> mBle;

    private BleScanAdapter mAdapter;
    private List<BleDevice> bleDeviceList = new ArrayList<>();

    public static BleScanFragment newInstance() {
        return new BleScanFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ble_scan;
    }

    private OnBleScanFragmentClickListener onBleScanFragmentClickListener;

    public void setOnBleScanFragmentClickListener(OnBleScanFragmentClickListener onBleScanFragmentClickListener) {
        this.onBleScanFragmentClickListener = onBleScanFragmentClickListener;
    }

    public interface OnBleScanFragmentClickListener {
        void onAccessClick(int type);
    }

    @Override
    protected void initView() {
        mAdapter = new BleScanAdapter(getContext());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mAdapter);

//        initBle();
//        scan();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBle.destory(BaseApp.getContext());
    }

    @OnClick(R.id.retry_tv)
    public void onViewClicked() {
        scan();
    }

    @Override
    public void onItemClick(int position) {
        onBleScanFragmentClickListener.onAccessClick(position);
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
                .setUuidService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"))//主服务的uuid
                .setUuidWriteCha(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"))//可写特征的uuid
                .create(getContext());

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
                if (scanRecord != null && scanRecord.length >= 7) {
                    if (scanRecord[4] == -1 && scanRecord[5] == 89 && scanRecord[6] == 0) {
                        for (BleDevice d : bleDeviceList) {
                            if (d.getBleAddress().equals(device.getBleAddress())) {
                                return;
                            }
                        }
                        bleDeviceList.add(device);
                        mAdapter.setBleDeviceList(bleDeviceList);
                    }
                }
            }

            @Override
            public void onStop() {
                super.onStop();
                if (bleDeviceList.size() == 0) {
                    retryTv.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
