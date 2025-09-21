package com.medusa.gruul.goods.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 店铺关注
 *
 * @author WuDi
 * @since 2022-08-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_shop_follow")
@Accessors(chain = true)
public class ShopFollow extends BaseEntity {

    private static final long serialVersionUID = 1L;

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
    private String shopLogo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 业务字段 当前用户是否关注 当isFollow>0 时 表示已关注
     */
    @TableField(exist = false)
    private Long isFollow;

}
