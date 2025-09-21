package com.medusa.gruul.addon.distribute.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.distribute.model.dto.ProductQueryDTO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.common.model.base.ShopProductKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 分销商品关联 Mapper 接口
 *
 * @author 张治保
 * @since 2022-11-14
 */
public interface DistributeProductMapper extends BaseMapper<DistributeProduct> {


	/**
	 * 分销商品分页查询
	 *
	 * @param query 分页查询查询参数
	 * @return 分页查询结果
	 */
	IPage<DistributeProduct> productPage(@Param("query") ProductQueryDTO query);


	/**
	 * 查询属于分销商品的 商品配置
	 *
	 * @param shopProductKeys 店铺id 商品id
	 * @return 店铺分销商品与分佣配置
	 */
	List<DistributeProduct> getShoppProductConfigs(@Param("shopProductKeys") Set<ShopProductKey> shopProductKeys);


}
