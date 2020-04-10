package com.zhao.lock.ui.activity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.bean.TodoOrdersBean;
import com.zhao.lock.bean.WorkOrderBean;
import com.zhao.lock.core.constant.Constants;
import com.zhao.lock.ui.dialog.TipDialog;
import com.zhao.lock.util.AESUtils;
import com.zhao.lock.util.SharedPreferencesUtils;

import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rxhttp.wrapper.param.RxHttp;

public class LockActivity extends BaseActivity implements TipDialog.OnTipDialogClickListener {
    @BindView(R.id.title_left_rl)
    RelativeLayout titleLeftRl;
    @BindView(R.id.title_left_iv)
    ImageView titleLeftIv;
    @BindView(R.id.title_line_View)
    View titleLineView;
    @BindView(R.id.pending_ly)
    LinearLayout pendingLy;
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

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean result = mBle.write(mBleDevice, AESUtils.write((byte) 0x01), new BleWriteCallback<BleDevice>() {
                @Override
                public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
                    if (characteristic != null && characteristic.getValue() != null && characteristic.getValue().length == 17) {
                        AESUtils.getRead(characteristic.getValue());
                        Toast.makeText(LockActivity.this, "写入加密数据成功", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            if (!result) {
                Toast.makeText(LockActivity.this, "写入加密数据失败", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lock;
    }

    @Override
    protected void initView() {
        initBle();

        titleLeftIv.setVisibility(View.VISIBLE);
        titleLineView.setVisibility(View.GONE);

        boolean showLock = getIntent().getBooleanExtra("showLock", false);
        if (showLock) {
            lockLy.setVisibility(View.VISIBLE);
        } else {
            lockLy.setVisibility(View.GONE);
        }
        String workId = getIntent().getStringExtra("workId");
        RxHttp.get("/app/workOrder/" + workId)
                .add("token", SharedPreferencesUtils.getInstance().getToken())
                .asClass(WorkOrderBean.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(workOrderBean -> {
                    address = workOrderBean.getData().getLock().getBleMac();

                    ticketNumberTv.setText("工单编号：" + workOrderBean.getData().getWorkId());
                    lockBodyNumberTv.setText("锁体编号：" + workOrderBean.getData().getLock().getUid());
                    cabinetNumber.setText("箱体编号：" + workOrderBean.getData().getBoxId());
                    timeTv.setText(workOrderBean.getData().getEffectTime() + " - " + workOrderBean.getData().getInvalidTime());
                    typeTv.setText(Html.fromHtml("操作类型：<font color='#0E5EAB'>" + ("0".equals(workOrderBean.getData().getLock().getArchStatus()) ? "关锁" : "开锁") + "</font>"));
                }, throwable -> {
                });
        RxHttp.get("/app/todoOrders")
                .add("token", SharedPreferencesUtils.getInstance().getToken())
                .asClass(TodoOrdersBean.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(todoOrdersBean -> {
                    if (todoOrdersBean.getCode() == 200 && todoOrdersBean.getData() != null && todoOrdersBean.getData().size() > 0) {
                        pendingLy.setVisibility(View.VISIBLE);
                        pendingTv.setText(Html.fromHtml("您有" + todoOrdersBean.getData().size() + "条<font color='#0E5EAB'>[待操作]</font>的订单"));
                    } else {
                        pendingLy.setVisibility(View.INVISIBLE);
                    }
                }, throwable -> {
                    pendingLy.setVisibility(View.INVISIBLE);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBle.destory(this);
    }

    @OnClick({R.id.title_left_rl, R.id.lock_ly})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.title_left_rl:
                finish();
                break;
            case R.id.lock_ly:
                mBle.connect(address, connectCallback);
//                TipDialog tipDialog = new TipDialog(this, this);
//                tipDialog.show();
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
                .setUuidService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"))//主服务的uuid
                .setUuidWriteCha(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"))//可写特征的uuid
                .setUuidReadCha(UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"))//可读特征的uuid
                .setUuidNotify(UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"))
                .create(this);
    }

    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            mBleDevice = device;
            handler.sendEmptyMessageDelayed(0, 2000);
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
