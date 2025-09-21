package com.medusa.gruul.addon.distribute.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author 张治保
 * date 2022/11/15
 */
@Getter
@RequiredArgsConstructor
public enum DistributeProductStatus {

    /**
     * 上架
     */
    ENABLE(1),

    /**
     * 下架
     */
    DISABLE(2),

    /**
     * 违规禁用
     */
    FORBIDDEN(3);

    @EnumValue
    private final Integer value;

    public static DistributeProductStatus toDistributeStatus(ProductStatus productStatus) {
        return switch (productStatus) {
            case REFUSE, UNDER_REVIEW, SELL_OFF, UNUSABLE -> DistributeProductStatus.DISABLE;
            case PLATFORM_SELL_OFF -> DistributeProductStatus.FORBIDDEN;
            default -> DistributeProductStatus.ENABLE;
        };
    }
}
