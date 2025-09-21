package com.medusa.gruul.addon.store.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.medusa.gruul.addon.store.model.enums.StoreStatus;
import com.medusa.gruul.addon.store.mp.entity.ShopAssistant;
import com.medusa.gruul.common.geometry.GeometryTypeHandler;
import com.vividsolutions.jts.geom.Point;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalTime;
import java.util.List;

/**
 * 门店店员VO
 *
 * @author xiaoq
 * @Description 门店 店员 VO
 * @date 2023-03-15 16:04
 */
@Data
@Accessors(chain = true)
public class ShopStoreAssistantVO {
    /**
     * 门店id
     */
    private Long id;

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

    /**
     * 所属门店店员信息
     */
    private List<ShopAssistant> shopAssistantList;
}
