package com.medusa.gruul.storage.service.service;

import com.medusa.gruul.storage.service.model.bo.UpdateStockOrder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/8/3
 */
public interface SkuStockDetailService {

    /**
     * 批量生成库存明细
     *
     * @param generateDetailsOrders 生成库存明细的订单
     */
    void generateStockDetails(List<UpdateStockOrder> generateDetailsOrders);

    /**
     * 商品审核通过生成明细
     *
     * @param productMap 商品信息
     */
    void generateStorageDetail(Map<Long, Set<Long>> productMap);

}
