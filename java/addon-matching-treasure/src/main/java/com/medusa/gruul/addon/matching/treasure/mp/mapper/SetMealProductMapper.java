package com.medusa.gruul.addon.matching.treasure.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.matching.treasure.model.dto.SetMealProductStats;
import com.medusa.gruul.addon.matching.treasure.mp.entity.SetMealProduct;
import com.medusa.gruul.goods.api.model.vo.SetMealBasicInfoVO;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;

/**
 *
 * 套餐商品 Mapper 接口
 * 
 *
 * @author WuDi
 * @since 2023-02-27
 */
public interface SetMealProductMapper extends BaseMapper<SetMealProduct> {

    /**
     * 功能描述
     *
     * @param setMealIds 套餐id集合
     * @return 商品详情套餐基本信息
     */
    List<SetMealBasicInfoVO> getSetMealBasicInfo(@Param("setMealIds") Set<Long> setMealIds);
    /**
     * 获取套餐商品
     * @param setMealId 套餐id
     * @return 套餐商品
     */
    List<SetMealProduct> getSetMealProduct(@Param("setMealId") Long setMealId);

    /**
     * 获取正在进行的套餐商品
     *
     * @param setMealProducts 套装参数（shopId、productId）
     * @return 套餐商品
     */
    List<SetMealProduct> getCurrentSetMealProduct(@Param("setMealProducts") Set<SetMealProduct> setMealProducts);

    /**
     * 获取套餐商品数量
     * @param setMealIds 套餐id集合
     * @return 套餐商品数量
     */
    List<SetMealProductStats> querySetMealProductCount(@Param("setMealIds") Set<Long> setMealIds);


}
