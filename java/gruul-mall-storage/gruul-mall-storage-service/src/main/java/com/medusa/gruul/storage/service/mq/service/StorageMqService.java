package com.medusa.gruul.storage.service.mq.service;

import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;

import java.util.Map;

/**
 * @author 张治保
 * date 2023/8/3
 */
public interface StorageMqService {

    /**
     * 发送更新库存的mq
     *
     * @param skuKeyStSvs 扣减库存参数 列表
     */
    void sendUpdateStockMsg(Map<ActivityShopProductSkuKey, StSvBo> skuKeyStSvs);

    /**
     * 发送更新价格mq
     *
     * @param priceUpdate 价格更新数据
     */
    void sendUpdateSkuPriceMsg(ProductPriceUpdateDTO priceUpdate);

}
