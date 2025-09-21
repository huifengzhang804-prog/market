package com.medusa.gruul.addon.bargain.model;

import lombok.*;

import java.io.Serializable;
import java.util.Set;

/**
 * @author 张治保
 * @since 2024/5/10
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BargainKey implements Serializable {
    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 活动产品ids
     */
    private Set<Long> productIds;
}
