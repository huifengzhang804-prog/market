package com.medusa.gruul.addon.seckill.model.dto;

import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;


/**
 * 秒杀活动详情
 *
 * @author 张治保
 * @since 2024/5/28
 */
@Getter
@Setter
@ToString
public class SeckillActivityDTO implements BaseDTO {

    /**
     * 秒杀活动名称
     */
    @NotBlank
    private String name;

    /**
     * 活动日期
     */
    @NotNull
    private LocalDate date;

    /**
     * 活动场次
     */
    @Min(0)
    @Max(23)
    private Integer round;

    /**
     * 支付超时时间 单位 分 3-360
     */
    @NotNull
    @Range(min = CommonPool.NUMBER_THREE, max = CommonPool.NUMBER_THREE * CommonPool.NUMBER_ONE_HUNDRED_TWENTY)
    private Integer payTimeout;

    /**
     * 叠加优惠信息
     */
    @NotNull
    private StackableDiscount stackable = new StackableDiscount();


    /**
     * 绑定的商品
     */
    @NotEmpty
    private Set<@Valid SeckillActivityProductDTO> products;


    public void validParam() {
        //设置支付超时时间
        stackable.setPayTimeout(Duration.ofMinutes(payTimeout));
        //活动日期不能早于当前日期
        if (date.isBefore(LocalDate.now())) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
    }

}
