package com.medusa.gruul.common.addon.provider;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.addon.model.bean.AddonDefinition;
import com.medusa.gruul.common.addon.model.constant.AddonConst;
import com.medusa.gruul.common.addon.model.handler.AddonHook;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.config.GlobalAppProperties;
import com.medusa.gruul.global.i18n.I18N;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

/**
 * @author 张治保
 * date 2022/9/14
 */
@Slf4j
public class AddonSupportHook implements ApplicationContextAware {

    private final String addonRecordKey;
    private final String addonServiceName;
    private ApplicationContext applicationContext;

    public AddonSupportHook(GlobalAppProperties globalAppProperties) {
        this.addonServiceName = globalAppProperties.getName();
        this.addonRecordKey = RedisUtil.key(AddonConst.REDIS_ADDON_PROVIDER, this.addonServiceName);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 分布式
     */
    @Bean(name = "supportKeyGenerator")
    @ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "false", matchIfMissing = true)
    public ISupportKeyGenerator supportKeyGenerator() {
        return annotation -> StrUtil.isEmpty(annotation.filter()) ?
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, annotation.service(), annotation.supporterId(), annotation.methodName()) :
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, annotation.service(), annotation.supporterId(), annotation.methodName(), annotation.filter());
    }

    /**
     * 单体
     */
    @Bean(name = "supportKeyGenerator")
    @ConditionalOnProperty(prefix = "gruul", name = "single", havingValue = "true")
    public ISupportKeyGenerator supportKeyGeneratorSingle() {
        return annotation -> StrUtil.isEmpty(annotation.filter()) ?
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, addonServiceName, annotation.supporterId(), annotation.methodName()) :
                RedisUtil.key(AddonConst.REDIS_ADDON_SUPPORTER, addonServiceName, annotation.supporterId(), annotation.methodName(), annotation.filter());
    }

    /**
     * 插件钩子函数
     */
    @Bean
    public AddonHook addonHook(ISupportKeyGenerator supportKeyGenerator) {
        return new AddonHook() {
            @Override
            public void install() {
                RedisUtil.getRedisTemplate().opsForSet().add(AddonConst.ADDON_PROVIDER_REGISTER, addonServiceName);
                log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.provider.scanning"), addonServiceName);
                Map<String, List<AddonDefinition>> addonDefinitionsMap = AddonSupportScanner.scan(applicationContext, supportKeyGenerator);
                if (CollUtil.isEmpty(addonDefinitionsMap)) {
                    log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.provider.scan.empty"), addonServiceName);
                    return;
                }
                addonDefinitionsMap.forEach(
                        (cacheKey, addonDefinitions) -> {
                            RedisUtil.setCacheMapValue(cacheKey, addonServiceName, addonDefinitions);
                            log.debug(AddonConst.ADDON_LOG_TEMPLATE, I18N.msg("addon.provider.working"), addonServiceName);
                        }
                );
                RedisUtil.setCacheSet(addonRecordKey, addonDefinitionsMap.keySet());
            }

            @Override
            public void uninstall() {
            }
        };
    }


}
