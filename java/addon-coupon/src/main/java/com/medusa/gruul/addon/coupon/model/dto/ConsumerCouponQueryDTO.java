package com.medusa.gruul.addon.coupon.model.dto;

import com.medusa.gruul.addon.coupon.model.enums.ConsumerQueryStatus;
import com.medusa.gruul.addon.coupon.model.vo.CouponVO;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.BaseDTO;
import com.medusa.gruul.global.model.o.BaseQueryPageDTO;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;


/**
 * 消费者端优惠券分页查询条件
 *
 * @author 张治保
 * date 2022/11/3
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ConsumerCouponQueryDTO extends BaseQueryPageDTO<CouponVO> implements BaseDTO {

    /**
     * 是否查询平台优惠券
     */
    @NotNull
    private Boolean isPlatform;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 查询状态
     */
    @NotNull
    private ConsumerQueryStatus status;


    @Override
    public void validParam() {
        if (isPlatform && status.isShopMust()) {
            throw new GlobalException("平台不能使用该类型查询");
        }
    }
}
