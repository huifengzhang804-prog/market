package com.medusa.gruul.order.service.modules.afs.service;

import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.order.api.entity.ShopOrderItem;

import java.util.List;

/**
 * @author 张治保
 * date 2022/8/6
 */
public interface OrderAfsService {

    /**
     * 更新 店铺订单商品 售后状态
     *
     * @param completeItem 原订单商品信息
     * @param afsNo        当前售后工单号
     * @param targetStatus 目标售后状态
     * @return 是否更新成功
     */
    boolean updateShopOrderItemAfsStatus(ShopOrderItem completeItem, String afsNo, AfsStatus targetStatus);


    /**
     * 关闭商品订单项售后工单 如果可以关闭
     *
     * @param isSystem       是否是系統操作
     * @param shopOrderItems 商品订单项
     * @param reason         关闭原因
     */
    void closeAfsIfClosable(boolean isSystem, List<ShopOrderItem> shopOrderItems, String reason);

}
