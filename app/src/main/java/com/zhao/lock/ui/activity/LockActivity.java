package com.zhao.lock.ui.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.core.constant.Constants;
import com.zhao.lock.ui.dialog.TipDialog;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class LockActivity extends BaseActivity implements TipDialog.OnTipDialogClickListener {
    @BindView(R.id.title_left_rl)
    RelativeLayout titleLeftRl;
    @BindView(R.id.title_left_iv)
    ImageView titleLeftIv;
    @BindView(R.id.title_line_View)
    View titleLineView;
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

    private Ble<BleDevice> mBle;
    private BleDevice mBleDevice;
    private String address;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock;
    }

    @Override
    protected void initView() {
//        initBle();

        titleLeftIv.setVisibility(View.VISIBLE);
        titleLineView.setVisibility(View.GONE);

        pendingTv.setText(Html.fromHtml("您有一条<font color='#0E5EAB'>[待操作]</font>的订单"));
        ticketNumberTv.setText("工单编号：ABC800214");
        lockBodyNumberTv.setText("锁体编号：NB885607");
        cabinetNumber.setText("箱体编号：AB123545");
        timeTv.setText("2020.03.02 16:00 - 2020.03.02 17:30 ");
        typeTv.setText(Html.fromHtml("操作类型：<font color='#0E5EAB'>开锁 - 关锁</font>"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mBle.destory(this);
    }

    @OnClick({R.id.title_left_rl, R.id.lock_ly})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_left_rl:
                finish();
                break;
            case R.id.lock_ly:
//                mBle.connect(address, connectCallback);
                TipDialog tipDialog = new TipDialog(this, this);
                tipDialog.show();
                break;
        }
    }

    @Override
    public void onOpenCloseClick(int type) {
        if (mBleDevice == null || !mBleDevice.isConnected()) {
            return;
        }

        if (type == Constants.OPEN) {
            boolean result = mBle.write(mBleDevice, "open".getBytes(), new BleWriteCallback<BleDevice>() {
                @Override
                public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
                    Toast.makeText(LockActivity.this, "解锁成功", Toast.LENGTH_SHORT).show();
                }
            });
            if (!result) {
                Toast.makeText(LockActivity.this, "解锁失败", Toast.LENGTH_SHORT).show();
            }
        } else if (type == Constants.CLOSE) {
            boolean result = mBle.write(mBleDevice, "close".getBytes(), new BleWriteCallback<BleDevice>() {
                @Override
                public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
                    Toast.makeText(LockActivity.this, "锁定成功", Toast.LENGTH_SHORT).show();
                }
            });
            if (!result) {
                Toast.makeText(LockActivity.this, "锁定失败", Toast.LENGTH_SHORT).show();
            }
        }
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
                .create(this);
    }

    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            mBleDevice = device;
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            Toast.makeText(LockActivity.this, "连接异常，异常状态码:" + errorCode, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectTimeOut(BleDevice device) {
            super.onConnectTimeOut(device);
            Toast.makeText(LockActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
        }
    };
}
