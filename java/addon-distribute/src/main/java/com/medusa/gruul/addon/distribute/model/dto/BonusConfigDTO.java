package com.medusa.gruul.addon.distribute.model.dto;

import cn.hutool.core.util.ArrayUtil;
import com.medusa.gruul.addon.distribute.model.enums.Level;
import com.medusa.gruul.addon.distribute.model.enums.ShareType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.web.valid.annotation.Price;
import com.medusa.gruul.global.model.exception.GlobalException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Comparator;

/**
 * @author 张治保
 * date 2022/11/16
 */
@Getter
@Setter
@ToString
public class BonusConfigDTO {


    /**
     * 比较器
     */
    private static final Comparator<Long> BONUS_SHARE_COMPARATOR = (o1, o2) -> o2 == null ? 0 : o1 >= o2 ? -1 : 1;

    /**
     * 比例上下限
     */
    private static final Long RATE_LOWER_LIMIT = Long.valueOf(CommonPool.NUMBER_ZERO);
    private static final Long RATE_UPPER_LIMIT = CommonPool.UNIT_CONVERSION_TEN_THOUSAND * CommonPool.UNIT_CONVERSION_HUNDRED;

    /**
     * 固定金额 上下限
     */
    private static final Long FIXED_AMOUNT_LOWER_LIMIT = 0L;
    private static final Long FIXED_AMOUNT_UPPER_LIMIT = 9000 * CommonPool.UNIT_CONVERSION_TEN_THOUSAND;


    /**
     * 佣金类型 佣金类型 1.统一设置 2.固定金额 3.百分比
     */
    @NotNull
    private ShareType shareType;

    /**
     * 一级分佣
     */
    @Price(message = "佣金百分比要为正整数")
    private Long one;

    /**
     * 二级分佣
     */
    @Price(message = "佣金百分比要为正整数")
    private Long two;

    /**
     * 三级分佣
     */
    @Price(message = "佣金百分比要为正整数")
    private Long three;


    public void validParam(Level currentLevel) {
        if (!paramValidPass(currentLevel)) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR);
        }
    }

    private boolean paramValidPass(Level currentLevel) {
        Long l1 = getOne();
        Long l2 = getTwo();
        Long l3 = getThree();
        if (!ArrayUtil.isSorted(new Long[]{l1, l2, l3}, BONUS_SHARE_COMPARATOR)) {
            return Boolean.FALSE;
        }
        ShareType currentShareType = getShareType();
        //检查分销层级对应的
        switch (currentLevel) {
            case ONE -> {
                if (l1 == null || l2 != null || l3 != null) {
                    return Boolean.FALSE;
                }
                return validShareType(currentShareType, l1);
            }
            case TWO -> {
                if (l2 == null || l3 != null) {
                    return Boolean.FALSE;
                }
                return validShareType(currentShareType, l1, l2);
            }
            case THREE -> {
                if (l2 == null || l3 == null) {
                    return Boolean.FALSE;
                }
                return validShareType(currentShareType, l1, l2, l3);
            }
            default -> {
            }
        }
        return Boolean.TRUE;
    }

    private boolean validShareType(ShareType shareType, Long... values) {
        for (Long value : values) {
            boolean errorParam = (ShareType.RATE == shareType && !isLegalRate(value)) || !isLegalFixedAmount(value);
            if (errorParam) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    private boolean isLegalRate(Long rate) {
        return rate >= RATE_LOWER_LIMIT && rate <= RATE_UPPER_LIMIT;
    }

    private boolean isLegalFixedAmount(Long amount) {
        return amount >= FIXED_AMOUNT_LOWER_LIMIT && amount <= FIXED_AMOUNT_UPPER_LIMIT;
    }
}
