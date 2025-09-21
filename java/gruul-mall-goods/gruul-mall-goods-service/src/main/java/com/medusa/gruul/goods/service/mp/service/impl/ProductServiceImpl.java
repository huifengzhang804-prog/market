package com.medusa.gruul.goods.service.mp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.medusa.gruul.common.custom.aggregation.classify.dto.CategoryRankDTO;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.base.*;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.mp.IManualTransaction;
import com.medusa.gruul.common.mp.config.MybatisPlusConfig;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.common.mp.model.DS;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.mp.model.TenantShop;
import com.medusa.gruul.common.mp.page.PageBean;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.common.system.model.model.ClientType;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.global.model.o.RangeDateTime;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.entity.ProductCategory;
import com.medusa.gruul.goods.api.entity.ProductLabel;
import com.medusa.gruul.goods.api.entity.SupplierRateRecord;
import com.medusa.gruul.goods.api.enums.GoodsRabbit;
import com.medusa.gruul.goods.api.model.CategoryLevel;
import com.medusa.gruul.goods.api.model.dto.*;
import com.medusa.gruul.goods.api.model.enums.*;
import com.medusa.gruul.goods.api.model.param.*;
import com.medusa.gruul.goods.api.model.vo.*;
import com.medusa.gruul.goods.service.addon.GoodsAddonSupporter;
import com.medusa.gruul.goods.service.model.dto.ConsignmentProductDTO;
import com.medusa.gruul.goods.service.model.dto.ProductDetailDTO;
import com.medusa.gruul.goods.service.model.dto.ShopProductSkuIdDTO;
import com.medusa.gruul.goods.service.model.param.PurchaseProductParam;
import com.medusa.gruul.goods.service.model.vo.*;
import com.medusa.gruul.goods.service.mp.mapper.ProductMapper;
import com.medusa.gruul.goods.service.mp.service.IProductCategoryService;
import com.medusa.gruul.goods.service.mp.service.IProductLabelService;
import com.medusa.gruul.goods.service.mp.service.IProductService;
import com.medusa.gruul.goods.service.mp.service.ISupplierRateRecordService;
import com.medusa.gruul.goods.service.service.CategoryService;
import com.medusa.gruul.goods.service.util.GoodsUtil;
import com.medusa.gruul.order.api.entity.OrderEvaluate;
import com.medusa.gruul.order.api.model.OrderCompletedDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.search.api.model.ProductActivityVO;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.enums.OperaReason;
import com.medusa.gruul.shop.api.enums.ShopRabbit;
import com.medusa.gruul.shop.api.model.dto.ShopsEnableDisableDTO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.medusa.gruul.storage.api.dto.ShopProductKeyDTO;
import com.medusa.gruul.storage.api.dto.SkuDTO;
import com.medusa.gruul.storage.api.dto.StorageSpecSkuDTO;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.enums.StockChangeType;
import com.medusa.gruul.storage.api.enums.StockType;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.storage.api.vo.ProductSaleVolumeVO;
import com.medusa.gruul.storage.api.vo.ProductSkusVO;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import com.medusa.gruul.user.api.enums.MemberType;
import com.medusa.gruul.user.api.model.dto.UserFootMarkDTO;
import com.medusa.gruul.user.api.model.vo.MemberAggregationInfoVO;
import com.medusa.gruul.user.api.rpc.UserRpcService;
import io.vavr.Tuple;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


/**
 * 商品信息表 服务实现类
 *
 * @author xiaoq
 * @since 2022-03-04
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements IProductService {


    private final Executor globalExecutor;
    private final IProductCategoryService productCategoryService;
    private final IProductLabelService productLabelService;
    private final RabbitTemplate rabbitTemplate;
    private final GoodsAddonSupporter goodsAddonSupporter;
    private final OrderRpcService orderRpcService;
    private final ISupplierRateRecordService supplierRateRecordService;
    private final RedissonClient redissonClient;
    private final SqlSessionFactory sqlSessionFactory;
    private final UaaRpcService uaaRpcService;
    private CategoryService categoryService;
    private ShopRpcService shopRpcService;
    private UserRpcService userRpcService;
    private StorageRpcService storageRpcService;
    private SearchRpcService searchRpcService;


    /**
     * 获取销售价格,长度超过1024，只取最大和最小两个价格
     *
     * @param sortedSalePrices 价格数组
     * @return 价格数组
     */
    private static List<Long> getSalePrices(List<Long> sortedSalePrices) {
        if (sortedSalePrices.toString().length() > CommonPool.ONE_THOUSAND_TWENTY_FOUR) {
            sortedSalePrices = sortedSalePrices.stream()
                    .collect(Collectors.collectingAndThen(
                            Collectors.toList(),
                            list -> {
                                if (!list.isEmpty()) {
                                    return new ArrayList<>(List.of(Collections.min(list), Collections.max(list)));
                                } else {
                                    return new ArrayList<>();
                                }
                            }
                    ));
        }
        return sortedSalePrices;
    }

    /**
     * 商品发布
     *
     * @param productDto 商品信息Dto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product issueProduct(ProductDTO productDto) {
        Long shopId = ISecurity.userMust().getShopId();
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
        productDto.checkDistributionMode(shopInfo.getShopMode());
        // 渲染product商品基础信息
        Product product = productDto.coverProduct().setShopId(shopId);
        if (product.getScore() == null) {
            product.setScore(BigDecimal.valueOf(5));
        }

        // 扩展信息
        setExtendInfo(product, productDto.getPlatformCategory(), productDto.getShopCategory(),
                productDto.getProductAttributes(), productDto.getProductParameters(),
                productDto.getConsignmentPriceSetting());
        List<SkuDTO> skus = productDto.getSkus();
        List<Long> sortedSalePrices = skus.stream().map(SkuDTO::getSalePrice).sorted().collect(Collectors.toList());
        //统计有限库存SKU的总库存数
        // 统计商品SKU中存在有限库存的SKU数量
        if (Objects.isNull(productDto.getId())) {
            //属性新增商品
            long count = skus.stream().filter(skuDTO -> StockType.LIMITED.equals(skuDTO.getStockType()))
                    .count();
            if (count > 0) {
                Long totalStock = skus.stream()
                        .filter(x -> StockType.LIMITED.equals(x.getStockType()))
                        .map(SkuDTO::getInitStock).reduce(0L, Long::sum);
                product.setTotalStock(totalStock);
            } else {
                //商品的所有的SKU都是无限库存
                product.setTotalStock(null);
            }
        } else {
            //采购商品发布 会走这里 获取商品的库存信息
            Set<Long> skuIds = skus.stream()
                    .filter(x -> Objects.nonNull(x.getId()) && StockType.LIMITED.equals(x.getStockType()))
                    .map(SkuDTO::getId)
                    .collect(Collectors.toSet());
            if (CollUtil.isEmpty(skuIds)) {
                product.setTotalStock(null);
            } else {
                Set<ActivityShopProductSkuKey> shopProductSkuKeys = skuIds.stream()
                        .map(skuId -> {
                            ActivityShopProductSkuKey key = new ActivityShopProductSkuKey();
                            key.setSkuId(skuId).setProductId(product.getId())
                                    .setShopId(shopId)
                                    .setActivityId(0L)
                                    .setActivityType(OrderType.COMMON);
                            return key;

                        }).collect(Collectors.toSet());

                Map<ActivityShopProductSkuKey, Long> activityShopProductSkuKeyLongMap =
                        storageRpcService.skuStockBatch(Boolean.FALSE, shopProductSkuKeys);
                Long total = activityShopProductSkuKeyLongMap.values().stream()
                        .reduce(0L, Long::sum);
                product.setTotalStock(total);
            }


        }

        //总销量默认为0
        product.setTotalSalesVolume(0L);
        product.setPic(StrUtil.split(product.getAlbumPics(), StrPool.COMMA).get(CommonPool.NUMBER_ZERO));

        /* 部分商品数据渲染
         */
        SellType sellType = productDto.getSellType();
        product.setStatus(ProductStatus.SELL_ON);
        if (SellType.OWN != sellType && productDto.getId() != null) {
            /* 非自有商品处理
             */


            product.setId(productDto.getId());
            product.setSupplierId(productDto.getSupplierId());
            if (SellType.CONSIGNMENT == sellType) {
                product.getExtra().setDeliveryUserId(ISecurity.userMust().getId());
                product.getExtra().setDeliveryUserPhone(ISecurity.userMust().getMobile());
                product.getExtra().setSupplierCustomDeductionRatio(productDto.getSupplierCustomDeductionRatio());
                ConsignmentPriceSettingDTO consignmentPriceSetting = productDto.getConsignmentPriceSetting();
                //代销商品修改商品价格
                boolean isRegular = consignmentPriceSetting.getType() == PricingType.REGULAR;
                sortedSalePrices = sortedSalePrices.stream()
                        .map(sortedSalePrice -> sortedSalePrice + (isRegular ? consignmentPriceSetting.getSale()
                                : (sortedSalePrice * consignmentPriceSetting.getSale() / 1000000))).toList();
            }
        } else {
            /* 平台审核商品状态
             */
            ProductAuditType productAuditType = FastJson2.convert(
                    RedisUtil.getCacheObject(GoodsConstant.GOODS_PRODUCT_AUDIT),
                    ProductAuditType.class
            );
            if (productAuditType == ProductAuditType.MANUALLY_AUDIT) {
                //人工审核
                ProductStatus productStatus = goodsAddonSupporter.getProductStatus(product.getStatus());
                if (productStatus != null) {
                    product.setStatus(productStatus);
                    if (productStatus == ProductStatus.SELL_ON) {
                        product.getExtra().setAuditTime(LocalDateTime.now());
                    }
                }
            } else {
                //系统审核 自动通过
                product.getExtra().setAuditTime(LocalDateTime.now());
            }
            product.getExtra().setSubmitTime(LocalDateTime.now());
        }
        product.setSalePrice(sortedSalePrices.get(CommonPool.NUMBER_ZERO));
        //多规则商品sku太多，长度超过1024后，取最大和最小两个值
        sortedSalePrices = getSalePrices(sortedSalePrices);
        product.setSalePrices(sortedSalePrices);
        //商品信息落库
        boolean success = DS.sharding(() -> this.save(product));
        GoodsError.PRODUCT_ISSUE_FAIL.falseThrow(success);

        if (SellType.OWN != sellType) {
            //如果是代销商品则检查供应商是否可用
            if (SellType.CONSIGNMENT == sellType) {
                ShopInfoVO supplier = shopRpcService.getShopInfoByShopId(product.getSupplierId());
                GoodsError.SUPPLIER_HAVE_DELETE.trueThrow(
                        supplier == null || ShopStatus.NORMAL != supplier.getStatus());
            }
            goodsAddonSupporter.purchaseProductIssue(product.getId(), shopId, product.getSupplierId());
        }

        //商品标签id不为空时
        product.setProductLabel(
                product.getLabelId() == null ? null :
                        TenantShop.disable(
                                () -> productLabelService.lambdaQuery().eq(ProductLabel::getId, product.getLabelId())
                                        .one())
        );
        //店铺类型
        product.setShopType(shopInfo.getShopType());
        //店铺名称
        product.setShopName(shopInfo.getName());
        // sku信息
        editProductSkuInfo(productDto, product, skus);

        // 发送商品发布广播
        sendProductBroadcast(productDto.getShopCategory(), productDto.getPlatformCategory(), product,
                GoodsRabbit.GOODS_RELEASE.exchange(), GoodsRabbit.GOODS_RELEASE.routingKey());
        //店铺上架商品数量变化的MQ
        IManualTransaction.afterCommit(
                () -> globalExecutor.execute(() -> rabbitTemplate
                        .convertAndSend(ShopRabbit.SHOP_ON_SHELF_GOODS_COUNT_CHANGE.exchange(),
                                ShopRabbit.SHOP_ON_SHELF_GOODS_COUNT_CHANGE.routingKey(),
                                product.getShopId()
                        )
                ));
        return product;
    }

    /**
     * 商品删除
     *
     * @param productIds 商品ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProductList(Set<Long> productIds) {
        Long shopId = ISecurity.userMust().getShopId();
        // 已下架的商品才能删除
        long size = this.lambdaQuery()
                .eq(Product::getShopId, shopId)
                .in(Product::getStatus, ProductStatus.SELL_OFF, ProductStatus.PLATFORM_SELL_OFF,
                        ProductStatus.SUPPLIER_SELL_OFF)
                .in(BaseEntity::getId, productIds)
                .count();
        GoodsError.PRODUCT_CANNOT_BE_DELETED.trueThrow(productIds.size() != size);

        Set<String> cacheKeys = productIds.stream()
                .map(productId -> GoodsUtil.productCacheKey(shopId, productId))
                .collect(Collectors.toSet());
        // 双删
        Boolean success = RedisUtil.doubleDeletion(
                () -> removeByIds(productIds),
                () -> RedisUtil.delete(cacheKeys)
        );
        GoodsError.PRODUCT_DELETE_FAIL.falseThrow(success);
        rabbitTemplate.convertAndSend(
                GoodsRabbit.GOODS_DELETE.exchange(),
                GoodsRabbit.GOODS_DELETE.routingKey(),
                new ProductDeleteDTO()
                        .setShopId(shopId)
                        .setProductIds(productIds)
        );
    }

    /**
     * 商品上下架 同步更新缓存
     *
     * @param productStatusChange 商品状态更改信息
     * @param status              产品上下架状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(boolean isPlatform, ProductStatusChangeDTO productStatusChange, ProductStatus status) {
        Set<ShopProductKey> productKeys = productStatusChange.getKeys();
        ISecurity.match()
                .ifAnyShopAdmin((secureUser) -> productKeys.forEach(key -> key.setShopId(secureUser.getShopId())));
        //获取商品信息
        List<Product> currentProducts = TenantShop.disable(() -> this.lambdaQuery()
                .select(Product::getShopId, BaseEntity::getId, Product::getStatus, Product::getSellType, Product::getSupplierId)
                .apply(SqlHelper.inSql(
                        List.of("shop_id", "id"), productKeys, List.of(ShopProductKey::getShopId, ShopProductKey::getProductId)
                ))
                .list()
        );
        if (CollUtil.isEmpty(currentProducts)) {
            return;
        }
        //排除 平台下架商品
        currentProducts = currentProducts.stream()
                .filter(product -> product.getStatus() != ProductStatus.PLATFORM_SELL_OFF).collect(Collectors.toList());
        if (CollUtil.isEmpty(currentProducts)) {
            return;
        }
        // 店铺不可操作平台已拒绝的商品
        if (!isPlatform) {
            currentProducts = currentProducts.stream().filter(product -> product.getStatus() != ProductStatus.REFUSE)
                    .collect(Collectors.toList());
            if (CollUtil.isEmpty(currentProducts)) {
                return;
            }
        }
        // 记录代销商品的key
        Set<ShopProductKey> consignmentProductKeys = new HashSet<>();
        // 转换成 店铺ID 对应 商品 id 集合对象列表
        List<ProductUpdateStatusDTO> updateStatusList = currentProducts.stream()
                .filter(product -> {
                    if (!isPlatform || SellType.CONSIGNMENT != product.getSellType()) {
                        return true;
                    }
                    consignmentProductKeys.add(new ShopProductKey(product.getSupplierId(), product.getId()));
                    return false;
                })
                .collect(Collectors.groupingBy(Product::getShopId))
                .entrySet()
                .stream()
                .peek(
                        entry -> entry.getValue()
                                .forEach(product -> {
                                    if (status == ProductStatus.SELL_ON && !validConsignmentProductStatus(product.getStatus())) {
                                        throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "供应商下架与禁用的商品不能上架");
                                    }
                                })
                ).map(entry -> new ProductUpdateStatusDTO()
                        .setShopId(entry.getKey())
                        .setProductIds(
                                entry.getValue()
                                        .stream()
                                        .map(Product::getId)
                                        .collect(Collectors.toSet())
                        )
                        .setProductStatus(status)
                )
                .collect(Collectors.toList());
        //违规参数
        ProductViolationDTO productViolation = productStatusChange.getProductViolation();

        //平台下架才需要 (代销商品去更新供应商商品状态)
        if (isPlatform && CollUtil.isNotEmpty(consignmentProductKeys)) {
            goodsAddonSupporter.syncSupplierProduct(
                    new ProductStatusChangeDTO()
                            .setKeys(consignmentProductKeys)
                            .setProductViolation(productViolation)
                            .setExplain(productStatusChange.getExplain()),
                    status
            );
        }
        if (CollUtil.isEmpty(updateStatusList)) {
            return;
        }
        // 商品状态处理
        handlerGoodsStatus(status, productViolation, updateStatusList, productStatusChange.getExplain());
        for (Product currentProduct : currentProducts) {
            //店铺上架商品数量变化的MQ
            IManualTransaction.afterCommit(
                    () -> globalExecutor.execute(() -> rabbitTemplate
                            .convertAndSend(ShopRabbit.SHOP_ON_SHELF_GOODS_COUNT_CHANGE.exchange(),
                                    ShopRabbit.SHOP_ON_SHELF_GOODS_COUNT_CHANGE.routingKey(),
                                    currentProduct.getShopId()
                            )
                    ));
        }

    }

    /**
     * 商品信息修改
     *
     * @param productDto 商品信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(ProductDTO productDto) {
        Long shopId = ISecurity.userMust().getShopId();
        //校验mode
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
        productDto.checkDistributionMode(shopInfo.getShopMode());

        //校验商品
        Product product = checkoutProductInfo(productDto.getId(), shopId, Boolean.TRUE);
        //商品基础信息修改
        Product newProduct = productDto.coverProduct().setShopId(shopId);
        newProduct.setExtra(product.getExtra());
        //扩展信息处理
        setExtendInfo(newProduct, productDto.getPlatformCategory(), productDto.getShopCategory(),
                productDto.getProductAttributes(), productDto.getProductParameters(),
                productDto.getConsignmentPriceSetting());
        newProduct.setPic(StrUtil.split(newProduct.getAlbumPics(), StrPool.COMMA).get(CommonPool.NUMBER_ZERO));
        // 修改商品 不可更改项 取原商品信息 -- 商品类型 销售类型
        newProduct.setProductType(product.getProductType());
        newProduct.setSellType(product.getSellType());
        newProduct.setPlatformCategoryId(product.getPlatformCategoryId());
        List<SkuDTO> skus = productDto.getSkus();
        List<Long> sortedSalePrices = skus.stream().map(SkuDTO::getSalePrice).sorted().toList();
        newProduct.setSalePrice(sortedSalePrices.get(CommonPool.NUMBER_ZERO));
        sortedSalePrices = getSalePrices(sortedSalePrices);
        newProduct.setSalePrices(sortedSalePrices);
        newProduct.setStatus(
                ProductStatus.REFUSE == product.getStatus() ? ProductStatus.UNDER_REVIEW : product.getStatus());
        //商品标签
        newProduct.setProductLabel(
                productDto.getLabelId() != null ? TenantShop.disable(
                        () -> productLabelService.lambdaQuery().eq(ProductLabel::getId, productDto.getLabelId()).one())
                        : null
        );
        // 同步修改商品信息
        Integer update = RedisUtil.doubleDeletion(() -> baseMapper.update(newProduct,
                        Wrappers.lambdaQuery(Product.class).eq(Product::getId, newProduct.getId())
                                .eq(Product::getShopId, newProduct.getShopId())),
                RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, shopId, product.getId()));
        GoodsError.PRODUCT_UPDATE_FAIL.trueThrow(update < CommonPool.NUMBER_ONE);

        // sku信息
        editProductSkuInfo(productDto, product, skus);
        //发送商品修改广播
        newProduct.setShopId(shopId).setCreateTime(product.getCreateTime());
        sendProductBroadcast(productDto.getShopCategory(), productDto.getPlatformCategory(), newProduct,
                GoodsRabbit.GOODS_UPDATE.exchange(), GoodsRabbit.GOODS_UPDATE.routingKey());
    }

    /**
     * 查询单个商品信息
     *
     * @param productId 商品id
     * @param shopId    店铺id
     * @return 商品详情
     */
    @Override
    public ProductVO getProductById(Long productId, Long shopId) {
        ProductVO productVO = new ProductVO();
        Product productInfo = this.getProductInfo(shopId, productId);
        GoodsError.CURRENT_GOODS_NOT_EXIST.trueThrow(productInfo == null);
        BeanUtil.copyProperties(productInfo, productVO);
        //赋值商品VO额外数据 商品展示分类
        productVO.setProductCategory(
                ISystem.shopId(shopId, () -> this.baseMapper.queryProductCategory(productInfo.getCategoryId())));
        return productVO;
    }

    /**
     * 查询商品详情
     *
     * @param param 商品详情参数
     * @return 商品详情
     */
    @Override
    public ProductDetailVO details(ProductDetailDTO param) {
        //商品详情
        Long shopId = param.getShopId();
        Long productId = param.getProductId();
        Product product = getProductInfo(shopId, productId);
        if (product == null) {
            // 发送商品删除信息 保证es数据同步
            rabbitTemplate.convertAndSend(
                    GoodsRabbit.GOODS_DELETE.exchange(),
                    GoodsRabbit.GOODS_DELETE.routingKey(),
                    new ProductDeleteDTO()
                            .setShopId(shopId)
                            .setProductIds(Collections.singleton(productId))
            );
            throw GoodsError.CURRENT_GOODS_NOT_EXIST.exception();
        }
        if (ProductStatus.SELL_ON != product.getStatus()) {
            // 发送商品状态更正信息 保证es数据同步
            rabbitTemplate
                    .convertAndSend(GoodsRabbit.GOODS_UPDATE_STATUS.exchange(),
                            GoodsRabbit.GOODS_UPDATE_STATUS.routingKey(),
                            Collections.singletonList(
                                    new ProductUpdateStatusDTO()
                                            .setProductIds(Collections.singleton(productId))
                                            .setShopId(shopId)
                                            .setProductStatus(product.getStatus()))
                    );
            throw GoodsError.CURRENT_GOODS_NOT_EXIST.exception();
        }
        //渲染响应数据
        Set<DistributionMode> distributionModes = Sets.newHashSet(CollUtil.emptyIfNull(product.getDistributionMode()));
        if (distributionModes.contains(DistributionMode.INTRA_CITY_DISTRIBUTION)) {
            Boolean supportIcStatus = goodsAddonSupporter.queryShopIcStatus(shopId);
            if (BooleanUtil.isFalse(supportIcStatus)) {
                //店铺关闭同城配送方式 则移除同城配送方式
                distributionModes.remove(DistributionMode.INTRA_CITY_DISTRIBUTION);
            }
        }
        ProductDetailVO detail = new ProductDetailVO()
                .setShopId(shopId)
                .setProductId(productId)
                .setProductType(product.getProductType())
                .setName(product.getName())
                .setPic(product.getPic())
                .setWidePic(product.getWidePic())
                .setDistributionMode(distributionModes)
                .setFreightTemplateId(product.getFreightTemplateId())
                .setAlbumPics(StrUtil.isBlank(product.getAlbumPics()) ? List.of()
                        : StrUtil.split(product.getAlbumPics(), StrPool.COMMA))
                .setVideoUrl(product.getVideoUrl())
                .setServiceIds(Set.copyOf(CollUtil.emptyIfNull(product.getServiceIds())))
                .setSaleDescribe(product.getSaleDescribe())
                .setDetail(product.getDetail())
                .setExtra(product.getExtra())
                .setSellType(product.getSellType());

        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        globalExecutor,
                        //类目信息
                        () -> detail.setProductCategory(categoryService.getCategoryInfoByShopIdAndCategoryId(shopId, product.getCategoryId())),

                        //规格信息
                        () -> detail.setSpecsSkus(storageRpcService.getProductSpecsSkus(shopId, productId))
                )
        );


        //获取当前登录的用户 id 
        Long userId = ISecurity.userOpt().map(SecureUser::getId).getOrNull();

        //当前选择的 skuId  如果参数里的 sku id 为空 则取sku 列表的第一个 sku
        Long skuId = param.getSkuId();
        //初始化代表详情页第一次查询详情数据 即无 sku id
        boolean init = skuId == null;

        List<StorageSku> skus = detail.getSpecsSkus().getSkus();
        //初始化时自动选取一个sku
        //1. 尽可能取有库存的skuId
        //2. 如果不存在 则取第一个 skuId
        if (init) {
            for (StorageSku storageSku : skus) {
                //无限库存 或 库存大于0 则取该skuId
                if (storageSku.getStockType() == StockType.UNLIMITED || storageSku.getStock() > 0) {
                    skuId = storageSku.getId();
                    break;
                }
            }
            //如果不存在 则取第一个 skuId
            if (skuId == null) {
                skuId = skus.get(CommonPool.NUMBER_ZERO).getId();
            }
        }

        //设置当前选择的 sku id
        detail.setSkuId(skuId)
                //设置销量
                .setSale(
                        skus.stream()
                                .map(sku -> sku.getSalesVolume() + sku.getInitSalesVolume())
                                .reduce(0L, Long::sum)
                );
        //非套餐活动信息
        ProductActivityVO recentActivity = searchRpcService.getRecentActivity(shopId, productId);
        log.warn("活动信息 {}", recentActivity);
        if (recentActivity != null) {
            //非套餐的活动  秒杀，拼团、 砍价、
            //套餐数据额外查询
            ActivityDetailVO activity = goodsAddonSupporter.activity(
                    recentActivity.getActivityType(),
                    recentActivity.getActivityId(),
                    userId,
                    new ShopProductSkuKey(shopId, productId, skuId)
            );
            if (activity != null) {
                LocalDateTime now = LocalDateTime.now();
                RangeDateTime time = activity.getTime();
                boolean activityOpen = !now.isBefore(time.getStart()) && !now.isAfter(time.getEnd());
                boolean skuActivity = activity.getActivityPrice() != null;
                detail.setActivity(activity)
                        .setActivityOpen(activityOpen)
                        .setSkuActivity(skuActivity);
                //如果活动已开始且当前sku参与了该活动  则重置 叠加优惠
                if (activityOpen && skuActivity) {
                    if (Objects.nonNull(activity.getStackable())) {
                        detail.setStackable(activity.getStackable());
                    } else {
                        detail.setStackable(new StackableDiscount());
                    }

                    //查询活动库存
                    ActivityShopProductSkuKey skuKey = (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                            .setSkuId(skuId)
                            .setProductId(productId)
                            .setShopId(shopId)
                            .setActivityType(activity.getType())
                            .setActivityId(activity.getActivityId());
                    detail.setActivityStock(
                            storageRpcService.skuStockBatch(false, Set.of(skuKey))
                                    .getOrDefault(skuKey, (long) CommonPool.NUMBER_ZERO)
                    );
                }
            }
        }
        //商品 key
        ShopProductKey key = new ShopProductKey(shopId, productId);
        //总金额
        ProductPriceVO price = price(param.getNum(), param.getFeatures(), detail);
        Long totalAmount = price.getEstimate();
        //当前的叠加优惠信息
        StackableDiscount stackable = detail.getStackable();
        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        globalExecutor,
                        //计算折扣数据 会员、满减、优惠券
                        () -> detail.setDiscountMap(discountMap(userId, key, totalAmount, stackable)),
                        // 计算预计赚数据 分销、返利
                        () -> detail.setEarningMap(
                                earningMap(userId, key, totalAmount, detail.getExtra().getCustomDeductionRatio())),
                        //其它额外信息
                        () -> {
                            if (!init) {
                                return;
                            }
                            //初始化查询时才需要查询以下数据
                            // 查询用户是否已收藏商品
                            detail.setWhetherCollect(
                                    userId != null && userRpcService.isUserCollectAndAddFootMark(
                                            userId,
                                            new UserFootMarkDTO()
                                                    .setShopId(shopId)
                                                    .setProductId(productId)
                                                    .setProductPic(detail.getPic())
                                                    .setProductName(detail.getName())
                                                    .setProductPrice(price.getSkuPrice())
                                                    .setPlatformCategoryId(
                                                            detail.getExtra().getPlatformCategory().getThree())
                                    )
                            );
                            //查询套餐活动
                            List<SetMealBasicInfoVO> packages = goodsAddonSupporter.getSetMealBasicInfo(shopId,
                                    productId);
                            detail.setPackages(packages == null ? List.of() : packages);
                        }
                )
        );
        //渲染折扣计算器 预估价计算逻辑
        renderPriceItems(price, detail);
        return detail.setPrice(price);
    }

    /**
     * 渲染价格计算逻辑 折扣计算器
     *
     * @param price  当前商品价格
     * @param detail 商品详情
     */
    private void renderPriceItems(ProductPriceVO price, ProductDetailVO detail) {
        List<ProductPriceItemVO> priceItems = new ArrayList<>(CommonPool.NUMBER_EIGHT);
        //预估价
        Long estimate = price.getEstimate();
        //当前价
        priceItems.add(
                new ProductPriceItemVO()
                        .setPrice(estimate)
                        .setDesc(
                                //当前价、秒杀价、拼团价。。。。。
                                (!detail.getActivityOpen() || !detail.getSkuActivity() ? "当前"
                                        : detail.getActivity().getType().getDesc()) + "价"
                        )
        );
        price.setItems(priceItems);
        Map<DiscountType, ProductDiscountVO> discountMap = detail.getDiscountMap();
        if (CollUtil.isEmpty(discountMap)) {
            return;
        }
        //折扣计算信息
        for (Map.Entry<DiscountType, ProductDiscountVO> entry : discountMap.entrySet()) {
            ProductDiscountVO discount = entry.getValue();
            Long discountAmount = discount.getDiscount();
            if (discountAmount == null || discountAmount <= 0) {
                continue;
            }
            DiscountType type = entry.getKey();
            priceItems.add(new ProductPriceItemVO().setPrice(discountAmount).setDesc(type.getDesc()));
            estimate -= discountAmount;
        }
        //更新预估价
        price.setEstimate(Math.max(estimate, CommonPool.NUMBER_ZERO));
    }


    /**
     * 当前选择的商品总额
     *
     * @param num      选择的数量
     * @param features 选择的商品属性
     * @param detail   商品详情信息
     * @return 商品总额
     */
    private ProductPriceVO price(Long num, Map<Long, Set<Long>> features, ProductDetailVO detail) {
        List<StorageSku> skus = detail.getSpecsSkus().getSkus();
        //更新当前sku id数据
        Long finalSkuId = detail.getSkuId();
        //划线价 
        long underlined = CommonPool.NUMBER_ZERO;
        //原始销售价
        long skuPrice = CommonPool.NUMBER_ZERO;
        //预估价 即目前的销售价 默认第一个 sku 价格
        long estimate = CommonPool.NUMBER_ZERO;
        //根据 skuId 匹配 获取 sku 的价格
        for (StorageSku storageSku : skus) {
            if (finalSkuId.equals(storageSku.getId())) {
                underlined = storageSku.getPrice();
                skuPrice = storageSku.getSalePrice();
                estimate = skuPrice;
                break;
            }
        }
        //如果活动已开启 并且当前 sku 参与了活动 设置预估价为当前的活动价
        if (detail.getActivityOpen() && detail.getSkuActivity()) {
            estimate = detail.getActivity().getActivityPrice();
        }
        if (CollUtil.isNotEmpty(features)) {
            long attributesAmount = detail.getExtra().attributesAmount(features);
            estimate += attributesAmount;
            underlined += attributesAmount;
        }
        //当前选择的 sku 的数量
        Long curNum = ObjectUtil.defaultIfNull(num, (long) CommonPool.NUMBER_ONE);
        //销售价*数量 =商品总额  选择的数量为空则设置为 1
        return new ProductPriceVO()
                .setUnderlined(underlined * curNum)
                .setSkuPrice(skuPrice)
                .setEstimate(estimate * curNum);
    }


    /**
     * 折扣数据 会员、满减、优惠券
     *
     * @param userId      用户 id
     * @param key         商品 key
     * @param totalAmount 当前选择的商品总额
     * @param stackable   叠加优惠信息
     * @return 折扣数据
     */
    private Map<DiscountType, ProductDiscountVO> discountMap(Long userId, ShopProductKey key, Long totalAmount,
                                                             StackableDiscount stackable) {
        Map<DiscountType, ProductDiscountVO> discountMap = new HashMap<>(DiscountType.values().length);
        ProductDiscountVO couponDiscount;
        //优惠券   
        if (stackable.isCoupon()
                && (couponDiscount = goodsAddonSupporter.discount(DiscountType.COUPON, userId, key, totalAmount))
                != null) {
            discountMap.put(DiscountType.COUPON, couponDiscount);
        }
        ProductDiscountVO fullDiscount;
        //优惠券   
        if (stackable.isFull()
                && (fullDiscount = goodsAddonSupporter.discount(DiscountType.FULL, userId, key, totalAmount)) != null) {
            discountMap.put(DiscountType.FULL, fullDiscount);
        }
        //会员
        if (userId == null) {
            return discountMap;
        }
        // 会员价，获取当前用户会员等级折扣, 取商品第一个sku计算
        MemberAggregationInfoVO member = userRpcService.getTopMemberCardInfo(userId);
        ProductDiscountVO vipDiscount = new ProductDiscountVO()
                .setDiscount(0L);
        //是否是付费会员
        boolean isVip = false;
        //会员折扣描述
        String discountDesc = null;
        if (member != null) {
            MemberAggregationInfoVO.ProductDiscount memberDiscount = member.memberProductDiscount(totalAmount);
            vipDiscount.setDiscount(memberDiscount.getDiscountAmount());
            discountDesc = memberDiscount.getDiscountDesc();
            isVip = MemberType.PAID_MEMBER == member.getMemberType();
        }
        discountMap.put(
                DiscountType.VIP,
                vipDiscount.setData(
                        new JSONObject()
                                .putOpt("isVip", isVip)
                                .putOpt("discountDesc", discountDesc)
                )
        );
        return discountMap;
    }

    /**
     * 预计赚数据(分销、返利)
     *
     * @param userId         当前登录的用户 id
     * @param key            商品 key
     * @param totalAmount    当前商品总额
     * @param deductionRatio 服务费比率
     * @return 预计赚数据
     */
    private Map<EarningType, Long> earningMap(
            Long userId, ShopProductKey key, Long totalAmount, Long deductionRatio
    ) {
        Map<EarningType, Long> earningMap = new HashMap<>(CommonPool.NUMBER_TWO);
        EarningParam earningParam = new EarningParam()
                .setUserId(userId)
                .setKey(key)
                .setCurAmount(totalAmount);
        Long distribute = goodsAddonSupporter.earning(EarningType.DISTRIBUTE, earningParam);
        if (distribute != null) {
            earningMap.put(EarningType.DISTRIBUTE, distribute);
        }

        earningParam.setExtra(
                com.alibaba.fastjson2.JSONObject.from(
                        new EstimateRebateDTO()
                                .setAmount(totalAmount)
                                .setServiceFeePercent(ObjectUtil.defaultIfNull(deductionRatio, 0L))
                )
        );
        Long rebate = goodsAddonSupporter.earning(EarningType.REBATE, earningParam);
        if (rebate != null) {
            earningMap.put(EarningType.REBATE, rebate);
        }
        return earningMap;
    }


    /**
     * 获取商品列表 by productParam
     *
     * @param productParam 查询条件
     * @return 商品列表信息
     */
    @Override
    public IPage<ProductVO> getProductList(ProductParam productParam) {
        IPage<ProductVO> result = baseMapper.queryProductList(productParam);

        List<ProductVO> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }

        Set<Long> labIds = records.stream().map(ProductVO::getLabelId)
                .filter(Objects::nonNull).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(labIds)) {
            //填充商品标签
            Map<Long, ProductLabel> labelMap = productLabelService.queryLabelList(labIds);
            for (ProductVO record : records) {
                record.setProductLabel(labelMap.get(record.getLabelId()));
            }
        }
        Map<ActivityShopProductKey, ProductVO> shopProductKeyMap = records.stream()
                .collect(Collectors.toMap(record -> {
                    ActivityShopProductKey key = new ActivityShopProductKey();

                    key.setProductId(record.getId()).setShopId(
                            record.getSellType() != SellType.CONSIGNMENT ? record.getShopId()
                                    : record.getSupplierId()).setActivityType(OrderType.COMMON).setActivityId(0L);
                    return key;
                }, v -> v));
        Map<ActivityShopProductKey, List<StorageSku>> shopProductKeyListMap =
                storageRpcService.productSkuStockBatch(
                        shopProductKeyMap.keySet());
        // 使用流来设置 StorageSkus 和调整价格
        shopProductKeyListMap.forEach((shopProductKey, skuList) -> {
            ProductVO productVO = shopProductKeyMap.get(shopProductKey);
            if (Objects.nonNull(productVO)) {
                productVO.setStorageSkus(skuList);
                if (productVO.getSellType() == SellType.CONSIGNMENT) {
                    // 代销商品重新处理逻辑
                    handleConsignmentPrices(productVO, skuList);
                }
            }
        });

        return result;
    }


    /**
     * 平台查询商品信息
     *
     * @param platformProductParam 查询条件
     * @return 平台商品信息
     */
    @Override
    public Page<PlatformProductVO> queryProductInfoByParam(PlatformProductParam platformProductParam) {
        Page<PlatformProductVO> result = DS.sharding(() -> baseMapper.queryProductInfoByParam(platformProductParam));
        List<PlatformProductVO> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }
        Set<Long> labelIds = records.stream().map(PlatformProductVO::getLabelId).filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(labelIds)) {
            //填充商品标签
            Map<Long, ProductLabel> labelMap = productLabelService.queryLabelList(labelIds);
            for (PlatformProductVO record : records) {
                record.setProductLabel(labelMap.get(record.getLabelId()));
            }
        }
        Set<ActivityShopProductKey> skuKeys = new HashSet<>();
        records.forEach(bean -> {
            //价格
            if (bean.getSalePrices().size() == CommonPool.NUMBER_ONE) {
                bean.setSalePrice(bean.getSalePrices().get(0));
            } else {
                //最高 最低价格
                bean.setMaxPrice(bean.getSalePrices().get(bean.getSalePrices().size() - 1));
                bean.setMinPrice(bean.getSalePrices().get(0));
            }
            ActivityShopProductKey key = new ActivityShopProductKey();
            key.setProductId(bean.getId())
                    .setShopId(bean.getSellType() != SellType.CONSIGNMENT ? bean.getShopId() : bean.getSupplierId())
                    .setActivityType(OrderType.COMMON).setActivityId(0L);
            bean.setActivityShopProductKey(key);
            skuKeys.add(key);
        });

        Map<ActivityShopProductKey, List<StorageSku>> shopProductKeyListMap = storageRpcService.productSkuStockBatch(
                skuKeys);
        // 使用流来设置 StorageSkus 和调整价格
        records.forEach(bean -> {
            List<StorageSku> storageSkus = shopProductKeyListMap.get(bean.getActivityShopProductKey());
//            log.warn(storageSkus.toString());
            bean.setStorageSkus(storageSkus);
            if (bean.getSellType() == SellType.CONSIGNMENT) {
                // 代销商品重新处理逻辑
                handleConsignmentPrices(bean, storageSkus);
            }
        });
        return result;
    }

    /**
     * 根据平台类目Id 获取商品信息List
     *
     * @param platformCategoryParam 商品查询param by平台类目
     * @param levelCategoryList     三级类目
     * @return Page<ApiPlatformProductVO>
     */
    @Override
    public Page<ApiPlatformProductVO> getProductInfoByPlatformCategoryId(List<Long> levelCategoryList,
                                                                         PlatformCategoryParam platformCategoryParam) {
        return DS.sharding(
                () -> baseMapper.queryProductInfoByPlatformCategoryIds(platformCategoryParam, levelCategoryList));
    }


    /**
     * 根据排序type获取店铺商品信息
     *
     * @param apiProductParam apiProductParam
     * @return 商品详情
     */
    @Override
    public Page<ApiProductVO> getProductInfoByParam(ApiProductParam apiProductParam) {
        return this.baseMapper.getProductInfoByParam(apiProductParam);
    }


    /**
     * 根据平台类目三级Id 获取商铺信息List
     *
     * @param platformCategoryParam platformCategoryParam
     * @return Page<ApiPlatformProductVO>
     */
    @Override
    public Page<ApiPlatformProductVO> getProductInfoByPlatformThirdlyCategoryId(
            PlatformCategoryParam platformCategoryParam) {
        Page<ApiPlatformProductVO> productPage = TenantShop.disable(
                () -> this.baseMapper.getProductInfoByPlatformThirdlyCategoryId(platformCategoryParam));
        List<ApiPlatformProductVO> records = productPage.getRecords();
        if (records.isEmpty()) {
            return productPage;
        }
        Map<String, ProductStatisticsVO> productStatisticsMap = storageRpcService.getProductStatisticsMap(
                records.stream().map(product -> new ShopProductKeyDTO().setShopId(product.getShopId())
                        .setProductId(product.getId())).collect(Collectors.toList()));
        records.forEach(record -> record.setStatistics(
                productStatisticsMap.get(RedisUtil.key(record.getShopId(), record.getId()))));

        return productPage;
    }


    /**
     * 店铺状态改变 开启/禁用
     *
     * @param shopsEnableDisable 店铺禁用启用参数
     */
    @Override
    public void shopChange(ShopsEnableDisableDTO shopsEnableDisable) {
        Set<Long> shopIds = shopsEnableDisable.getShopIds();
        if (CollUtil.isEmpty(shopIds)) {
            return;
        }
        Set<String> productCacheKeys = shopIds.stream()
                .flatMap((shopId) -> RedisUtil.keys(GoodsUtil.productCacheKeyPattern(shopId)).stream())
                .collect(Collectors.toSet());
        if (BooleanUtil.isTrue(shopsEnableDisable.getEnable())) {
            //店铺开启 上架店铺因禁用店铺下架的商品并设置缓存
            TenantShop.disable(() -> {
                this.lambdaUpdate().in(Product::getShopId, shopIds).eq(Product::getStatus, ProductStatus.UNUSABLE)
                        .set(Product::getStatus, ProductStatus.SELL_ON).update();
            });
            //启用 也要删除原来商品的换成
            if (CollectionUtil.isNotEmpty(productCacheKeys)) {
                RedisUtil.delete(productCacheKeys);

            }
            return;
        }
        // 店铺禁用 下架店铺正在上架的商品并删除缓存
        RedisUtil.doubleDeletion(() -> TenantShop.disable(() -> {
            LambdaUpdateChainWrapper<Product> updateWrapper = this.lambdaUpdate().in(Product::getShopId, shopIds);
            //如果是删除操作 则直接删除该店铺商品
            if (shopsEnableDisable.getReason() == OperaReason.DELETED) {
                updateWrapper.remove();
                return;
            }
            updateWrapper.eq(Product::getStatus, ProductStatus.SELL_ON).set(Product::getStatus, ProductStatus.UNUSABLE)
                    .update();
        }), () -> {
            if (CollUtil.isNotEmpty(productCacheKeys)) {
                RedisUtil.delete(productCacheKeys);
            }

        });
    }


    /**
     * 获取商品信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品信息
     */
    @Override
    public Product getProductInfo(Long shopId, Long productId) {
        return init(() -> TenantShop.disable(
                        () -> DS.sharding(() -> this.baseMapper.getProductInfoById(productId, Boolean.FALSE, shopId))),
                GoodsUtil.productCacheKey(shopId, productId));
    }

    /**
     * 批量获取商品 信息
     *
     * @param shopProductKeys shopId,productId
     * @return map<{ shopId, productId }, product>
     */
    @Override
    public Map<ShopProductKey, Product> getProductBatch(Set<ShopProductKey> shopProductKeys) {
        if (CollUtil.isEmpty(shopProductKeys)) {
            return Collections.emptyMap();
        }
        List<Product> productCache = this.getProductCache(shopProductKeys);
        Map<ShopProductKey, Product> productKeyMap = new HashMap<>(this.toProductKeyMap(productCache));
        if (productKeyMap.size() == shopProductKeys.size()) {
            //设置商品标签
            setProductLabel(productKeyMap);
            return productKeyMap;
        }
        Set<ShopProductKey> needLoadKeys = shopProductKeys.stream()
                .filter(key -> !productKeyMap.containsKey(key))
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(needLoadKeys)) {
            //设置商品标签
            setProductLabel(productKeyMap);
            return productKeyMap;
        }
        productKeyMap.putAll(
                this.toProductKeyMap(
                        TenantShop.disable(
                                () -> DS.sharding(() -> baseMapper.getProductBatch(needLoadKeys))
                        )
                )
        );
        //设置商品标签
        setProductLabel(productKeyMap);
        return productKeyMap;
    }

    /**
     * 批量获取商品 信息 包含已删除商品
     *
     * @param shopProductKeys shopId,productId
     * @return map<{ shopId, productId }, product>
     */
    @Override
    public Map<ShopProductKey, Product> getProductListBatch(Set<ShopProductKey> shopProductKeys) {
        if (CollUtil.isEmpty(shopProductKeys)) {
            return Collections.emptyMap();
        }
        return this.toProductKeyMap(
                //禁用多租户
                TenantShop.disable(
                        //切换为 sharding datasource
                        () -> DS.sharding(() -> baseMapper.getProductListBatch(shopProductKeys))
                )
        );
    }

    /**
     * 设置商品标签
     *
     * @param productKeyMap 商品map
     */
    private void setProductLabel(Map<ShopProductKey, Product> productKeyMap) {
        Set<Long> labelIds = productKeyMap.values().stream().map(Product::getLabelId).collect(Collectors.toSet());
        if (CollUtil.isEmpty(labelIds)) {
            return;
        }
        List<ProductLabel> productLabels = TenantShop.disable(() -> productLabelService.listByIds(labelIds));
        Map<Long, ProductLabel> productLabelMap = coverProductLabelMap(productLabels);
        productKeyMap.values().forEach(value -> value.setProductLabel(productLabelMap.get(value.getLabelId())));
    }


    /**
     * 商品标签 转 map
     *
     * @param productLabels 商品标签列表
     * @return Map<Long, ProductLabel>
     */
    private Map<Long, ProductLabel> coverProductLabelMap(List<ProductLabel> productLabels) {
        if (CollUtil.isEmpty(productLabels)) {
            return Collections.emptyMap();
        }
        return productLabels.stream().collect(Collectors.toMap(BaseEntity::getId, productLabel -> productLabel));
    }


    /**
     * 获取商品信息
     *
     * @param productSupplier Supplier
     * @param key             redisKey
     * @return Product
     */
    @Override
    public Product init(Supplier<Product> productSupplier, String key) {
        return RedisUtil.
                getCacheMap(Product.class,
                        productSupplier,
                        Duration.ofSeconds(RedisUtil.expireWithRandom(CommonPool.UNIT_CONVERSION_TEN_THOUSAND)),
                        key
                );
    }

    /**
     * 获取店铺在售商品数量
     *
     * @param shopId 店铺id
     * @return 店铺在售商品数量
     */
    @Override
    public Long getShopSalesProductCount(Long shopId) {
        return ISystem.shopId(shopId, () -> this.lambdaQuery().eq(Product::getStatus, ProductStatus.SELL_ON).count());
    }

    /**
     * 获取平台三级类目下商品数量
     *
     * @param thirdIds 平台类目三级ids
     * @return map<平台类目ids, 商品数量>
     */
    @Override
    public List<ProductNumVO> getProductNumByPlatformThirdCategoryId(Set<Long> thirdIds) {
        return TenantShop.disable(() -> DS.sharding(() -> baseMapper.getProductNumByPlatformThirdCategoryId(thirdIds)));
    }


    /**
     * 获取随机商品
     *
     * @param productRandomParam 参数
     * @return 商品
     */
    @Override
    public Page<Product> randomGoods(ProductRandomParam productRandomParam) {
        return TenantShop.disable(() -> DS.sharding(() -> baseMapper.randomGoods(productRandomParam)));
    }

    /**
     * 平台获取商品数量 by status
     *
     * @return List<商品状态数量VO>
     */
    @Override
    public Map<ProductStatus, Long> getGoodsQuantity() {
        HashMap<ProductStatus, Long> map = new HashMap<>(CommonPool.NUMBER_THREE);
        List<ProductStatusQuantityVO> productStatusQuantityList = TenantShop.disable(
                () -> baseMapper.queryGoodsQuantity());
        //获取平台下架商品数量
        Optional<Long> any = productStatusQuantityList.stream()
                .filter(bean -> bean.getStatus() == ProductStatus.PLATFORM_SELL_OFF)
                .map(ProductStatusQuantityVO::getQuantity).findAny();
        Long count = any.orElseGet(() -> Long.valueOf(CommonPool.NUMBER_ZERO));
        //获取商品数量
        Long reduce = productStatusQuantityList.stream().map(ProductStatusQuantityVO::getQuantity)
                .reduce((long) CommonPool.NUMBER_ZERO, Long::sum);
        map.put(ProductStatus.SELL_ON, reduce);
        map.put(ProductStatus.PLATFORM_SELL_OFF, count);

        // 获取供应商商品数量
        Map<String, Integer> supplierGoodsMap = goodsAddonSupporter.countProductsOfAllSupplier();
        Optional.ofNullable(supplierGoodsMap).map(item -> {
            map.put(ProductStatus.SELL_ON, reduce + item.get(GoodsConstant.ADDON_SUPPLIER_NEW_COUNT_PRODUCT_KEY));
            map.put(ProductStatus.PLATFORM_SELL_OFF,
                    count + item.get(GoodsConstant.ADDON_SUPPLIER_IRREGULARITY_PRODUCT_KEY));
            return null;
        });
        return map;
    }

    /**
     * 获取今日新增商品数量
     *
     * @return 今日新增商品数量
     */
    @Override
    public Long getTodayAddGoodsQuantity() {
        Final<Long> box = new Final<>(0L);
        ISecurity.match().ifAnyShopAdmin(secureUser -> box.set(secureUser.getShopId()));
        Long count = TenantShop.disable(() -> baseMapper.queryTodayAddGoodsQuantity(box.get()));
        return count == null ? CommonPool.NUMBER_ZERO : count;
    }


    /**
     * 根据类目id 及类目级别 获取商品信息
     *
     * @param categoryRank 类目级别dto
     * @return Page<ApiProductVO>
     */
    @Override
    public Page<ApiProductVO> getProductInfoByCategoryId(CategoryRankDTO categoryRank) {
        Set<Long> ids = categoryRank.getIds();
        switch (categoryRank.getCategoryLevel()) {
            case LEVEL_1:
                List<ProductCategory> oneCategory = productCategoryService.lambdaQuery()
                        .in(ProductCategory::getParentId, categoryRank.getIds()).list();
                if (CollUtil.isEmpty(oneCategory)) {
                    return null;
                }
                categoryRank.setIds(oneCategory.stream().map(BaseEntity::getId).collect(Collectors.toSet()));
            case LEVEL_2:
                List<ProductCategory> list = productCategoryService.lambdaQuery()
                        .in(ProductCategory::getParentId, categoryRank.getIds()).list();
                ids = list.stream().map(BaseEntity::getId).collect(Collectors.toSet());
                if (ids.size() < CommonPool.NUMBER_ONE) {
                    return null;
                }
                categoryRank.getIds().clear();
        }
        categoryRank.setIds(ids);
        return baseMapper.getProductInfoByCategoryId(categoryRank);
    }

    /**
     * pc端-看了又看
     *
     * @param productRandomParam 参数
     * @return 商品详情-看了又看
     */
    @Override
    public Page<ApiProductLookAndSeeVO> lookAndSeePage(ProductRandomParam productRandomParam) {
        Long shopId = ISystem.shopIdOpt().get();
        productRandomParam.setShopId(shopId);
        Page<Product> productPage = baseMapper.randomGoods(productRandomParam);
        List<Product> records = productPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return null;
        }
        Map<Long, Long> evaluatePerson = orderRpcService.getEvaluatePerson(
                records.stream().map(Product::getId).collect(Collectors.toSet()));
        List<ApiProductLookAndSeeVO> collect = records.stream().map(product -> {
            ApiProductLookAndSeeVO apiProductLookAndSeeVO = new ApiProductLookAndSeeVO();
            apiProductLookAndSeeVO.setProductId(product.getId()).setProductName(product.getName())
                    .setProductPrice(product.getSalePrices().get(CommonPool.NUMBER_ZERO)).setPic(product.getPic())
                    .setEvaluated(Option.of(evaluatePerson.get(product.getId()))
                            .getOrElse(Long.valueOf(CommonPool.NUMBER_ZERO))).setShopId(product.getShopId());
            return apiProductLookAndSeeVO;
        }).collect(Collectors.toList());
        return PageBean.getPage(productRandomParam, collect, productPage.getTotal(), productPage.getPages());
    }

    /**
     * pc端-店铺热销
     *
     * @param shopId 店铺id
     * @param size   查询数量
     * @return 店铺热销商品VO
     */
    @Override
    public List<ProductSaleVolumeVO> shopHotSales(Long shopId, Long size) {
        //todo 销量统计 需要优化 更容易找出排行榜前几位的商品
        // 扫描的数据过多 消耗数据库性能 后面考虑 redis 统计排行榜
        // 暂定查询2倍的数据量  避免商品已被下架的情况
        List<ProductSaleVolumeVO> ranks = storageRpcService.getShopProductSaleVolume(shopId, size * 2);
        if (CollUtil.isEmpty(ranks)) {
            return List.of();
        }
        //获取评价人数 
        Set<Long> productIds = ranks.stream()
                .map(ProductSaleVolumeVO::getProductId)
                .collect(Collectors.toSet());
        //todo 未携带店铺 id 分库分表、代销、采购商品等情况下会出问题
        Map<Long, Long> evaluatePerson = orderRpcService.getEvaluatePerson(productIds);

        Map<Long, Product> productMap = TenantShop.disable(() ->
                        this.lambdaQuery()
                                .select(Product::getId, Product::getName, Product::getPic)
                                .eq(ClientType.CONSUMER == ISystem.clientTypeMust(), Product::getStatus, ProductStatus.SELL_ON)
                                .eq(Product::getShopId, shopId)
                                .in(Product::getId, productIds)
                                .list()
                )
                .stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        return ranks.stream()
                .filter(rank -> productMap.containsKey(rank.getProductId()))
                .limit(size)
                .peek(rank -> {
                    Long productId = rank.getProductId();
                    Product product = productMap.get(productId);
                    rank.setProductName(product.getName())
                            .setPic(product.getPic())
                            .setEvaluated(evaluatePerson.getOrDefault(productId, 0L));
                }).toList();
    }

    /**
     * pc端-热门关注
     *
     * @return 热门关注商品
     */
    @Override
    public List<ApiProductPopularAttentionVO> shopPopularAttention() {
        Long shopId = ISecurity.userMust().getShopId();
        // 根据shopId查询店铺商品的收藏
        List<Long> productIds = userRpcService.getShopProductCollection(shopId);
        IPage<Product> page = new Page<>(CommonPool.NUMBER_ONE, CommonPool.NUMBER_FIVE);
        page = this.lambdaQuery().select(Product::getId, Product::getPic, Product::getName, Product::getSalePrices)
                .eq(Product::getStatus, ProductStatus.SELL_ON)
                .in(CollUtil.isNotEmpty(productIds), Product::getId, productIds).page(page);
        List<Product> productList = page.getRecords();
        if (CollUtil.isEmpty(productList)) {
            return new ArrayList<>();
        }
        // 获取评价人数
        Map<Long, Long> evaluatePersonMap = orderRpcService.getEvaluatePerson(
                new HashSet<>(productList.stream().map(Product::getId).collect(Collectors.toSet())));
        return productList.stream().map(
                product -> new ApiProductPopularAttentionVO()
                        .setProductId(product.getId())
                        .setProductName(product.getName())
                        .setProductPrice(product.getSalePrices().get(CommonPool.NUMBER_ZERO))
                        .setPic(product.getPic())
                        .setEvaluated(evaluatePersonMap.get(product.getId()) == null
                                ? 0L
                                : evaluatePersonMap.get(product.getId()))).collect(Collectors.toList()
        );
    }

    /**
     * 根据平台三级类目ids 获取  ApiProductVO
     *
     * @param categoryRank 类目等级dto
     * @return ApiProductVO
     */
    @Override
    public Page<ApiProductVO> getApiProductInfoByPlatformCategoryId(CategoryRankDTO categoryRank) {
        Page<ApiProductVO> page = TenantShop.disable(
                () -> DS.sharding(() ->
                        baseMapper.getApiProductInfoByPlatformCategoryId(categoryRank)));
        List<ApiProductVO> records = page.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            return page;
        }
        Set<Long> shopIds = records.stream().map(ApiProductVO::getShopId).collect(Collectors.toSet());
        //SKU信息
        Final<Map<ActivityShopProductKey, List<StorageSku>>> storageSkuListMap = new Final<>();
        //店铺信息
        Final<Map<Long, ShopInfoVO>> shopInfoVOMap = new Final<>();

        //优惠券Map集合
        Final<Map<Long, List<GoodsCouponVO>>> couponMap = new Final<>(Map.of());

        CompletableTask.getOrThrowException(
                CompletableTask.allOf(
                        globalExecutor,
                        //SKU信息
                        () -> {
                            Set<ActivityShopProductKey> shopProductKeys = records.stream().map(x -> {
                                ActivityShopProductKey activityShopProductKey = new ActivityShopProductKey();
                                activityShopProductKey.setShopId(x.getShopId());
                                activityShopProductKey.setProductId(x.getId());
                                activityShopProductKey.setActivityType(OrderType.COMMON);
                                activityShopProductKey.setActivityId(0L);
                                return activityShopProductKey;

                            }).collect(Collectors.toSet());
                            storageSkuListMap.set(storageRpcService.productSkuStockBatch(shopProductKeys));
                        },
                        //店铺信息
                        () -> {
                            List<ShopInfoVO> shopInfoByShopIdList = shopRpcService.getShopInfoByShopIdList(shopIds);
                            shopInfoVOMap.set(shopInfoByShopIdList.stream().collect(Collectors.toMap(x -> x.getId(), x -> x)));
                        },
                        //优惠券信息
                        () -> {
                            List<GoodsCouponVO> couponList = goodsAddonSupporter.getCouponListForProduct(shopIds);
                            if (CollUtil.isNotEmpty(couponList)) {
                                couponMap.set(couponList.stream().collect(Collectors.groupingBy(GoodsCouponVO::getShopId)));
                            }
                        }

                )
        );

        records.stream().map(x -> {
            ShopInfoVO shopInfoVO = shopInfoVOMap.get().get(x.getShopId());
            if (Objects.nonNull(shopInfoVO)) {
                x.setShopType(shopInfoVO.getShopType());
            }
            if (Objects.nonNull(x.getFreightTemplateId()) && x.getFreightTemplateId().equals(0L)) {
                x.setFreeShipping(Boolean.TRUE);
            }
            List<StorageSku> storageSkus = storageSkuListMap.get().get(new ActivityShopProductKey()
                    .setProductId(x.getId())
                    .setShopId(x.getShopId())
                    .setActivityType(OrderType.COMMON)
                    .setActivityId(0L));
            if (CollectionUtil.isNotEmpty(storageSkus)) {
                List<String> specImages = storageSkus.stream().map(StorageSku::getImage).collect(Collectors.toList());
                x.setSpecImages(specImages);
            }
            x.setCoupons(couponMap.get().get(x.getShopId()));


            return x;
        }).toList();
        return page;
    }

    /**
     * 分页获取商品和规格信息
     *
     * @param productParam 商品查询参数
     * @return 商品规格信息
     */
    @Override
    public IPage<ProductSkusVO> getProductSkus(ProductParam productParam) {
        Page<Product> productPage = DS.sharding(() ->
                this.lambdaQuery()
                        .select(Product::getId, Product::getName,
                                Product::getPic, Product::getSalePrice,
                                Product::getShopId)
                        .eq(Product::getStatus, ProductStatus.SELL_ON)
                        .eq(productParam.getCategoryId() != null, Product::getCategoryId, productParam.getCategoryId())
                        .le(productParam.getLowestPrice() != null, Product::getSalePrice, productParam.getLowestPrice())
                        .ge(productParam.getHighestPrice() != null, Product::getSalePrice,
                                productParam.getHighestPrice())
                        .like(productParam.getName() != null, Product::getName, productParam.getName())
                        .page(new Page<>(productParam.getCurrent(), productParam.getSize()))
        );
        List<Product> productList = productPage.getRecords();
        if (CollUtil.isEmpty(productList)) {
            return null;
        }
        List<ShopProductKeyDTO> shopProductKeys = productList.stream()
                .map(product -> new ShopProductKeyDTO().setShopId(product.getShopId()).setProductId(product.getId()))
                .collect(Collectors.toList());
        // 获取sku信息
        List<ProductSkusVO.SkuVO> skus = storageRpcService.getProductSkusByShopProductKeys(shopProductKeys);
        Map<Long, List<ProductSkusVO.SkuVO>> productSkuMap = skus.stream()
                .collect(Collectors.groupingBy(ProductSkusVO.SkuVO::getProductId));
        //类型转换        
        return productPage.convert(
                product -> {
                    List<ProductSkusVO.SkuVO> skuVos = productSkuMap.get(product.getId());
                    Long lowestPrice =
                            skuVos != null ? skuVos.stream().min(Comparator.comparing(ProductSkusVO.SkuVO::getSkuPrice)).get()
                                    .getSkuPrice() : 0L;
                    Long highestPrice =
                            skuVos != null ? skuVos.stream().max(Comparator.comparing(ProductSkusVO.SkuVO::getSkuPrice))
                                    .get()
                                    .getSkuPrice() : 0L;
                    ProductSkusVO productSkusVO = new ProductSkusVO();
                    productSkusVO.setProductId(product.getId()).setProductName(product.getName())
                            .setProductPic(product.getPic()).setCategoryId(product.getCategoryId())
                            .setShopId(product.getShopId()).setLowestPrice(lowestPrice).setHighestPrice(highestPrice)
                            .setSkus(skuVos);
                    return productSkusVO;
                }
        );
    }


    /**
     * 保存供应商商品评分
     *
     * @param orderCompleted 订单完成数据
     */
    @Override
    public void saveSupplierProductRate(OrderCompletedDTO orderCompleted) {
        List<OrderEvaluate> orderEvaluates = orderCompleted.getOrderEvaluates();
        if (CollUtil.isEmpty(orderEvaluates)) {
            return;
        }
        Long shopId = orderCompleted.getShopId();
        Set<Long> productIds = orderEvaluates.stream().map(OrderEvaluate::getProductId).collect(Collectors.toSet());
        //批量查询
        Map<Long, Long> productMap = TenantShop.disable(
                        () -> this.lambdaQuery()
                                .select(Product::getProviderId)
                                .eq(Product::getShopId, shopId)
                                .isNotNull(Product::getProviderId)
                                .in(BaseEntity::getId, productIds)
                                .list()
                ).stream()
                .collect(Collectors.toMap(BaseEntity::getId, Product::getProviderId));
        List<SupplierRateRecord> supplierRateRecordList = new ArrayList<>(orderEvaluates.size());
        orderEvaluates.forEach(bean -> {
            Long providerId = productMap.get(bean.getProductId());
            if (providerId == null) {
                return;
            }
            supplierRateRecordList.add(
                    new SupplierRateRecord()
                            .setProductId(bean.getId())
                            .setRate(bean.getRate())
                            .setShopId(bean.getShopId())
                            .setSupplierId(providerId)
            );
        });
        supplierRateRecordService.saveBatch(supplierRateRecordList);
    }

    /**
     * 获取条件商品信息 包含以删除商品信息
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @return 商品信息
     */
    @Override
    public Product getConditionProductInfo(Long shopId, Long productId) {
        return ISystem.shopId(shopId, () -> {
            Product productInfo = this.getProductInfo(shopId, productId);
            if (productInfo == null) {
                productInfo = DS.sharding(() -> this.baseMapper.getConditionProductInfo(shopId, productId));
            }
            return productInfo;
        });
    }

    @Override
    public List<ProductDeliveryVO> getProductDelivery(List<ShopProductSkuIdDTO> shopProductSkuIds) {
        Map<Long, List<Long>> shopProductIdMap = shopProductSkuIds.stream().collect(
                Collectors.groupingBy(ShopProductSkuIdDTO::getShopId,
                        Collectors.mapping(ShopProductSkuIdDTO::getProductId, Collectors.toList())));
        Set<Long> productIds = shopProductIdMap.values().stream().flatMap(Collection::stream)
                .collect(Collectors.toSet());
        List<Product> products = TenantShop.disable(() ->
                DS.sharding(() ->
                        this.lambdaQuery()
                                .select(Product::getId, Product::getShopId, Product::getFreightTemplateId,
                                        Product::getDistributionMode)
                                .in(Product::getId, productIds).in(Product::getShopId, shopProductIdMap.keySet())
                                .eq(Product::getStatus, ProductStatus.SELL_ON)
                                .list()
                )
        );
        if (CollectionUtils.isEmpty(products)) {
            return Collections.emptyList();
        }
        Map<ActivityShopProductKey, List<StorageSku>> activityShopProductKeyListMap =
                storageRpcService.productSkuStockBatch(
                        products.stream().map(product -> {
                            ActivityShopProductKey activityShopProductKey = new ActivityShopProductKey();
                            activityShopProductKey.setProductId(product.getId());
                            activityShopProductKey.setShopId(product.getShopId());
                            activityShopProductKey.setActivityId(0L);
                            activityShopProductKey.setActivityType(OrderType.COMMON);
                            return activityShopProductKey;
                        }).collect(Collectors.toSet()));
        Map<Long, Product> productMap = products.stream().collect(Collectors.toMap(Product::getId, v -> v));
        Map<Long, BigDecimal> skuWeightMap = new HashMap<>(shopProductSkuIds.size());
        if (CollUtil.isNotEmpty(activityShopProductKeyListMap)) {
            skuWeightMap = activityShopProductKeyListMap.values().stream().flatMap(Collection::stream).toList().stream()
                    .collect(Collectors.toMap(StorageSku::getId, StorageSku::getWeight));
        }
        Map<Long, BigDecimal> finalSkuWeightMap = skuWeightMap;
        return shopProductSkuIds.stream().filter(shopProduct -> productMap.get(shopProduct.getProductId()) != null)
                .map(shopProductSkuId -> {
                    Long productId = shopProductSkuId.getProductId();
                    Long skuId = shopProductSkuId.getSkuId();
                    Product product = productMap.get(productId);
                    return new ProductDeliveryVO().setShopId(shopProductSkuId.getShopId()).setProductId(productId)
                            .setDistributionMode(product.getDistributionMode())
                            .setFreightTemplateId(product.getFreightTemplateId()).setSkuId(skuId)
                            .setWeight(finalSkuWeightMap.get(skuId));
                }).toList();
    }

    /**
     * 查询当前签约类目商品是否存在
     *
     * @param signingCategorySecondIds 二级签约类目ids
     * @param shopId                   店铺id
     * @return 是否存在
     */
    @Override
    public boolean getSigningCategoryProduct(Set<Long> signingCategorySecondIds, Long shopId) {
        return this.baseMapper.querySigningCategoryProduct(signingCategorySecondIds, shopId);
    }

    /**
     * 商品名称修改
     *
     * @param productId 商品id
     * @param name      商品名称
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductName(Long productId, String name) {
        Long shopId = ISecurity.userMust().getShopId();
        //已删除商品更新名称，只单独更新数据库
        Product conditionProduct = TenantShop.disable(() -> this.baseMapper.getConditionProductInfo(shopId, productId));
        if (conditionProduct == null) {
            throw GoodsError.CURRENT_GOODS_NOT_EXIST.exception();
        }
        conditionProduct.setName(name);
        RedisUtil.doubleDeletion(
                () -> TenantShop.disable(() -> this.baseMapper.updateProductName(conditionProduct)),
                RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, shopId, productId)
        );
        rabbitTemplate.convertAndSend(
                GoodsRabbit.GOODS_NAME_UPDATE.exchange(),
                GoodsRabbit.GOODS_NAME_UPDATE.routingKey(),
                new ProductNameUpdateDTO()
                        .setKey(new ShopProductKey(shopId, productId))
                        .setName(name)
        );

    }

    /**
     * 获取商品库存基础信息
     *
     * @param param 检索条件
     * @return IPage<ProductStockVO>
     */

    @Override
    public IPage<ProductStockVO> getProductStockBaseInfo(ProductStockParam param) {
        Long shopId = ISystem.shopIdMust();
        List<Long> excludeProductIds = Lists.newArrayList();
        ActivityShopProductKey activityKey = new ActivityShopProductKey();
        activityKey.setShopId(shopId).setActivityType(OrderType.COMMON)
                .setActivityId((long) CommonPool.NUMBER_ZERO);
        if (Lists.newArrayList(StockChangeType.ALLOCATION_OUTBOUND, StockChangeType.OTHER_OUTBOUND)
                .contains(param.getStockChangeType())) {
            //调拨出库、其它出库 过滤掉库存为0(有限库存)的商品 过滤掉无限库存的商品
            excludeProductIds = storageRpcService.getShopEmptyStockProIds(activityKey, Boolean.TRUE, Boolean.TRUE);

        }
        if (Lists.newArrayList(StockChangeType.ALLOCATION_INBOUND,
                        StockChangeType.OTHER_INBOUND)
                .contains(param.getStockChangeType())) {
            //调拨入库 其它入库  将【无限库存】的商品从列表中剔除掉
            excludeProductIds = storageRpcService.getShopEmptyStockProIds(activityKey, Boolean.FALSE, Boolean.TRUE);
        }
        //新增盘点
        if (param.getAddInventory()) {
            //新增盘点 过滤掉无限库存的商品
            excludeProductIds = storageRpcService.getShopEmptyStockProIds(activityKey, Boolean.FALSE, Boolean.TRUE);
        }

        IPage<ProductStockVO> result = this.baseMapper.queryProductStockBaseInfo(param, excludeProductIds);
        List<ProductStockVO> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }
        Map<ActivityShopProductKey, ProductStockVO> shopProductKeyMap = records.stream()
                .collect(Collectors.toMap(record -> {
                    ActivityShopProductKey key = new ActivityShopProductKey();
                    key.setProductId(record.getId()).setShopId(record.getShopId()).setActivityType(OrderType.COMMON)
                            .setActivityId(0L);
                    return key;
                }, v -> v));
        Map<ActivityShopProductKey, List<StorageSku>> shopProductKeyListMap = storageRpcService.productSkuStockBatch(
                shopProductKeyMap.keySet());
        shopProductKeyMap.forEach((shopProductKey, productStockVO) -> productStockVO.setStorageSkus(
                shopProductKeyListMap.get(shopProductKey)));

        return result;
    }

    /**
     * 获取采购发布商品VO信息
     *
     * @param param 查询param
     * @return IPage<SupplierIssueProductListVO>
     */
    @Override
    public IPage<SupplierIssueProductListVO> getPurchaseIssueProducts(PurchaseProductParam param) {
        // 全部 跟下架 查询删除的商品
        if (param.getStatus() == null || param.getStatus() == ProductStatus.SELL_OFF) {
            param.setIsDeleted(Boolean.TRUE);
        }

        IPage<SupplierIssueProductListVO> page = this.baseMapper.queryPurchaseIssueProducts(param);
        List<SupplierIssueProductListVO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return page;
        }
        supplierGoods(records);
        return page;
    }


    /**
     * 供应商商品状态更新
     *
     * @param supplierGoodsUpdateStatus 更新的商品数据
     */
    @Override
    public void supplierGoodsUpdateStatus(SupplierGoodsUpdateStatusDTO supplierGoodsUpdateStatus) {
        ProductStatus productStatus = supplierGoodsUpdateStatus.getProductUpdateStatus()
                .get(0)
                .getProductStatus();
        List<ProductUpdateStatusDTO> updateStatusList = supplierGoodsUpdateStatus.getProductUpdateStatus()
                .stream()
                .map(item -> {
                    List<Product> productList = new ArrayList<>();
                    Set<ProductStatus> productStatuses = switch (productStatus) {
                        case SELL_ON -> Set.of(ProductStatus.SUPPLIER_SELL_OFF);
                        case SELL_OFF ->
                                Set.of(ProductStatus.SELL_ON, ProductStatus.SELL_OFF, ProductStatus.PLATFORM_SELL_OFF);
                        case PLATFORM_SELL_OFF -> Set.of(ProductStatus.SELL_OFF, ProductStatus.SELL_ON,
                                ProductStatus.SUPPLIER_SELL_OFF, ProductStatus.SUPPLIER_DISABLE);
                        default -> Set.of();
                    };
                    if (CollUtil.isNotEmpty(productStatuses)) {
                        productList = TenantShop.disable(
                                () -> lambdaQuery().select(Product::getId, Product::getShopId, Product::getStatus)
                                        .in(Product::getId, item.getProductIds())
                                        .in(Product::getStatus, productStatuses)
                                        .eq(Product::getSellType, SellType.CONSIGNMENT)
                                        .eq(Product::getSupplierId, item.getShopId()).list());
                    }
                    return productList;
                }).filter(CollUtil::isNotEmpty).flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Product::getShopId)).entrySet().stream()
                .map(entry -> new ProductUpdateStatusDTO().setShopId(entry.getKey())
                        .setProductIds(entry.getValue().stream().map(Product::getId).collect(Collectors.toSet()))
                        .setProductStatus(
                                productStatus == ProductStatus.SELL_ON ? ProductStatus.SELL_OFF : productStatus))
                .collect(Collectors.toList());
        handlerGoodsStatus(productStatus == ProductStatus.SELL_OFF ? ProductStatus.SUPPLIER_SELL_OFF
                        : productStatus == ProductStatus.SELL_ON ? ProductStatus.SELL_OFF : productStatus,
                supplierGoodsUpdateStatus.getProductViolation(), updateStatusList, null);
    }


    @Override
    public void supplierForceGoodsStatus(Set<ShopProductKey> keys) {
        if (CollUtil.isEmpty(keys)) {
            return;
        }
        boolean hashProductId = keys.iterator().next().getProductId() != null;
        Set<ShopProductKey> shopProductKeys = baseMapper.queryGoodsByProductIds(hashProductId, keys);
        if (CollUtil.isEmpty(shopProductKeys)) {
            return;
        }
        Set<String> cacheKeys = shopProductKeys.stream()
                .map(shopProductKey -> GoodsUtil.productCacheKey(shopProductKey.getShopId(),
                        shopProductKey.getProductId())).collect(Collectors.toSet());
        RedisUtil.doubleDeletion(() -> baseMapper.supplierForceGoodsStatus(shopProductKeys),
                () -> RedisUtil.delete(cacheKeys));
        Map<Long, List<ShopProductKey>> collect = shopProductKeys.stream()
                .collect(Collectors.groupingBy(ShopProductKey::getShopId));
        collect.forEach((key, value) -> globalExecutor.execute(
                () -> rabbitTemplate.convertAndSend(GoodsRabbit.GOODS_DELETE.exchange(),
                        GoodsRabbit.GOODS_DELETE.routingKey(), new ProductDeleteDTO().setShopId(key).setProductIds(
                                value.stream().map(ShopProductKey::getProductId).collect(Collectors.toSet())))));
    }

    /**
     * 供应商商品信息修改
     *
     * @param supplierProduct 商品信息
     */
    @Override
    public void supplierUpdateGoods(Product supplierProduct) {
        List<Product> products = TenantShop.disable(
                () -> lambdaQuery().eq(Product::getSupplierId, supplierProduct.getShopId())
                        .eq(Product::getSellType, SellType.CONSIGNMENT).eq(Product::getId, supplierProduct.getId())
                        .list());
        if (CollUtil.isEmpty(products)) {
            return;
        }
        products.forEach(product -> {
            handlerSupplierGoods(product, supplierProduct);
            Integer update = TenantShop.disable(() -> RedisUtil.doubleDeletion(() -> baseMapper.update(product,
                            Wrappers.lambdaQuery(Product.class).eq(Product::getId, product.getId())
                                    .eq(Product::getShopId, product.getShopId())),
                    RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, product.getShopId(), product.getId())));
            GoodsError.PRODUCT_UPDATE_FAIL.trueThrow(update < CommonPool.NUMBER_ONE);
            sendProductBroadcast(product.getExtra().getShopCategory(), product.getExtra().getPlatformCategory(),
                    product, GoodsRabbit.GOODS_UPDATE.exchange(), GoodsRabbit.GOODS_UPDATE.routingKey());
        });

    }

    /**
     * 获取代销商品信息
     *
     * @param id 商品id
     */
    @Override
    public ProductVO getConsignmentProductInfo(Long id) {
        ProductVO productVO = new ProductVO();
        Product product = this.lambdaQuery().select(
                        Product::getName,
                        Product::getExtra,
                        BaseEntity::getId,
                        Product::getShopId,
                        Product::getSupplierId,
                        Product::getProductType,
                        Product::getSellType,
                        Product::getWidePic,
                        Product::getVideoUrl,
                        Product::getSaleDescribe,
                        Product::getStatus,
                        Product::getSalePrices,
                        Product::getLabelId
                )
                .eq(BaseEntity::getId, id)
                .eq(Product::getShopId, ISystem.shopIdOpt().get())
                .ne(Product::getStatus, ProductStatus.SUPPLIER_SELL_OFF).one();
        GoodsError.CURRENT_CONSIGNMENT_PRODUCT_NOT_EXIST_OR_EXCEPTION.trueThrow(product == null);
        BeanUtil.copyProperties(product, productVO);
        Set<ShopProductKey> shopProductKeys = new HashSet<>();
        shopProductKeys.add(new ShopProductKey().setProductId(product.getId()).setShopId(product.getSupplierId()));
        List<StorageSpecSkuDTO> storageSpecSku = shopProductKeys.stream()
                .map(key -> storageRpcService.getStorageSpecSku(Collections.singleton(key))).findFirst()
                .orElse(Collections.emptyList());
        productVO.setStorageSpecSku(storageSpecSku);
        return productVO;
    }

    /**
     * 代销商品修改
     *
     * @param consignmentProduct 代销商品修改DTO
     */
    @Override
    public void consignmentProductUpdate(ConsignmentProductDTO consignmentProduct) {
        Long shopId = ISecurity.userMust().getShopId();
        consignmentProduct.getConsignmentPriceSetting().validParam();
        Product product = checkoutProductInfo(consignmentProduct.getId(), shopId, Boolean.FALSE);
        if (product.getSellType() != SellType.CONSIGNMENT && product.getStatus() == ProductStatus.SUPPLIER_DISABLE) {
            throw new GlobalException("商品异常");
        }
        List<StorageSpecSkuDTO> storageSpecSku = storageRpcService.getStorageSpecSku(Collections.singleton(
                new ShopProductKey().setProductId(product.getId()).setShopId(product.getSupplierId())));
        List<Long> sortedSalePrices = storageSpecSku.get(CommonPool.NUMBER_ZERO).getSkus().stream()
                .map(SkuDTO::getSalePrice).toList();
        ConsignmentPriceSettingDTO consignmentPriceSetting = consignmentProduct.getConsignmentPriceSetting();
        //代销商品修改商品价格
        boolean isRegular = consignmentPriceSetting.getType() == PricingType.REGULAR;
        sortedSalePrices = sortedSalePrices.stream()
                .map(sortedSalePrice -> sortedSalePrice + (isRegular ? consignmentPriceSetting.getSale()
                        : (sortedSalePrice * consignmentPriceSetting.getSale() / 1000000))).toList();
        //代销商品修改商品价格
        ProductExtraDTO extra = product.getExtra();
        product.setName(consignmentProduct.getName());
        product.setSalePrice(sortedSalePrices.get(CommonPool.NUMBER_ZERO));
        product.setSalePrices(sortedSalePrices);
        product.setCategoryId(consignmentProduct.getShopCategory().getTwo());
        product.setExtra(extra.setConsignmentPriceSetting(consignmentProduct.getConsignmentPriceSetting())
                .setShopCategory(consignmentProduct.getShopCategory()));
        product.setSaleDescribe(consignmentProduct.getSaleDescribe());
        product.setLabelId(consignmentProduct.getLabelId());
        updateProduct2DB(product, extra);
    }

    /**
     * 商品复制
     *
     * @param product         商品
     * @param supplierProduct 修改商品的信息
     */
    private void handlerSupplierGoods(Product product, Product supplierProduct) {
        ConsignmentPriceSettingDTO consignmentPriceSetting = product.getExtra().getConsignmentPriceSetting();
        //代销商品修改商品价格
        boolean isRegular = consignmentPriceSetting.getType() == PricingType.REGULAR;
        List<Long> salePrices = supplierProduct.getSalePrices().stream()
                .map(sortedSalePrice -> isRegular ? consignmentPriceSetting.getSale()
                        : (sortedSalePrice * consignmentPriceSetting.getSale() / 1000000)).toList();
        product.setAlbumPics(supplierProduct.getAlbumPics()).setDetail(supplierProduct.getDetail())
                .setPic(supplierProduct.getPic()).setSalePrices(salePrices).setSalePrice(salePrices.get(0))
                .setVideoUrl(supplierProduct.getVideoUrl()).setBrandId(supplierProduct.getBrandId())
                .setWidePic(supplierProduct.getWidePic()).setFreightTemplateId(supplierProduct.getFreightTemplateId())
                .setExtra(product.getExtra()
                        .setSupplierCustomDeductionRatio(supplierProduct.getExtra().getCustomDeductionRatio())
                        .setProductParameters(supplierProduct.getExtra().getProductParameters())
                        .setProductAttributes(supplierProduct.getExtra().getProductAttributes()));
    }

    /**
     * 获取缓存中的商品
     *
     * @param shopProductKeys ShopProductKey
     * @return List<Product>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private List<Product> getProductCache(Set<ShopProductKey> shopProductKeys) {
        //获取缓存中的商品
        List<Object> caches = RedisUtil.executePipelined(redisOperations -> {
            HashOperations hashOperations = redisOperations.opsForHash();
            for (ShopProductKey shopProductKey : shopProductKeys) {
                hashOperations.entries(
                        GoodsUtil.productCacheKey(shopProductKey.getShopId(), shopProductKey.getProductId()));
            }
        });
        return FastJson2.convert(caches.stream().filter(cache -> {
            if (!(cache instanceof Map map)) {
                return false;
            }
            return !map.isEmpty();
        }).toList(), new TypeReference<>() {
        });
    }

    /**
     * 获取已发布的供应商商品  采购 正常返回   代销要计算相关金额数据
     *
     * @param records 供应商商品信息
     */
    private void supplierGoods(List<SupplierIssueProductListVO> records) {
        if (!records.isEmpty()) {
            //铺货员id
            Set<Long> deliverUserIds = records.stream().map(x -> x.getExtra().getDeliveryUserId())
                    .collect(Collectors.toSet());
            Map<Long, UserInfoVO> userDataInfoMap = uaaRpcService.getUserDataBatchByUserIds(deliverUserIds);

            Set<Long> supplierIds = records.stream().map(SupplierIssueProductListVO::getSupplierId)
                    .collect(Collectors.toSet());
            List<ShopInfoVO> shopInfoByShopIdList = shopRpcService.getShopInfoByShopIdList(supplierIds);
            Map<Long, ShopInfoVO> shopInfoMap = shopInfoByShopIdList.stream()
                    .collect(Collectors.toMap(ShopInfoVO::getId, x -> x));
            records.forEach(bean -> {
                UserInfoVO userInfoVO = userDataInfoMap.get(bean.getExtra().getDeliveryUserId());
                if (Objects.nonNull(userInfoVO)) {
                    bean.getExtra().setDeliveryUserName(userInfoVO.getNickname());
                }
                Long supplierId = bean.getSupplierId();
                ShopInfoVO shopInfoVO = shopInfoMap.get(supplierId);
                if (Objects.nonNull(shopInfoVO)) {
                    bean.setSupplierName(shopInfoVO.getName());
                    bean.setSupplierContractNumber(shopInfoVO.getContractNumber());

                }

            });
        }
        Map<ActivityShopProductKey, SupplierIssueProductListVO> shopProductKeyMap = records.stream()
                .collect(Collectors.toMap(record -> {
                    ActivityShopProductKey key = new ActivityShopProductKey();
                    key.setProductId(record.getId()).setShopId(
                                    record.getSellType() == SellType.CONSIGNMENT ? record.getSupplierId() :
                                            record.getShopId())
                            .setActivityType(OrderType.COMMON).setActivityId(0L);
                    return key;
                }, v -> v));
        Map<ActivityShopProductKey, List<StorageSku>> shopProductKeyListMap = storageRpcService.productSkuStockBatch(
                shopProductKeyMap.keySet());

        // 使用流来设置 StorageSkus 和调整价格
        shopProductKeyListMap.forEach((shopProductKey, skuList) -> {
            SupplierIssueProductListVO supplierIssueProductListVO = shopProductKeyMap.get(shopProductKey);
            supplierIssueProductListVO.setStorageSkus(skuList);
            //填充salePrices字段
            if (CollectionUtil.isNotEmpty(skuList)) {
                supplierIssueProductListVO.setSalePrices(
                        skuList.stream().map(StorageSku::getSalePrice).sorted().collect(Collectors.toList()));
            }
            if (supplierIssueProductListVO.getSellType() == SellType.CONSIGNMENT) {
                // 代销商品重新处理逻辑
                handleConsignmentPrices(supplierIssueProductListVO, skuList);
            }

        });

    }

    /**
     * 采购发布商品修改状态
     *
     * @param id 商品id
     */
    @Override
    public void purchaseIssueProductUpdateStatus(Long id) {
        Long shopId = ISystem.shopIdOpt().get();
        Product productInfo = this.baseMapper.getProductInfoById(id, Boolean.TRUE, shopId);
        GoodsError.CURRENT_GOODS_NOT_EXIST.trueThrow(productInfo == null);
        assert productInfo != null;
        GoodsError.CURRENT_PRODUCT_NOT_AVAILABLE.trueThrow(
                productInfo.getSellType() != SellType.PURCHASE || productInfo.getStatus() != ProductStatus.SELL_OFF);
        supplierSellOnGoodsHandler(productInfo);

    }


    /**
     * 采购商品处理
     *
     * @param productInfo 商品信息
     */
    private void supplierSellOnGoodsHandler(Product productInfo) {
        Boolean deleted = productInfo.getDeleted();

        this.baseMapper.updateSupplierSellGoods(productInfo.getId(), productInfo.getShopId());

        /*  更新es
         */
        if (deleted) {
            // 确保更新商品状态为未删除状态
            productInfo.setDeleted(Boolean.FALSE);
            productInfo.setStatus(ProductStatus.SELL_ON);
            globalExecutor.execute(() -> rabbitTemplate.convertAndSend(GoodsRabbit.GOODS_RELEASE.exchange(),
                    GoodsRabbit.GOODS_RELEASE.routingKey(), new ProductBroadcastDTO().setProduct(productInfo)
                            .setShopCategory(productInfo.getExtra().getShopCategory())
                            .setPlatformCategory(productInfo.getExtra().getPlatformCategory())));
            return;
        }
        ProductUpdateStatusDTO updateStatus = new ProductUpdateStatusDTO();
        updateStatus.setShopId(productInfo.getShopId()).setProductStatus(ProductStatus.SELL_ON)
                .setProductIds(Collections.singleton(productInfo.getId()));
        List<ProductUpdateStatusDTO> updateStatusList = Collections.singletonList(updateStatus);
        globalExecutor.execute(() -> rabbitTemplate.convertAndSend(GoodsRabbit.GOODS_UPDATE_STATUS.exchange(),
                GoodsRabbit.GOODS_UPDATE_STATUS.routingKey(), updateStatusList));
    }


    /**
     * 已铺货代销商品
     *
     * @param purchaseProductParam 查询参数
     */
    @Override
    public IPage<SupplierIssueProductListVO> getPaveGoods(PurchaseProductParam purchaseProductParam) {
        IPage<SupplierIssueProductListVO> page = baseMapper.getPaveGoods(purchaseProductParam);
        List<SupplierIssueProductListVO> records = page.getRecords();
        if (CollUtil.isEmpty(records)) {
            return page;
        }
        supplierGoods(records);
        return page;
    }

    /**
     * 代销商品修改状态
     *
     * @param productId 商品id
     */
    @Override
    public void consignmentProductUpdateStatus(Long productId) {
        Long shopId = ISystem.shopIdOpt().get();
        Product productInfo = this.baseMapper.getProductInfoById(productId, Boolean.TRUE, shopId);
        if (productInfo == null) {
            throw new GlobalException("当前商品不存在");
        }
        if (!validConsignmentProductStatus(productInfo.getStatus())) {
            throw new GlobalException("当前商品不可进行上架操作");
        }
        supplierSellOnGoodsHandler(productInfo);
    }

    /**
     * 根据{@code supplierId}和{@code productId}获取商品信息
     *
     * @param supplierId 供应商ID
     * @param productId  商品id
     * @return {@link Product}
     */
    @Override
    public Product getProductBySupplierIdAndProductId(Long supplierId, Long productId) {
        List<Product> products = this.baseMapper.getProductBySupplierIdAndProductId(supplierId, productId,
                Boolean.TRUE);
        if (CollectionUtils.isEmpty(products)) {
            return null;
        }
        return products.get(0);
    }

    /**
     * B端商品查看功能 商品基础信息
     *
     * @param id     商品id
     * @param shopId 店铺id
     * @return 查看商品基础信息
     */
    @Override
    public LookProductVO getLookProductInfo(Long id, Long shopId) {
        LookProductVO lookProductVO = ISystem.shopId(shopId, () -> this.baseMapper.queryLookProductInfo(id, shopId));

        GoodsError.CURRENT_GOODS_NOT_EXIST.trueThrow(lookProductVO == null);

        assert lookProductVO != null;
        ProductExtraDTO extra = lookProductVO.getExtra();
        CategoryLevelName categoryLevelName = ISystem.shopId(shopId,
                () -> productCategoryService.getCategoryLevelName(extra.getShopCategory()));
        CategoryLevelName platfromCategoryLevelName = goodsAddonSupporter.getPlatformCategoryLevelName(
                extra.getPlatformCategory());

        lookProductVO.setShopCategoryName(categoryLevelName);
        lookProductVO.setPlatformCategoryName(platfromCategoryLevelName);

        Long queryShopId = shopId;
        if (SellType.CONSIGNMENT.equals(lookProductVO.getSellType())) {
            //代销商品 查询库存时店铺的ID设置成供应商ID 采购和自有的店铺ID都是当前店铺ID
            queryShopId = lookProductVO.getSupplierId();
        }
        ActivityShopProductKey activityKey = (ActivityShopProductKey) new ActivityShopProductKey()
                .setProductId(lookProductVO.getId())
                .setShopId(queryShopId)
                .setActivityId(0L).setActivityType(OrderType.COMMON);
        Map<ActivityShopProductKey, List<StorageSku>> activityShopProductKeyListMap =
                storageRpcService.productSkuStockBatch(
                        Collections.singleton(activityKey));
        List<StorageSku> storageSkus = activityShopProductKeyListMap.get(activityKey);
        if (lookProductVO.getSupplierId() != null) {
            handleConsignmentPrices(lookProductVO, storageSkus);
        }
        lookProductVO.setStorageSkus(storageSkus);

        //商品标签
        if (null != lookProductVO.getLabelId()) {
            lookProductVO.setProductLabel(
                    TenantShop.disable(
                            () -> productLabelService.lambdaQuery().eq(ProductLabel::getId, lookProductVO.getLabelId())
                                    .one())
            );
        }
        return lookProductVO;
    }


    /**
     * 获取审核商品列表信息
     *
     * @param auditProductParam 审核商品列表查询param
     * @return AuditProductVO
     */
    @Override
    public IPage<AuditProductVO> getAuditProductList(AuditProductParam auditProductParam) {
        if (auditProductParam.getShopId() == null) {
            auditProductParam.setShopId(ISystem.shopIdOpt().get());
        }
        IPage<AuditProductVO> auditProductList = TenantShop.disable(
                () -> this.baseMapper.queryAuditProductList(auditProductParam));
        List<AuditProductVO> records = auditProductList.getRecords();
        if (CollUtil.isEmpty(records)) {
            return auditProductList;
        }
        records.forEach(bean -> {
            //价格
            if (bean.getSalePrices().size() == CommonPool.NUMBER_ONE) {
                bean.setSalePrice(bean.getSalePrices().get(0));
            } else {
                //最高 最低价格
                bean.setMaxPrice(bean.getSalePrices().get(bean.getSalePrices().size() - 1));
                bean.setMinPrice(bean.getSalePrices().get(0));
            }
            switch (bean.getStatus()) {
                case UNDER_REVIEW -> bean.setAuditStatus(ProductAuditStatus.UNDER_REVIEW);
                case REFUSE -> bean.setAuditStatus(ProductAuditStatus.REFUSE);
                default -> bean.setAuditStatus(ProductAuditStatus.ALREADY_PASSED);
            }

        });
        return auditProductList;
    }

    @Override
    public void auditProductSubmit(Long id) {
        Long shopId = ISecurity.userMust().getShopId();
        Product product = checkoutProductInfo(id, shopId, Boolean.FALSE);
        if (product.getStatus() != ProductStatus.REFUSE) {
            throw new GlobalException("当前商品状态不可再次提交审核");
        }
        ProductStatus productStatus = goodsAddonSupporter.getProductStatus(product.getStatus());
        if (productStatus != null) {
            product.getExtra().setSubmitTime(LocalDateTime.now());
            product.setStatus(productStatus);
            if (productStatus == ProductStatus.SELL_ON) {
                product.getExtra().setAuditTime(LocalDateTime.now());
            }
            // 更新mysql redis
            updateProduct2DB(product, product.getExtra());
        }

    }

    /**
     * 按照创建时间排序获取最新的前5条商品
     *
     * @param shopIds 店铺ids
     * @return 商品信息
     */
    @Override
    public List<ProductVO> getTopFiveProductOrderTime(Set<Long> shopIds) {
        return baseMapper.getTopFiveProductOrderTime(shopIds);
    }

    /**
     * 获取有上架商品的店铺id集合
     *
     * @return 店铺id集合
     */
    @Override
    public List<Long> getShopIdListBySellOnProduct() {
        return baseMapper.getShopIdListBySellOnProduct();
    }

    /**
     * 更新商品的类目扣率
     *
     * @param dto 更新商品类目扣率DTO
     */
    @Override
    public void updateProductCategoryDeductionRation(
            CategorySigningCustomDeductionRationMqDTO dto) {
        Long shopId = dto.getShopId();
        if (ShopMode.SUPPLIER.equals(dto.getShopMode())) {
            //处理店铺代销供应商的商品
            dealPurchaseProduct(dto, shopId);
        } else {
            //处理店铺自有商品
            dealShopOwnProduct(dto, shopId);
        }

    }

    /**
     * 更新店铺代销供应商的商品
     *
     * @param dto
     * @param shopId
     */
    private void dealPurchaseProduct(CategorySigningCustomDeductionRationMqDTO dto, Long shopId) {

        Set<Long> productIds = TenantShop.disable(
                        () -> this.query()
                                .select("id")
                                .eq("supplier_id", shopId)
                                .eq("extra->'$.platformCategory.two'", dto.getSecondCategoryId())
                                .list()
                ).stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(productIds)) {
            return;
        }
        //渲染商品你缓存 key
        Set<String> productCacheKeys = productIds.stream()
                .map(productId -> RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, shopId, productId))
                .collect(Collectors.toSet());
        //缓存双删
        RedisUtil.doubleDeletion(
                () -> TenantShop.disable(() ->
                        this.lambdaUpdate()
                                .setSql(
                                        SqlHelper.renderJsonSetSql(
                                                "extra",
                                                Tuple.of("supplierCustomDeductionRatio", dto.getDeductionRation())
                                        )
                                )
                                .in(BaseEntity::getId, productIds)
                                .update()
                ),
                () -> RedisUtil.delete(productCacheKeys)
        );
    }

    /**
     * 更新店铺自有商品的类目扣率
     *
     * @param dto
     * @param shopId
     */
    private void dealShopOwnProduct(CategorySigningCustomDeductionRationMqDTO dto, Long shopId) {
        Set<Long> productIds = TenantShop.disable(
                        () -> this.query()
                                .select("id")
                                .eq("shop_id", shopId)
                                .eq("extra->'$.platformCategory.two'", dto.getSecondCategoryId())
                                .list()
                ).stream()
                .map(BaseEntity::getId)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(productIds)) {
            return;
        }
        //渲染商品你缓存 key
        Set<String> productCacheKeys = productIds.stream()
                .map(productId -> RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, shopId, productId))
                .collect(Collectors.toSet());
        //缓存双删
        RedisUtil.doubleDeletion(
                () -> TenantShop.disable(() ->
                        this.lambdaUpdate()
                                .setSql(
                                        SqlHelper.renderJsonSetSql(
                                                "extra",
                                                Tuple.of("customDeductionRatio", dto.getDeductionRation())
                                        )
                                )
                                .in(BaseEntity::getId, productIds)
                                .update()
                ),
                () -> RedisUtil.delete(productCacheKeys)
        );
    }

    /**
     * 商品详情，sku切换
     *
     * @param shopId    店铺id
     * @param productId 商品id
     * @param skuId     skuId
     * @param salePrice 销售价
     */
    @Override
    public ProductVO getProductChangeSkuInfo(Long shopId, Long productId, Long skuId, Long salePrice) {
        ProductVO productVO = new ProductVO();
        Product productInfo = getProductInfo(shopId, productId);
        GoodsError.CURRENT_GOODS_NOT_EXIST.trueThrow(productInfo == null);
        productVO.setExtra(productInfo.getExtra());
        // 计算预计价格和预计收益
//        estimatePriceAndEarningAmount(shopId, productId, salePrice, secureUser, productVO);
        return productVO;
    }


    /**
     * sku 金额修改同步 修正 SalePrices
     *
     * @param priceUpdate 商品价格更新DTO
     */
    @Override
    public void productSkuPriceUpdate(ProductPriceUpdateDTO priceUpdate) {
        Set<Long> shopIds = priceUpdate.getShopIds();
        if (CollUtil.isEmpty(shopIds)) {
            return;
        }
        List<Long> salePrices = priceUpdate.getSkuPriceMap().values()
                .stream()
                .map(ProductPriceUpdateDTO.SkuPriceDTO::getSalePrice)
                .sorted()
                .toList();
        TenantShop.disable(() ->
                this.lambdaUpdate()
                        .set(Product::getSalePrices, JSON.toJSONString(salePrices))
                        .in(Product::getShopId, shopIds)
                        .eq(Product::getId, priceUpdate.getProductId())
                        .update()
        );
    }

    /**
     * 更新商品标签
     *
     * @param id      商品Id
     * @param labelId 商品标签Id
     */
    @Override
    public void updateProductLabel(Long id, Long labelId) {
        Long shopId = ISystem.shopIdMust();
        System.out.println("labelId = " + labelId);
        if (Objects.isNull(labelId)) {
            //删除商品标签
            RedisUtil.doubleDeletion(() -> {
                getBaseMapper().clearLabel(id);
                IManualTransaction.afterCommit(
                        () -> globalExecutor.execute(() -> rabbitTemplate
                                .convertAndSend(GoodsRabbit.GOODS_RELEASE.exchange(),
                                        GoodsRabbit.GOODS_LABEL_DELETE.routingKey(),
                                        new Product().setId(id)
                                )
                        ));
            }, RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, shopId, id));


        } else {
            //更新商品标签
            RedisUtil.doubleDeletion(() -> {
                        TenantShop.disable(() -> {
                            lambdaUpdate().set(Product::getLabelId, labelId)
                                    .eq(Product::getId, id).update();
                            ProductLabel label = productLabelService.getById(labelId);
                            IManualTransaction.afterCommit(
                                    () -> globalExecutor.execute(() -> rabbitTemplate
                                            .convertAndSend(GoodsRabbit.GOODS_RELEASE.exchange(),
                                                    GoodsRabbit.GOODS_LABEL_UPDATE.routingKey(),
                                                    new Product().setProductLabel(label).setId(id)
                                            )
                                    ));
                        });
                    },
                    RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, shopId, id));


        }

    }

    /**
     * 违规下架商品数量
     *
     * @param productParam 商品param查询数据
     * @return 违规下架商品数量
     */
    @Override
    public int illegalProductCount(PlatformProductParamNoPage productParam) {
        return baseMapper.illegalProductCount(productParam);

    }

    /**
     * 重新上架
     *
     * @param shopProductKey ShopProductKey
     */
    @Override
    public void restoreSale(ShopProductKey shopProductKey) {
        Long productId = shopProductKey.getProductId();
        Long shopId = shopProductKey.getShopId();
        Product dbProduct = TenantShop.disable(() ->
                this.lambdaQuery()
                        .select(Product::getStatus, Product::getSellType, Product::getSupplierId)
                        .eq(Product::getId, productId)
                        .eq(Product::getShopId, shopId)
                        .one()
        );
        if (Objects.isNull(dbProduct)) {
            //查不到商品
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (!ProductStatus.PLATFORM_SELL_OFF.equals(dbProduct.getStatus())) {
            //或者商品不处于违规下架的状态
            throw GoodsError.PRODUCT_STATUS_NOT_CORRECT.exception();
        }
        //代销商品
        if (SellType.CONSIGNMENT == dbProduct.getSellType()) {
            goodsAddonSupporter.syncSupplierProduct(
                    new ProductStatusChangeDTO()
                            .setKeys(Set.of(new ShopProductKey(dbProduct.getSupplierId(), productId))),
                    ProductStatus.SELL_OFF
            );
            return;
        }
        RedisUtil.doubleDeletion(
                () -> TenantShop.disable(() ->
                        lambdaUpdate()
                                .set(Product::getStatus, ProductStatus.SELL_OFF)
                                .setSql("extra=JSON_REMOVE(extra, '$.productViolation')")
                                .eq(BaseEntity::getId, productId)
                                .eq(Product::getShopId, shopId)
                                .eq(Product::getStatus, ProductStatus.PLATFORM_SELL_OFF)
                                .update()
                ),
                () -> RedisUtil.delete(
                        GoodsUtil.productCacheKey(shopId, productId)
                )
        );


    }

    @Override
    public Integer auditingCount(AuditProductParamNoPage productParam) {
        return TenantShop.disable(
                () -> getBaseMapper().auditingCount(productParam));
    }

    @Override

    public Long getProductCount(ProductParamNoPage productParam) {
        return TenantShop.disable(() -> getBaseMapper().productCount(productParam));
    }

    @Override
    public void updateProductSales(Map<ShopProductSkuKey, StSvBo> mergeResult) {
        log.info("批量更新 sku 库存：{}", mergeResult.toString());
        if (MapUtil.isEmpty(mergeResult)) {
            return;
        }
        //批量锁
        RLock multiLock = redissonClient.getMultiLock(
                mergeResult.keySet().stream()
                        .map(GoodsUtil::generateRedissonLockKey)
                        .map(redissonClient::getLock)
                        .toArray(RLock[]::new)
        );
        //加锁
        multiLock.lock();
        try {
            //批量执行器
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            //批量更新数据库库存数据 并归还库存和销量
            try {

                ProductMapper mapper = sqlSession.getMapper(ProductMapper.class);
                for (Map.Entry<ShopProductSkuKey, StSvBo> entry : mergeResult.entrySet()) {
                    ShopProductSkuKey key = entry.getKey();
                    RedisUtil.doubleDeletion(() -> {
                                StSvBo value = entry.getValue();
                                LambdaUpdateWrapper<Product> updateWrapper = Wrappers.lambdaUpdate(Product.class)
                                        //销量
                                        .setSql(StrUtil.format(GoodsConstant.SQL_SALES_VOLUME_INCREMENT_SQL_TEMPLATE,
                                                value.getSales()))
                                        //库存
                                        .setSql(StrUtil.format(GoodsConstant.SQL_STOCK_INCREMENT_TEMPLATE,
                                                value.getStock()))
                                        .eq(Product::getShopId, key.getShopId())
                                        .eq(Product::getId, key.getProductId());
                                int affectCount = TenantShop.disable(() -> mapper.update(null, updateWrapper));

                                log.info("更新商品库存和销量：{}", affectCount);
                            },
                            RedisUtil.delete(GoodsUtil.productCacheKey(key.getShopId(), key.getProductId()))
                    );
                }
                sqlSession.commit();
//                Set<Long> shopIds = mergeResult.keySet().stream().map(ShopProductSkuKey::getShopId).collect(Collectors.toSet());
//                shopIds.forEach(shopId -> {
//                    globalExecutor.execute(() -> rabbitTemplate
//                            .convertAndSend(ShopRabbit.SHOP_ON_SHELF_GOODS_COUNT_CHANGE.exchange(),
//                                    ShopRabbit.SHOP_ON_SHELF_GOODS_COUNT_CHANGE.routingKey(),
//                                    shopId
//                            ));
//                });


            } catch (Exception exception) {
                log.error("商品总库存更新出错", exception);
                sqlSession.rollback();
                throw SystemCode.DATA_UPDATE_FAILED.exception();
            } finally {
                sqlSession.close();
            }
        } finally {
            //释放锁
            multiLock.unlock();
        }

    }

    /**
     * 判断代销商品状态是否可以上架
     *
     * @return Boolean
     */
    private boolean validConsignmentProductStatus(ProductStatus productStatus) {
        return switch (productStatus) {
            case SUPPLIER_SELL_OFF,
                 SUPPLIER_DISABLE,
                 PLATFORM_SELL_OFF -> false;
            default -> true;
        };
    }

    /**
     * toProductKeyMap
     *
     * @param products 商品List
     * @return Map<ShopProductKey, Product>
     */
    private Map<ShopProductKey, Product> toProductKeyMap(List<Product> products) {
        if (CollUtil.isEmpty(products)) {
            return Collections.emptyMap();
        }
        return products.stream().collect(Collectors.toMap(
                product -> new ShopProductKey().setShopId(product.getShopId()).setProductId(product.getId()),
                product -> product));
    }


    /**
     * 商品信息校验
     *
     * @param id                      商品id
     * @param shopId                  店铺id
     * @param isNotFiltrationSellType 是否过滤类型查询
     * @return Product
     */
    private Product checkoutProductInfo(Long id, Long shopId, boolean isNotFiltrationSellType) {
        Product product = TenantShop.disable(() -> this.lambdaQuery()
                .eq(BaseEntity::getId, id)
                .eq(Product::getShopId, shopId)
                .ne(isNotFiltrationSellType, Product::getSellType, SellType.CONSIGNMENT)
                .one());
        if (product == null) {
            throw GoodsError.CURRENT_GOODS_NOT_EXIST.exception();
        }
        GoodsError.PRODUCT_STATUS_OPERATE_EXCEPTION.trueThrow(product.getStatus() == ProductStatus.PLATFORM_SELL_OFF);
        return product;
    }


    /**
     * 商品扩展字段处理
     *
     * @param product                 商品id
     * @param platformCategory        平台类目信息
     * @param shopCategory            店铺类目信息
     * @param productAttributes       产品属性
     * @param productParameters       产品参数
     * @param consignmentPriceSetting 代销价格设置
     */
    private void setExtendInfo(Product product, com.medusa.gruul.goods.api.model.CategoryLevel platformCategory,
                               com.medusa.gruul.goods.api.model.CategoryLevel shopCategory,
                               List<ProductFeaturesValueDTO> productAttributes,
                               List<ProductFeaturesValueDTO> productParameters,
                               ConsignmentPriceSettingDTO consignmentPriceSetting) {
        Optional.ofNullable(productAttributes).ifPresent(params -> {
            List<ProductFeaturesValueDTO> collect = params.stream().collect(
                    Collectors.toMap(ProductFeaturesValueDTO::getId, entity -> entity,
                            (existing, replacement) -> existing)).values().stream().toList();
            GoodsError.ATTRIBUTES_REPETITION.trueThrow(params.size() != collect.size());
            params.forEach(productAttribute -> productAttribute.getFeatureValues().forEach(featureValue -> {
                if (featureValue.getFeatureValueId() == null) {
                    featureValue.setFeatureValueId(
                            MybatisPlusConfig.IDENTIFIER_GENERATOR.nextId(new FeatureValueDTO()).longValue());
                }
            }));
        });
        // 获取自定义扣率
        Long customDeductionRatio = goodsAddonSupporter.getCustomDeductionRatio(platformCategory.getTwo(),
                product.getShopId());

        ProductExtraDTO productExtra = new ProductExtraDTO();
        // 设置自定义扣率,商品类目,平台类目,产品属性 ，产品参数
        productExtra.setCustomDeductionRatio(customDeductionRatio == null ? 0L : customDeductionRatio);
        productExtra.setShopCategory(shopCategory);
        productExtra.setPlatformCategory(platformCategory);
        productExtra.setProductParameters(productParameters);
        productExtra.setProductAttributes(productAttributes);
        if (product.getExtra() != null) {
            productExtra.setSubmitTime(Option.of(product.getExtra().getSubmitTime()).getOrNull());
            productExtra.setAuditTime(Option.of(product.getExtra().getAuditTime()).getOrNull());
        }
        if (BeanUtil.isNotEmpty(consignmentPriceSetting)) {
            consignmentPriceSetting.validParam();
            productExtra.setConsignmentPriceSetting(consignmentPriceSetting);
        }
        product.setExtra(productExtra);
    }


    /**
     * 商品状态处理
     *
     * @param status           改变的状态
     * @param productViolation 商品违规信息
     * @param updateStatusList List<ProductUpdateStatusDTO>
     * @param explain          拒绝说明
     */
    private void handlerGoodsStatus(ProductStatus status, ProductViolationDTO productViolation,
                                    List<ProductUpdateStatusDTO> updateStatusList, String explain) {
        updateStatusList.forEach(updateStatus -> {
            if (CollUtil.isEmpty(updateStatus.getProductIds())) {
                return;
            }
            Set<String> keys = updateStatus.getProductIds().stream()
                    .map(productId -> GoodsUtil.productCacheKey(updateStatus.getShopId(), productId))
                    .collect(Collectors.toSet());
            RedisUtil.doubleDeletion(() -> TenantShop.disable(() -> {
                LambdaUpdateChainWrapper<Product> set = this.lambdaUpdate()
                        .in(Product::getId, updateStatus.getProductIds())
                        .eq(Product::getShopId, updateStatus.getShopId()).set(Product::getStatus, status);
                if (productViolation != null) {
                    set.setSql(SqlHelper.renderJsonSetSql("extra", Tuple.of("productViolation",
                            JSON.toJSONString(productViolation.setExamineDateTime(LocalDateTime.now())), null)));

                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                if (explain != null && status == ProductStatus.REFUSE) {
                    set.setSql(SqlHelper.renderJsonSetSql("extra", Tuple.of("explain", explain)));
                    set.setSql(SqlHelper.renderJsonSetSql("extra",
                            Tuple.of("auditTime", LocalDateTime.now().format(formatter))));
                }
                if (status == ProductStatus.SELL_ON && ISystem.shopIdOpt().get() == 0L) {
                    set.setSql(SqlHelper.renderJsonSetSql("extra",
                            Tuple.of("auditTime", LocalDateTime.now().format(formatter))));
                }

                set.update();
            }), () -> RedisUtil.delete(keys));

        });
        // 该处代表平台审核通过
        ISecurity.match().when(
                bean -> {
                    if (status == ProductStatus.SELL_ON) {
                        Map<Long, Set<Long>> productMap = updateStatusList.stream().collect(
                                Collectors.toMap(ProductUpdateStatusDTO::getShopId,
                                        ProductUpdateStatusDTO::getProductIds));
                        storageRpcService.generateStorageDetail(productMap);
                    }
                },
                Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN
        );

        globalExecutor.execute(() -> {
            rabbitTemplate
                    .convertAndSend(GoodsRabbit.GOODS_UPDATE_STATUS.exchange(),
                            GoodsRabbit.GOODS_UPDATE_STATUS.routingKey(),
                            updateStatusList);
            if (status == ProductStatus.SELL_OFF || status == ProductStatus.PLATFORM_SELL_OFF) {
                globalExecutor.execute(() -> rabbitTemplate
                        .convertAndSend(GoodsRabbit.GOODS_SELL_OFF.exchange(),
                                GoodsRabbit.GOODS_SELL_OFF.routingKey(),
                                updateStatusList));
            }
        });
    }

    /**
     * 发送mq广播  发布 修改
     *
     * @param shopCategory     店铺类目信息
     * @param platformCategory 平台类目信息
     * @param product          商品信息
     * @param exchange         exchange
     * @param routingKey       routingKey
     */
    private void sendProductBroadcast(CategoryLevel shopCategory, CategoryLevel platformCategory, Product product,
                                      String exchange, String routingKey) {
        IManualTransaction.afterCommit(
                () -> globalExecutor.execute(() -> rabbitTemplate
                        .convertAndSend(exchange,
                                routingKey,
                                new ProductBroadcastDTO()
                                        .setProduct(product)
                                        .setShopCategory(shopCategory)
                                        .setPlatformCategory(platformCategory)
                        )
                ));
    }


    /**
     * 编辑商品Sku信息
     *
     * @param productDto 商品DTO
     * @param product    商品信息
     * @param skus       skus信息
     */
    private void editProductSkuInfo(ProductDTO productDto, Product product, List<SkuDTO> skus) {
        if (productDto.getSellType() == SellType.CONSIGNMENT) {
            //绑定代销关系
            storageRpcService.saveConsignmentRelation(product.getShopId(), product.getId(), product.getSupplierId());
            return;
        }
        // 编辑(新增/修改) 商品sku信息
        StorageSpecSkuDTO storageSpecSkuDTO = new StorageSpecSkuDTO();
        // 设置 实物商品库存状态一定是有限库存
        skus.forEach(sku -> sku.setStockType(
                product.getProductType() == ProductType.REAL_PRODUCT ? StockType.LIMITED : sku.getStockType()));
        storageSpecSkuDTO.setProductId(product.getId());
        storageSpecSkuDTO.setSkus(skus);
        storageSpecSkuDTO.setProductName(product.getName());
        storageSpecSkuDTO.setSellType(product.getSellType());
        storageSpecSkuDTO.setSpecGroups(productDto.getSpecGroups());
        storageSpecSkuDTO.setProductCurrentStatus(new JSONObject().set("status", product.getStatus()));

        storageRpcService.saveOrUpdateSpecSku(storageSpecSkuDTO);
    }


    /**
     * 代销商品处理价格
     *
     * @param product 商品信息
     * @param skuList skuList
     */

    private <T> void handleConsignmentPrices(T product, List<StorageSku> skuList) {
        if (CollUtil.isEmpty(skuList)) {
            return;
        }
        ConsignmentPriceSettingDTO consignmentPriceSetting = getObjectConsignmentPriceSetting(product);
        if (consignmentPriceSetting != null) {
            // 代销商品重新处理逻辑
            skuList.forEach(
                    sku -> {
                        ConsignmentPriceDTO consignmentPrice = consignmentPriceSetting.singlePrice(sku.getSalePrice());
                        sku.setSalePrice(consignmentPrice.getSalePrice());
                        sku.setPrice(consignmentPrice.getPrice());
                    }
            );
        }

    }

    private <T> ConsignmentPriceSettingDTO getObjectConsignmentPriceSetting(T product) {
        if (product instanceof ProductVO) {
            return ((ProductVO) product).getExtra().getConsignmentPriceSetting();
        } else if (product instanceof SupplierIssueProductListVO) {
            return ((SupplierIssueProductListVO) product).getExtra().getConsignmentPriceSetting();
        } else if (product instanceof PlatformProductVO) {
            return ((PlatformProductVO) product).getExtra().getConsignmentPriceSetting();
        } else if (product instanceof LookProductVO) {
            return ((LookProductVO) product).getExtra().getConsignmentPriceSetting();
        }
        return null;
    }


    /**
     * 修改商品信息 mysql redis es
     *
     * @param product 商品信息
     * @param extra   商品额外信息
     */
    private void updateProduct2DB(Product product, ProductExtraDTO extra) {
        product.setProductLabel(
                product.getLabelId() != null ? TenantShop.disable(
                        () -> productLabelService.lambdaQuery().eq(ProductLabel::getId, product.getLabelId()).one())
                        : null
        );
        Integer update = RedisUtil.doubleDeletion(() ->
                        TenantShop.disable(() -> baseMapper.update(
                                product,
                                Wrappers.lambdaQuery(Product.class)
                                        .eq(Product::getId, product.getId())
                                        .eq(Product::getShopId, product.getShopId())
                        )),
                RedisUtil.key(GoodsConstant.GOODS_DETAIL_CACHE_KEY, product.getShopId(), product.getId())
        );
        GoodsError.PRODUCT_UPDATE_FAIL.trueThrow(update < CommonPool.NUMBER_ONE);
        sendProductBroadcast(
                extra.getShopCategory(),
                extra.getPlatformCategory(),
                product,
                GoodsRabbit.GOODS_UPDATE.exchange(),
                GoodsRabbit.GOODS_UPDATE.routingKey()
        );
    }

    @Override
    public List<Long> getNewUpShop(List<Long> shopIds) {
        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(1);
        String dateTime = DateUtil.formatLocalDateTime(localDateTime);
        return TenantShop.disable(() -> {
            return getBaseMapper().getNewUpShop(dateTime, shopIds);
        });
    }


    /*
     *  兼容单体
     */

    @Lazy
    @Autowired
    public void setStorageRpcService(StorageRpcService storageRpcService) {
        this.storageRpcService = storageRpcService;
    }

    @Lazy
    @Autowired
    public void setSearchRpcService(SearchRpcService searchRpcService) {
        this.searchRpcService = searchRpcService;
    }

    @Lazy
    @Autowired
    public void setUserRpcService(UserRpcService userRpcService) {
        this.userRpcService = userRpcService;
    }

    @Lazy
    @Autowired
    public void setShopRpcService(ShopRpcService shopRpcService) {
        this.shopRpcService = shopRpcService;
    }

    @Lazy
    @Autowired
    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
}
