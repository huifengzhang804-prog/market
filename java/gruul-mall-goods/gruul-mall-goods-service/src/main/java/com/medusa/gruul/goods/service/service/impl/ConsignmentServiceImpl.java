package com.medusa.gruul.goods.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.goods.api.entity.ConsignmentSetting;
import com.medusa.gruul.goods.api.model.dto.ConsignmentPriceSettingDTO;
import com.medusa.gruul.goods.api.model.dto.PaveGoodsDTO;
import com.medusa.gruul.goods.api.model.dto.ProductDTO;
import com.medusa.gruul.goods.api.model.dto.SinglePaveGoodsDTO;
import com.medusa.gruul.goods.service.addon.GoodsAddonSupporter;
import com.medusa.gruul.goods.service.model.param.PurchaseProductParam;
import com.medusa.gruul.goods.service.model.vo.SupplierIssueProductListVO;
import com.medusa.gruul.goods.service.mp.service.IConsignmentSettingService;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.goods.service.service.ConsignmentService;
import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 代销设置
 *
 * @author miskw
 * @date 2023/8/8
 * @describe 描述
 */
@Service
@RequiredArgsConstructor
public class ConsignmentServiceImpl implements ConsignmentService {
    private final IConsignmentSettingService consignmentSettingService;
    private final IProductService productService;
    private final GoodsAddonSupporter goodsAddonSupporter;
    private final StorageRpcService storageRpcService;


    /**
     * 代销设置修改
     *
     * @param consignmentPriceSetting 代销设置参数
     */
    @Override
    public void consignmentConfig(Long shopId, ConsignmentPriceSettingDTO consignmentPriceSetting) {
        consignmentPriceSetting.validParam();
        ConsignmentSetting consignmentSetting = TenantShop.disable(
                () -> consignmentSettingService.lambdaQuery()
                        .select(ConsignmentSetting::getId, ConsignmentSetting::getShopId)
                        .eq(ConsignmentSetting::getShopId, shopId)
                        .oneOpt()
        ).orElseGet(() -> new ConsignmentSetting().setShopId(shopId));
        consignmentSetting.setSale(consignmentPriceSetting.getSale())
                .setScribe(consignmentPriceSetting.getScribe())
                .setType(consignmentPriceSetting.getType());
        TenantShop.disable(
                () -> {
                    if (consignmentSetting.getId() != null) {
                        SystemCode.DATA_UPDATE_FAILED.falseThrow(consignmentSettingService.updateById(consignmentSetting));
                    } else {
                        SystemCode.DATA_ADD_FAILED.falseThrow(consignmentSettingService.save(consignmentSetting));
                    }
                }
        );
    }

    @Override
    public ConsignmentSetting config(Long shopId) {
        return TenantShop.disable(
                () -> consignmentSettingService.lambdaQuery()
                        .select(ConsignmentSetting::getId, ConsignmentSetting::getShopId,
                                ConsignmentSetting::getType, ConsignmentSetting::getSale,
                                ConsignmentSetting::getScribe, ConsignmentSetting::getCreateTime)
                        .eq(ConsignmentSetting::getShopId, shopId)
                        .one());
    }

    @Override
    public void paveGoods(PaveGoodsDTO paveGoods, Long shopId) {
        List<ProductDTO> products = goodsAddonSupporter.getSupplierGoods(paveGoods.getShopProductKeys());
        SystemCode.DATA_NOT_EXIST.falseThrow(CollUtil.isNotEmpty(products));
        ConsignmentPriceSettingDTO consignmentPriceSetting = paveGoods.getConsignmentPriceSetting();
        consignmentPriceSetting.validParam();
        List<StorageSpecSkuDTO> storageSpecSku = storageRpcService.getStorageSpecSku(paveGoods.getShopProductKeys());
        SystemCode.DATA_NOT_EXIST.falseThrow(CollUtil.isNotEmpty(storageSpecSku));
        Map<Long, ProductDTO> productMap = products.stream().collect(Collectors.toMap(ProductDTO::getId, productDTO -> productDTO));

        storageSpecSku.forEach(storageSpecSkuDTO -> {
            ProductDTO productDTO = productMap.get(storageSpecSkuDTO.getProductId());
            storageSpecSkuDTO.setProductName(productDTO.getName());
            storageSpecSkuDTO.setSellType(productDTO.getSellType());
            productDTO.setConsignmentPriceSetting(consignmentPriceSetting)
                    .setSkus(storageSpecSkuDTO.getSkus())
                    .setShopCategory(paveGoods.getShopCategory())
                    .setSpecGroups(storageSpecSkuDTO.getSpecGroups());
            productService.issueProduct(productDTO);
        });

    }

    /**
     * 已铺货代销商品
     *
     * @param purchaseProductParam 查询参数
     */
    @Override
    public IPage<SupplierIssueProductListVO> getPaveGoods(PurchaseProductParam purchaseProductParam) {
        return productService.getPaveGoods(purchaseProductParam);
    }

    @Override
    public void consignmentProductUpdateStatus(Long productId) {
        productService.consignmentProductUpdateStatus(productId);
    }

    /**
     * 单个一键铺货
     *
     * @param singlePaveGoods 单个一键铺货参数
     */
    @Override
    public void singlePaveGoods(SinglePaveGoodsDTO singlePaveGoods) {
        Set<ShopProductKey> productKey = Set.of(singlePaveGoods.getShopProductKey());
        //查询供应商商品
        List<ProductDTO> supplierGoods = goodsAddonSupporter.getSupplierGoods(productKey);
        SystemCode.DATA_NOT_EXIST.trueThrow(CollUtil.isEmpty(supplierGoods));
        //校验参数
        singlePaveGoods.getConsignmentPriceSetting().validParam();
        //查询供应商 sku 数据
        List<StorageSpecSkuDTO> storageSpecSku = storageRpcService.getStorageSpecSku(productKey);
        SystemCode.DATA_NOT_EXIST.falseThrow(CollUtil.isNotEmpty(storageSpecSku));

        ProductDTO productDTO = supplierGoods.get(0);
        StorageSpecSkuDTO storageSpecSkuDTO = storageSpecSku.get(0);
        storageSpecSkuDTO.setProductName(productDTO.getName());
        storageSpecSkuDTO.setSellType(productDTO.getSellType());

        productDTO.setConsignmentPriceSetting(singlePaveGoods.getConsignmentPriceSetting())
                .setSkus(storageSpecSkuDTO.getSkus())
                .setName(singlePaveGoods.getName())
                .setSellType(SellType.CONSIGNMENT)
                .setSaleDescribe(singlePaveGoods.getSaleDescribe())
                .setShopCategory(singlePaveGoods.getShopCategory())
                .setSpecGroups(storageSpecSkuDTO.getSpecGroups());
        productService.issueProduct(productDTO);
    }


}
