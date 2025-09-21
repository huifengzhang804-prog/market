package com.medusa.gruul.addon.distribute.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.distribute.model.dto.ProductQueryDTO;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.addon.distribute.mp.mapper.DistributeProductMapper;
import com.medusa.gruul.addon.distribute.mp.service.IDistributeProductService;
import com.medusa.gruul.common.model.base.ShopProductKey;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 分销商品关联 服务实现类
 *
 * @author 张治保
 * @since 2022-11-14
 */
@Service
public class DistributeProductServiceImpl extends ServiceImpl<DistributeProductMapper, DistributeProduct> implements IDistributeProductService {


	@Override
	public IPage<DistributeProduct> productPage(ProductQueryDTO query) {
		return baseMapper.productPage(query);
	}

	@Override
	public List<DistributeProduct> getShoppProductConfigs(Set<ShopProductKey> shopProductKeys) {
		return baseMapper.getShoppProductConfigs(shopProductKeys);
	}
}
