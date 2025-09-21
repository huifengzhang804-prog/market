package com.medusa.gruul.addon.distribute.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.distribute.model.dto.ProductQueryDTO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.common.model.base.ShopProductKey;

import java.util.List;
import java.util.Set;

/**
 * 分销商品关联 服务类
 *
 * @author 张治保
 * @since 2022-11-14
 */
public interface IDistributeProductService extends IService<DistributeProduct> {

	/**
	 * 分销商品分页查询
	 *
	 * @param query 查询参数
	 * @return 分页查询结果
	 */
	IPage<DistributeProduct> productPage(ProductQueryDTO query);

	/**
	 * 查询属于分销商品的 商品配置
	 *
	 * @param shopProductKeys 店铺id 商品id
	 * @return 店铺分销商品与分佣配置
	 */
	List<DistributeProduct> getShoppProductConfigs(Set<ShopProductKey> shopProductKeys);
}
