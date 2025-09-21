package com.medusa.gruul.addon.distribute.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.distribute.model.ShopOrderKey;
import com.medusa.gruul.addon.distribute.model.dto.DistributorOrderQueryDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorMainOrderVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderBonusStatisticVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderVO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributorOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 分销订单关联表 Mapper 接口
 *
 * @author 张治保
 * @since 2022-11-17
 */
public interface DistributorOrderMapper extends BaseMapper<DistributorOrder> {

	/**
	 * 根据用户id 分页查询所有客户的分销订单
	 *
	 * @param userId 用户id
	 * @param query  查询条件
	 * @return 查询结果
	 */
	IPage<DistributorOrder> distributorOrderPageByUserId(@Param("query") DistributorOrderQueryDTO query, @Param("userId") Long userId);

	/**
	 * 分页查询分销订单 根据订单号店铺分组
	 *
	 * @param query 查询条件
	 * @return 查询结果
	 */
	IPage<DistributorMainOrderVO> distributorOrderPage(@Param("query") DistributorOrderQueryDTO query);


	/**
	 * 根据订单key查询订单列表
	 *
	 * @param orderKeys 订单key
	 * @param query     查询条件
	 * @return 订单列表
	 */
	List<DistributorOrderVO> getOrdersByKey(@Param("orderKeys") Set<ShopOrderKey> orderKeys, @Param("query") DistributorOrderQueryDTO query);

	/**
	 * 查询订单佣金统计
	 *
	 * @param orderKeys 订单key
	 * @param query     查询条件
	 * @return 订单统计结果
	 */
	DistributorOrderBonusStatisticVO getStatistic(@Param("orderKeys") Set<ShopOrderKey> orderKeys, @Param("query") DistributorOrderQueryDTO query);
}
