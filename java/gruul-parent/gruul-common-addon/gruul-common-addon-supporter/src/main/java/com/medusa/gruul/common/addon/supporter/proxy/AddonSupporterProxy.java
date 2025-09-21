package com.medusa.gruul.common.addon.supporter.proxy;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.helper.AddonSupporterHelper;
import com.medusa.gruul.common.addon.supporter.invoker.DefaultAddonSupporterMethodInvoker;
import com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext;
import lombok.Getter;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.Serial;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 代理类 参考MapperProxy
 *
 * @author 张治保
 * date 2022/9/16
 */
public class AddonSupporterProxy<T> implements InvocationHandler, Serializable {

    @Serial
    private static final long serialVersionUID = -4724728452352L;
    private final ScannerContext scannerContext;
    @Getter
    private final Class<T> addonSupporterInterface;

    private final Map<Method, DefaultAddonSupporterMethodInvoker> addonSupporterMethodCache = new ConcurrentHashMap<>();


    public AddonSupporterProxy(ScannerContext scannerContext, Class<T> addonSupporterInterface) {
        this.scannerContext = scannerContext;
        this.addonSupporterInterface = addonSupporterInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return addonSupporterMethod(method).invoke(proxy, method, args);
    }

    private DefaultAddonSupporterMethodInvoker addonSupporterMethod(Method method) {
        return addonSupporterMethodCache.computeIfAbsent(
                method,
                m -> new DefaultAddonSupporterMethodInvoker(
                        scannerContext,
                        AddonSupporterHelper.getAddonSupporter(addonSupporterInterface),
                        getAnno(m)
                )
        );
    }

    private AddonMethod getAnno(Method method) {
        AddonMethod annotation = AnnotationUtils.findAnnotation(method, AddonMethod.class);
        if (annotation == null) {
            return new AddonMethod() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return AddonMethod.class;
                }

                @Override
                public Class<?> returnType() {
                    return method.getReturnType();
                }

                @Override
                public boolean arg1Filter() {
                    return false;
                }

            };
        }
        return annotation;
    }
}
