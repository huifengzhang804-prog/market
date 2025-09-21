package com.medusa.gruul.shop.service.model.dto;

import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.global.model.helper.Address;
import com.medusa.gruul.global.model.o.RangeTime;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 张治保
 * @since 2024/11/15
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopDetailVO implements Address {

    /**
     * 店铺状态
     */
    private ShopStatus status;

    /**
     * 店铺运营模式
     */
    private ShopMode shopMode;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 店铺logo
     */
    private String logo;

    /**
     * 定点经纬度
     */
    private Point location;

    /**
     * 关注人数
     */
    private Long follow;

    /**
     * 当前用户是否已关注
     */
    private Boolean isFollow;

    /**
     * 起送费 （O2O）
     */
    private Long minLimitPrice;

    /**
     * 店铺直线距离
     */
    private BigDecimal distance;

    //todo --------------- 店铺首页需要 -----------------
    /**
     * 商户号
     */
    private String no;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 店铺介绍
     */
    private String briefing;

    /**
     * 店铺头部背景
     */
    private String headBackground;

    /**
     * 联系方式
     */
    private String contractNumber;

    /**
     * 省市区
     */
    private List<String> area;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 营业时间
     */
    private RangeTime openTime;

    /**
     * 上新提示
     */
    private String newTips;

    /**
     * 在售商品数量
     */
    private Long productNum;

    /**
     * 销量
     */
    private Long sales;

    //todo --------------- 商品详情需要 -----------------

    /**
     * 店铺综合体验评分
     */
    private String score;

}
