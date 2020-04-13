package com.zhao.lock.util;

public class SocketUtils {

    public static byte[] crc16(byte[] buffer) {
        byte[] bytes = new byte[22];
        bytes[0] = 0x7e;
        bytes[1] = 0x02;
        bytes[2] = 0x01;
        bytes[3] = 0x02;
        bytes[4] = 0x03;
        bytes[5] = 0x04;
        bytes[6] = 0x74;
        bytes[7] = 0x44;
        bytes[8] = (byte) 0xdd;
        bytes[9] = 0x6a;
        bytes[10] = (byte) 0x91;
        bytes[11] = 0x73;
        bytes[12] = 0x54;
        bytes[13] = (byte) 0xfc;
        bytes[14] = 0x59;
        bytes[15] = (byte) 0xd9;
        bytes[16] = 0x76;
        bytes[17] = 0x30;
        bytes[18] = 0x4c;
        bytes[19] = 0x1c;
        bytes[20] = 0x73;
        bytes[21] = (byte) 0xa7;

        int crc = 0xFFFF;

        for (int j = 0; j < buffer.length; j++) {
            crc = ((crc >>> 8) | (crc << 8)) & 0xffff;
            crc ^= (buffer[j] & 0xff);//byte to int, trunc sign
            crc ^= ((crc & 0xff) >> 4);
            crc ^= (crc << 12) & 0xffff;
            crc ^= ((crc & 0xFF) << 5) & 0xffff;
        }
        crc &= 0xffff;
        byte[] crcBytes = new byte[2];
        byte[] crcBytesNormal = DataConvert.intToBytes2(crc);
        for (int i = 2; i < crcBytesNormal.length; i++) {
            crcBytes[i - 2] = crcBytesNormal[i];
        }
        return crcBytes;
    }
}
