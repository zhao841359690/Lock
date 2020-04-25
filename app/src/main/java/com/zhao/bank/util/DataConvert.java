package com.zhao.bank.util;

import java.util.ArrayList;
import java.util.List;

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


    private static byte hexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    public static byte[] hexToByteArray(String inHex) {
        int hexlen = inHex.length();
        byte[] result;
        if (hexlen % 2 == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static boolean isChkSame(List<Boolean> list1, List<Boolean> list2) {
        for (int i = 0; i < list1.size(); i++) {
            if (list1.get(i) != list2.get(i)) {
                return false;
            }
        }
        return true;
    }

    public static List<byte[]> needSend05(byte[] data) {
        List<byte[]> needSendList = new ArrayList<>();
        int total = (int) Math.ceil(data.length / 10.0);

        for (int i = 0; i < total; i++) {
            byte[] bytes = new byte[10];
            if (i == total - 1) {
                for (int i1 = i * 10; i1 < data.length; i1++) {
                    bytes[i1 % 10] = data[i1];
                }
            } else {
                bytes[0] = data[i * 10];
                bytes[1] = data[i * 10 + 1];
                bytes[2] = data[i * 10 + 2];
                bytes[3] = data[i * 10 + 3];
                bytes[4] = data[i * 10 + 4];
                bytes[5] = data[i * 10 + 5];
                bytes[6] = data[i * 10 + 6];
                bytes[7] = data[i * 10 + 7];
                bytes[8] = data[i * 10 + 8];
                bytes[9] = data[i * 10 + 9];
            }
            needSendList.add(bytes);
        }
        return needSendList;
    }
}
