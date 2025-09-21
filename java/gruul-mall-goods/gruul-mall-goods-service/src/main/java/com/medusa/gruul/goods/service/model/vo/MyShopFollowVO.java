package com.medusa.gruul.goods.service.model.vo;


import com.medusa.gruul.goods.api.model.vo.ApiProductPopularAttentionVO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author WuDi
 */
@Data
@Accessors(chain = true)
public class MyShopFollowVO {

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 店铺名称
     */
    private String shopName;

    /**
     * 店铺logo
     */
    private String logo;

    /**
     * 关注人数
     */
    private Integer numberFollowers;

    /**
     * 是否有上新
     */
    private Boolean newProducts = Boolean.FALSE;

    /**
     * 关注店铺的商品 4个
     */
    private List<ApiProductPopularAttentionVO> popularAttentionVOList;

}
