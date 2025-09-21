package com.medusa.gruul.storage.api.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 库存变化类型
 *
 * @author xiaoq
 * @Description StockChangeType.java
 * @date 2023-07-21 15:46
 */

@Getter
@RequiredArgsConstructor
public enum StockChangeType {
    /**
     * 发布入库  商品发布时 --- 针对商品 所有sku变动类型相同
     */
    PUBLISHED_INBOUND(0,"发布入库"),

    /**
     * 编辑入库  库存编辑时 --- 针对sku sku变动类型不同
     */
    EDITED_INBOUND(1,"编辑入库"),

    /**
     * 盘盈入库 库存盘点时 --- 针对sku sku变动类型不同
     */
    OVERAGE_INBOUND(2,"盘盈入库"),

    /**
     * 退货入库 订单退货成功 --- 针对sku sku变动类型相同
     */
    RETURNED_INBOUND(3,"退货入库"),

    /**
     * 订单取消入库  --- 针对sku sku变动类型相同
     */
    ORDER_CANCELLED_INBOUND(4,"订单取消入库"),

    /**
     * 调拨入库  出入库 --- 针对sku 所有sku变动类型相同
     */
    ALLOCATION_INBOUND(5,"调拨入库"),

    /**
     * 其它入库 出入库 --- 针对sku 所有sku变动类型相同
     */
    OTHER_INBOUND(6,"其它入库"),

    /**
     * 销售出库 出入库 --- 针对sku 所有sku变动类型相同
     */
    SOLD_OUTBOUND(7,"销售出库"),

    /**
     * 编辑出库 出入库 --- 针对sku 所有sku变动类型相同
     */
    EDITED_OUTBOUND(8,"编辑出库"),

    /**
     * 盘亏出库  库存盘点时 --- 针对sku sku变动类型不同
     */
    SHORTAGE_OUTBOUND(9,"盘亏出库"),

    /**
     * 调拨出库 出入库 --- 针对商品 所有sku变动类型相同
     */
    ALLOCATION_OUTBOUND(10,"调拨出库"),

    /**
     * 其它出库 出入库 --- 针对sku 所有sku变动类型相同
     */
    OTHER_OUTBOUND(11,"其它出库"),

    /**
     * 采购入库 出入库 --- 针对sku 所有sku变动类型相同
     */
    PURCHASE_INBOUND(12,"采购入库");

    @EnumValue
    private final Integer value;

    private final String desc;
}
