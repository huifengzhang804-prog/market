package com.medusa.gruul.addon.distribute.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.distribute.model.ShopOrderKey;
import com.medusa.gruul.addon.distribute.model.dto.DistributorOrderQueryDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorMainOrderVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderBonusStatisticVO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorOrderVO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributorOrder;
import com.medusa.gruul.addon.distribute.mp.mapper.DistributorOrderMapper;
import com.medusa.gruul.addon.distribute.mp.service.IDistributorOrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 分销订单关联表 服务实现类
 *
 * @author 张治保
 * @since 2022-11-17
 */
@Service
public class DistributorOrderServiceImpl extends ServiceImpl<DistributorOrderMapper, DistributorOrder> implements IDistributorOrderService {

	@Override
	public IPage<DistributorOrder> distributorOrderPageByUserId(Long userId, DistributorOrderQueryDTO query) {
		return baseMapper.distributorOrderPageByUserId(query, userId);
	}

	@Override
	public IPage<DistributorMainOrderVO> distributorOrderPage(DistributorOrderQueryDTO query) {
		return baseMapper.distributorOrderPage(query);
	}

	@Override
	public List<DistributorOrderVO> getOrdersByKey(Set<ShopOrderKey> orderKeys, DistributorOrderQueryDTO query) {
		return baseMapper.getOrdersByKey(orderKeys, query);
	}

	@Override
	public DistributorOrderBonusStatisticVO getStatistic(Set<ShopOrderKey> orderKeys, DistributorOrderQueryDTO query) {
		return baseMapper.getStatistic(orderKeys, query);
	}
}
