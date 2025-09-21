package com.medusa.gruul.common.encrypt;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.AsymmetricDecryptor;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.symmetric.AES;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2023/11/14
 */
@RequiredArgsConstructor
public class DefaultCryptImpl implements ICrypt {

    private static final String AES_KEY_VALUE_SPLIT = "#";
    private final AsymmetricDecryptor crypto;

    @Override
    public String encrypt(String value) {
        //随机生成AES密钥
        AES aes = new AES();
        // 加密后的参数内容 + 分割符 + 加密密钥
        return aes.encryptBase64(value) + AES_KEY_VALUE_SPLIT + Base64.encode(aes.getSecretKey().getEncoded());
    }

    @Override
    public String decrypt(String value) {
        return crypto.decryptStr(value, KeyType.PrivateKey);
    }
}
