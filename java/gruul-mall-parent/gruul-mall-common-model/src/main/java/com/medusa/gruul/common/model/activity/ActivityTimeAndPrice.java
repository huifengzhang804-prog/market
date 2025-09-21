package com.medusa.gruul.common.model.activity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 张治保
 * @since 2024/5/31
 */
@Getter
@Setter
@Accessors(chain = true)
public class ActivityTimeAndPrice implements Serializable {

    /**
     * 活动时间范围 与 叠加优惠
     */
    private SimpleActivity activity;

    /**
     * sku id 及对应价格
     */
    private Map<Long, Long> price;
}
