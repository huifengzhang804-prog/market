package com.medusa.gruul.addon.matching.treasure.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.goods.api.model.vo.SetMealBasicInfoVO;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMealProduct;
import com.medusa.gruul.goods.api.model.dto.ProductBroadcastDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * 套餐商品 服务类
 * 
 *
 * @author WuDi
 * @since 2023-02-27
 */
public interface ISetMealProductService extends IService<SetMealProduct> {

    /**
     * 商品更新
     * @param productUpdate 商品更新
     */
    void productUpdate(ProductBroadcastDTO productUpdate);
    /**
     * 功能描述
     * @param  setMealIds   套餐id集合
     * @return 商品详情套餐基本信息
     */
    List<SetMealBasicInfoVO> getSetMealBasicInfo(Set<Long> setMealIds);

    /**
     * 获取套餐商品
     * @param setMealId 套餐id
     * @return 套餐商品
     */
    List<SetMealProduct> getSetMealProduct(Long setMealId);

    /**
     * 获取正在进行的套餐商品
     * @param setMealProducts 套装参数（shopId、productId）
     * @return 套餐商品
     */
    List<SetMealProduct> getCurrentSetMealProduct(Set<SetMealProduct> setMealProducts);

    /**
     * 查询指定活动参与的商品数量
     * @param setMealIds
     * @return
     */
    Map<Long,Integer> querySetMealProductCount(Set<Long> setMealIds);


}
