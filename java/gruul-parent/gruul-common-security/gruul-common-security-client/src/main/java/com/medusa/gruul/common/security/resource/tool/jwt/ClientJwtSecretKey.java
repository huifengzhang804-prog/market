package com.medusa.gruul.common.security.resource.tool.jwt;

import cn.hutool.crypto.symmetric.AES;
import com.medusa.gruul.global.model.helper.SecureHelper;

import java.security.PublicKey;

/**
 * 认证客户端jwt秘钥数据
 *
 * @author 张治保
 * @since 2023/11/30
 */
public final class ClientJwtSecretKey implements IClientSecretKey {

    /**
     * jwt  验签算法公钥
     */
    private final PublicKey publicKey;

    /**
     * jwt payload 加密算法
     */
    private final AES payloadSecret;

    public ClientJwtSecretKey(String publicKey, String payloadKey) {
        this.publicKey = SecureHelper.esPublicKey(publicKey);
        this.payloadSecret = SecureHelper.aesFromKey(payloadKey);
    }

    @Override
    public PublicKey verifyKey() {
        return publicKey;
    }

    @Override
    public AES payload() {
        return payloadSecret;
    }
}