package com.medusa.gruul.addon.store.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.store.model.vo.ShopStoreTransactionSummaryVO;
import com.medusa.gruul.addon.store.mp.entity.ShopStoreOrder;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2023-03-16 18:29
 */
public interface ShopStoreOrderMapper extends BaseMapper<ShopStoreOrder> {
    /**
     *  获取门店交易汇总
     *
     * @param storeId 门店id
     * @param shopId 店铺id
     * @return 门店交易汇总
     */
    ShopStoreTransactionSummaryVO queryStoreTransactionSummary(@Param("storeId") Long storeId, @Param("shopId") Long shopId);
}
