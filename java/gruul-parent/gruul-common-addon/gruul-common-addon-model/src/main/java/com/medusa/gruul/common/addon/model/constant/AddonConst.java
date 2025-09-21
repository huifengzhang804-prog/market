package com.medusa.gruul.common.addon.model.constant;

/**
 * @author 张治保
 * date 2022/2/21
 */
public interface AddonConst {

    /**
     * 插件日志模板
     */
    String ADDON_LOG_TEMPLATE = "<<{}>>> ：「「{}」」";

    /**
     * 插件provider 注册次数前缀 后面会拼上服务名
     */
    String ADDON_PROVIDER_REGISTER = "addon:register:provider";

    /**
     * 插件支持器前缀
     */
    String REDIS_ADDON_SUPPORTER = "addon:supporter";

    /**
     * 插件 提供器前缀
     */
    String REDIS_ADDON_PROVIDER = "addon:provider";

    /**
     * 插件 试图前缀
     */
    String REDIS_ADDON_VIEW = "addon:view";
}
