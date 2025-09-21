package com.medusa.gruul.goods.api.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * 商品状态枚举
 *
 * @author xiaoq
 * @since 2022-05-06
 */
@Getter
public enum ProductStatus {

    /**
     * 已拒绝
     */
    REFUSE(-2, "已拒绝"),
    /**
     * 审核中
     */
    UNDER_REVIEW(-1, "审核中"),
    /**
     * 下架
     */
    SELL_OFF(0, "下架"),
    /**
     * 上架
     */
    SELL_ON(1, "上架"),
    /**
     * 已售完
     */
    SELL_OUT(2, "已售完"),


    /**
     * 平台下架商品
     */
    PLATFORM_SELL_OFF(3, "平台下架"),


    /**
     * 店铺不可用
     */
    UNUSABLE(4, "店铺不可用"),

    /**
     * 供应商下架
     */
    SUPPLIER_SELL_OFF(5, "供应商下架"),

    /**
     * 供应商禁用
     */
    SUPPLIER_DISABLE(6, "供应商禁用"),
    /**
     * 库存预警
     */
    STOCK_ALERT(7, "库存预警"),


//    /**
//     * 将违规下架商品恢复销售
//     * 业务状态由 3 改为0
//     */
//    RESTORE_SALES(7, "恢复销售")
    ;


    /**
     * 值
     */
    @EnumValue
    private final int status;


    /**
     * 描述
     */
    private final String desc;

    ProductStatus(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

}
