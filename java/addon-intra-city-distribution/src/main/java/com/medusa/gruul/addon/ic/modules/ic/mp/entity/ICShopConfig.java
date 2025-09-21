package com.medusa.gruul.addon.ic.modules.ic.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.addon.ic.modules.ic.model.dto.BillMethod;
import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author 张治保
 * @since 2024/8/13
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_ic_shop_conf", autoResultMap = true)
public class ICShopConfig extends BaseEntity {
    /**
     * shopId
     */
    private Long shopId;
    /**
     * 是否启用同城配送 只有为true 的时候 下面其他配置才生效
     */
    private Boolean icStatus;

    /**
     * 是否启用商家配送
     */
    private Boolean enableSelf;

    /**
     * 是否启用第三方开放平台配送 目前只有 UU 跑腿
     */
    private Boolean enableOpen;

    /**
     * 默认的配送方
     */
    private ICDeliveryType defaultType;

    /**
     * 是否需要保温箱
     */
    private Boolean warmBox;

    /**
     * 配送范围 在这个范围内 才可以同城配送 单位 km
     */
    private BigDecimal deliveryRange;

    /**
     * 配送说明
     */
    private String description;

    /**
     * 起送金额
     */
    private Long minLimit;

    /**
     * 基础配送费
     */
    private Long baseDelivery;

    /**
     * 计费方式 按重量或距离计算配送费 在基础配送费的基础上额外增加配送费
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private BillMethod billMethod;

    /**
     * 免配送费限制，订单支付金额大于这个值时免配送费
     */
    private Long freeLimit;

}
