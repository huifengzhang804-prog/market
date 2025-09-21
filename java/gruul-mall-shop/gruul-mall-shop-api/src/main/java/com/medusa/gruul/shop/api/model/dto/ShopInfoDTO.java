package com.medusa.gruul.shop.api.model.dto;

import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalTime;

/**
 * @author 张治保
 * date 2022/5/25
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ShopInfoDTO implements BaseDTO {
    /**
     * 店铺id
     */
    private Long id;

    /**
     * 店铺编号
     */
    private String no;

    /**
     * 店铺名称
     */
    private String name;

    /**
     * 公司名称
     */
    private String companyName;

    /**
     * 管理员id
     */
    private Long userId;

    /**
     * 联系方式
     */
    private String contractNumber;

    /**
     * 店铺状态
     */
    private ShopStatus status;

    /**
     * 联系地址
     */
    private String address;

    /**
     * 店铺地址定位
     */
    private Point location;

    /**
     * 店铺logo
     */
    private String logo;

    /**
     * 店铺介绍
     */
    private String briefing;

    /**
     * 店铺头部背景
     */
    private String headBackground;

    /**
     * 营业开始时间
     */
    private LocalTime start;

    /**
     * 营业结束时间
     */
    private LocalTime end;

    /**
     * 上新提示
     */
    private String newTips;
}
