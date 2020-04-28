package com.zhao.bank.ui.activity;

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

import com.zhao.bank.R;
import com.zhao.bank.app.BaseApp;
import com.zhao.bank.base.BaseActivity;
import com.zhao.bank.bean.TodoOrdersBean;
import com.zhao.bank.bean.TypeBean;
import com.zhao.bank.bean.WorkOrderBean;
import com.zhao.bank.core.constant.Constants;
import com.zhao.bank.ui.dialog.TipDialog;
import com.zhao.bank.util.BleUtils;
import com.zhao.bank.util.DataConvert;
import com.zhao.bank.util.SharedPreferencesUtils;
import com.zhao.bank.util.SocketUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.OnClick;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotiftCallback;
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

    private BleDevice mBleDevice;
    private String address;
    private String uid;

    private ExecutorService mThreadPool;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;

    private List<byte[]> write05 = new ArrayList<>();
    private int write05Index = 0;
    private boolean isSend05 = false;

    private List<byte[]> needWrite05 = new ArrayList<>();
    private int needWrite05Index = 0;
    private boolean needSend05 = false;

    private List<byte[]> write06 = new ArrayList<>();

    @SuppressLint("HandlerLeak")
    private Handler send05Handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isSend05) {
                boolean result = Ble.getInstance().write(mBleDevice, BleUtils.newInstance().write05(needWrite05Index, needWrite05), characteristic1 -> {
                });
                if (!result) {
                    needSend05 = false;
                }
            } else {
                send05Handler.removeMessages(0);
                send05Handler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler autoConnectHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Ble.getInstance().write(mBleDevice, BleUtils.newInstance().writeConnect(), characteristic -> {
                autoConnectHandler.removeMessages(0);
                autoConnectHandler.sendEmptyMessageDelayed(0, 1000 * 30);
            });
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean result = Ble.getInstance().write(mBleDevice, BleUtils.newInstance().writeConnect(), characteristic -> {
                progressDialog.dismiss();
                tipDialog.show();

                autoConnectHandler.removeMessages(0);
                autoConnectHandler.sendEmptyMessageDelayed(0, 1000 * 30);
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
        initSocket();

        titleLeftIv.setVisibility(View.VISIBLE);
        titleLineView.setVisibility(View.GONE);

        tipDialog = new TipDialog(this, this);
        tipDialog.setOnDismissListener(dialogInterface -> {
            if (Ble.getInstance() != null && mBleDevice != null) {
                Ble.getInstance().disconnect(mBleDevice);
                Ble.getInstance().refreshDeviceCache(address.toUpperCase());
            }
        });

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
                    uid = workOrderBean.getData().getLock().getHexUid();

                    ticketNumberTv.setText("工单编号：" + workOrderBean.getData().getWorkId());
                    lockBodyNumberTv.setText("锁体编号：" + uid);
                    cabinetNumber.setText("箱体编号：" + workOrderBean.getData().getBoxId());
                    timeTv.setText(workOrderBean.getData().getEffectTime() + " - " + workOrderBean.getData().getInvalidTime());
                    typeTv.setText(Html.fromHtml("操作类型：<font color='#0E5EAB'>" + (showLock ? "开锁" : "开锁") + "</font>"));
                    tipDialog.setOpenOrClose(showLock);
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
        try {
            mThreadPool.shutdown();

            outputStream.close();
            inputStream.close();
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.removeMessages(0);
        autoConnectHandler.removeMessages(0);
        send05Handler.removeMessages(0);
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

                BleUtils.newInstance().clearData();

                Ble.getInstance().refreshDeviceCache(address.toUpperCase());
                Ble.getInstance().connect(address.toUpperCase(), connectCallback);
                break;
        }
    }

    @Override
    public void onOpenCloseClick(int type) {
        if (mBleDevice == null || !mBleDevice.isConnected()) {
            return;
        }

        if (needSend05) {
            Toast.makeText(this, "正在发送数据，请稍后点击", Toast.LENGTH_SHORT).show();
            return;
        }

        if (type == Constants.OPEN) {
            openOrClose(true);
        } else if (type == Constants.CLOSE) {
            openOrClose(false);
        }
    }

    private void initSocket() {
        //初始化线程池
        mThreadPool = Executors.newCachedThreadPool();
        //socket配置
        mThreadPool.execute(() -> {
            try {
                socket = new Socket(Constants.IP, Constants.PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            if (device.isConnected()) {
                mBleDevice = device;
                setNotify(device);
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

    private void setNotify(BleDevice device) {
        Ble.getInstance().startNotify(device, new BleNotiftCallback<BleDevice>() {
            @Override
            public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                if (characteristic.getValue() != null && characteristic.getValue().length == 17) {
                    TypeBean typeBean = BleUtils.newInstance().read(characteristic.getValue());
                    if (typeBean != null) {
                        if (Constants.READ_4 == typeBean.getType()) {
                            runOnUiThread(() -> {
                                if (typeBean.getLockType() == Constants.Lock0 || typeBean.getLockType() == Constants.Lock3) {
                                    if (tipDialog != null) {
                                        tipDialog.dismiss();
                                    }
                                }
                            });
                        } else if (Constants.READ_5 == typeBean.getType()) {
                            runOnUiThread(() -> {
                                if (isSend05) {
                                    if (!typeBean.isOk() && write05Index < (write05.size() - 1)) {
                                        write05Index++;
                                        boolean result = Ble.getInstance().write(mBleDevice, BleUtils.newInstance().write05(write05Index, write05), characteristic1 -> {
                                        });
                                        if (!result) {
                                            isSend05 = false;
                                        }
                                    } else {
                                        isSend05 = false;
                                    }
                                } else {
                                    if (!typeBean.isOk() && needWrite05Index < (needWrite05.size() - 1)) {
                                        needWrite05Index++;
                                        boolean result = Ble.getInstance().write(mBleDevice, BleUtils.newInstance().write05(needWrite05Index, needWrite05), characteristic1 -> {
                                        });
                                        if (!result) {
                                            needSend05 = false;
                                        }
                                    } else {
                                        needSend05 = false;
                                    }
                                }
                            });
                        } else if (Constants.READ_6 == typeBean.getType()) {
                            runOnUiThread(() -> {
                                write06.add(typeBean.getData());
                                Ble.getInstance().write(mBleDevice, BleUtils.newInstance().write06(typeBean.getIdx(), (byte) 0x00), characteristic1 -> {
                                });
                                if (typeBean.isOk()) {
                                    if (!socket.isConnected()) {
                                        try {
                                            socket.connect(new InetSocketAddress(Constants.IP, Constants.PORT));
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    byte[] data = new byte[write06.size() * 10];
                                    int size = 0;
                                    for (byte[] bytes : write06) {
                                        for (int i = 0; i < bytes.length; i++) {
                                            data[size + i] = bytes[i];
                                        }
                                        size += bytes.length;
                                    }
                                    write06 = new ArrayList<>();
                                    mThreadPool.execute(() -> {
                                        try {
                                            outputStream = socket.getOutputStream();

                                            outputStream.write(data);
                                            outputStream.flush();

                                            inputStream = socket.getInputStream();
                                            byte[] head = new byte[2];
                                            int readHead = inputStream.read(head);
                                            if (readHead != -1) {
                                                int total = 22 + (head[1] - 1) * 16;
                                                byte[] elseData = new byte[total];
                                                int read = inputStream.read(elseData);
                                                byte[] data05 = new byte[total + 2];
                                                for (int i = 0; i < data05.length; i++) {
                                                    if (i < 2) {
                                                        data05[i] = head[i];
                                                    } else {
                                                        data05[i] = elseData[i - 2];
                                                    }
                                                }
                                                if (read != -1 && !Arrays.equals(data05, Constants.ERROR)) {
                                                    runOnUiThread(() -> {
                                                        needSend05 = true;
                                                        needWrite05 = DataConvert.needSend05(data05);
                                                        needWrite05Index = 0;

                                                        send05Handler.removeMessages(0);
                                                        send05Handler.sendEmptyMessageDelayed(0, 1000);
                                                    });
                                                } else {
                                                    needSend05 = false;
                                                }
                                            } else {
                                                needSend05 = false;
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    });
                                }
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            autoConnectHandler.removeMessages(0);
                            Ble.getInstance().cancelNotify(mBleDevice);

                            progressDialog.dismiss();
                            tipDialog.dismiss();
                            Toast.makeText(LockActivity.this, "上行验证码相同,蓝牙已断开连接", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            }
        });
    }

    private void openOrClose(boolean openOrClose) {
        progressDialog.setMessage(openOrClose ? "开锁中..." : "关锁中...");
        progressDialog.show();

        mThreadPool.execute(() -> {
            try {
                if (!socket.isConnected()) {
                    socket.connect(new InetSocketAddress(Constants.IP, Constants.PORT));
                }

                outputStream = socket.getOutputStream();

                outputStream.write(SocketUtils.writeLock(uid, openOrClose));
                outputStream.flush();

                inputStream = socket.getInputStream();
                byte[] head = new byte[2];
                int readHead = inputStream.read(head);
                if (readHead != -1) {
                    int total = 22 + (head[1] - 1) * 16;
                    byte[] elseData = new byte[total];
                    int read = inputStream.read(elseData);
                    byte[] data = new byte[total + 2];
                    for (int i = 0; i < data.length; i++) {
                        if (i < 2) {
                            data[i] = head[i];
                        } else {
                            data[i] = elseData[i - 2];
                        }
                    }
                    if (read != -1 && !Arrays.equals(data, Constants.ERROR)) {
                        isSend05 = true;
                        write05 = DataConvert.needSend05(data);
                        write05Index = 0;
                        boolean result = Ble.getInstance().write(mBleDevice, BleUtils.newInstance().write05(write05Index, write05), characteristic1 -> {
                        });
                        if (!result) {
                            runOnUiThread(() -> {
                                isSend05 = false;
                                progressDialog.dismiss();
                                tipDialog.dismiss();
                                Toast.makeText(this, "验证失败,没有权限", Toast.LENGTH_LONG).show();
                            });
                        }
                    } else {
                        runOnUiThread(() -> {
                            isSend05 = false;
                            progressDialog.dismiss();
                            tipDialog.dismiss();
                            Toast.makeText(this, "验证失败,没有权限", Toast.LENGTH_LONG).show();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        isSend05 = false;
                        progressDialog.dismiss();
                        tipDialog.dismiss();
                        Toast.makeText(this, "验证失败,没有权限", Toast.LENGTH_LONG).show();
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    tipDialog.dismiss();
                    Toast.makeText(LockActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

            }
        });
    }
}
