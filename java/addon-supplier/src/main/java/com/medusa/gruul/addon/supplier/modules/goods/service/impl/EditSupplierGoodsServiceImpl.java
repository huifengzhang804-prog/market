package com.medusa.gruul.addon.supplier.modules.goods.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.medusa.gruul.addon.supplier.addon.SupplierAddonSupporter;
import com.medusa.gruul.addon.supplier.model.dto.ProductStatusChangeDTO;
import com.medusa.gruul.addon.supplier.model.enums.SupplierError;
import com.medusa.gruul.addon.supplier.modules.goods.service.EditSupplierGoodsService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.addon.supplier.mp.mapper.SupplierGoodsMapper;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierGoodsService;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.dto.*;
import com.medusa.gruul.goods.api.model.enums.GoodsError;
import com.medusa.gruul.goods.api.model.enums.ProductAuditType;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.model.enums.ProductType;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.storage.api.dto.SkuDTO;
import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import io.vavr.Tuple;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 编辑供应商商品ServiceImpl
 *
 * @author xiaoq
 * @since 2023-07-17 13:20
 */
@Service
@RequiredArgsConstructor
public class EditSupplierGoodsServiceImpl implements EditSupplierGoodsService {

    private final SqlSessionFactory sqlSessionFactory;
    private final ISupplierGoodsService supplierGoodsService;
    private final StorageRpcService storageRpcService;
    private final SupplierAddonSupporter supplierAddonSupporter;
    private final Executor globalExecutor;
    private final RabbitTemplate rabbitTemplate;
    private final GoodsRpcService goodsRpcService;

    /**
     * 供应商商品新增
     *
     * @param supplierProduct 供应商商品信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product issueSupplierProduct(ProductDTO supplierProduct) {
        SupplierGoods supplierGoods = new SupplierGoods();
        Product product = new Product();
        Long shopId = ISecurity.userMust().getShopId();
        BeanUtils.copyProperties(supplierProduct, product);
        // 代销商品校验
        SupplierError.CONSIGNMENT_PRODUCT_CHECK_FAIL.trueThrow(supplierGoods.getSellType() == SellType.CONSIGNMENT && supplierGoods.getProductType() != ProductType.REAL_PRODUCT);

        List<SkuDTO> skus = supplierProduct.getSkus();
        // 渲染product商品基础信息
        List<Long> collect = skus.stream().map(SkuDTO::getSalePrice).sorted().collect(Collectors.toList());

        int zero = CommonPool.NUMBER_ZERO;
        product.setPic(product.getAlbumPics().split(StrPool.COMMA, zero)[zero]);
        product.setSalePrice(collect.get(zero));
        product.setSalePrices(collect);
        product.setPlatformCategoryId(Option.of(supplierProduct.getPlatformCategory()).map(CategoryLevel::getThree).getOrNull());
        product.setShopId(shopId);
        product.setDistributionMode(Collections.singletonList(DistributionMode.EXPRESS));
        setExtendInfo(product, supplierProduct.getPlatformCategory(), supplierProduct.getProductAttributes(), supplierProduct.getProductParameters());
        product.setStatus(ProductStatus.SELL_ON);
        supplierGoods.setSupplierProductStatus(ProductStatus.SELL_ON);
        ProductAuditType productAuditType = goodsRpcService.getProductAuditSetting();
        if (productAuditType == ProductAuditType.MANUALLY_AUDIT) {
            //人工审核，设置商品状态为审核中
            product.setStatus(ProductStatus.UNDER_REVIEW);
            supplierGoods.setSupplierProductStatus(ProductStatus.UNDER_REVIEW);
        } else {
            product.getExtra().setAuditTime(LocalDateTime.now());
        }
        product.getExtra().setSubmitTime(LocalDateTime.now());
        //总库存
        Long totalStock = null;
        long count = skus.stream().filter(skuDTO -> StockType.LIMITED.equals(skuDTO.getStockType()))
                .count();
        if (count > 0) {
            totalStock = skus.stream()
                    .filter(x -> StockType.LIMITED.equals(x.getStockType()))
                    .map(SkuDTO::getInitStock)
                    .reduce(0L, Long::sum);

        }

        supplierGoods.setProduct(product)
                .setSupplierGoodsName(product.getName())
                .setPlatformCategoryParentId(Option.of(supplierProduct.getPlatformCategory()).map(CategoryLevel::getTwo).getOrNull())
                .setPlatformCategoryId(Option.of(supplierProduct.getPlatformCategory()).map(CategoryLevel::getThree).getOrNull())
                .setSellType(supplierProduct.getSellType())
                .setShopId(shopId)
                .setTotalStock(totalStock)
                .setProductType(supplierProduct.getProductType());
        supplierGoodsService.save(supplierGoods);
        // 编辑(新增/修改) 商品sku信息
        StorageSpecSkuDTO storageSpecSkuDTO = new StorageSpecSkuDTO();
        skus.forEach(sku -> {
            if (StockType.UNLIMITED.equals(sku.getStockType())) {
                sku.setInitStock(0L);
            }
        });
//        skus.stream().filter(sku -> StrUtil.isBlank(sku.getImage())).forEach(sku -> sku.setImage(product.getPic()));
        storageSpecSkuDTO.setProductCurrentStatus(new JSONObject().set("status", supplierGoods.getSupplierProductStatus()));
        storageSpecSkuDTO.setProductName(supplierProduct.getName());
        storageSpecSkuDTO.setSellType(supplierGoods.getSellType());
        storageSpecSkuDTO.setProductId(supplierGoods.getId());
        storageSpecSkuDTO.setSkus(skus);
        storageSpecSkuDTO.setSpecGroups(supplierProduct.getSpecGroups());
        storageRpcService.saveOrUpdateSpecSku(storageSpecSkuDTO);
        return product;
    }

    /**
     * 删除供应商商品信息
     *
     * @param ids 供应商商品ids
     */
    @Override
    public void deleteSupplierProductList(Set<Long> ids) {
        Long shopId = ISecurity.userMust().getShopId();
        List<SupplierGoods> products = supplierGoodsService.lambdaQuery()
                .select(SupplierGoods::getSellType, SupplierGoods::getId, SupplierGoods::getShopId)
                .eq(SupplierGoods::getShopId, shopId)
                .in(SupplierGoods::getSupplierProductStatus, Arrays.asList(ProductStatus.SELL_OFF, ProductStatus.PLATFORM_SELL_OFF))
                .in(BaseEntity::getId, ids)
                .list();
        if (CollUtil.isEmpty(products)) {
            return;
        }
        Set<Long> productIds = products.stream().map(BaseEntity::getId).collect(Collectors.toSet());
        SupplierError.PRODUCT_CANNOT_BE_DELETED.trueThrow(productIds.size() != ids.size());
        supplierGoodsService.removeByIds(productIds);
        Set<ShopProductKey> productKeys = products.stream()
                .filter(goods -> SellType.CONSIGNMENT == goods.getSellType())
                .map(product -> new ShopProductKey().setShopId(shopId).setProductId(product.getId()))
                .collect(Collectors.toSet());
        if (CollUtil.isNotEmpty(productKeys)) {
            globalExecutor.execute(
                    () -> rabbitTemplate.convertAndSend(
                            GoodsRabbit.SUPPLIER_FORCE_GOODS_STATUS.exchange(),
                            GoodsRabbit.SUPPLIER_FORCE_GOODS_STATUS.routingKey(),
                            productKeys
                    )
            );
        }


    }

    /**
     * 修改供应商商品信息
     *
     * @param supplierProduct 供应商商品信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSupplierProduct(ProductDTO supplierProduct) {
        SupplierGoods supplierGoods = checkoutSupplierProductInfo(supplierProduct.getId());
        Product product = supplierGoods.getProduct();
        List<SkuDTO> skus = supplierProduct.getSkus();
        BeanUtils.copyProperties(supplierProduct, product);
        // 修改商品 不可修改销售类型 平台类目  商品类型
        product.setSellType(supplierGoods.getSellType());
        product.setProductType(supplierGoods.getProductType());
        product.setPlatformCategoryId(supplierGoods.getPlatformCategoryId());
        product.setId(supplierProduct.getId());
        product.setPic(supplierProduct.getAlbumPics().split(StrPool.COMMA)[CommonPool.NUMBER_ZERO]);
        product.setSalePrices(skus.stream().map(SkuDTO::getSalePrice).sorted().collect(Collectors.toList()));
        product.setDistributionMode(Collections.singletonList(DistributionMode.EXPRESS));
        setExtendInfo(product, supplierProduct.getPlatformCategory(), supplierProduct.getProductAttributes(), supplierProduct.getProductParameters());
        supplierGoods.setProduct(product)
                .setPlatformCategoryParentId(supplierGoods.getPlatformCategoryParentId())
                .setPlatformCategoryId(supplierGoods.getPlatformCategoryId())
                .setSupplierGoodsName(product.getName());
        supplierGoodsService.updateById(supplierGoods);
        StorageSpecSkuDTO storageSpecSkuDTO = new StorageSpecSkuDTO();
        storageSpecSkuDTO.setProductId(supplierGoods.getId());
        storageSpecSkuDTO.setSkus(skus);
        storageSpecSkuDTO.setSpecGroups(supplierProduct.getSpecGroups());
        storageRpcService.saveOrUpdateSpecSku(storageSpecSkuDTO);
        if (supplierGoods.getSellType() == SellType.CONSIGNMENT) {
            globalExecutor.execute(
                    () -> rabbitTemplate.convertAndSend(
                            GoodsRabbit.SUPPLIER_UPDATE_GOODS.exchange(),
                            GoodsRabbit.SUPPLIER_UPDATE_GOODS.routingKey(),
                            // 重新设置供应商Id
                            supplierGoods.getProduct()

                    )
            );
        }
    }

    /**
     * 修改供应商商品名称
     *
     * @param id   供应商商品id
     * @param name 供应商商品name
     */
    @Override
    public void updateSupplierProductName(Long id, String name) {
        //供应商商品已删除，也需要修改商品名称，去除这里的校验，把已删除的商品查询出来
        //SupplierGoods supplierGoods = checkoutSupplierProductInfo(id);
        SupplierGoods supplierGoods = new SupplierGoods();
        supplierGoods.setSupplierGoodsName(name).setId(id);
        supplierGoodsService.updateSupplierGoodById(supplierGoods);
    }

    /**
     * 供应商商品上下架
     *
     * @param param  商品状态change
     * @param status 修改状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSupplierProductStatus(boolean supplierUpdate, ProductStatusChangeDTO param, ProductStatus status) {
        Set<ShopProductKey> keys;
        //组合商品 key to ShopProductKey
        //供应商修改商品状态
        if (supplierUpdate) {
            Long shopId = ISecurity.userMust().getShopId();
            keys = param.getProductIds()
                    .stream()
                    .map(productId -> new ShopProductKey(shopId, productId))
                    .collect(Collectors.toSet());
        } else {
            //平台修改商品状态
            List<SupplierGoods> goods = supplierGoodsService.lambdaQuery()
                    .select(SupplierGoods::getShopId, BaseEntity::getId)
                    .in(BaseEntity::getId, param.getProductIds())
                    .list();
            if (CollUtil.isEmpty(goods)) {
                return;
            }
            keys = goods.stream()
                    .map(good -> new ShopProductKey(good.getShopId(), good.getId()))
                    .collect(Collectors.toSet());
        }
        this.updateProductStatus(
                supplierUpdate,
                new com.medusa.gruul.goods.api.model.dto.ProductStatusChangeDTO()
                        .setKeys(keys)
                        .setProductViolation(param.getProductViolation())
                        .setExplain(param.getExplain()),
                status
        );
    }

    @Override
    public void supplierAuditProductSubmit(Long id) {
        SupplierGoods supplierGoods = checkoutSupplierProductInfo(id);
        if (supplierGoods.getSupplierProductStatus() != ProductStatus.REFUSE) {
            throw new GlobalException("当前商品状态不可再次提交审核");
        }
        ProductStatus productStatus = supplierAddonSupporter.getProductStatus(supplierGoods.getSupplierProductStatus());
        if (productStatus != null) {
            supplierGoods.getProduct().getExtra().setSubmitTime(LocalDateTime.now());

            supplierGoods.getProduct().setStatus(productStatus);
            supplierGoods.setSupplierProductStatus(productStatus);
            if (productStatus == ProductStatus.SELL_ON) {
                supplierGoods.getProduct().getExtra().setAuditTime(LocalDateTime.now());
            }
        }
        supplierGoodsService.updateById(supplierGoods);
    }

    /**
     * 供应商违规下架商品恢复销售
     *
     * @param key 商品 key
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreSaleSupplierProduct(ShopProductKey key) {
        SupplierGoods good = TenantShop.disable(() ->
                supplierGoodsService.lambdaQuery()
                        .select(SupplierGoods::getSupplierProductStatus)
                        .eq(BaseEntity::getId, key.getProductId())
                        .eq(SupplierGoods::getShopId, key.getShopId())
                        .one()
        );
        if (good == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (!ProductStatus.PLATFORM_SELL_OFF.equals(good.getSupplierProductStatus())) {
            throw SupplierError.SUPPLIER_PRODUCT_STATUS_NOT_CORRECT.exception();
        }
        this.updateProductStatus(
                false,
                new com.medusa.gruul.goods.api.model.dto.ProductStatusChangeDTO()
                        .setKeys(Set.of(key))
                        .setProductViolation(null)
                        .setExplain(null),
                ProductStatus.SELL_OFF
        );

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(boolean supplierUpdate, com.medusa.gruul.goods.api.model.dto.ProductStatusChangeDTO param, ProductStatus status) {
        Set<ShopProductKey> keys = param.getKeys();
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        List<SupplierGoods> goods = supplierGoodsService.lambdaQuery()
                .select(SupplierGoods::getShopId, BaseEntity::getId)
                .apply(
                        SqlHelper.inSql(
                                List.of("shop_id", "id"),
                                keys,
                                List.of(ShopProductKey::getShopId, ShopProductKey::getProductId)
                        )
                )
                //排除平台下架 和未审核通过的商品
                .notIn(supplierUpdate, SupplierGoods::getSupplierProductStatus, ProductStatus.PLATFORM_SELL_OFF, ProductStatus.REFUSE)
                .list();
        //跳过
        if (CollUtil.isEmpty(goods)) {
            return;
        }
        ProductViolationDTO productViolation = param.getProductViolation();
        // 转换成 店铺ID 对应 商品 id 集合对象列表
        Map<Long, Set<Long>> shopIdProductIds = goods.stream()
                .collect(
                        Collectors.groupingBy(
                                SupplierGoods::getShopId,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        items -> items.stream().map(BaseEntity::getId).collect(Collectors.toSet())
                                )
                        )
                );
        //批量更新
        String explain = param.getExplain();
        LocalDateTime now = LocalDateTime.now();
        com.baomidou.mybatisplus.extension.toolkit.SqlHelper.executeBatch(
                sqlSessionFactory,
                new Slf4jImpl(this.getClass().getSimpleName()),
                sqlSession -> {
                    SupplierGoodsMapper mapper = sqlSession.getMapper(SupplierGoodsMapper.class);
                    shopIdProductIds.forEach(
                            (shopId, productIds) -> {
                                LambdaUpdateWrapper<SupplierGoods> updateWrapper = Wrappers.lambdaUpdate(SupplierGoods.class)
                                        .eq(SupplierGoods::getShopId, shopId)
                                        .in(BaseEntity::getId, productIds)
                                        .set(SupplierGoods::getSupplierProductStatus, status)
                                        .setSql(SqlHelper.renderJsonSetSql("product", Tuple.of("status", status.name())));
                                //平台恢复销售 移除违规信息
                                if (!supplierUpdate && status == ProductStatus.SELL_OFF) {
                                    updateWrapper.setSql(
                                            "product=JSON_REMOVE(product, '$.extra.productViolation')"
                                    );
                                    mapper.update(updateWrapper);
                                    return;
                                }
                                if (productViolation != null) {
                                    //违规下架
                                    updateWrapper.setSql(
                                            SqlHelper.renderJsonSetSql("product", Tuple.of("extra.productViolation", JSON.toJSONString(productViolation.setExamineDateTime(now)), null))
                                    );
                                }
                                if (explain != null && status == ProductStatus.REFUSE) {
                                    updateWrapper.setSql(SqlHelper.renderJsonSetSql("product", Tuple.of("extra.explain", explain)));
                                    updateWrapper.setSql(SqlHelper.renderJsonSetSql("product", Tuple.of("extra.auditTime", now.format(FastJson2.DATETIME_FORMATTER))));
                                }
                                if (!supplierUpdate && status == ProductStatus.SELL_ON) {
                                    updateWrapper.setSql(SqlHelper.renderJsonSetSql("product", Tuple.of("extra.auditTime", now.format(FastJson2.DATETIME_FORMATTER))));
                                }
                                mapper.update(updateWrapper);
                            }
                    );
                }
        );
        IManualTransaction.afterCommit(
                () -> globalExecutor.execute(
                        () -> {
                            //1. 广播供应商商品状态变化
                            rabbitTemplate.convertAndSend(
                                    GoodsRabbit.SUPPLIER_GOODS_UPDATE_STATUS.exchange(),
                                    GoodsRabbit.SUPPLIER_GOODS_UPDATE_STATUS.routingKey(),
                                    new SupplierGoodsUpdateStatusDTO()
                                            .setProductUpdateStatus(
                                                    shopIdProductIds.entrySet()
                                                            .stream()
                                                            .map(
                                                                    entry -> new ProductUpdateStatusDTO()
                                                                            .setShopId(entry.getKey())
                                                                            .setProductIds(entry.getValue())
                                                                            .setProductStatus(status)
                                                            ).toList()
                                            )
                                            .setProductViolation(productViolation)
                            );
                            //2. 生成库存明细
                            if (supplierUpdate && status == ProductStatus.SELL_ON) {
                                storageRpcService.generateStorageDetail(shopIdProductIds);
                            }
                        }
                )
        );
    }


    private SupplierGoods checkoutSupplierProductInfo(Long id) {
        SupplierGoods supplierGoods = supplierGoodsService.getById(id);
        GoodsError.CURRENT_GOODS_NOT_EXIST.trueThrow(supplierGoods == null);
        assert supplierGoods != null;
        GoodsError.PRODUCT_STATUS_OPERATE_EXCEPTION.trueThrow(supplierGoods.getSupplierProductStatus() == ProductStatus.PLATFORM_SELL_OFF);
        return supplierGoods;
    }


    private void setExtendInfo(Product product, com.medusa.gruul.goods.api.model.CategoryLevel platformCategory, List<ProductFeaturesValueDTO> productAttributes, List<ProductFeaturesValueDTO> productParameters) {
        Optional.ofNullable(productAttributes)
                .ifPresent(params -> {
                    List<ProductFeaturesValueDTO> collect = params.stream()
                            .collect(Collectors.toMap(ProductFeaturesValueDTO::getId, entity -> entity, (existing, replacement) -> existing))
                            .values()
                            .stream()
                            .toList();
                    GoodsError.ATTRIBUTES_REPETITION.trueThrow(params.size() != collect.size());
                    params.forEach(productAttribute -> productAttribute.getFeatureValues().forEach(featureValue -> {
                        if (featureValue.getFeatureValueId() == null) {
                            featureValue.setFeatureValueId(MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(new FeatureValueDTO()).longValue());
                        }
                    }));
                });
        Long customDeductionRatio = supplierAddonSupporter.getCustomDeductionRatio(platformCategory.getTwo(), product.getShopId());
        ProductExtraDTO productExtra = new ProductExtraDTO();
        // 设置自定义扣率,商品类目,平台类目,产品属性 ，产品参数
        productExtra.setCustomDeductionRatio(customDeductionRatio == null ? 0L : customDeductionRatio);
        productExtra.setPlatformCategory(platformCategory);
        productExtra.setProductParameters(productParameters);
        productExtra.setProductAttributes(productAttributes);
        //更新商品，审核时间
        ProductExtraDTO extra = Optional.ofNullable(product.getExtra()).orElse(new ProductExtraDTO());
        productExtra.setSubmitTime(extra.getSubmitTime()).setAuditTime(extra.getAuditTime());
        product.setExtra(productExtra);
    }
}

