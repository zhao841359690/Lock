package com.zhao.bank.util;


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
    public static byte[] encrypt(byte[] data, byte[] key) {
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
    public static byte[] decrypt(byte[] data, byte[] key) {
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


    private static byte[] bytes17to16(byte[] bytes) {
        byte[] decrypt = new byte[16];
        for (int i = 0; i < bytes.length - 1; i++) {
            decrypt[i] = bytes[i];
        }
        return decrypt;
    }
}
