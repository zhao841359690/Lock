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

    public static byte[] getWrite(byte type) {
        byte[] bytes = new byte[16];
        bytes[0] = type;

        bytes[1] = 0x00;
        bytes[2] = 0x00;
        bytes[3] = 0x00;
        bytes[4] = 0x00;

        if (type == 0x01) {
            bytes[5] = 0x00;
            bytes[6] = 0x00;
            bytes[7] = 0x00;
            bytes[8] = 0x00;
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

        byte[] write = new byte[17];
        for (int i = 0; i <= encrypt.length; i++) {
            if (i == encrypt.length) {
                write[i] = getXor(encrypt);
            } else {
                write[i] = encrypt[i];
            }
        }
        return write;
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
