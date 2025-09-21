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
