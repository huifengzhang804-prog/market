package com.medusa.gruul.addon.coupon.model.dto;

import com.medusa.gruul.addon.coupon.model.enums.CouponType;
import com.medusa.gruul.addon.coupon.model.enums.QueryStatus;
import com.medusa.gruul.addon.coupon.mp.entity.Coupon;
import com.medusa.gruul.global.model.o.BaseQueryPageDTO;
import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @author 张治保
 * date 2022/11/2
 */
@Getter
@Setter
@ToString
public class CouponQueryDTO extends BaseQueryPageDTO<Coupon> {

    /**
     * 有效状态
     */
    private QueryStatus status;

    /**
     * 优惠券类型
     */
    private CouponType type;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;


    /**
     * 关键词
     */
    private String keywords;

    /**
     * startDateTime
     *
     * @return startDateTime
     */
    public LocalDateTime getStartDateTime() {
        return Option.of(getStartDate()).map(LocalDate::atStartOfDay).getOrNull();
    }

    /**
     * endDateTime
     *
     * @return endDateTime
     */
    public LocalDateTime getEndDateTime() {
        return Option.of(getEndDate()).map(endDate -> endDate.atTime(LocalTime.MAX)).getOrNull();
    }


}
