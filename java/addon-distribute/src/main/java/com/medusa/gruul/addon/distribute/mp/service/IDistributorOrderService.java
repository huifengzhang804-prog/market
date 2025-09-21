package com.medusa.gruul.addon.distribute.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.distribute.model.ShopOrderKey;
import com.medusa.gruul.addon.distribute.model.dto.DistributorOrderQueryDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorMainOrderVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderBonusStatisticVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderVO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributorOrder;

import java.util.List;
import java.util.Set;

/**
 * 分销订单关联表 服务类
 *
 * @author 张治保
 * @since 2022-11-17
 */
public interface IDistributorOrderService extends IService<DistributorOrder> {


	/**
	 * 根据用户id 分页查询所有客户的分销订单
	 *
	 * @param userId 用户id
	 * @param query  查询条件
	 * @return 查询结果
	 */
	IPage<DistributorOrder> distributorOrderPageByUserId(Long userId, DistributorOrderQueryDTO query);

	/**
	 * 分页查询分销订单
	 *
	 * @param query 查询条件
	 * @return 查询结果
	 */
	IPage<DistributorMainOrderVO> distributorOrderPage(DistributorOrderQueryDTO query);

	/**
	 * 根据订单key查询订单列表
	 *
	 * @param orderKeys 订单key
	 * @param query     查询条件
	 * @return 订单列表
	 */
	List<DistributorOrderVO> getOrdersByKey(Set<ShopOrderKey> orderKeys, DistributorOrderQueryDTO query);

	/**
	 * 查询订单统计
	 *
	 * @param orderKeys 订单key
	 * @param query     查询条件
	 * @return 订单统计结果
	 */
	DistributorOrderBonusStatisticVO getStatistic(Set<ShopOrderKey> orderKeys, DistributorOrderQueryDTO query);
}
