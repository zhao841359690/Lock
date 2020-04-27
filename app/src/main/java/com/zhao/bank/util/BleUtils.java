package com.zhao.bank.util;


import com.zhao.bank.bean.TypeBean;
import com.zhao.bank.core.constant.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BleUtils {
    private static BleUtils bleUtils = null;

    private byte[] chk = new byte[4];
    private boolean first = true;
    private List<Boolean> myUpChkList;

    public BleUtils() {
        if (myUpChkList == null) {
            myUpChkList = new ArrayList<>(16);
            for (int i = 0; i < 16; i++) {
                myUpChkList.add(false);
            }
        }
    }

    public static BleUtils newInstance() {
        if (bleUtils == null) {
            synchronized (BleUtils.class) {
                if (bleUtils == null) {
                    bleUtils = new BleUtils();
                }
            }
        }
        return bleUtils;
    }

    public void clearData() {
        first = true;

        chk = null;
        chk = new byte[4];

        myUpChkList = null;
        myUpChkList = new ArrayList<>(16);
        for (int i = 0; i < 16; i++) {
            myUpChkList.add(false);
        }
    }

    public byte[] writeConnect() {
        byte[] bytes = new byte[16];
        bytes[0] = 0x01;
        if (first) {
            first = false;
        } else {
            chk = chkUpdateDownRandom(chk);
        }
        for (int i = 0; i < chk.length; i++) {
            bytes[i + 1] = chk[i];
        }
        byte[] userIdBytes = SharedPreferencesUtils.getInstance().getUserId().getBytes();
        for (int i = 0; i < userIdBytes.length; i++) {
            bytes[i + 5] = userIdBytes[i];
        }
        byte[] encrypt = AESUtils.encrypt(bytes, Constants.KEY);
        byte[] writeConnect = new byte[17];
        for (int i = 0; i <= encrypt.length; i++) {
            if (i == encrypt.length) {
                writeConnect[i] = getXor(encrypt);
            } else {
                writeConnect[i] = encrypt[i];
            }
        }
        return writeConnect;
    }

    public byte[] write05(int idx, byte[] data) {
        byte[] bytes = new byte[16];
        bytes[0] = 0x05;
        chk = chkUpdateDownRandom(chk);
        for (int i = 0; i < chk.length; i++) {
            bytes[i + 1] = chk[i];
        }
        bytes[5] = (byte) idx;
        for (int i = 0; i < data.length; i++) {
            bytes[i + 6] = data[i];
        }
        byte[] encrypt = AESUtils.encrypt(bytes, Constants.KEY);
        byte[] write05 = new byte[17];
        for (int i = 0; i <= encrypt.length; i++) {
            if (i == encrypt.length) {
                write05[i] = getXor(encrypt);
            } else {
                write05[i] = encrypt[i];
            }
        }
        return write05;
    }

    public byte[] write06(byte idx, byte ret) {
        byte[] bytes = new byte[16];
        bytes[0] = 0x06;
        chk = chkUpdateDownRandom(chk);
        for (int i = 0; i < chk.length; i++) {
            bytes[i + 1] = chk[i];
        }
        bytes[5] = idx;
        bytes[6] = ret;
        byte[] encrypt = AESUtils.encrypt(bytes, Constants.KEY);
        byte[] write06 = new byte[17];
        for (int i = 0; i <= encrypt.length; i++) {
            if (i == encrypt.length) {
                write06[i] = getXor(encrypt);
            } else {
                write06[i] = encrypt[i];
            }
        }
        return write06;
    }

    private byte getXor(byte[] bytes) {
        byte temp = bytes[0];
        for (int i = 1; i < bytes.length; i++) {
            temp ^= bytes[i];
        }
        return temp;
    }

    public TypeBean read(byte[] bytes) {
        TypeBean typeBean = new TypeBean();
        byte[] bytes1 = AESUtils.decrypt(bytes, Constants.KEY);
        byte typeByte = 0x00;
        byte[] chkBytes = new byte[4];
        byte[] dataBytes = new byte[11];
        for (int i = 0; i < bytes1.length; i++) {
            if (i == 0) {
                typeByte = bytes1[i];
            } else if (i <= 4) {
                chkBytes[i - 1] = bytes1[i];
            } else {
                dataBytes[i - 5] = bytes1[i];
            }
        }
        List<Boolean> getUpChkList = chkGetUpRandom(chkBytes);
        //判断上行随机数是否和上次相同，如果相同则不做任何操作
        if (DataConvert.isChkSame(getUpChkList, myUpChkList)) {
            return null;
        } else {
            myUpChkList = getUpChkList;
            chk = chkUpdateDownRandom(chkBytes);

            if (typeByte == 0x01) {
                typeBean.setType(Constants.READ_1);
            } else if (typeByte == 0x04) {
                typeBean.setType(Constants.READ_4);
                if (dataBytes[0] == 0x00) {
                    typeBean.setLockType(Constants.Lock0);
                } else if (dataBytes[0] == 0x01) {
                    typeBean.setLockType(Constants.Lock1);
                } else if (dataBytes[0] == 0x02) {
                    typeBean.setLockType(Constants.Lock2);
                } else if (dataBytes[0] == 0x03) {
                    typeBean.setLockType(Constants.Lock3);
                }
            } else if (typeByte == 0x05) {
                typeBean.setType(Constants.READ_5);
                typeBean.setIdx(dataBytes[0]);
                typeBean.setRet(dataBytes[1]);
                String idx = Integer.toBinaryString((dataBytes[0] & 0xFF) + 0x100).substring(1).substring(0, 1);
                if ("1".equals(idx)) {
                    typeBean.setOk(true);
                } else {
                    typeBean.setOk(false);
                }
            } else if (typeByte == 0x06) {
                typeBean.setType(Constants.READ_6);
                typeBean.setIdx(dataBytes[0]);
                typeBean.setRet(dataBytes[1]);
                String idx = Integer.toBinaryString((dataBytes[0] & 0xFF) + 0x100).substring(1).substring(0, 1);
                if ("1".equals(idx)) {//此转发数据发送完成
                    typeBean.setOk(true);
                    byte[] data = new byte[10];
                    for (int i = 1; i < dataBytes.length; i++) {
                        data[i - 1] = dataBytes[i];
                    }
                    typeBean.setData(data);
                } else {
                    typeBean.setOk(false);
                    byte[] data = new byte[10];
                    for (int i = 1; i < dataBytes.length; i++) {
                        data[i - 1] = dataBytes[i];
                    }
                    typeBean.setData(data);
                }
            }
            return typeBean;
        }
    }

    /**
     * 功能: 获取上行随机数
     * 说明: 在验证码中获取上行随机数
     *
     * @param chkBytes 接收数据帧的验证码
     * @return 上行随机数
     */
    private List<Boolean> chkGetUpRandom(byte[] chkBytes) {
        List<Boolean> upList = new ArrayList<>(16);
        for (int i = 0; i < 16; i++) {
            upList.add(false);
        }
        BitArray bitArray = new BitArray(32, chkBytes);
        for (int i = 0; i < 32; i++) {
            if (i % 2 == 0) {//偶数位
                upList.set(i / 2, bitArray.get(i));
            }
        }
        return upList;
    }

    /**
     * 功能: 更新下行随机数
     * 说明: 更新验证码中的下行随机数(奇数位1-32)
     *
     * @param chkBytes 接收数据帧的验证码
     * @return 新的验证码
     */
    private byte[] chkUpdateDownRandom(byte[] chkBytes) {
        Random random = new Random();

        BitArray bitArray = new BitArray(32, chkBytes);
        for (int i = 0; i < 32; i++) {
            if (i % 2 != 0) {//奇数位
                bitArray.set(i, random.nextBoolean());
            }
        }
        return bitArray.toByteArray();
    }
}
