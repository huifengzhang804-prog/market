package com.medusa.gruul.storage.service.service.rpc;

import com.medusa.gruul.storage.api.bo.OrderStockBO;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.api.rpc.StorageOrderRpcService;
import com.medusa.gruul.storage.service.model.bo.UpdateStockOrder;
import com.medusa.gruul.storage.service.service.SkuStockService;
import com.medusa.gruul.storage.service.util.StorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 张治保
 * date 2022/7/16
 */
@Service
@DubboService
@RequiredArgsConstructor
@Slf4j
public class StorageOrderRpcServiceImpl implements StorageOrderRpcService {

    private final SkuStockService skuStockService;

    @Override
    public void reduceSkuStock(OrderStockBO skuStock) {
        // 渲染库存扣减数据
        //代销商品扣除供应商商品库存
        //如果是活动商品 扣除活动商品库存 且扣除普通商品库存
        //如果是普通商品 扣除普通商品库存
        StorageUtil.orderNoCheck(
                skuStock.getNo(),
                () -> skuStockService.updateStock(
                        false,
                        true,
                        List.of(
                                new UpdateStockOrder()
                                        .setGenerateDetail(true)
                                        .setOrderNo(skuStock.getNo())
                                        .setChangeType(StockChangeType.SOLD_OUTBOUND)
                                        .setStSvMap(skuStock.getSkuKeyStSvMap())
                        )
                ),
                () -> {
                }
        );

    }

}
