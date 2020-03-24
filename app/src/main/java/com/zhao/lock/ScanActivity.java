package com.zhao.lock;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.qw.soul.permission.SoulPermission;
import com.qw.soul.permission.bean.Permission;
import com.qw.soul.permission.bean.Permissions;
import com.qw.soul.permission.callbcak.CheckRequestPermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class ScanActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTitleTv;
    private ImageView mOtherIv;
    private LinearLayout mHomeLy;
    private ImageView mHomeIv;
    private LinearLayout mKeyLy;
    private ImageView mKeyIv;
    private LinearLayout mLogLy;
    private ImageView mLogIv;
    private LinearLayout mPositionLy;
    private ImageView mPositionIv;
    private TextView mScanTv;
    private RecyclerView mScanRv;
    private ScanAdapter mAdapter;
    private List<BleDevice> bleDeviceList = new ArrayList<>();

    private ProgressDialog progressDialog;
    private Ble<BleDevice> mBle;

    private boolean lock = true;
    private BleDevice mBleDevice = null;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (lock) {//解锁
                boolean result = mBle.write(mBleDevice, "open".getBytes(), new BleWriteCallback<BleDevice>() {
                    @Override
                    public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
                        Toast.makeText(ScanActivity.this, "解锁成功", Toast.LENGTH_SHORT).show();
                    }
                });
                if (!result) {
                    Toast.makeText(ScanActivity.this, "解锁失败", Toast.LENGTH_SHORT).show();
                }
            } else {//锁定
                boolean result = mBle.write(mBleDevice, "close".getBytes(), new BleWriteCallback<BleDevice>() {
                    @Override
                    public void onWriteSuccess(BluetoothGattCharacteristic characteristic) {
                        Toast.makeText(ScanActivity.this, "锁定成功", Toast.LENGTH_SHORT).show();
                    }
                });
                if (!result) {
                    Toast.makeText(ScanActivity.this, "锁定失败", Toast.LENGTH_SHORT).show();
                }
            }
            progressDialog.dismiss();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ImmersionBar.with(this)
                .statusBarView(findViewById(R.id.status_bar_view))
                .keyboardEnable(true)
                .init();

        getPermission();
        initBle();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBle.destory(this);
    }

    private void getPermission() {
        SoulPermission.getInstance().checkAndRequestPermissions(Permissions.build(Manifest.permission.ACCESS_COARSE_LOCATION)
                , new CheckRequestPermissionsListener() {
                    @Override
                    public void onAllPermissionOk(Permission[] allPermissions) {

                    }

                    @Override
                    public void onPermissionDenied(Permission[] refusedPermissions) {

                    }
                });
    }

    private void initView() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);

        mTitleTv=findViewById(R.id.title_tv);

        mOtherIv = findViewById(R.id.other_iv);
        mHomeLy = findViewById(R.id.home_ly);
        mHomeLy.setOnClickListener(this);
        mHomeIv = findViewById(R.id.home_iv);

        mKeyLy = findViewById(R.id.key_ly);
        mKeyLy.setOnClickListener(this);
        mKeyIv = findViewById(R.id.key_iv);

        mLogLy = findViewById(R.id.log_ly);
        mLogLy.setOnClickListener(this);
        mLogIv = findViewById(R.id.log_iv);

        mPositionLy = findViewById(R.id.position_ly);
        mPositionLy.setOnClickListener(this);
        mPositionIv = findViewById(R.id.position_iv);

        mScanTv = findViewById(R.id.scan_tv);
        mScanTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBle.isSupportBle(ScanActivity.this)) {
                    Toast.makeText(ScanActivity.this, "BLE is not supported", Toast.LENGTH_SHORT).show();
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
                        progressDialog.setMessage("扫描中···");
                        progressDialog.show();
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
                                mAdapter.setDataList(bleDeviceList);
                            }
                        }
                    }

                    @Override
                    public void onStop() {
                        super.onStop();
                        progressDialog.dismiss();
                    }
                });
            }
        });

        mScanRv = findViewById(R.id.scan_rv);
        mScanRv.setLayoutManager(new LinearLayoutManager(this));
        mScanRv.setHasFixedSize(true);
        mAdapter = new ScanAdapter(this);
        mScanRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ScanAdapter.OnItemClickListener() {
            @Override
            public void onOpenClick(BleDevice bleDevice) {
                if (mBle.isScanning()) {
                    mBle.stopScan();
                }
                lock = true;
                progressDialog.setMessage("解锁中···");
                progressDialog.show();
                mBleDevice = bleDevice;
                if (bleDevice.isConnected()) {
                    handler.sendEmptyMessageDelayed(1, 2000);
                } else {
                    mBle.connect(bleDevice, connectCallback);
                }
            }

            @Override
            public void onCloseClick(BleDevice bleDevice) {
                if (mBle.isScanning()) {
                    mBle.stopScan();
                }
                lock = false;
                progressDialog.setMessage("锁定中···");
                progressDialog.show();
                mBleDevice = bleDevice;
                if (bleDevice.isConnected()) {
                    handler.sendEmptyMessageDelayed(1, 2000);
                } else {
                    mBle.connect(bleDevice, connectCallback);
                }
            }

            @Override
            public void onDisconnectClick(BleDevice bleDevice) {
                if (mBleDevice.isConnected()) {
                    mBle.disconnect(bleDevice);
                    Toast.makeText(ScanActivity.this, "断开连接", Toast.LENGTH_SHORT).show();
                }
            }
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
                .setUuidService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"))//主服务的uuid
                .setUuidWriteCha(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"))//可写特征的uuid
                .create(getApplicationContext());
    }

    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            if (device.isConnected()) {
                handler.sendEmptyMessageDelayed(1, 2000);
            }
        }

        @Override
        public void onConnectException(BleDevice device, int errorCode) {
            super.onConnectException(device, errorCode);
            progressDialog.dismiss();
            Toast.makeText(ScanActivity.this, "连接异常，异常状态码:" + errorCode, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnectTimeOut(BleDevice device) {
            super.onConnectTimeOut(device);
            progressDialog.dismiss();
            Toast.makeText(ScanActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View v) {
        mHomeIv.setImageResource(R.drawable.home_normal);
        mKeyIv.setImageResource(R.drawable.key_normal);
        mLogIv.setImageResource(R.drawable.log_normal);
        mPositionIv.setImageResource(R.drawable.position_normal);
        switch (v.getId()) {
            case R.id.home_ly:
                mHomeIv.setImageResource(R.drawable.home_active);

                mTitleTv.setText("首页");
                mOtherIv.setImageResource(R.drawable.about);

                mOtherIv.setVisibility(View.VISIBLE);
                mScanRv.setVisibility(View.GONE);
                mScanTv.setVisibility(View.GONE);
                break;
            case R.id.key_ly:
                mKeyIv.setImageResource(R.drawable.key_active);

                mTitleTv.setText("钥匙");
                mOtherIv.setVisibility(View.GONE);
                mScanRv.setVisibility(View.VISIBLE);
                mScanTv.setVisibility(View.VISIBLE);
                break;
            case R.id.log_ly:
                mLogIv.setImageResource(R.drawable.log_active);

                mTitleTv.setText("日志");
                mOtherIv.setImageResource(R.drawable.log);

                mOtherIv.setVisibility(View.VISIBLE);
                mScanRv.setVisibility(View.GONE);
                mScanTv.setVisibility(View.GONE);
                break;
            case R.id.position_ly:
                mPositionIv.setImageResource(R.drawable.position_active);

                mTitleTv.setText("位置");
                mOtherIv.setImageResource(R.drawable.position);

                mOtherIv.setVisibility(View.VISIBLE);
                mScanRv.setVisibility(View.GONE);
                mScanTv.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
