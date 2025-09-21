package com.medusa.gruul.common.encrypt;

import cn.hutool.crypto.asymmetric.RSA;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author 张治保
 * @since 2023/11/14
 */
@EnableConfigurationProperties(CryptProperties.class)
public class CryptAutoconfigure {
    
    public CryptAutoconfigure(CryptProperties encryptProperties) {
        ICrypt crypt = encryptProperties.isEnable() ?
                new DefaultCryptImpl(
                        new RSA(
                                encryptProperties.getPrivateKey(),
                                encryptProperties.getPublicKey()
                        )
                ) : null;
        CryptHelper.setEncryptDecrypt(encryptProperties.isEnable(), crypt);
    }
}
