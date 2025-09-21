package com.medusa.gruul.search.api.enums;

import com.medusa.gruul.common.model.enums.RabbitParent;
import lombok.RequiredArgsConstructor;

/**
 * Search mq 配置
 *
 * @author xiaoq
 * Description Search mq 配置
 * date 2023-02-03 11:24
 */
@RequiredArgsConstructor
public enum SearchRabbit implements RabbitParent {
    /**
     * 分类删除
     */
    CLASSIFY_REMOVE("classify_remove"),;


    /**
     * 路由key
     */
    private final String routingKey;

    public static final String EXCHANGE = "search.direct";

    @Override
    public String exchange() {
        return SearchRabbit.EXCHANGE;
    }

    @Override
    public String routingKey() {
        return routingKey;
    }
}
