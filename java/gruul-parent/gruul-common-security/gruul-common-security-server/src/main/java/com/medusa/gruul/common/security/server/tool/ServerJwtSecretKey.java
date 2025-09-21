package com.medusa.gruul.common.security.server.tool;

import cn.hutool.crypto.asymmetric.ECIES;
import cn.hutool.crypto.symmetric.AES;
import com.medusa.gruul.common.security.resource.tool.jwt.IClientSecretKey;
import com.medusa.gruul.global.model.helper.SecureHelper;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author 张治保
 * @since 2023/11/30
 */
public class ServerJwtSecretKey implements IClientSecretKey {
    private final ECIES ecies;
    private final AES payloadSecret;

    public ServerJwtSecretKey(String privateKey, String publicKey, String payloadKey) {
        this.ecies = new ECIES(privateKey, publicKey);
        this.payloadSecret = new AES(SecureHelper.aesKey(payloadKey));
    }

    public PrivateKey signKey() {
        return ecies.getPrivateKey();
    }

    @Override
    public PublicKey verifyKey() {
        return ecies.getPublicKey();
    }

    @Override
    public AES payload() {
        return payloadSecret;
    }
}
