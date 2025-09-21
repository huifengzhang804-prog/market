package com.medusa.gruul.common.security.resource.tool.jwt;

import cn.hutool.crypto.symmetric.AES;

import java.security.PublicKey;

/**
 * jwt 加解密完整key 数据
 *
 * @author 张治保
 * @since 2023/11/29
 */
public interface IClientSecretKey {

    /**
     * 公钥 用于 jwt数据验签
     *
     * @return PublicKey
     */
    PublicKey verifyKey();

    /**
     * payload key 用于加解密 payload信息
     *
     * @return aes crypto
     */
    AES payload();


}
