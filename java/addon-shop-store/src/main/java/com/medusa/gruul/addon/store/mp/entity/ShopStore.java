package com.medusa.gruul.addon.store.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.store.model.enums.StoreStatus;
import com.medusa.gruul.common.geometry.GeometryTypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalTime;

/**
 *
 *
 * @author xiaoq
 * @Description 店铺门店表
 * @date 2023-03-07 10:04
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_shop_store", autoResultMap = true)
public class ShopStore extends BaseEntity {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 门店图片
     */
    private String storeImg;


    /**
     * 门店logo
     */
    private String storeLogo;

    /**
     * 门店状态
     */
    private StoreStatus status;




    /**
     * 负责人名称
     */
    private String functionaryName;


    /**
     * 负责人电话号
     */
    private String functionaryPhone;


    /**
     * 营业开始时间
     */
    private LocalTime businessStartTime;

    /**
     * 营业结束时间
     */
    private LocalTime businessEndTime;

    /**
     * 定点经纬度
     */
    @TableField(typeHandler = GeometryTypeHandler.class)
    private Point location;

    /**
     * 详细地址
     */
    private String detailedAddress;


    /**
     * 开始提货天
     */
    private Integer startDeliveryDay;

    /**
     * 结束提货天
     */
    private Integer endDeliveryDay;



}
