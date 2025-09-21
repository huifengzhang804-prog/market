package com.medusa.gruul.addon.supplier.addon.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.medusa.gruul.addon.supplier.addon.AddonSupplierProvider;
import com.medusa.gruul.addon.supplier.model.SupplierConst;
import com.medusa.gruul.addon.supplier.modules.goods.service.EditSupplierGoodsService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoodsPublish;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierGoodsPublishService;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierGoodsService;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderService;
import com.medusa.gruul.addon.supplier.utils.DateUtils;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductDTO;
import com.medusa.gruul.goods.api.model.dto.ProductStatusChangeDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.storage.api.bo.SupplierOrderBO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 插件供应者实现类
 *
 * @author xiaoq
 * @Description AddonSupplierProviderImpl.java
 * @date 2023-07-17 09:18
 */
@Slf4j
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class AddonSupplierProviderImpl implements AddonSupplierProvider {

    private final EditSupplierGoodsService editSupplierGoodsService;
    private final ISupplierGoodsPublishService supplierGoodsPublishService;
    private final ISupplierGoodsService supplierGoodsService;
    private final ISupplierOrderService supplierOrderService;

    /**
     * 采购商品发布成功
     *
     * @param productId  商品id
     * @param shopId     店铺id
     * @param supplierId 供应商id
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "purchaseProductIssue")
    public void purchaseProductIssue(Long productId, Long shopId, Long supplierId) {
        //更新商品发布状态
        boolean exists = supplierGoodsPublishService.lambdaQuery()
                .eq(SupplierGoodsPublish::getSupplierId, supplierId)
                .eq(SupplierGoodsPublish::getShopId, shopId)
                .eq(SupplierGoodsPublish::getProductId, productId)
                .exists();
        if (exists) {
            supplierGoodsPublishService.lambdaUpdate()
                    .set(SupplierGoodsPublish::getPublished, Boolean.TRUE)
                    .eq(SupplierGoodsPublish::getSupplierId, supplierId)
                    .eq(SupplierGoodsPublish::getShopId, shopId)
                    .eq(SupplierGoodsPublish::getProductId, productId)
                    .update();
            return;
        }
        supplierGoodsPublishService.save(new SupplierGoodsPublish()
                .setProductId(productId)
                .setCreateTime(LocalDateTime.now())
                .setShopId(shopId)
                .setSupplierId(supplierId)
                .setPublished(Boolean.TRUE)
        );

    }

    /**
     * 同步供应商商品的状态
     *
     * @param param  商品key及状态描述
     * @param status 目标状态
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "syncSupplierProduct")
    public void syncSupplierProduct(ProductStatusChangeDTO param, ProductStatus status) {
        editSupplierGoodsService.updateProductStatus(
                false,
                param,
                status
        );
    }

    @Override
    @AddonProvider(service = Services.GRUUL_MALL_STORAGE, supporterId = "storageAddonSupporter", methodName = "getSupplierProductBatch")
    public Map<ShopProductKey, Product> getSupplierProductBatch(Set<ShopProductKey> shopProductKeys) {
        if (CollUtil.isEmpty(shopProductKeys)) {
            return Collections.emptyMap();
        }
        // 批量查询商品
        return supplierGoodsService.getProductBatch(shopProductKeys);
    }

    @Override
    @AddonProvider(service = "gruul-mall-addon-platform", supporterId = "platformAddonSupporter", methodName = "getSupplierSigningCategoryProduct")
    public Boolean getSupplierSigningCategoryProduct(Set<Long> collect, Long shopId) {
        return supplierGoodsService.lambdaQuery()
                .eq(SupplierGoods::getShopId, shopId)
                .in(SupplierGoods::getPlatformCategoryParentId, collect)
                .exists();
    }

    /**
     * 获取所有供应商新增的商品
     *
     * @return 新增的商品数量
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "countProductsOfAllSupplier")
    public Map<String, Integer> countProductsOfAllSupplier() {
        Map<String, Integer> result = new HashMap<>();
        result.put(SupplierConst.ADDON_SUPPLIER_NEW_COUNT_PRODUCT_KEY, this.supplierGoodsService.countAllSupplierNewProduct());
        result.put(SupplierConst.ADDON_SUPPLIER_IRREGULARITY_PRODUCT_KEY, this.supplierGoodsService.countIrregularityProduct());
        return result;
    }

    /**
     * 获取时间范围内新增的供应商店铺数量.
     * 如果时间范围为空,则忽略时间范围获取所有.
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 新增的店铺数量
     */
    @Override
    public Integer getNewCreatedShopNumber(LocalDateTime beginTime, LocalDateTime endTime) {
        return null;
    }

    /**
     * 根据订单号获取订单详情
     *
     * @param orderNo 订单号
     * @return {@link  JSON}
     */
    @Override
    @AddonProvider(service = "gruul-mall-addon-invoice", supporterId = "invoiceAddonSupporter", methodName = "getPurchaseOrderById")
    public Map<String, String> getPurchaseOrderById(String orderNo) {
        return Optional.ofNullable(
                supplierOrderService.lambdaQuery()
                        .eq(SupplierOrder::getNo, orderNo)
                        .eq(SupplierOrder::getType, SellType.PURCHASE)
                        .one()
        ).map(supplierOrder -> {
            Map<String, String> result = new HashMap<>();
            String completedTime = DateUtils.convertLocalDateTime2String(supplierOrder.getTimeNodes().getDeliveryTime(), DateUtils.DATE_TIME_FORMATTER);
            result.put("completedTime", completedTime);
            result.put("billMoney", String.valueOf(supplierOrder.getPayAmount()));
            return result;
        }).orElse(new HashMap<>());
    }

    /**
     * 获取供应商商品
     *
     * @param shopProductKeys 供应商id + 商品id
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_GOODS, supporterId = "goodsAddonSupporter", methodName = "getSupplierGoods")
    public List<ProductDTO> getSupplierGoods(Set<ShopProductKey> shopProductKeys) {
        List<SupplierGoods> supplierGoods = supplierGoodsService.getSupplierGoods(shopProductKeys);

        if (CollUtil.isEmpty(supplierGoods)) {
            return new ArrayList<>();
        }
        return supplierGoods.stream()
                .map(this::productToProductDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据主单号查询订单
     *
     * @param mainNos 主单号
     * @param orderNo 订单号
     * @return 订单集合
     */
    @Override
    @AddonProvider(service = Services.GRUUL_MALL_STORAGE, supporterId = "storageAddonSupporter", methodName = "getOrderByMainNo")
    public List<SupplierOrderBO> getOrderByMainNo(String orderNo, Set<String> mainNos) {
        List<SupplierOrder> supplierOrderList = supplierOrderService.getOrderByMainNo(orderNo, mainNos);
        return BeanUtil.copyToList(supplierOrderList, SupplierOrderBO.class);
    }

    @Override
    @AddonProvider(service = "gruul-mall-addon-invoice", supporterId = "invoiceAddonSupporter", methodName = "syncSupplyOrderInvoiceStatus")
    public void syncSupplyOrderInvoiceStatus(String orderNo, Integer invoiceStatusCode) {
        supplierOrderService.syncOrderInvoiceStatus(orderNo, invoiceStatusCode);
    }

    private ProductDTO productToProductDTO(SupplierGoods supplierGoods) {
        ProductDTO productDTO = new ProductDTO();
        BeanUtil.copyProperties(supplierGoods.getProduct(), productDTO);
        productDTO.setId(supplierGoods.getId());
        productDTO.setSupplierId(supplierGoods.getShopId());
        productDTO.setProductAttributes(supplierGoods.getProduct().getExtra().getProductAttributes());
        productDTO.setProductParameters(supplierGoods.getProduct().getExtra().getProductParameters());
        productDTO.setSupplierCustomDeductionRatio(supplierGoods.getProduct().getExtra().getCustomDeductionRatio());
        productDTO.setPlatformCategory(supplierGoods.getProduct().getExtra().getPlatformCategory());
        return productDTO;
    }
}
