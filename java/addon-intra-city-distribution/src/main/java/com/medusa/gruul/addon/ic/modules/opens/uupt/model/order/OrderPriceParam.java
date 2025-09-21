package com.medusa.gruul.addon.ic.modules.opens.uupt.model.order;

import com.medusa.gruul.addon.ic.modules.opens.uupt.model.enums.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author 张治保
 * @since 2024/8/12
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderPriceParam {

    /**
     * 否 三方对接平台订单ID
     */
    private String originId;

    /**
     * 是否是急单
     */
    private UrgentOrder urgentOrder;

    /**
     * 是 订单分类、 订单小类，参考枚举值
     */
    private SendType sendType;

    /**
     * 是 发货地址
     */
    private String fromAddress;

    /**
     * 否 发货详细地址具体门牌号或门店名称
     */
    private String fromUserNote;

    /**
     * 是 发货地址坐标纬度(坐标系为百度地图坐标系)
     */
    private BigDecimal fromLat;

    /**
     * 是 发货地址坐标经度(坐标系为百度地图坐标系)
     */
    private BigDecimal fromLng;

    /**
     * 是 收货人地址
     */
    private String toAddress;

    /**
     * 否 收货人详细地址具体门牌号
     */
    private String toUserNote;

    /**
     * 是 收货地址坐标纬度(坐标系为百度地图坐标系)
     */
    private BigDecimal toLat;

    /**
     * 是 收货地址坐标经度(坐标系为百度地图坐标系)
     */
    private BigDecimal toLng;

    /**
     * 否 行政区编码（区县级），adCode优先于cityName和countyName
     */
    private String adCode;

    /**
     * 否 订单所在城市名 称(如郑州市就填”郑州市“，必须带上“市”)
     */
    private String cityName;

    /**
     * 是 订单所在县级地名称(如金水区就填“金水区”)
     */
    private String countyName;

    /**
     * 否 预约类型，默认实时订单，参考预约类型枚举
     */
    private SubscribeType subscribeType;

    /**
     * 否 预约时间时间戳（秒级）
     */
    private Long subscribeTime;

    /**
     * 否 优惠券类型，默认个人优惠券，参考优惠券类型枚举（必须企业帐号才可以使用）
     */
    private CouponType couponType;

    /**
     * 否 物品类型，参考物品类型枚举
     */
    private GoodsType goodsType;

    /**
     * 否 货物重量（单位KG）
     */
    private Integer goodsWeight;

    /**
     * 否 商品保价，价格（1-10000）按照一定费率收取保价费用（单位元）
     */
    private Integer goodsPrice;

    /**
     * 否 下单额外支付小费（单位元）
     */
    private Integer onlineFee;

    /**
     * 否 第三方平台门店ID
     */
    private String thirdShopId;

    /**
     * 否  UU服务编码，默认专送，参考UU服务编码枚举
     */
    private ServiceCode serviceCode;

    /**
     * 否 行业分类，参考行业分类枚举
     */
    private BusinessCategory businessCategory;
}
