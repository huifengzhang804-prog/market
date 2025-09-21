<!-- p -->

哈希键序列化器

**完整类名**：com.medusa.gruul.common.redis.HashKeySerializer  
**作者**：张治保 | **创建日期**：2024/7/2  
**核心价值**：实现Redis哈希键的通用序列化机制，支持非String类型键的存储和读取

<!-- c -->
设计目标与使用场景

1. 解决Redis哈希结构键值类型限制问题
2. 统一键的序列化规则，兼容数字、对象等类型
3. 与StringRedisSerializer保持兼容性

**方法清单**：

- serialize(Object value)：将任意对象序列化为字节数组
- deserialize(byte[] bytes)：将字节数组反序列化为String对象

<!-- p -->
serialize方法

<!-- c -->
**定位**：HashKeySerializer.serialize  
**功能**：将包括String在内的各种类型键转换为字节流  
**重载说明**：无重载方法

**参数说明**：
- value：允许任意非空对象，自动调用toString()转换
- 特殊值：null直接返回null，避免空指针异常

**返回值**：
- UTF-8编码的字节数组
- null表示空值

```java

@Override
public byte[] serialize(@Nullable Object value) throws SerializationException {
    if (value == null) {
        return null;
    }
    // 类型检查：自动转换非String类型为String
    return (value instanceof String str ? str : String.valueOf(value)).getBytes(charset);
}
```

<!-- p -->
deserialize方法

<!-- c -->
**定位**：HashKeySerializer.deserialize  
**功能**：将字节流还原为String类型的键  
**重载说明**：无重载方法
**参数说明**：
- bytes：允许null输入，表示空值
- 非空字节数组必须为有效UTF-8编码

**返回值**：
- 原始字符串对象
- null表示空值

```java

@Override
public Object deserialize(@Nullable byte[] bytes) throws SerializationException {
    return (bytes == null ? null : new String(bytes, charset));
}
```