package com.medusa.gruul.common.model.activity;

import com.medusa.gruul.common.model.base.StackableDiscount;
import com.medusa.gruul.global.model.o.RangeDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 活动时间范围 与 叠加优惠
 *
 * @author 张治保
 * @since 2024/5/31
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SimpleActivity {

    /**
     * 活动时间范围
     */
    private RangeDateTime range;

    /**
     * 活动叠加优惠 与支付超时时间
     */
    private StackableDiscount stackable;
}