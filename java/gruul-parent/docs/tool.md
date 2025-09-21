<!-- p -->
gruul-common-encrypt

加解密模块，提供基于RSA和AES的数据加解密功能。

<!-- c -->
Crypt 加解密注解

- **完整类名**: com.medusa.gruul.common.encrypt.Crypt
- **文件路径**: common\encrypt\Crypt.java
- **作者**: 张治保
- **日期**: 2023/11/14

**完整代码**:
```java
package com.medusa.gruul.common.encrypt;

import com.alibaba.fastjson2.annotation.JSONField;
import com.medusa.gruul.common.encrypt.rw.CryptReader;
import com.medusa.gruul.common.encrypt.rw.CryptWriter;

import java.lang.annotation.*;

/**
 * 加解密注解
 *
 * @author 张治保
 * @since 2023/11/14
 */
@Target({ElementType.FIELD/*, ElementType.PARAMETER*/})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JSONField(serializeUsing = CryptWriter.class, deserializeUsing = CryptReader.class)
@Inherited
public @interface Crypt {
}
```

<!-- c -->
CryptAutoconfigure 自动配置类

- **完整类名**: com.medusa.gruul.common.encrypt.CryptAutoconfigure
- **文件路径**: common\encrypt\CryptAutoconfigure.java
- **作者**: 张治保
- **日期**: 2023/11/14

**完整代码**:
```java
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
```

<!-- c -->
CryptHelper 加解密工具类

- **完整类名**: com.medusa.gruul.common.encrypt.CryptHelper
- **文件路径**: common\encrypt\CryptHelper.java
- **作者**: 张治保
- **日期**: 2023/11/14

**主要方法**:
- `encrypt()`: 加密
- `decrypt()`: 解密
- `setEncryptDecrypt()`: 设置是否启用及加解密工具

**完整代码**:
```java
package com.medusa.gruul.common.encrypt;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * @author 张治保
 * @since 2023/11/14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CryptHelper {

    private static boolean enable = true;
    private static ICrypt encryptDecrypt;

    /**
     * 加密
     *
     * @param value 原始值
     * @return 加密后的值
     */
    public static String encrypt(String value) {
        return enable ? encryptDecrypt.encrypt(value) : value;
    }

    /**
     * 解密
     *
     * @param value 加密后的值
     * @return 解密后的值
     */
    public static String decrypt(String value) {
        return enable ? encryptDecrypt.decrypt(value) : value;
    }

    /**
     * 设置是否启用及 加解密工具
     *
     * @param enable         是否启用
     * @param encryptDecrypt 加解密工具
     */
    public static void setEncryptDecrypt(boolean enable, ICrypt encryptDecrypt) {
        CryptHelper.enable = enable;
        CryptHelper.encryptDecrypt = encryptDecrypt;
    }
}
```

<!-- c -->
CryptProperties 配置属性类

- **完整类名**: com.medusa.gruul.common.encrypt.CryptProperties
- **文件路径**: common\encrypt\CryptProperties.java
- **作者**: 张治保
- **日期**: 2023/11/14

**主要属性**:
- `enable`: 是否开启加解密
- `privateKey`: 参数解密私钥
- `publicKey`: 参数加密公钥

**完整代码**:
```java
package com.medusa.gruul.common.encrypt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 张治保
 * @since 2023/11/14
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "spring.security.crypt")
public class CryptProperties {
    /**
     * 是否开启加解密
     */
    private boolean enable = true;

    /**
     * 参数解密私钥
     */
    private String privateKey;

    /**
     * 参数加密公钥
     */
    private String publicKey;
}
```

<!-- c -->
DefaultCryptImpl 默认加解密实现

- **完整类名**: com.medusa.gruul.common.encrypt.DefaultCryptImpl
- **文件路径**: common\encrypt\DefaultCryptImpl.java
- **作者**: 张治保
- **日期**: 2023/11/14

**完整代码**:
```java
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

    private final AsymmetricDecryptor crypto;

    @Override
    public String encrypt(String value) {
        AES aes = new AES();
        return aes.encryptBase64(value) + Base64.encodeUrlSafe(aes.getSecretKey().getEncoded());
    }

    @Override
    public String decrypt(String value) {
        return crypto.decryptStr(value, KeyType.PrivateKey);
    }
}
```

<!-- c -->
ICrypt 加解密接口

- **完整类名**: com.medusa.gruul.common.encrypt.ICrypt
- **文件路径**: common\encrypt\ICrypt.java
- **作者**: 张治保
- **日期**: 2023/11/14

**主要方法**:
- `encrypt()`: 加密
- `decrypt()`: 解密

**完整代码**:
```java
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
```

<!-- c -->
CryptReader 参数解密反序列化器

- **完整类名**: com.medusa.gruul.common.encrypt.rw.CryptReader
- **文件路径**: encrypt\rw\CryptReader.java
- **作者**: 张治保
- **日期**: 2023/11/27

**完整代码**:
```java
package com.medusa.gruul.common.encrypt.rw;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.reader.ObjectReader;
import com.medusa.gruul.common.encrypt.CryptHelper;
import com.medusa.gruul.common.fastjson2.FastJson2;

import java.lang.reflect.Type;

/**
 * 参数解密反序列化器
 *
 * @author 张治保
 * @since 2023/11/27
 */
public class CryptReader implements ObjectReader<Object> {

    @Override
    public Object readObject(JSONReader jsonReader, Type fieldType, Object fieldName, long features) {
        String str = jsonReader.readString();
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        return FastJson2.convert(CryptHelper.decrypt(str), (Class<?>) fieldType);
    }
}
```

<!-- c -->
CryptWriter 参数加密序列化器

- **完整类名**: com.medusa.gruul.common.encrypt.rw.CryptWriter
- **文件路径**: encrypt\rw\CryptWriter.java
- **作者**: 张治保
- **日期**: 2023/11/27

**完整代码**:
```java
package com.medusa.gruul.common.encrypt.rw;

import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.writer.ObjectWriter;
import com.medusa.gruul.common.encrypt.CryptHelper;
import com.medusa.gruul.common.fastjson2.FastJson2;

import java.lang.reflect.Type;

/**
 * 参数加密序列化器
 *
 * @author 张治保
 * @since 2023/11/27
 */
public class CryptWriter implements ObjectWriter<Object> {

    @Override
    public void write(JSONWriter jsonWriter, Object object, Object fieldName, Type fieldType, long features) {
        jsonWriter.writeString(CryptHelper.encrypt(FastJson2.convert(object, String.class)));
    }
}
```

<!-- c -->
模块关系图

```mermaid
graph TD
    A[请求数据] -->|@Crypt注解| B(CryptWriter)
    B -->|加密| C[CryptHelper]
    C -->|RSA加密| D[加密数据]
    E[加密数据] -->|@Crypt注解| F(CryptReader)
    F -->|解密| G[CryptHelper]
    G -->|RSA解密| H[原始数据]
```