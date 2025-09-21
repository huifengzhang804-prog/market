package com.medusa.gruul.order.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.order.api.entity.Order;
import com.medusa.gruul.order.service.model.enums.OrderQueryStatus;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * 订单查询条件
 *
 * @author 张治保
 * date 2022/3/11
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderQueryDTO extends Page<Order> {

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
     * 查询指定供应商的商品
     */
    private Long supplierId;

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
    private String shopStoreId;

    /**
     * 店铺ids 供查询条件店铺类型使用
     */
    private Set<Long> shopIds;

    /**
     * 配送方式
     */
    private DistributionMode distributionMode;

    /**
     * 订单所属渠道
     */
    private Platform platform;

    /**
     * 导出的订单ids
     */
    private Set<String> exportOrderNos;
    /**
     * 导出的店铺订单ids
     */
    private Set<String> exportShopOrderNos;

    /**
     * 小程序发货提示
     */
    private Boolean isMiniAppTip = Boolean.FALSE;
    /**
     * 关键词 PC端搜索 订单号或者商品名称
     */
    private String keywords;

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