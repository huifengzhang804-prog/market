package com.medusa.gruul.order.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.context.SystemContextHolder;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.common.system.model.model.Systems;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * mock 数据的工具类
 *
 * @author 张治保
 * @since 2024/2/20
 */
public interface MockUtil {

    static <T> T system(Systems systems, Supplier<T> task) {
        SystemContextHolder.set(systems);
        T result;
        try {
            result = task.get();
        } finally {
            SystemContextHolder.clear();
        }
        return result;
    }

    static <T> T consumerSystem(Supplier<T> task) {
        return system(
                new Systems()
                        .setClientType(ClientType.CONSUMER)
                        .setPlatform(Platform.H5)
                        .setIp("")
                        .setDeviceId(IdUtil.nanoId()),
                task
        );
    }

    static <T> T consumerAuth(Supplier<T> task) {
        return ISecurity.withAuthentication(
                UsernamePasswordAuthenticationToken.authenticated(
                        ISecurity.toPrincipal(
                                new SecureUser<>()
                                        .setId(RandomUtil.randomLong())
                                        .setUsername("U_" + RandomUtil.randomString(5))
                                        .setMobile("188" + RandomUtil.randomNumbers(8))
                                        .setRoles(Set.of(Roles.USER))
                        ),
                        null, List.of()
                ),
                task
        );
    }


}

