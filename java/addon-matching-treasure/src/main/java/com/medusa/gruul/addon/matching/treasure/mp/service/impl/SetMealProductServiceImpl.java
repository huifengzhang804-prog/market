package com.medusa.gruul.addon.matching.treasure.mp.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealProductStats;
import com.medusa.gruul.goods.api.model.vo.SetMealBasicInfoVO;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMealProduct;
import com.medusa.gruul.addon.matching.treasure.mp.mapper.SetMealProductMapper;
import com.medusa.gruul.addon.matching.treasure.mp.service.ISetMealProductService;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductBroadcastDTO;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 套餐商品 服务实现类
 *
 * @author WuDi
 * @since 2023-02-27
 */
@Service
@RequiredArgsConstructor
public class SetMealProductServiceImpl extends ServiceImpl<SetMealProductMapper, SetMealProduct> implements ISetMealProductService {

    
    /**
     * 商品更新
     *
     * @param productUpdate 商品更新
     */
    @Override
    public void productUpdate(ProductBroadcastDTO productUpdate) {
        Product product = productUpdate.getProduct();
        Long productId = product.getId();
        Long shopId = product.getShopId();
        boolean exists = this.lambdaQuery()
                .eq(SetMealProduct::getProductId, productId)
                .eq(SetMealProduct::getShopId, shopId)
                .exists();
        if (!exists) {
            return;
        }
        this.lambdaUpdate()
                .set(SetMealProduct::getProductName, product.getName())
                .set(SetMealProduct::getProductPic, product.getPic())
                .eq(SetMealProduct::getProductId, productId)
                .eq(SetMealProduct::getShopId, shopId)
                .update();

    }
    /**
     * 功能描述
     * @param  setMealIds   套餐id集合
     * @return 商品详情套餐基本信息
     */
    @Override
    public List<SetMealBasicInfoVO> getSetMealBasicInfo(Set<Long> setMealIds) {
        return baseMapper.getSetMealBasicInfo(setMealIds);
    }
    /**
     * 获取套餐商品
     * @param setMealId 套餐id
     * @return 套餐商品
     */
    @Override
    public List<SetMealProduct> getSetMealProduct(Long setMealId) {
        return baseMapper.getSetMealProduct(setMealId);
    }

    /**
     * 获取正在进行的套餐商品
     *
     * @param setMealProducts 套装参数（shopId、productId）
     * @return 套餐商品
     */
    @Override
    public List<SetMealProduct> getCurrentSetMealProduct(Set<SetMealProduct> setMealProducts) {
        return baseMapper.getCurrentSetMealProduct(setMealProducts);
    }

    @Override
    public Map<Long, Integer> querySetMealProductCount(Set<Long> setMealIds) {
        List<SetMealProductStats> setMealProductStats = baseMapper.querySetMealProductCount(
            setMealIds);
        if (CollectionUtil.isEmpty(setMealProductStats)) {
            return Maps.newHashMap();
        }
        return setMealProductStats.stream().collect(Collectors.toMap(x->x.getSetMealId(),x->x.getProductCount()));

    }



}
