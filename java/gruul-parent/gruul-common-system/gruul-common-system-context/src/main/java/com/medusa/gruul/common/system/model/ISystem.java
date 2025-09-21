package com.medusa.gruul.common.system.model;

import com.medusa.gruul.common.system.model.context.SystemContextHolder;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.system.model.model.Systems;
import com.medusa.gruul.global.model.constant.GlobalCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import io.vavr.control.Option;

import java.util.function.Supplier;

/**
 * @author 张治保
 * date 2022/2/11
 */
public interface ISystem {

    /**
     * 获取系统信息
     *
     * @return 可能为空的系统信息
     */
    static Option<Systems> systemOpt() {
        return Option.of(SystemContextHolder.get());
    }

    /**
     * 获取系统信息
     *
     * @return 系统信息
     */
    static Systems systemMust() {
        return systemOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 平台
     *
     * @return 可能为空的平台
     */
    static Option<Platform> platformOpt() {
        return systemOpt().map(Systems::getPlatform);
    }

    /**
     * 平台
     *
     * @return 平台
     */
    static Platform platformMust() {
        return platformOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 客户端ip
     *
     * @return 可能为空的ip
     */
    static Option<String> ipOpt() {
        return systemOpt().map(Systems::getIp);
    }

    /**
     * 客户端ip
     *
     * @return ip
     */
    static String ipMust() {
        return ipOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }


    /**
     * 设备id
     *
     * @return 可能为空的设备id
     */
    static Option<String> deviceIdOpt() {
        return systemOpt().map(Systems::getDeviceId);
    }

    /**
     * 设备id
     *
     * @return 设备id
     */
    static String deviceIdMust() {
        return deviceIdOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 客户端类型
     *
     * @return 可能为空的客户端类型
     */
    static Option<ClientType> clientTypeOpt() {
        return systemOpt().map(Systems::getClientType);
    }

    /**
     * 客户端类型
     *
     * @return 客户端类型
     */
    static ClientType clientTypeMust() {
        return clientTypeOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 请求版本号
     *
     * @return 可能为空的版本号
     */
    static Option<String> versionOpt() {
        return systemOpt().map(Systems::getVersion);
    }

    /**
     * 请求版本号
     *
     * @return 版本号
     */
    static String versionMust() {
        return versionOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 租户id 可能为空
     *
     * @return 租户id 可能为空
     */
    static Option<Long> platformIdOpt() {
        return systemOpt().map(Systems::getPlatformId);
    }

    /**
     * 租户id
     *
     * @return 租户id
     */
    static Long platformIdMust() {
        return platformIdOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * shopId 可能为空
     *
     * @return shopId 可能为空
     */
    static Option<Long> shopIdOpt() {
        return systemOpt().map(Systems::getShopId);
    }

    /**
     * shopId
     *
     * @return shopId
     */
    static Long shopIdMust() {
        return shopIdOpt().getOrElseThrow(() -> new GlobalException(GlobalCode.REQUEST_INVALID, "bad request"));
    }

    /**
     * 修改shopId 并执行后续操作
     * 并且 在后续操完成后 修改回原来的shopId
     *
     * @param shopId  shopId
     * @param factory 后续操作
     * @param <T>     后续操作返回值类型
     * @return 后续操作返回值
     */
    static <T> T shopId(Long shopId, Supplier<T> factory) {
        Systems systems = ISystem.systemOpt()
                .getOrElse(() -> {
                    Systems newSystems = new Systems();
                    SystemContextHolder.set(newSystems);
                    return newSystems;
                });
        Long preShopId = systems.getShopId();
        systems.setShopId(shopId);
        try {
            return factory.get();
        } finally {
            if (preShopId == null) {
                SystemContextHolder.clear();
            } else {
                systems.setShopId(preShopId);
            }
        }
    }

    /**
     * 修改shopId 并执行后续操作
     * 并且 在后续操完成后 修改回原来的shopId
     *
     * @param shopId   shopId
     * @param runnable 后续操作
     */
    static void shopId(Long shopId, Runnable runnable) {
        ISystem.shopId(shopId, () -> {
            runnable.run();
            return null;
        });
    }
}
