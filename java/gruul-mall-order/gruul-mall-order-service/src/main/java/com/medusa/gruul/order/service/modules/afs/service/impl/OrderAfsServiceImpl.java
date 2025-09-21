package com.medusa.gruul.order.service.modules.afs.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.afs.api.model.AfsCloseDTO;
import com.medusa.gruul.afs.api.rpc.AfsRpcService;
import com.medusa.gruul.common.module.app.afs.AfsStatus;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.enums.ItemStatus;
import com.medusa.gruul.order.service.modules.afs.service.OrderAfsService;
import com.medusa.gruul.order.service.mp.service.IShopOrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 张治保
 * date 2022/8/6
 */
@Service
@RequiredArgsConstructor
public class OrderAfsServiceImpl implements OrderAfsService {

    private final IShopOrderItemService shopOrderItemService;
    private AfsRpcService afsRpcService;

    @Lazy
    @Autowired
    public void setAfsRpcService(AfsRpcService afsRpcService) {
        this.afsRpcService = afsRpcService;
    }

    @Override
    public boolean updateShopOrderItemAfsStatus(ShopOrderItem completeItem, String afsNo, AfsStatus targetStatus) {
        AfsStatus currentStatus = completeItem.getAfsStatus();
        boolean currentHaveNoAfsStatus = currentStatus == null;
        return ISystem.shopId(
                completeItem.getShopId(),
                () -> shopOrderItemService.lambdaUpdate()
                        .set(ShopOrderItem::getAfsStatus, targetStatus)
                        .set(ShopOrderItem::getAfsNo, afsNo)
                        .set(targetStatus.isRefunded(), ShopOrderItem::getStatus, ItemStatus.CLOSED)
                        .set(ShopOrderItem::getUpdateTime, LocalDateTime.now())
                        .eq(ShopOrderItem::getOrderNo, completeItem.getOrderNo())
                        .eq(ShopOrderItem::getId, completeItem.getId())
                        .isNull(currentHaveNoAfsStatus, ShopOrderItem::getAfsStatus)
                        .eq(!currentHaveNoAfsStatus, ShopOrderItem::getAfsStatus, currentStatus)
                        .update()
        );
    }

    @Override
    public void closeAfsIfClosable(boolean isSystem, List<ShopOrderItem> shopOrderItems, String reason) {
        if (CollUtil.isEmpty(shopOrderItems)) {
            return;
        }
        List<AfsCloseDTO> afsCloses = shopOrderItems.stream()
                .filter(shopOrderItem -> {
                    AfsStatus afsStatus = shopOrderItem.getAfsStatus();
                    return StrUtil.isNotBlank(shopOrderItem.getAfsNo())
                            && afsStatus.isCanClose()
                            && !afsStatus.isCanReRequest();
                })
                .map(
                        shopOrderItem -> new AfsCloseDTO()
                                .setAfsNo(shopOrderItem.getAfsNo())
                                .setIsSystem(isSystem)
                                .setCurrentStatus(shopOrderItem.getAfsStatus())
                                .setUpdateShopOrderItem(Boolean.FALSE)
                                .setReason(reason)
                ).toList();
        if (CollUtil.isEmpty(afsCloses)) {
            return;
        }
        afsRpcService.closeAfsBatch(afsCloses);
    }

}
