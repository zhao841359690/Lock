package com.zhao.bank.app;

import android.app.Application;
import android.content.Context;

import com.clj.fastble.BleManager;
import com.clj.fastble.scan.BleScanRuleConfig;

public class BaseApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initBle();
    }

    public static Context getContext() {
        return context;
    }

    private void initBle() {
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(0)
                .setOperateTimeout(10 * 1000)
                .init(this);
        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(10 * 1000)
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);
    }
}
