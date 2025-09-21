package com.medusa.gruul.addon.coupon.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * @author 张治保
 * date 2022/11/11
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ProductCouponPageDTO extends Page<CouponVO> {

    /**
     * 店铺id
     */
    @NotNull
    private Long shopId;

    /**
     * 商品id
     */
    @NotNull
    private Long productId;

    /**
     * 商品金额
     */
    @NotNull
    @Min(1)
    private Long amount;
}
