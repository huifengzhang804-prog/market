package com.medusa.gruul.common.encrypt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author 张治保
 * @since 2023/11/14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CryptHelper {

    private static boolean enable = true;
    private static ICrypt encryptDecrypt;

    /**
     * 加密
     *
     * @param value 原始值
     * @return 加密后的值
     */
    public static String encrypt(String value) {
        return enable ? encryptDecrypt.encrypt(value) : value;
    }


    /**
     * 解密
     *
     * @param value 加密后的值
     * @return 解密后的值
     */
    public static String decrypt(String value) {
        return enable ? encryptDecrypt.decrypt(value) : value;
    }

    /**
     * 设置是否启用及 加解密工具
     *
     * @param enable         是否启用
     * @param encryptDecrypt 加解密工具
     */
    public static void setEncryptDecrypt(boolean enable, ICrypt encryptDecrypt) {
        CryptHelper.enable = enable;
        CryptHelper.encryptDecrypt = encryptDecrypt;
    }
}
