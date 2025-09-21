package com.medusa.gruul.common.dubbo.rpc;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.utils.SimpleReferenceCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.apache.dubbo.spring.boot.autoconfigure.DubboConfigurationProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author 张治保
 * duubo3 配置文件 {@link DubboConfigurationProperties}
 * date 2022/9/13
 */
@RequiredArgsConstructor
public class IDynamicDubbo {

    private static final String GENERIC = "true";
    private static ConsumerConfig consumerConfig;

    public static GenericService genericService(String interfaceName) {
        SimpleReferenceCache cache = SimpleReferenceCache.getCache();
        GenericService genericService = cache.get(interfaceName);
        return Option.of(genericService)
                .getOrElse(
                        () -> {
                            GenericService service = cache.get(interfaceName);
                            if (service != null) {
                                return service;
                            }
                            synchronized (interfaceName.intern()) {
                                service = cache.get(interfaceName);
                                if (service != null) {
                                    return service;
                                }
                                ReferenceConfig<GenericService> referenceConfig = getGenericServiceReferenceConfig(interfaceName);
                                return cache.get(referenceConfig);
                            }
                        }
                );
    }

    /**
     * 获取泛化服务
     *
     * @param interfaceName 接口名称
     * @return 泛化服务
     */
    @NotNull
    private static ReferenceConfig<GenericService> getGenericServiceReferenceConfig(String interfaceName) {
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setId(interfaceName);
        referenceConfig.setInterface(interfaceName);
        referenceConfig.setConsumer(IDynamicDubbo.consumerConfig);
        referenceConfig.setTimeout(IDynamicDubbo.consumerConfig.getTimeout());
        referenceConfig.setGeneric(GENERIC);
        referenceConfig.setAsync(Boolean.FALSE);
        return referenceConfig;
    }

    @Autowired
    public void setConsumerConfig(DubboConfigurationProperties dubboConfigurationProperties) {
        IDynamicDubbo.consumerConfig = dubboConfigurationProperties.getConsumer();
    }
}
