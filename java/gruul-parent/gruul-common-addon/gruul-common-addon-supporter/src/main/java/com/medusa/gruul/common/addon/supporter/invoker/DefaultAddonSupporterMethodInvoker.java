package com.medusa.gruul.common.addon.supporter.invoker;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.addon.supporter.helper.AddonSupporterHelper;
import com.medusa.gruul.common.addon.supporter.sacnner.ScannerContext;
import com.medusa.gruul.common.redis.util.RedisUtil;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * @author 张治保
 * date 2022/9/16
 */
@RequiredArgsConstructor
public class DefaultAddonSupporterMethodInvoker implements IAddonSupporterMethodInvoker {

    /**
     * 扫描上下文
     */
    private final ScannerContext scannerContext;

    /**
     * addonSupporter注解
     */
    private final AddonSupporter addonSupporter;

    /**
     * addonMethod注解
     */
    private final AddonMethod addonMethod;

    /**
     * key生成函数
     */
    private volatile Function<Object[], String> keyFunc;
    /**
     * args生成函数
     */
    private volatile Function<Object[], Object[]> argsFunc;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        args = args == null ? new Object[0] : args;
        if (keyFunc != null) {
            return addonSupporterMethodInvoke(keyFunc.apply(args), argsFunc.apply(args));
        }
        syncRenderFunc(method.getName(), args);
        return addonSupporterMethodInvoke(keyFunc.apply(args), argsFunc.apply(args));
    }

    /**
     * 渲染并设置func函数 加锁 double check
     *
     * @param methodName 方法名
     * @param methodArgs 方法参数
     */
    public synchronized void syncRenderFunc(String methodName, Object[] methodArgs) {
        if (keyFunc != null) {
            return;
        }
        int length;
        if (methodArgs == null || (length = methodArgs.length) < 1 || !addonMethod.arg1Filter()) {
            String keyPrefix = AddonSupporterHelper.getCacheKey(addonSupporter, methodName, null);
            keyFunc = args -> keyPrefix;
            argsFunc = args -> args;
            return;
        }
        String keyPrefix = AddonSupporterHelper.getCacheKey(addonSupporter, methodName, null);
        keyFunc = args -> RedisUtil.key(keyPrefix, args[0].toString());
        argsFunc = args -> {
            Object[] trueArgs = new Object[length - 1];
            if (length > 1) {
                System.arraycopy(args, 1, trueArgs, 0, length - 1);
            }
            return trueArgs;
        };

    }

    /**
     * 调用支持者方法
     *
     * @param cacheKey 缓存key
     * @param args     参数
     * @return 结果
     */
    public Object addonSupporterMethodInvoke(String cacheKey, Object[] args) {
        return AddonSupporterHelper.addonsInvokedResult(
                scannerContext,
                cacheKey,
                addonMethod.returnType(),
                args
        );
    }

}
