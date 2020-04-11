package com.zhao.lock.util;


import com.zhao.lock.core.constant.Constants;

import java.util.Random;

public class BleUtils {
    private int chk = 0;
    private short upChk = 0;

    public static BleUtils newInstance() {
        return new BleUtils();
    }

    public byte[] writeConnect() {
        byte[] bytes = new byte[16];
        bytes[0] = 0x01;
        byte[] chkBytes = DataConvert.intToBytes2(chk);
        for (int i = 0; i < chkBytes.length; i++) {
            bytes[i + 1] = chkBytes[i];
        }
        byte[] userIdBytes = DataConvert.intToBytes2(SharedPreferencesUtils.getInstance().getUserId());
        for (int i = 0; i < userIdBytes.length; i++) {
            bytes[i + 5] = userIdBytes[i];
        }
        for (int i = 0; i < 7; i++) {
            bytes[i + 9] = 0x00;
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

    public byte[] write05() {
        byte[] bytes = new byte[16];
        bytes[0] = 0x05;
        byte[] chkBytes = DataConvert.intToBytes2(chk);
        for (int i = 0; i < chkBytes.length; i++) {
            bytes[i + 1] = chkBytes[i];
        }
        for (int i = 0; i < 11; i++) {
            bytes[i + 5] = 0x00;
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

    public byte[] write06() {
        byte[] bytes = new byte[16];
        bytes[0] = 0x06;
        byte[] chkBytes = DataConvert.intToBytes2(chk);
        for (int i = 0; i < chkBytes.length; i++) {
            bytes[i + 1] = chkBytes[i];
        }
        for (int i = 0; i < 11; i++) {
            bytes[i + 5] = 0x00;
        }
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

        for (byte aByte : bytes) {
            temp ^= aByte;
        }
        return temp;
    }

    public byte[] read(byte[] bytes) {
        byte[] bytes1 = AESUtils.decrypt(bytes, Constants.KEY);
        byte typeByte = 0x00;
        byte[] chkBytes = new byte[4];
        byte[] dataBytes = new byte[11];
        for (int i = 0; i < bytes1.length; i++) {
            if (i == 0) {
                typeByte = bytes1[i];
            } else if (i <= 4) {
                chkBytes[i] = bytes1[i];
            } else {
                dataBytes[i] = bytes1[i];
            }
        }
        int getChk = DataConvert.byteToInt2(chkBytes);
        short getUpChk = chkGetUpRandom(getChk);
        //判断上行随机数是否和上次相同，如果相同则不做任何操作
        if (upChk == getUpChk) {
            return null;
        } else {
            upChk = getUpChk;
            chk = chkUpdateDownRandom(getChk);
            if (typeByte == 0x01) {

            } else if (typeByte == 0x04) {

            } else if (typeByte == 0x05 || typeByte == 0x06) {
                String idx = Integer.toBinaryString((dataBytes[0] & 0xFF) + 0x100).substring(1).substring(0, 1);
                if ("1".equals(idx)) {//此转发数据发送完成

                } else {

                }
            }
            return dataBytes;
        }
    }

    /**
     * 功能: 获取上行随机数
     * 说明: 在验证码中获取上行随机数
     *
     * @param chkCode 接收数据帧的验证码
     * @return 上行随机数
     */
    private short chkGetUpRandom(int chkCode) {
        short ret;
        ret = 0;
        for (byte i = 0; i < 16; i++) {
            ret <<= 1;
            if ((chkCode & 0x80000000) != 0x0) {
                ret |= 0x0001;
            }
            chkCode <<= 2;
        }
        return ret;
    }

    /**
     * 功能: 更新下行随机数
     * 说明: 更新验证码中的下行随机数
     *
     * @param chkCode 原始验证码
     * @return 新的验证码
     */
    private int chkUpdateDownRandom(int chkCode) {
        Random random = new Random();
        short downRandom = (short) random.nextInt(Short.MAX_VALUE);

        int tmp;
        tmp = 0x40000000;
        for (byte i = 0; i < 16; i++) {
            chkCode &= ~tmp;
            if ((downRandom & 0x8000) != 0x0) {
                chkCode |= tmp;
            }
            tmp >>= 2;
            downRandom <<= 1;
        }
        return chkCode;
    }
}
