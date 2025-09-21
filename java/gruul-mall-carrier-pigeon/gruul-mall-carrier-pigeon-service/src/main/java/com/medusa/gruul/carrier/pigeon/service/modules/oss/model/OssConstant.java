package com.medusa.gruul.carrier.pigeon.service.modules.oss.model;

/**
 * @Description: oss常量
 * @Author: xiaoq
 * @Date : 2022-03-05 16:52
 */
public interface OssConstant {

    /**
     * oss 缓存前缀
     */
    String OSS_PREFIX = "gruul:mall:pigeon:oss";

    /**
     * oss 配置key
     */
    String OSS_KEY = OSS_PREFIX + ":conf";
    /**
     * oss 分布式锁
     */
    String OSS_LOCK_KEY = OSS_KEY + ":lock";
    /**
     * 默认使用的 oss 平台
     */
    String OSS_USING_KEY = OSS_PREFIX + ":using";
    /**
     * 本地 oss 资源文件访问路径
     */
    String LOCAL_OSS_REQUEST_PATH = "gruul/oss/";


}
