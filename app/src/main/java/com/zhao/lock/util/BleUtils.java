package com.zhao.lock.util;


import com.zhao.lock.bean.TypeBean;
import com.zhao.lock.core.constant.Constants;

import java.util.ArrayList;
import java.util.List;
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
        byte[] userIdBytes = SharedPreferencesUtils.getInstance().getUserId().getBytes();
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

    public List<byte[]> write05(byte[] data) {
        List<byte[]> allByteList = new ArrayList<>();
        int total = (int) Math.ceil(data.length / 10.0);

        for (int i = 0; i < total; i++) {
            byte[] bytes = new byte[16];
            bytes[0] = 0x05;
            byte[] chkBytes = DataConvert.intToBytes2(chk);
            for (int y = 0; y < chkBytes.length; y++) {
                bytes[y + 1] = chkBytes[y];
            }
            if (i == total - 1) {
                bytes[5] = (byte) -1;
                for (int i1 = i * 10; i1 < data.length; i1++) {
                    bytes[i1 % 10 + 6] = data[i1];
                }
            } else {
                bytes[5] = (byte) i;
                bytes[6] = data[i * 10];
                bytes[7] = data[i * 10 + 1];
                bytes[8] = data[i * 10 + 2];
                bytes[9] = data[i * 10 + 3];
                bytes[10] = data[i * 10 + 4];
                bytes[11] = data[i * 10 + 5];
                bytes[12] = data[i * 10 + 6];
                bytes[13] = data[i * 10 + 7];
                bytes[14] = data[i * 10 + 8];
                bytes[15] = data[i * 10 + 9];
            }
            byte[] write05 = new byte[17];
            for (int y = 0; y <= bytes.length; y++) {
                if (y == bytes.length) {
                    write05[y] = getXor(bytes);
                } else {
                    write05[y] = bytes[y];
                }
            }
            allByteList.add(write05);
        }
        return allByteList;
    }

    private byte getXor(byte[] bytes) {
        byte temp = bytes[0];

        for (byte aByte : bytes) {
            temp ^= aByte;
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
        int getChk = DataConvert.byteToInt2(chkBytes);
        short getUpChk = chkGetUpRandom(getChk);
        //判断上行随机数是否和上次相同，如果相同则不做任何操作
        if (upChk == getUpChk) {
            return null;
        } else {
            upChk = getUpChk;
            chk = chkUpdateDownRandom(getChk);
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
            } else if (typeByte == 0x06) {
                typeBean.setType(Constants.READ_6);
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
