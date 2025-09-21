package com.medusa.gruul.common.security.server.test;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.ECIES;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import io.jsonwebtoken.Jwts;
import org.junit.Test;

import javax.crypto.SecretKey;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * @author 张治保
 * @since 2023/11/2
 */
public class GenerateKeysTest {


    public static RSA rsa() {
        return new RSA(
                "MIIEugIBADANBgkqhkiG9w0BAQEFAASCBKQwggSgAgEAAoIBAQCmt+09flJi9g20XXFMXhLcm7ivqqSaiD6yxS0pDRFlZLNzwO/21QuMHNikIwY9VRX5xvi4FZHHBL87a5Ky/SqQLlCbO0mw4anmhaO5EpZPcwXCPJZ2p1+kptlbMxdTNp9UZk6Ipv9pAFFzJGa3LLK3IpcfWIGTLYj09+zYqx8ldbw7dnaJfbBkRB8kCjxlflBTxS1K080DH8jmqbLw6zbgdeTjLe3kiDw6+yXlL9S5euzmnry8ha7k6zkYKmsqL2t8A3Ck5lGpGvLP5FN+TWJfOOjhsznhE9tyvhLFIekvu3cktzZI3wix/Ufo1e9aYZgKVufm8YlrvTpAqJ2ji0BzAgMBAAECgf9QwMYpAwbk6Em7E4fNdcOtF8BdVkpkj5nO/bLiL3I9qolBiDKzXH47X72Pw2dCTfRXCAlxIQF7ZNE79t8X04jKTaS9i/olsyiYgCLNgjNMSGqBqtc2odux5R9jDVw+8jY6q1Ne3YTZV04cd4VJNF1cV2TbygL5RxUPkwcl670qZxPgsL6OJWW8wSGUXcTT5453dl1es7eK9Ol0Z/ZvHGimTVOLx/hU0sDdZkhS4qwgxp1iHaH0KArGx2HUpiDykA2hxy3TyfTQaMrS1RaUxsta+6vfPdylIEG99TcS+ZlkVzqQUUzJQ0hqfGa/tJIFsINHHNjvzxqK97aFSreKl6ECgYEA4WO9VX00y58GyvVRuz4x89jyF/wF3oVJs3Ek75h6EYWvhrRUrhBlKOruOolFAPlugLgk8iozpc0SLnAe8g1UGGbrXDZV42UwEVxQ6usW+tlm7NhoNtF7OmWN4bx6CYu05lBEzES3ufk88kIDL+PDC/5XUWorWHip/DPUjYxVbHcCgYEAvVxUv4IwN5eBurgELBuV4/i4l7f0+l+K0ouSWmKc00Spg6hu5MH7rLsW8A8u9tG0LernLYZebmSax1VmZUz6Smwm6/TS8SZCZyoHNBu/86Mu1RwNoDK3zK3G/VrYCCxoYYsPmUkvbEm+pa7caVDr5ON7M0ne4cWCMrgsWLFpFuUCgYAHDNmlTO9T1zudKaNO2hkx6X1ZJrcASOcPR+DB5eEbnzToX8euUjD34LFm/1g7OZItUJVHRe6rpwiEj1lIdQZGZSHDlqeFH2srf9nAH25ullJT4kkQPek+4GJZx02B8u7LrYMZaKTeml8/rLTw0I0PWPtePpXwhIW8r9pcOyGI7QKBgG8Ho12DHXiLKnLsEO434oSAQuQa5dakjMZ1NgQvTBsIaIidCyCwqTwXmxR5IqVwQkCmOpTT86yw6pw0c8yz/RIEJjI2JDVnpInO99hS6RMXPI+SWl7zPhgSUNofGQp5115SZnwPkHHjL5kAVZae7yIPTB6/kqM45uWMXdxHcgINAoGAbTpMuocolurojv8m/trZEqT08+7+kS5+y9pIEZ8Ka1vFYCSAc5Dz0OXjT3u8eES2wqgbio+8FOW7TsgBiuLSS1wXPw3jmWU1xuiQrnCnN24XQ9yy0CoFD/lcx5ib8BwC4MrWDGphRTaRpP2hoOW5w/VUWc1q0XaWGWsTA7LO1wY=",
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAprftPX5SYvYNtF1xTF4S3Ju4r6qkmog+ssUtKQ0RZWSzc8Dv9tULjBzYpCMGPVUV+cb4uBWRxwS/O2uSsv0qkC5QmztJsOGp5oWjuRKWT3MFwjyWdqdfpKbZWzMXUzafVGZOiKb/aQBRcyRmtyyytyKXH1iBky2I9Pfs2KsfJXW8O3Z2iX2wZEQfJAo8ZX5QU8UtStPNAx/I5qmy8Os24HXk4y3t5Ig8Ovsl5S/UuXrs5p68vIWu5Os5GCprKi9rfANwpOZRqRryz+RTfk1iXzjo4bM54RPbcr4SxSHpL7t3JLc2SN8Isf1H6NXvWmGYClbn5vGJa706QKido4tAcwIDAQAB"
        );
    }

    /**
     * 优先推荐 jwt官方生成器
     */
    @Test
    public void jwtKey() {
        KeyPair esKeyPair = Jwts.SIG.ES256.keyPair().build();
        Base64.Encoder encoder = Base64.getEncoder();
        System.out.println("ES256公钥: " + encoder.encodeToString(esKeyPair.getPublic().getEncoded()));
        System.out.println("ES256私钥: " + encoder.encodeToString(esKeyPair.getPrivate().getEncoded()));
    }

    /**
     * 生成jwt的公钥和私钥
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

    @Test
    public void ECIES() {
        ECIES ecies = new ECIES();
        String privateKeyBase64 = ecies.getPrivateKeyBase64();
        String publicKeyBase64 = ecies.getPublicKeyBase64();
        System.out.println("公钥: " + publicKeyBase64);
        System.out.println("私钥: " + privateKeyBase64);

    }

    @Test
    public void test2() {
        RSA rsa = rsa();
        System.out.println(rsa.encryptBase64("11111", KeyType.PrivateKey));
    }

    @Test
    public void test() {
        String data = "pfh6cHJBhK2FXN7Pn+Cj4uyBFIunoZjG2TKtEz9QIbWKO6je4dhbN0Z9WfdW51lCNYUFjGGspVX3RNH6GTaDcTk1rbnTSHIxepYwLQcyymcgXMv1C2aCk9qVjSQ2NrjdgHeXh3/cYRXeBxfbAwqbcUAmbSTgL0LNWKmnuxClBSdtIZGfCSLV4gblDWe4C0+WH91azD/FctNWF75qLtROX5g6FK/KqawNtNO1JSgnAugvP39JFwIiPvxKW3GY18d/s6F5L+2Y5AvvpLHanE8p583HgY9Y9cL1J5u/IfFbyMdV1VTzHAOQwcUf8xjtJTDIianNUl62uXRaOjfz7KWCnA==";
        System.out.println(rsa().decryptStr(data, KeyType.PrivateKey));
    }


    /**
     * 生成 aes key
     *
     * @return base64Key
     */
    @Test
    public void aesBase64Key() {
        SecretKey secretKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 128);
        //生成 aes
        System.out.println(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }


}
