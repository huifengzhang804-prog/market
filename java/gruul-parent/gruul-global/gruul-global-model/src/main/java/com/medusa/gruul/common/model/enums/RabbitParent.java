package com.medusa.gruul.common.model.enums;

/**
 * 所有rabbit枚举的父类 抽象
 *
 * @author 张治保
 * date 2022/7/7
 */
public interface RabbitParent {

    /**
     * 获取交换机名称
     *
     * @return 交换机名称
     */
    String exchange();

    /**
     * 获取路由key名称
     *
     * @return 路由key
     */
    String routingKey();
}
