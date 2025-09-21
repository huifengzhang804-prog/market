package com.medusa.gruul.search.api.model;

import cn.hutool.json.JSON;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 优惠券VO
 *
 * @author wufuzhong
 * @Date 2023-12-07
 */
@Data
@Accessors(chain = true)
public class SearchCouponVO implements Serializable {

    private static final long serialVersionUID = -7342560037118381475L;

    /**
     * 优惠券主键
     */
    private Long id;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 优惠券指定商品
     */
    private List<SearchCouponProductVO> searchCouponProductVOList;

    /**
     * 优惠券排除商品
     */
    private List<SearchCouponProductVO> excludeCouponProductVOList;

    /**
     * 优惠信息描述
     */
    private String sourceDesc;

    /**
     * 同一个店铺
     * 按照 无门槛现金券、无门槛折扣券、满减券、满折券 排序
     */
    private Integer rowNum;
    /**
     * 优惠券的信息
     */
    private JSON data;
}
