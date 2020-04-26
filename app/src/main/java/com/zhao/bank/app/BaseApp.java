package com.zhao.bank.app;

import android.app.Application;
import android.content.Context;

import java.util.UUID;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.model.BleDevice;

public class BaseApp extends Application {
    private static Context context;
    public static Ble<BleDevice> mBle;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initBle();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mBle.destory(this);
    }

    public static Context getContext() {
        return context;
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
                .setUuidService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"))//主服务的uuid
                .setUuidWriteCha(UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb"))//可写特征的uuid
                .setUuidReadCha(UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"))//可读特征的uuid
                .setUuidNotify(UUID.fromString("0000ffe2-0000-1000-8000-00805f9b34fb"))
                .create(this);
    }
}
