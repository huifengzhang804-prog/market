package com.medusa.gruul.addon.store.service;

import com.medusa.gruul.addon.store.model.dto.StoreOrderStockUpDTO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreOrderInfoVO;
import com.medusa.gruul.addon.store.model.vo.ShopStoreTransactionSummaryVO;

/**
 * 移动端店铺门店服务层
 *
 * @author xiaoq
 * @Description 移动端店铺门店服务层
 * @date 2023-03-16 17:55
 */
public interface MobileShopStoreOrderService {
    /**
     * 获取门店订单核销码 By OrderNo
     *
     * @param oderNo 订单号
     * @param storeId 门店id
     * @return 门店 核销码 信息
     */
    ShopStoreOrderInfoVO getStoreOrderCodeByOrderNo(Long storeId, String oderNo);

    /**
     * 门店订单开始备货
     *
     * @param storeOrderStockUp 门店订单备货DTO
     */
    void storeOrderProceedStockUp(StoreOrderStockUpDTO storeOrderStockUp);

    /**
     * 门店订单核销
     *
     * @param storeId 门店id
     * @param code 核销码
     */
    void storeOrderVerification(Long storeId, String code);


    /**
     * 获取店铺门店交易汇总
     *
     * @param storeId 门店id
     * @return 店铺门店交易汇总VO
     */
    ShopStoreTransactionSummaryVO getStoreTransactionSummary(Long storeId);
}
