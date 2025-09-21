package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author miskw
 * @date 2023/7/15
 * @describe 分销状态
 */
@Getter
@RequiredArgsConstructor
public enum DistributionStatus {
    /**
     * 分销中
     */
    IN_DISTRIBUTION(0),
    /**
     * 取消分销
     */
    CANCEL_DISTRIBUTION(1);
    @EnumValue
    private final int value;

    public static DistributionStatus toDistributeStatus(ProductStatus productStatus) {
        return switch (productStatus) {
            case REFUSE, UNDER_REVIEW, SELL_OFF, UNUSABLE, PLATFORM_SELL_OFF, SELL_OUT, SUPPLIER_SELL_OFF, SUPPLIER_DISABLE ->
                    DistributionStatus.CANCEL_DISTRIBUTION;
            // 商品状态变动只可取消分销，不能改为分销中
            default -> null;
        };
    }

}
