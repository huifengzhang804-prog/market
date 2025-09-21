package com.medusa.gruul.addon.ic.modules.uupt.model;

/**
 * @author 张治保
 * @since 2024/8/23
 */
public interface UuptConstant {

    String UUPT_CACHE_PREFIX = "addon:intra-city:uupt";

    /**
     * uu跑腿开放平台配置缓存 key
     */
    String UUPT_CONFIG_CACHE_KEY = UUPT_CACHE_PREFIX + ":config";

    /**
     * 店铺与 uupt 的 openid绑定关系的缓存 key
     */
    String UUPT_SHOP_OPENID_CACHE_KEY = UUPT_CACHE_PREFIX + ":openid";

    /**
     * 开放城市列表缓存 key
     */
    String UUPT_OPEN_CITY_CACHE_KEY = UUPT_CACHE_PREFIX + ":city";


    /**
     * 发送短信验证码接口如果返回此错误码 表示需要回显图片验证码 并要求用户输入图片验证码
     * 图片验证码（接口返回错误码88000106,返回base64图片,提交图片上数字验证码）
     * todo uupt文档里这个错误码是 88100106，实际返回了 88000106
     */
    Integer UUPT_NEED_CAPTCHA = 88000106;


    /**
     * UUPT余额不足
     */
    Integer UUPT_BALANCE_NOT_ENOUGH = 88100029;


}
