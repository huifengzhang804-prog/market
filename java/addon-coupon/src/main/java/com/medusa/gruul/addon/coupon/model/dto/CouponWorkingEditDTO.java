package com.medusa.gruul.addon.coupon.model.dto;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.addon.coupon.model.enums.CouponProductType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

/**
 * @author 张治保
 * date 2022/11/2
 */
@Getter
@Setter
@ToString
public class CouponWorkingEditDTO {

    /**
     * 优惠券名称
     */
    @NotBlank
    @Length(max = 10)
    private String name;

    /**
     * 领券立即生效 持续时间
     */
    @Min(1)
    private Integer days;

    /**
     * 固定日期段 结束日期
     */
    private LocalDate endDate;

    /**
     * 作用的商品类型 平台全部商品 店铺全部商品 点不指定商品生效 店铺指定商品不生效
     */
    @NotNull
    private CouponProductType productType;

    /**
     * 指定商品 生效/不生效的 id 列表
     */
    @Size(min = 1)
    private Set<Long> productIds;


    /**
     * 检查优惠券类型及其 额外参数
     */
    public void validParam(boolean isPlatform) {
        Boolean isPlatformType = getProductType().getIsPlatform();
        if (isPlatform && isPlatformType) {
            return;
        }
        if (isPlatformType) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }

        CouponProductType productType = this.getProductType();
        if (!productType.getIsAssigned()) {
            this.setProductIds(null);
            return;
        }
        if (CollUtil.isEmpty(this.getProductIds())) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }

    }
}
