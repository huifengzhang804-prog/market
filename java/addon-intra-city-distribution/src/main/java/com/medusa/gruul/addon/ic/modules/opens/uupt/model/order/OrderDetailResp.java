package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.OrderState;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
public class OrderDetailResp {
    /**
     * UU订单号
     */
    private String orderCode;

    /**
     * 三方对接平台订单ID
     */
    private String originId;

    /**
     * 发货地址详细地址
     */
    private String fromAddress;

    /**
     * 收货人地址详细地址
     */
    private String toAddress;

    /**
     * 发货地址经坐标，百度地图坐标系经纬度（格式:113.71776,34.767501）
     */
    private String fromLocation;

    /**
     * 收货地址经坐标，百度地图坐标系经纬度（格式:113.71776,34.767501）
     */
    private String toLocation;

    /**
     * 配送距离（单位：米）
     */
    private Long distance;

    /**
     * 订单总金额（优惠前）,包含小费
     */
    private Long orderPrice;

    /**
     * 总优惠金额
     */
    private String priceOff;

    /**
     * 费用明细列表
     */
    private List<OrderPriceDetailResp> feeDetailList;

    /**
     * 备注
     */
    private String note;

    /**
     * 当前状态1下单成功 3跑男抢单 4已到达 5已取件 6到达目的地 10收件人已收货 -1订单取消
     */
    private OrderState state;

    /**
     * 发单时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime addTime;

    /**
     * 完成时间(yyyy-MM-dd HH:mm:ss)
     */
    private LocalDateTime finishTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 跑男姓名(跑男接单后)
     */
    private String driverName;

    /**
     * 跑男电话(跑男接单后)
     */
    private String driverMobile;

    /**
     * 跑男的坐标，百度地图坐标系经纬度（格式:113.71776,34.767501）
     */
    private String driverLastLoc;

    /**
     * 跑男接单后预计到达时间(格式:yyyy-MM-dd HH:mm:ss)取货前为到达取货地，取货后为到达收货地
     */
    private LocalDateTime expectedArriveTime;

    /**
     * 收货确认码
     */
    private String confirmCode;

    /**
     * 取件码？文档里没有 响应结果里有
     */
    private String pickupCode;
}
