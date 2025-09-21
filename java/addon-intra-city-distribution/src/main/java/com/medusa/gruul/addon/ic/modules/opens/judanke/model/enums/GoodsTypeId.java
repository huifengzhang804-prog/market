package com.medusa.gruul.addon.ic.modules.opens.judanke.model.enums;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * @since 2024/8/7
 */
@Getter
@RequiredArgsConstructor
public enum GoodsTypeId {
    /**
     * （2=快餐美食，13=水果生鲜，
     * 4=日用百货，5=蛋糕甜品，
     * 6=鲜花绿植，1=文件证件，

     */
    /**
     * 快餐美食
     */
    A(2),
    /**
     * 水果生鲜
     */
    B(13),
    /**
     * 日用百货
     */
    C(4),
    /**
     * 蛋糕甜品
     */
    D(5),
    /**
     * 鲜花绿植
     */
    E(6),
    /**
     * 文件证件
     */
    F(1),
    /**
     * 手机数码
     */
    G(7),
    /**
     * 龙虾烧烤
     */
    H(14),
    /**
     * 火锅串串
     */
    I(15),
    /**
     * 成人用品
     */
    J(16),
    /**
     * 医药健康
     */
    K(11),
    /**
     * 其它
     */
    L(12),
    ;
    @JSONField
    private final int value;
}
