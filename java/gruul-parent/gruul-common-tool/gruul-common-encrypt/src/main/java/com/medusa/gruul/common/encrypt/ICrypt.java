package com.medusa.gruul.common.encrypt;

/**
 * 加解密抽象工具
 *
 * @author 张治保
 * @since 2023/11/14
 */
public interface ICrypt {

    /**
     * 加密
     *
     * @param value 原始值
     * @return 加密后的值
     */
    String encrypt(String value);

    /**
     * 解密
     *
     * @param value 加密后的值
     * @return 解密后的值
     */
    String decrypt(String value);
}
