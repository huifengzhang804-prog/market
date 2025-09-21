package com.medusa.gruul.common.module.app.addon;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.TypeUtil;
import com.medusa.gruul.common.redis.util.RedisUtil;
import jakarta.annotation.Nullable;

/**
 * @author 张治保
 * @since 2024/5/28
 */
public class CommonConfigService<Config> {

    /**
     * 默认的缓存前缀
     */
    private static final String CACHE_KEY_PREFIX = "gruul:config";


    private final String type;

    /**
     * 默认配置
     */
    private final Config defaultConf;
    /**
     * 配置java数据类型
     */
    private final Class<Config> configType;

    /**
     * 内存缓存
     */
    private volatile Config config;

    @SuppressWarnings("unchecked")
    public CommonConfigService(String type, Config defaultConf) {
        this.defaultConf = defaultConf;
        this.type = type;
        this.configType = (Class<Config>) TypeUtil.getTypeArgument(this.getClass(), 0);

    }

    /**
     * 设置配置时的操作检查
     *
     * @param config 配置
     */
    public void setBefore(Config config) {
    }

    /**
     * 配置修改成功后的操作
     *
     * @param config 配置
     * @param ex     当保存发生异常时 不为空  为空表示保存成功
     */
    public void setAfter(Config config, @Nullable Throwable ex) {
    }

    /**
     * 配置类型
     *
     * @return 配置类型
     */
    public String type() {
        return type;
    }


    /**
     * 检查配置是否已存在
     *
     * @return 配置是否已存在
     */
    public boolean exists() {
        if (config != null) {
            return true;
        }
        Boolean hasKey = RedisUtil.getRedisTemplate().hasKey(RedisUtil.key(CACHE_KEY_PREFIX, type()));
        return hasKey != null && hasKey;
    }

    /**
     * 获取配置
     *
     * @return 配置
     */
    public Config get() {
        //内存缓存不为空 返回该缓存
        if (config != null) {
            return config;
        }
        //如果内存缓存为空 查询 redis 缓存 如果 redis 缓存为空则取默认值作为内存缓存
        synchronized (this) {
            if (config != null) {
                return config;
            }
            config = ObjectUtil.defaultIfNull(
                    RedisUtil.getCacheMap(RedisUtil.key(CACHE_KEY_PREFIX, type()), configType),
                    defaultConf
            );
        }
        return config;
    }

    /**
     * 设置配置
     *
     * @param config 配置
     */
    public synchronized void set(Config config) {
        Throwable throwable = null;
        setBefore(config);
        try {
            this.config = config;
            RedisUtil.setCacheMap(
                    RedisUtil.key(CACHE_KEY_PREFIX, type()),
                    config
            );
        } catch (Exception ex) {
            throwable = ex;
        } finally {
            setAfter(config, throwable);
        }
    }

    /**
     * 设置配置
     *
     * @param value object type config convert
     */
    @SuppressWarnings("unchecked")
    public void setConf(Object value) {
        set((Config) value);
    }

    /**
     * 获取配置类型
     *
     * @return 配置类型
     */
    public Class<Config> configType() {
        return configType;
    }

}
