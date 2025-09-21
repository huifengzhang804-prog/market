package com.medusa.gruul.addon.distribute.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 店铺信息
 *
 * @author 张治保
 * @since 2022-11-16
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_distribute_shop")
public class DistributeShop extends BaseEntity {

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
