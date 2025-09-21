package com.medusa.gruul.order.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.order.service.model.enums.OrderQueryStatus;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 小程序订单查询条件
 *
 * @author wufuzhong
 * @Date 2024-1-22 10:43:12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderMiniAppDTO {

    /**
     * 订单号、商品名称
     */
    private String keywords;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 买家id
     */
    private Long buyerId;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 收货人姓名
     */
    private String receiverName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 是否拥有优先权(发货)
     */
    private Boolean isPriority;

    /**
     * 查询指定店铺的商品
     */
    private Long shopOrderShopId;


    /**
     * 查询指定供应商的商品
     */
    private Long shopOrderSupplierId;

    /**
     * 商品销售方式
     */
    private SellType sellType;

    /**
     * 订单状态
     */
    private OrderQueryStatus status;

    /**
     * 开始时间
     */
    private LocalDate startTime;

    /**
     * 结束时间
     */
    private LocalDate endTime;


    /**
     * 店铺门店id
     */
    private Long shopStoreId;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺ids 供查询条件店铺类型使用
     */
    private List<Long> shopIds;

    /**
     * 配送方式
     */
    private DistributionMode distributionMode;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 订单所属渠道
     */
    private Platform platform;

    /**
     * 导出的订单ids
     */
    private List<String> exportShopIds;
    /**
     * 导出的店铺订单ids
     */
    private List<String> exportShopOrderIds;

    public LocalDateTime getStartTime() {
        return Option.of(this.startTime).map(LocalDate::atStartOfDay).getOrNull();
    }

    public LocalDateTime getEndTime() {
        return Option.of(this.endTime).map(time -> time.atTime(LocalTime.MAX)).getOrNull();
    }

    public String getOrderNo() {
        return StrUtil.trimToNull(orderNo);
    }
}