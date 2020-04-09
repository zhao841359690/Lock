package com.zhao.lock.core.constant;

public class Constants {
    public static final String BASE_URL = "http://61.174.28.14:10072";

    /**
     * Tag fragment
     */
    public static final int TYPE_HOME_PAGE = 0;
    public static final int TYPE_MINE = 1;
    public static final int TYPE_ABOUT = 2;
    public static final int TYPE_MY_TICKET = 3;
    public static final int TYPE_BLE_SCAN = 4;

    public static final int SCAN_CODE = 1;
    public static final int NEW_TICKET = 2;
    public static final int NO_TICKET = 3;
    public static final int CASH_DRAWER_NUMBER = 4;
    public static final int LOCK_BODY_NUMBER = 5;

    public static final int OPEN = 0;
    public static final int CLOSE = 1;

    public static final int READ_1 = 0x01;
    public static final int READ_4 = 0x04;
    public static final int READ_5 = 0x05;
    public static final int READ_6 = 0x06;

    public static final byte[] KEY = {0x0F, 0x1E, 0x2D, 0x3C, 0x4B, 0x5A, 0x69, 0x78, (byte) 0x87, (byte) 0x96, (byte) 0xA5, (byte) 0xB4, (byte) 0xC3, (byte) 0xD2, (byte) 0xE1, (byte) 0xF0};
}
