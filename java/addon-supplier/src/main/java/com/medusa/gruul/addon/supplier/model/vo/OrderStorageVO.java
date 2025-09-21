package com.medusa.gruul.addon.supplier.model.vo;

import cn.hutool.core.collection.CollUtil;
import com.medusa.gruul.common.model.base.ProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/7/27
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class OrderStorageVO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 供应商 id
     */
    private Long supplierId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商品列表
     */
    private Set<OrderStorageProductVO> products;


    /**
     * 转换成 skukey 对应的出入库记录
     * 格式Map<ShopProductSkuKey,OrderStorageProductSkuVO>
     */
    public Map<ProductSkuKey, OrderStorageProductSkuVO> skuKeyStorageMap() {
        Map<ProductSkuKey, OrderStorageProductSkuVO> skuKeyStorageMap = new HashMap<>(products.size() * CommonPool.NUMBER_FOUR);
        Set<OrderStorageProductVO> currentProducts = getProducts();
        if (CollUtil.isEmpty(currentProducts)) {
            return skuKeyStorageMap;
        }
        for (OrderStorageProductVO currentProduct : currentProducts) {
            Set<OrderStorageProductSkuVO> currentSkus = currentProduct.getSkus();
            if (CollUtil.isEmpty(currentSkus)) {
                continue;
            }
            for (OrderStorageProductSkuVO skus : currentSkus) {
                skus.setProductName(currentProduct.getProductName());
                skuKeyStorageMap.put(
                        new ProductSkuKey().setProductId(currentProduct.getProductId()).setSkuId(skus.getSkuId()),
                        skus
                );
            }
        }
        return skuKeyStorageMap;
    }

}
