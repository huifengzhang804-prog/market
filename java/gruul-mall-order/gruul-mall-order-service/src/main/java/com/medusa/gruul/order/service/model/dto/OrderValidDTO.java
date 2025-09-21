package com.medusa.gruul.order.service.model.dto;

import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/4/2
 */
@Getter
@Setter
@ToString
public class OrderValidDTO implements BaseDTO {

    /**
     * 订单类型
     */
    @NotNull
    private OrderType orderType;

    /**
     * 活动id 入参
     */
    private Long activityId;

    /**
     * 购买的商品
     */
    @NotNull
    @Size(min = 1)
    @Valid
    private Set<ShopProduct> products;

    /**
     * 选择的配送方式
     */
    @NotNull
    private DistributionMode distributionMode;


    @Override
    public void validParam() {
        //校验活动订单参数
        if (orderType.isActivity() && activityId == null) {
            throw SystemCode.PARAM_VALID_ERROR.msgEx("activityId cannot be null");
        }
        //是否只能购买一个商品
        if (orderType.isOnlySingleProduct() && products.size() > CommonPool.NUMBER_ONE) {
            throw SystemCode.PARAM_VALID_ERROR.msgEx("orderType is onlySingleProduct, but products has more than one product");
        }
    }

    public Long activityId() {
        return activityId == null ? 0L : activityId;
    }


    @Getter
    @Setter
    @ToString
    @EqualsAndHashCode(of = "key")
    public static class ShopProduct implements Serializable {

        /**
         * 商品 key
         */
        @NotNull
        private ShopProductSkuKey key;

        /**
         * 购买数量
         */
        @NotNull
        @Min(1)
        private Integer num;
    }

}


