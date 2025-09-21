package com.medusa.gruul.common.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.function.Function;

/**
 * @author 张治保
 * date 2022/4/21
 */
@Getter
@RequiredArgsConstructor
public enum DistributionMode {

    /**
     * 快递配送
     */
    EXPRESS(1, false, new Function<>() {
        @Override
        public Map<String, BigDecimal> apply(Function<DistributionMode, Map<String, BigDecimal>> distributionModeLongFunction) {
            return distributionModeLongFunction.apply(EXPRESS);
        }
    }),

    /**
     * 同城配送
     */
    INTRA_CITY_DISTRIBUTION(2, false, new Function<>() {
        @Override
        public Map<String, BigDecimal> apply(Function<DistributionMode, Map<String, BigDecimal>> distributionModeLongFunction) {
            return distributionModeLongFunction.apply(INTRA_CITY_DISTRIBUTION);
        }
    }),

    /**
     * 店铺门店
     */
    SHOP_STORE(3, true, func -> Map.of()),
    
    /**
     * 虚拟配送
     */
    VIRTUAL(4, true, func -> Map.of());
    @EnumValue
    private final Integer value;

    /**
     * 是否不需要运费
     */
    private final boolean noFreight;

    /**
     * 计算运费的函数
     */
    private final Function<Function<DistributionMode, Map<String, BigDecimal>>, Map<String, BigDecimal>> function;

    public String getName() {
        return name();
    }
}
