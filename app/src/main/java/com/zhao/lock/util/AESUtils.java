package com.zhao.lock.util;

import com.zhao.lock.core.constant.Constants;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AESUtils {
    /**
     * AES加密
     *
     * @param data 将要加密的内容
     * @param key  密钥
     * @return 已经加密的内容
     */
    private static byte[] encrypt(byte[] data, byte[] key) {
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    /**
     * AES解密
     *
     * @param data 将要解密的内容
     * @param key  密钥
     * @return 已经解密的内容
     */
    private static byte[] decrypt(byte[] data, byte[] key) {
        data = bytes17to16(data);
        try {
            SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public static byte[] write(byte type) {
        byte[] bytes = new byte[16];
        bytes[0] = type;

        bytes[1] = 0x01;
        bytes[2] = 0x01;
        bytes[3] = 0x01;
        bytes[4] = 0x01;

        if (type == 0x01) {
            bytes[5] = 0x01;
            bytes[6] = 0x01;
            bytes[7] = 0x01;
            bytes[8] = 0x01;
            bytes[9] = 0x00;
            bytes[10] = 0x00;
            bytes[11] = 0x00;
            bytes[12] = 0x00;
            bytes[13] = 0x00;
            bytes[14] = 0x00;
            bytes[15] = 0x00;
        } else if (type == 0x05) {
            bytes[5] = 0x01;

            bytes[6] = 0x01;
            bytes[7] = 0x01;
            bytes[8] = 0x01;
            bytes[9] = 0x01;
            bytes[10] = 0x01;
            bytes[11] = 0x01;
            bytes[12] = 0x01;
            bytes[13] = 0x01;
            bytes[14] = 0x01;
            bytes[15] = 0x01;
        } else if (type == 0x06) {
            bytes[5] = 0x01;

            bytes[6] = 0x01;
            bytes[7] = 0x01;
            bytes[8] = 0x01;
            bytes[9] = 0x01;
            bytes[10] = 0x01;
            bytes[11] = 0x01;
            bytes[12] = 0x01;
            bytes[13] = 0x01;
            bytes[14] = 0x01;
            bytes[15] = 0x01;
        }

        byte[] encrypt = encrypt(bytes, Constants.KEY);

        byte[] bytes1 = new byte[17];
        bytes1[0] = encrypt[0];
        bytes1[1] = encrypt[1];
        bytes1[2] = encrypt[2];
        bytes1[3] = encrypt[3];
        bytes1[4] = encrypt[4];
        bytes1[5] = encrypt[5];
        bytes1[6] = encrypt[6];
        bytes1[7] = encrypt[7];
        bytes1[8] = encrypt[8];
        bytes1[9] = encrypt[9];
        bytes1[10] = encrypt[10];
        bytes1[11] = encrypt[11];
        bytes1[12] = encrypt[12];
        bytes1[13] = encrypt[13];
        bytes1[14] = encrypt[14];
        bytes1[15] = encrypt[15];
        bytes1[16] = getXor(bytes);
        return bytes1;
    }

    private static byte getXor(byte[] bytes) {
        byte temp = bytes[0];

        for (byte aByte : bytes) {
            temp ^= aByte;
        }
        return temp;
    }

    public static int getRead(byte[] bytes) {
        byte[] bytes1 = decrypt(bytes, Constants.KEY);
        if (bytes1[0] == 0x01) {
            return Constants.READ_1;
        } else if (bytes1[0] == 0x04) {
            return Constants.READ_4;
        } else if (bytes1[0] == 0x05) {
            return Constants.READ_5;
        } else if (bytes1[0] == 0x06) {
            return Constants.READ_6;
        }
        return Constants.READ_1;
    }

    private static byte[] bytes17to16(byte[] bytes) {
        byte[] decrypt = new byte[16];
        for (int i = 0; i < bytes.length - 1; i++) {
            decrypt[i] = bytes[i];
        }
        return decrypt;
    }
}
