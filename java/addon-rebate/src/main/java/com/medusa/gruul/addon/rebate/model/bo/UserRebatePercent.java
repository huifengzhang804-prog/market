package com.medusa.gruul.addon.rebate.model.bo;

import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.helper.AmountCalculateHelper;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 返利百分比配置
 *
 * @author 张治保
 * date 2023/9/12
 */

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class UserRebatePercent {

    /**
     * 返利是否未开启
     */
    private boolean disabled;

    /**
     * 返利百分比
     */
    private Long rebate = 0L;

    /**
     * 返利支付百分比
     */
    private Long pay = 0L;

    /**
     * 提现门槛
     */
    private Long withdrawalThreshold = Long.MAX_VALUE;


    /**
     * 用户的会员卡信息
     */
    private MemberAggregationInfoVO vipCard;


    /**
     * 返利真实比例
     *
     * @return 返利真实比例
     */
    public BigDecimal rebateRate() {
        return AmountCalculateHelper.getDiscountRate(rebate == null ? 0 : rebate, CommonPool.UNIT_CONVERSION_TEN_THOUSAND);
    }

    /**
     * 返利支付真实比例
     *
     * @return 返利支付真实比例
     */
    public BigDecimal payRate() {
        return AmountCalculateHelper.getDiscountRate(pay == null ? 0 : pay, CommonPool.UNIT_CONVERSION_TEN_THOUSAND);
    }


    /**
     * 是否没有返利
     *
     * @return true:没有返利 false:有返利
     */
    public boolean noRebate() {
        return rebate == null || rebate <= 0;
    }

    /**
     * 是否不能返利支付
     *
     * @return true:不能返利支付 false:能返利支付
     */
    public boolean noPay() {
        return pay == null || pay <= 0;
    }

}
