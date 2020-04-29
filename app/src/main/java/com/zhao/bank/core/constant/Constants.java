package com.zhao.bank.core.constant;

import rxhttp.wrapper.annotation.DefaultDomain;

public class Constants {
    @DefaultDomain //设置为默认域名
    public static final String BASE_URL = "http://61.174.28.14:10072";
    public static final String IP = "61.174.28.14";
    public static final int PORT = 10074;

    public static final String UUID_SERVICE = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String UUID_WRITE_CHA = "0000ffe1-0000-1000-8000-00805f9b34fb";
    public static final String UUID_READ_CHA = "0000ffe2-0000-1000-8000-00805f9b34fb";
    public static final String UUID_NOTIFY = "0000ffe2-0000-1000-8000-00805f9b34fb";

    /**
     * Tag fragment
     */
    public static final int TYPE_HOME_PAGE = 0;
    public static final int TYPE_MINE = 1;
    public static final int TYPE_ABOUT = 2;

    public static final int SCAN_CODE = 1;
    public static final int CASH_DRAWER_NUMBER = 2;
    public static final int LOCK_BODY_NUMBER = 3;

    public static final int OPEN = 0;
    public static final int CLOSE = 1;

    public static final int READ_1 = 1;
    public static final int READ_4 = 4;
    public static final int READ_5 = 5;
    public static final int READ_6 = 6;

    public static final int Lock0 = 0;//锁具已解锁，即锁具已完全打开
    public static final int Lock1 = 1;//机构解锁等待手柄或锁钩打开，挂锁不返回此状态
    public static final int Lock2 = 2;//机构已上锁等待手柄或锁钩回位，即可以上锁，上锁后手柄/锁钩将无法打开
    public static final int Lock3 = 3;//已完全锁闭，手柄/锁钩也已经回位，此时锁已无法打开

    public static final byte[] KEY = {0x0F, 0x1E, 0x2D, 0x3C, 0x4B, 0x5A, 0x69, 0x78, (byte) 0x87, (byte) 0x96, (byte) 0xA5, (byte) 0xB4, (byte) 0xC3, (byte) 0xD2, (byte) 0xE1, (byte) 0xF0};
    public static final byte[] ERROR = {0x7E, 0x01, 0x01, 0x02, 0x03, 0x04, (byte) 0xC2, (byte) 0xA8, (byte) 0xBF, (byte) 0xEE, (byte) 0x68, (byte) 0xBE, (byte) 0xE1, (byte) 0x40, (byte) 0x7C, (byte) 0xD2, (byte) 0xDD, (byte) 0xE7, (byte) 0xE8, 0x6D, (byte) 0xF9, (byte) 0x83, (byte) 0xBF, 0x5E};
}
