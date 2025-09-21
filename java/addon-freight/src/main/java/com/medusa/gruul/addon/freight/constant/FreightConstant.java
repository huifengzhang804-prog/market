package com.medusa.gruul.addon.freight.constant;

/**
 * @author 张治保
 * date 2022/8/10
 */
public interface FreightConstant {
    /**
     * 省市区缓存key
     */
    String CHINA_AREA_CACHE_KEY = "gruul:china:area";

    String CHINA_PROVINCES_CACHE_KEY = "gruul:china:provinces";
    String CHINA_CITYS_CACHE_KEY = "gruul:china:citys";

    String CHINA_AREAS_CACHE_KEY = "gruul:china:areas";

    String CHINA_BASE_CACHE_KEY = "gruul:china";

    /**
     * 快递100配置分布式锁
     */
    String KUAIDI_100_LOCK = "addon:freight:kuaidi:100:lock";


    /**
     * 快递100 key
     */
    String KUAIDI_KEY = "kuaidinum";

    /**
     * 快递100 成功码
     */
    String KUAIDI_CODE = "200";

}
