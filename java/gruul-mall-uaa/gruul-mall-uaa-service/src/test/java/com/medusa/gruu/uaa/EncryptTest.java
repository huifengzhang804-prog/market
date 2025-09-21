package com.medusa.gruu.uaa;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.medusa.gruul.global.model.helper.SecureHelper;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * @author 张治保
 * @since 2024/4/22
 */
public class EncryptTest {

    /**
     * 优先推荐 jwt官方生成器
     * es256 用于生成jwt的加签与验签 私钥和公钥
     */
    @Test
    public void jwtKey() {
        KeyPair esKeyPair = Jwts.SIG.ES256.keyPair().build();
        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("ES256公钥: " + encoder.encodeToString(esKeyPair.getPublic().getEncoded()));
        System.out.println("ES256私钥: " + encoder.encodeToString(esKeyPair.getPrivate().getEncoded()));
    }

    /**
     * 生成RSA的公钥和私钥 用于参数加解密
     */
    @Test
    public void RSA() {
        KeyPair keyPair = SecureUtil.generateKeyPair("RSA", 2048);
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        // 将公钥和私钥转换为Base64编码的字符串
        String publicKeyBase64 = java.util.Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String privateKeyBase64 = java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());
        // 打印公钥和私钥
        System.out.println("公钥: " + publicKeyBase64);
        System.out.println("私钥: " + privateKeyBase64);
    }

    /**
     * 用于加密 jwt payload 的密钥
     */
    @Test
    public void payloadKey() {
        System.out.println(
                SecureHelper.aesBase64Key()
        );
    }


    @Test
    public void generateEncryptedForm() {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbtzmEy7zEUDsskOHI8TFGBG/slgXIvzzO7bcejLyEiSMbTtZMNgGFewpYGaAb1fCMTUuc5HQrKx4T/qr2C2nkw0WjroE5UrykYag6qgpx2WXc68+cn0mODgaeczWnt0gjCqRn+lE1O9wxPbJwL85ZH58cWFjha1AMedeh5lg9iQIDAQAB";
        RSA rsa = new RSA(null, publicKey);
        String username = "admin";
        String password = "admin111";
        System.out.println(rsa.encryptBase64(username, KeyType.PublicKey));
        System.out.println(rsa.encryptBase64(password, KeyType.PublicKey));
    }

}
