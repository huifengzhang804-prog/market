package com.medusa.gruul.common.addon.supporter.proxy;

import com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.SmartFactoryBean;

/**
 * 插件驱动器 factoryBean
 *
 * @author 张治保
 * date 2022/9/16
 */
@RequiredArgsConstructor
public class AddonSupporterFactoryBean<T> implements SmartFactoryBean<T> {

    private final Class<T> addonSupporterInterface;
    @Setter
    private ScannerContext scannerContext;


    @Override
    public T getObject() {
        return AddonSupporterProxyFactory.newInstance(scannerContext, addonSupporterInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.addonSupporterInterface;
    }
}
