package com.medusa.gruul.shop.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.shop.api.enums.ExtractionType;
import com.medusa.gruul.shop.api.enums.ShopSettledWayEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;


/**
 * @author 张治保 date 2022/4/18
 */
@Getter
@Setter
@ToString
public class ShopQueryPageDTO extends Page<Object> {

    /**
     * 商户ID
     */
    private Long id;
    /**
     * 商户编号
     */
    private String no;
    /**
     * 商户名称
     */
    private String name;
    /**
     * 商户状态
     */
    private ShopStatus status;

    /**
     * 店铺类型
     */
    private ShopType shopType;

    /**
     * 店铺运营模式
     */
    private Set<ShopMode> shopModes;

    /**
     * 抽佣类型
     */
    private ExtractionType extractionType;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 有商品的店铺id集合
     */
    private Set<Long> shopIds;

    /**
     * 有商品的店铺
     */
    private Boolean productIsNotEmpty = Boolean.FALSE;

    /**
     * 申请开始时间
     */
    private LocalDateTime applyStartTime;
    /**
     * 申请结束时间
     */
    private LocalDateTime applyEndTime;

    /**
     * 入驻开始时间
     */
    private LocalDateTime settledStartTime;
    /**
     * 入驻结束时间
     */
    private LocalDateTime settledEndTime;
    /**
     * 入驻方式
     */
    private ShopSettledWayEnum settledWay;
    /**
     * 申请人手机号
     */
    private String applyUserPhone;
    /**
     * 审核人手机号
     */
    private String auditUserPhone;
    /**
     * 是否查询审核信息
     */
    private Boolean queryAuditInfo = Boolean.FALSE;
    /**
     * 是否是装修时查询使用
     */
    private Boolean forDecoration = Boolean.FALSE;


}
