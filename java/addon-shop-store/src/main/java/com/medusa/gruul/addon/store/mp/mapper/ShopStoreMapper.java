package com.medusa.gruul.addon.store.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.store.model.param.ShopStoreParam;
import com.medusa.gruul.addon.store.model.vo.ShopStoreAssistantVO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreListVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStore;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description 店铺门店数据层
 * @date 2023-03-07 17:54
 */
public interface ShopStoreMapper extends BaseMapper<ShopStore> {
    /**
     * 根据param 查询店铺门店列表
     *
     * @param shopStoreParam 查询条件
     * @return 分页数据
     */
    IPage<ShopStoreListVO> queryShopStoreListVO(@Param("shopStoreParam") ShopStoreParam shopStoreParam);

    /**
     *  根据店铺id 及 门店id 获取店铺门店详情
     *
     * @param id   门店id
     * @param shopId  店铺id
     * @return 店铺门店详情
     */
    ShopStoreAssistantVO queryShopStoreInfo(@Param("id") Long id, @Param("shopId") Long shopId);
}
