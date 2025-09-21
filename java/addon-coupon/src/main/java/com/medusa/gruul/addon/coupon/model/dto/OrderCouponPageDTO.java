package com.medusa.gruul.addon.coupon.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author 张治保
 * date 2022/11/7
 */
@Getter
@Setter
@ToString
public class OrderCouponPageDTO extends Page<CouponVO> {

    /**
     * 店铺id 平台为0
     */
    @NotNull
    private Long shopId;

    /**
     * 商品金额列表
     */
    @NotNull
    @Size(min = 1)
    private List<ProductAmountDTO> productAmounts;
}
