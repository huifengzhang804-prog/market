package com.medusa.gruul.global.model.helper;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * @author 张治保
 * @since 2023/10/31
 */
public interface SecureHelper {

    /**
     * aes 加密
     *
     * @param base64Key base64Key
     * @return AES
     */
    static AES aesFromKey(String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.aes(key);
    }

    /**
     * 生成 aes key
     *
     * @return base64Key
     */
    static String aesBase64Key() {
        SecretKey secretKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 128);
        //生成 aes
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * 生成公钥，仅用于非对称加密<br>
     * <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory">
     * 算法见
     * </a>
     *
     * @param algorithm 算法
     * @param base64Key base64公钥密钥
     * @return 公钥 {@link PublicKey}
     */
    static PublicKey publicKey(String algorithm, String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.generatePublicKey(algorithm, key);
    }

    /**
     * 生成私钥，仅用于非对称加密<br>
     * 算法见：
     * <a href="https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#KeyFactory">
     * 算法
     * </a>
     *
     * @param algorithm 算法
     * @param base64Key base64私钥密钥
     * @return 私钥 {@link PrivateKey}
     */
    static PrivateKey privateKey(String algorithm, String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.generatePrivateKey(algorithm, key);
    }


    /**
     * 生成密钥对，仅用于非对称加密<br>
     *
     * @param algorithm        算法
     * @param base64PublicKey  base64公钥密钥
     * @param base64PrivateKey base64私钥密钥
     * @return 密钥对 {@link KeyPair}
     */
    static KeyPair keyPair(String algorithm, String base64PublicKey, String base64PrivateKey) {
        return new KeyPair(publicKey(algorithm, base64PublicKey), privateKey(algorithm, base64PrivateKey));
    }

    /**
     * 生成密钥，仅用于对称加密<br>
     *
     * @param algorithm 算法
     * @param base64Key base64密钥
     * @return 密钥 {@link SecretKey}
     */
    static SecretKey secretKey(String algorithm, String base64Key) {
        byte[] key = Base64.getDecoder().decode(base64Key);
        return SecureUtil.generateKey(algorithm, key);
    }

    /**
     * 生成公钥，仅用于非对称加密<br>
     *
     * @param base64Key base64公钥密钥
     * @return 公钥 {@link PublicKey}
     */
    static PublicKey esPublicKey(String base64Key) {
        return publicKey("EC", base64Key);
    }

    /**
     * 生成私钥，仅用于非对称加密<br>
     *
     * @param base64Key base64私钥密钥
     * @return 私钥 {@link PrivateKey}
     */
    static PrivateKey esPrivateKey(String base64Key) {
        return privateKey("EC", base64Key);
    }

    /**
     * base64 密钥 转 SecretKey，仅用于对称加密<br>
     *
     * @param base64Key base64密钥
     * @return 密钥 {@link SecretKey}
     */
    static SecretKey aesKey(String base64Key) {
        return secretKey("AES", base64Key);
    }

}
