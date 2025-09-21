package com.medusa.gruul.addon.rebate.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.rebate.model.enums.MonthsOrder;
import com.medusa.gruul.addon.rebate.model.enums.RebateOrderStatus;
import com.medusa.gruul.addon.rebate.model.vo.RebateOrderStatistic;
import com.medusa.gruul.addon.rebate.mp.entity.RebateOrder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RebateOrderQueryDTO extends Page<RebateOrder> {


    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 买家昵称
     */
    private String buyerNickname;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 订单号或商品名称
     */
    private String keyword;

    /**
     * 下单开始时间
     */
    private String orderCreateTime;

    /**
     * 下单结束时间
     */
    private String orderEndTime;

    /**
     * 近一个月订单 三个月订单
     */
    private MonthsOrder monthOrders;

    /**
     * 订单状态:1->正常状态;2->系统关闭;3->买家关闭订单;4->卖家关闭订单
     */
    private RebateOrderStatus status;

    /**
     * 返利统计
     */
    private RebateOrderStatistic statistic;


    /**
     * 用户id
     */
    private Long userId;
    /**
     * 导出的订单号
     */
    private List<String> exportOrderNos;
}
