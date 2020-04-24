package com.zhao.bank.util;

public class DataConvert {
    /**
     * 将int类型的数据转换为byte数组
     * 原理：将int数据中的四个byte取出，分别存储
     *
     * @param n int数据
     * @return 生成的byte数组
     */
    public static byte[] intToBytes2(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }
        return b;
    }

    /**
     * 将byte数组转换为int数据
     *
     * @param b 字节数组
     * @return 生成的int数据
     */
    public static int byteToInt2(byte[] b) {
        return (((int) b[0]) << 24) + (((int) b[1]) << 16) + (((int) b[2]) << 8) + b[3];
    }

    public static byte[] hexToByteArray(String hexStr) {
        char[] chars = hexStr.toCharArray();
        byte[] bytes = new byte[chars.length / 2];
        int index = 0;
        for (int i = 0; i < chars.length; i++) {
            int highBit = hexStr.indexOf(chars[i]);
            int lowBit = hexStr.indexOf(chars[++i]);
            bytes[index] = (byte) (highBit << 4 | lowBit);
            index++;
        }
        return bytes;
    }
}
