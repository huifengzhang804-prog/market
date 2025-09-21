package com.medusa.gruul.goods.api.model.param;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.goods.api.entity.ShopFollow;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 店铺查询参数
 * @author WuDi
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApiShopFollowParam extends Page<ShopFollow> {


    /**
     * 名称
     */
    private String shopName;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 是否关注
     */
    private Boolean isFollow;

    /**
     * 用戶id
     */
    private Long userId;

}
