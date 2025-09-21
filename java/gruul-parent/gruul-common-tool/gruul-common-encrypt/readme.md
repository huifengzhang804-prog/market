## 参数加解密

### 参数加解密方式

1. 加密使用AES对称加密算法，即加密和解密使用同一个密钥。
   > 密钥随机生成 生成后的密钥将通过`#`和密文进行分割
   最终组成 `密文#密钥`的格式返回给客户端。
2. 解密使用RSA非对称加密算法，客户端保存公钥和私钥，服务端保存私钥。
   > 客户端通过公钥加密后的数据，服务端使用私钥解密。

具体的实现逻辑可以参考`com.medusa.gruul.common.encrypt.DefaultCryptImpl`类。

### 配置详解

配置类`com.medusa.gruul.common.encrypt.CryptProperties`

```yaml
spring:
  security:
    crypt:
      # 是否启用加解密 默认为true
      enable: true
      # 解密私钥
      private-key: xxxxxxxx
      # 客户端加密公钥 todo 预留配置 暂时可以不用配置
      public-key: xxxxxxxxxx
```