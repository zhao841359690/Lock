package com.zhao.lock.util;

import com.zhao.lock.core.constant.Constants;

public class SocketUtils {

    /**
     * 开关锁协议
     *
     * @param lockId
     * @param open   开锁/关锁
     * @return 写入socket的值
     */
    public static byte[] writeLock(int lockId, boolean open) {
        byte[] writeLockNormalBytes = new byte[22];
        writeLockNormalBytes[0] = 0x7e;
        writeLockNormalBytes[1] = 0x02;//负载分包数
        writeLockNormalBytes[2] = 0x01;//设备Id
        writeLockNormalBytes[3] = 0x02;
        writeLockNormalBytes[4] = 0x03;
        writeLockNormalBytes[5] = 0x04;

        //数据载荷的内容需要加密
        byte[] dataBytes = new byte[16];
        dataBytes[0] = 0x06;
        dataBytes[1] = (byte) 0xfe;
        dataBytes[2] = 0x01;//设备Id
        dataBytes[3] = 0x02;
        dataBytes[4] = 0x03;
        dataBytes[5] = 0x04;
        byte[] lockBytes = DataConvert.intToBytes2(lockId);
        dataBytes[6] = 0x54;//长度(锁号+用户id的总长度再+2)
        if (open) {//开锁
            dataBytes[7] = (byte) 0xe1;
        } else {//关锁
            dataBytes[7] = (byte) 0xe2;
        }
        for (int i = 0; i < lockBytes.length; i++) {
            writeLockNormalBytes[i + 8] = lockBytes[i];//锁号
        }
        byte[] userIdBytes = SharedPreferencesUtils.getInstance().getUserId().getBytes();
        for (int i = 0; i < userIdBytes.length; i++) {
            dataBytes[i + 12] = userIdBytes[i];//用户Id
        }
        byte[] encryptBytes = AESUtils.encrypt(dataBytes, Constants.KEY);
        for (int i = 0; i < encryptBytes.length; i++) {
            writeLockNormalBytes[i + 4] = encryptBytes[i];
        }

        //拼接校验crc16
        byte[] crc16 = crc16(writeLockNormalBytes);
        byte[] writeLockBytes = new byte[24];
        for (int i = 0; i <= writeLockNormalBytes.length + 1; i++) {
            if (i == writeLockNormalBytes.length) {
                writeLockBytes[i] = crc16[0];
            } else if (i == writeLockNormalBytes.length + 1) {
                writeLockBytes[i] = crc16[1];
            } else {
                writeLockBytes[i] = writeLockNormalBytes[i];
            }
        }
        return writeLockBytes;
    }

    private static byte[] crc16(byte[] buffer) {
        int crc = 0xFFFF;
        for (byte b : buffer) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            crc ^= (b & 0xff);//byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        byte[] crcBytes = new byte[2];
        byte[] crcBytesNormal = DataConvert.intToBytes2(crc);
        if (crcBytesNormal.length - 2 >= 0)
            System.arraycopy(crcBytesNormal, 2, crcBytes, 0, crcBytesNormal.length - 2);
        return crcBytes;
    }
}
