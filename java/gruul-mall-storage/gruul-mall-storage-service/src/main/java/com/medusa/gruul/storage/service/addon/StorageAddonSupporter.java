package com.medusa.gruul.storage.service.addon;

import com.medusa.gruul.common.addon.supporter.annotation.AddonMethod;
import com.medusa.gruul.common.addon.supporter.annotation.AddonSupporter;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.storage.api.bo.SupplierOrderBO;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaoq
 * @Description StorageAddonSupporter.java
 * @date 2023-08-02 14:02
 */
@AddonSupporter(id = "storageAddonSupporter")
public interface StorageAddonSupporter {

    /**
     * 供应商商品批量查询
     *
     * @param shopProductKeys key
     * @return Map<ShopProductKey, Product>
     */
    @AddonMethod(returnType = Map.class)
    Map<ShopProductKey, Product> getSupplierProductBatch(Set<ShopProductKey> shopProductKeys);

    /**
     * 根据主单号或订单号查询订单
     *
     * @param mainNos 主单号
     * @param orderNo 订单号
     * @return 订单集合
     */
    @AddonMethod(returnType = List.class)
    List<SupplierOrderBO> getOrderByMainNo(String orderNo, Set<String> mainNos);
}
