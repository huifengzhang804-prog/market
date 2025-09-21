package com.medusa.gruul.common.addon.provider;

/**
 * @author 张治保
 * date 2023/1/31
 */
@FunctionalInterface
public interface ISupportKeyGenerator {
    /**
     * 获取支持的support完整key
     *
     * @param addonProvider 提供者注解
     * @return 完整key
     */
    String key(AddonProvider addonProvider);
}
