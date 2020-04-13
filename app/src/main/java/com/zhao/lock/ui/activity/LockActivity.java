package com.zhao.lock.ui.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.easysocket.EasySocket;
import com.easysocket.config.EasySocketOptions;
import com.easysocket.entity.OriginReadData;
import com.easysocket.entity.SocketAddress;
import com.easysocket.interfaces.conn.ISocketActionListener;
import com.easysocket.interfaces.conn.SocketActionListener;
import com.zhao.lock.R;
import com.zhao.lock.base.BaseActivity;
import com.zhao.lock.bean.TodoOrdersBean;
import com.zhao.lock.bean.WorkOrderBean;
import com.zhao.lock.core.constant.Constants;
import com.zhao.lock.ui.dialog.TipDialog;
import com.zhao.lock.util.BleUtils;
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

    private TipDialog tipDialog;
    private ProgressDialog progressDialog;

    private Ble<BleDevice> mBle;
    private BleDevice mBleDevice;
    private String address;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean result = mBle.write(mBleDevice, BleUtils.newInstance().writeConnect(), new BleWriteCallback<BleDevice>() {
                @Override
                public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
                    progressDialog.dismiss();
                    if (characteristic != null && characteristic.getValue() != null && characteristic.getValue().length == 17) {
                        BleUtils.newInstance().read(characteristic.getValue());
                        tipDialog.show();
                    }
                }
            });

            if (!result) {
                progressDialog.dismiss();
                Toast.makeText(LockActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
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
        initSocket();


        titleLeftIv.setVisibility(View.VISIBLE);
        titleLineView.setVisibility(View.GONE);

        tipDialog = new TipDialog(this, this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

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
                    tipDialog.setOpenOrClose("1".equals(workOrderBean.getData().getLock().getArchStatus()));
                }, throwable -> {
                    Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
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
                progressDialog.setMessage("蓝牙连接中...");
                progressDialog.show();
                mBle.connect(address, connectCallback);
                break;
        }
    }

    @Override
    public void onOpenCloseClick(int type) {
        if (mBleDevice == null || !mBleDevice.isConnected()) {
            return;
        }

        if (type == Constants.OPEN) {

        } else if (type == Constants.CLOSE) {

        }
    }

    private void initBle() {
        mBle = Ble.options()//开启配置
                .setLogBleExceptions(true)//设置是否输出打印蓝牙日志（非正式打包请设置为true，以便于调试）
                .setThrowBleException(true)//设置是否抛出蓝牙异常
                .setAutoConnect(false)//设置是否自动连接
                .setFilterScan(true)//设置是否过滤扫描到的设备
                .setConnectFailedRetryCount(3)
                .setConnectTimeout(10 * 1000)//设置连接超时时长（默认10*1000 ms）
                .setScanPeriod(12 * 1000)//设置扫描时长（默认10*1000 ms）
                .setUuidService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"))//主服务的uuid
                .setUuidWriteCha(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"))//可写特征的uuid
                .setUuidReadCha(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"))//可读特征的uuid
                .setUuidNotify(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"))
                .create(this);
    }

    private void initSocket() {
        //socket配置
        EasySocketOptions options = new EasySocketOptions.Builder()
                .setSocketAddress(new SocketAddress(Constants.IP, Constants.PORT)) //主机地址
                .build();

        //初始化EasySocket
        EasySocket.getInstance()
                .options(options) //项目配置
                .buildConnection();//创建一个socket连接

        EasySocket.getInstance().subscribeSocketAction(socketActionListener);
    }

    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            if (device.isConnected()) {
                mBleDevice = device;
                handler.sendEmptyMessageDelayed(0, 2000);
            }
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            progressDialog.dismiss();
            Toast.makeText(LockActivity.this, "连接异常，异常状态码:" + errorCode, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectTimeOut(BleDevice device) {
            super.onConnectTimeOut(device);
            progressDialog.dismiss();
            Toast.makeText(LockActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
        }
    };

    private ISocketActionListener socketActionListener = new SocketActionListener() {
        @Override
        public void onSocketConnSuccess(SocketAddress socketAddress) {
            super.onSocketConnSuccess(socketAddress);
        }

        @Override
        public void onSocketConnFail(SocketAddress socketAddress, Boolean isNeedReconnect) {
            super.onSocketConnFail(socketAddress, isNeedReconnect);
            if (tipDialog != null) {
                tipDialog.dismiss();
            }
        }

        @Override
        public void onSocketDisconnect(SocketAddress socketAddress, Boolean isNeedReconnect) {
            super.onSocketDisconnect(socketAddress, isNeedReconnect);
            if (tipDialog != null) {
                tipDialog.dismiss();
            }
        }

        @Override
        public void onSocketResponse(SocketAddress socketAddress, OriginReadData originReadData) {
            super.onSocketResponse(socketAddress, originReadData);
        }
    };
}
