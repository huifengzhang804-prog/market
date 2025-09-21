package com.medusa.gruul.common.addon.supporter.proxy;


import com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext;

import java.lang.reflect.Proxy;

/**
 * 插件驱动器 代理 工厂
 *
 * @author 张治保
 * date 2022/9/16
 */
public interface AddonSupporterProxyFactory {


    /**
     * 实力化代理对象
     *
     * @param addonSupporterProxy 代理类
     * @param <T>                 类行泛型
     * @return 代理对象实例
     */
    @SuppressWarnings("unchecked")
    static <T> T newInstance(AddonSupporterProxy<T> addonSupporterProxy) {
        Class<T> addonSupporterInterface = addonSupporterProxy.getAddonSupporterInterface();
        return (T) Proxy.newProxyInstance(addonSupporterInterface.getClassLoader(), new Class[]{addonSupporterInterface}, addonSupporterProxy);
    }

    /**
     * 实力化代理对象
     *
     * @param scannerContext          插件扫描程序应用上下文
     * @param addonSupporterInterface 代理对象接口类
     * @param <T>                     类行泛型
     * @return 代理对象实例
     */
    static <T> T newInstance(ScannerContext scannerContext, Class<T> addonSupporterInterface) {
        return AddonSupporterProxyFactory.newInstance(new AddonSupporterProxy<>(scannerContext, addonSupporterInterface));
    }
}
