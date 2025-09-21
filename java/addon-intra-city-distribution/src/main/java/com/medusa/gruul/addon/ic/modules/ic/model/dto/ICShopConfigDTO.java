package com.medusa.gruul.addon.ic.modules.ic.model.dto;

import com.medusa.gruul.addon.ic.modules.ic.model.enums.ICDeliveryType;
import com.medusa.gruul.addon.ic.modules.ic.mp.entity.ICShopConfig;
import com.medusa.gruul.common.web.valid.annotation.Price;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author 张治保
 * @since 2024/8/13
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ICShopConfigDTO implements BaseDTO {

    /**
     * 是否启用同城配送 只有为true 的时候 下面其他配置才生效
     */
    private Boolean icStatus;
    /**
     * 是否启用商家配送
     */
    @NotNull
    private Boolean enableSelf;

    /**
     * 是否启用第三方开放平台配送 目前只有 UU 跑腿
     */
    @NotNull
    private Boolean enableOpen;

    /**
     * 默认的配送方 当enableSelf 和 enableOpen都为 true时不为空
     */
    private ICDeliveryType defaultType;

    /**
     * 是否需要保温箱
     */
    private Boolean warmBox;

    /**
     * 配送范围 在这个范围内 才可以同城配送
     */
    @NotNull
    @DecimalMin("1")
    @DecimalMax("50")
    private BigDecimal deliveryRange;

    /**
     * 配送说明
     */
    @Size(max = 100)
    private String description;

    /**
     * 起送金额
     */
    @NotNull
    @Min(0)
    @Price
    private Long minLimit;

    /**
     * 基础配送费
     */
    @Min(0)
    @Price
    @NotNull
    private Long baseDelivery;

    /**
     * 计费方式 按重量或距离计算配送费 在基础配送费的基础上额外增加配送费
     */
    @Valid
    @NotNull
    private BillMethod billMethod;

    /**
     * 免配送费限制，订单支付金额大于这个值时免配送费
     */
    @Min(0)
    @Price
    private Long freeLimit;

    public ICShopConfig toConfig(Long shopId) {
        return new ICShopConfig()
                .setIcStatus(icStatus)
                .setShopId(shopId)
                .setEnableSelf(enableSelf)
                .setEnableOpen(enableOpen)
                .setDefaultType(defaultType)
                .setWarmBox(warmBox)
                .setDeliveryRange(deliveryRange)
                .setDescription(description)
                .setMinLimit(minLimit)
                .setBaseDelivery(baseDelivery)
                .setBillMethod(billMethod)
                .setFreeLimit(freeLimit);
    }

    @Override
    public void validParam() {
        if (!enableSelf && !enableOpen) {
            throw new IllegalArgumentException();
        }
        //当两种配送方都不为空时 默认的配送方不能为空
        if (enableSelf && enableOpen && defaultType == null) {
            throw new IllegalArgumentException();
        }
        //当开启第三方开放平台时 是否需要保温箱不为空
        if (enableOpen && warmBox == null) {
            throw new IllegalArgumentException();
        }
    }
}
