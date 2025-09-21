package com.medusa.gruul.addon.distribute.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.medusa.gruul.addon.distribute.model.enums.DistributeProductStatus;
import com.medusa.gruul.addon.distribute.model.enums.DistributionStatus;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author 张治保
 * date 2022/11/15
 */
@Getter
@Setter
@ToString
public class ProductQueryDTO extends PageDTO<DistributeProduct> {


	/**
	 * 店铺id
	 */
	private Long shopId;


	/**
	 * 店铺名称
	 */
	private String shopName;

	/**
	 * 商品名称
	 */
	private String productName;

	/**
	 * 商品状态
	 */
	private DistributeProductStatus status;

	/**
	 * 商品分销状态
	 */
	private DistributionStatus distributionStatus;

}
