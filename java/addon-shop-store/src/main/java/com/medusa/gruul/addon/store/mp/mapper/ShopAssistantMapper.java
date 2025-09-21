package com.medusa.gruul.addon.store.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.store.model.vo.ShopAssistantVO;
import com.medusa.gruul.addon.store.mp.entity.ShopAssistant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-03-14 14:56
 */
public interface ShopAssistantMapper extends BaseMapper<ShopAssistant> {
    /**
     *  查询店铺店员相关信息
     *
     * @param shopId 店铺id
     * @return 店员VO
     */
    List<ShopAssistantVO> queryShopAssistantList(@Param("shopId") Long shopId);
}
