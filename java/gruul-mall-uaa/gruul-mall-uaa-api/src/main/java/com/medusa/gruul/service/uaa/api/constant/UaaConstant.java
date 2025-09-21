package com.medusa.gruul.service.uaa.api.constant;

/**
 * @author 张治保
 * date 2022/4/12
 */
public interface UaaConstant {
    /**
     * 短信验证码长度
     */
    int SMS_CODE_LENGTH = 4;

    /**
     * 创建新管理员 队列名称
     */
    String SHOP_ADMIN_CHANGE_QUEUE = "uaa.shop.admin.change";
    /**
     * 禁用/启用店铺
     */
    String SHOP_ENABLE_DISABLE_QUEUE = "uaa.shop.enable_disable";

    /**
     * 用户登陆/注册分布式锁
     */
    String USER_LOGIN_REG_LOCK = "gruul:mall:uaa:user:login";

    /**
     * 小程序码分布式锁
     */
    String WECHAT_CODE_LOCK = "code:lock";

    /**
     * 小程序schema缓存key
     */
    String WECHAT_SCHEMA_PREFIX = "gruul:mall:wechat:schema";

    /**
     * 小程序码缓存key
     */
    String WECHAT_CODE_PREFIX = "gruul:mall:wechat:code";

    /**
     * 会员等级正则
     */
    String MEMBER_RANK_REGEX = "(?i)(?:s)?vip([1-9]\\d*|\\d+)";

    /**
     * uaa support id
     */
    String UAA_SUPPORT_ID = "uaaPaidMember";

    /**
     * 店铺信息锁定
     */
    String UAA_SHOP_LOCK = "gruul:mall:uaa:shop:lock";
}
