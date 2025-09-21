package com.medusa.gruul.addon.supplier.addon;

import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductDTO;
import com.medusa.gruul.goods.api.model.dto.ProductStatusChangeDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.storage.api.bo.SupplierOrderBO;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 供应商插件供应者
 *
 * @author xiaoq
 * @Description SupplierProvider
 * @date 2023-07-17 09:17
 */
public interface AddonSupplierProvider {

    /**
     * 采购商品发布
     *
     * @param productId  商品id
     * @param shopId     店铺id
     * @param supplierId 供应商id
     */
    void purchaseProductIssue(@NotNull Long productId, @NotNull Long shopId, @NotNull Long supplierId);

    /**
     * 同步供应商商品的状态
     * 目前用于 同步平台下架 和恢复销售
     *
     * @param param  商品key及状态描述
     * @param status 目标状态
     */
    void syncSupplierProduct(ProductStatusChangeDTO param, ProductStatus status);

    /**
     * 批量获取商品 信息
     *
     * @param shopProductKeys shopId,productId
     * @return map<{ shopId, productId }, product>
     */
    Map<ShopProductKey, Product> getSupplierProductBatch(Set<ShopProductKey> shopProductKeys);


    /**
     * 获取供应商签约类目商品是否存在
     *
     * @param collect 平台耳机类目ids
     * @param shopId  供应商id
     * @return 是否存在
     */
    Boolean getSupplierSigningCategoryProduct(Set<Long> collect, Long shopId);

    /**
     * 获取所有供应商新增的商品&违规商品
     *
     * @return {@link Map}
     */
    Map<String, Integer> countProductsOfAllSupplier();

    /**
     * 获取时间范围内新增的供应商店铺数量.
     * 如果时间范围为空,则忽略时间范围获取所有.
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 新增的店铺数量
     */
    Integer getNewCreatedShopNumber(LocalDateTime beginTime, LocalDateTime endTime);

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return {@link  com.alibaba.fastjson2.JSON}
     */
    Map<String, String> getPurchaseOrderById(String orderNo);

    /**
     * 获取供应商商品
     *
     * @param shopProductKeys 供应商id + 商品id
     */
    List<ProductDTO> getSupplierGoods(Set<ShopProductKey> shopProductKeys);

    /**
     * 根据主单号或订单号查询订单
     *
     * @param mainNos 主单号
     * @param orderNo 订单号
     * @return 订单集合
     */
    List<SupplierOrderBO> getOrderByMainNo(String orderNo, Set<String> mainNos);

    /**
     * 同步供应商订单发票状态
     * @param orderNo
     * @param invoiceStatusCode
     */
    void syncSupplyOrderInvoiceStatus(String orderNo,Integer invoiceStatusCode);
}
