package com.medusa.gruul.service.uaa.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * @author: WuDi
 * @date: 2022/9/13
 */
@RequiredArgsConstructor
public enum UaaRabbit implements RabbitParent {


    /**
     * 更改用户信息
     */
    UPDATE_DATA("uaa.update.data"),

    /**
     * 更改用户权限
     */
    UPDATE_AUTHORITY("uaa.update.authority"),


    /**
     * 批量注册
     */
    BATCH_REGISTER("uaa.batch.register");

    /**
     * 路由key
     */
    private final String routingKey;

    public static final String EXCHANGE = "uaa.direct";


    @Override
    public String exchange() {
        return UaaRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
