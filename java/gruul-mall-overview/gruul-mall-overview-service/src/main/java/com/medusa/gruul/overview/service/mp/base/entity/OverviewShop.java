package com.medusa.gruul.overview.service.mp.base.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 店铺信息
 * </p>
 *
 * @author WuDi
 * @since 2022-11-23
 */
@Getter
@Setter
@ToString(callSuper = true)
@Accessors(chain = true)
@TableName("t_overview_shop")
public class OverviewShop extends BaseEntity {

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


}
