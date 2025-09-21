package com.medusa.gruul.addon.distribute.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.medusa.gruul.addon.distribute.model.DistributeConstant;
import com.medusa.gruul.addon.distribute.model.dto.ProductBindDTO;
import com.medusa.gruul.addon.distribute.model.dto.ProductQueryDTO;
import com.medusa.gruul.addon.distribute.model.enums.DistributionStatus;
import com.medusa.gruul.addon.distribute.model.enums.DistributorIdentity;
import com.medusa.gruul.addon.distribute.model.enums.Precompute;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeConf;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeProduct;
import com.medusa.gruul.addon.distribute.mp.entity.DistributeShop;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import com.medusa.gruul.addon.distribute.mp.mapper.DistributeProductMapper;
import com.medusa.gruul.addon.distribute.mp.service.IDistributeProductService;
import com.medusa.gruul.addon.distribute.mp.service.IDistributeShopService;
import com.medusa.gruul.addon.distribute.service.DistributeConfHandleService;
import com.medusa.gruul.addon.distribute.service.DistributeProductHandleService;
import com.medusa.gruul.addon.distribute.service.DistributorHandleService;
import com.medusa.gruul.addon.distribute.util.DistributeUtil;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductUpdateStatusDTO;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.search.api.model.ProductDistributed;
import com.medusa.gruul.search.api.rpc.SearchRpcService;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.dto.ProductPriceUpdateDTO;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import com.medusa.gruul.storage.api.vo.ProductStatisticsVO;
import io.vavr.control.Option;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.slf4j.Slf4jImpl;
import org.apache.ibatis.session.SqlSessionFactory;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 张治保 date 2022/11/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DistributeProductHandleServiceImpl implements DistributeProductHandleService, DisposableBean {

    private final GoodsRpcService goodsRpcService;
    private final ShopRpcService shopRpcService;
    private final StorageRpcService storageRpcService;
    private final SqlSessionFactory sqlSessionFactory;
    private final IDistributeShopService distributeShopService;
    private final IDistributeProductService distributeProductService;
    private final DistributorHandleService distributorHandleService;
    private final DistributeConfHandleService distributeConfHandleService;
    private final Map<ShopProductKey, AtomicLong> productSalesLockMap = new ConcurrentHashMap<>();
    private final Log mybatisLogging = new Slf4jImpl(this.getClass().getSimpleName());
    private final SearchRpcService searchRpcService;

    private static ShopProductKey getShopProductKey(Long shopId, Long productId) {
        return new ShopProductKey().setShopId(shopId).setProductId(productId);
    }

    /**
     * 处理代销商品shopId
     *
     * @param product    分销商品
     * @param productMap 商品
     * @return ShopProductKey
     */
    private static ShopProductKey convertShopProductKey(DistributeProduct product,
            Map<ShopProductKey, Product> productMap) {
        ShopProductKey shopProductKey = getShopProductKey(product.getShopId(), product.getProductId());
        Product shopProduct = productMap.get(shopProductKey);
        if (shopProduct != null && SellType.CONSIGNMENT.equals(shopProduct.getSellType())) {
            shopProductKey.setShopId(shopProduct.getSupplierId());
        }
        return shopProductKey;
    }

    @Override
    @Redisson(value = DistributeConstant.DISTRIBUTE_PRODUCT_BIND_LOCK, key = "#product.shopId+':'+#product.id")
    public void updateProductByProduct(Product product) {
        Long shopId = product.getShopId();
        Long productId = product.getId();
        if (!distributeProductService.lambdaQuery()
                .select(DistributeProduct::getId)
                .eq(DistributeProduct::getProductId, productId)
                .eq(DistributeProduct::getShopId, shopId)
                .exists()) {
            return;
        }

        distributeProductService.lambdaUpdate()
                .set(DistributeProduct::getName, product.getName())
                .set(DistributeProduct::getPic, product.getPic())
                .set(DistributeProduct::getSalePrices, JSON.toJSONString(product.getSalePrices()))
                .eq(DistributeProduct::getShopId, shopId)
                .eq(DistributeProduct::getProductId, productId)
                .update();
    }

    /**
     * 检查店铺id 并保存店铺信息到分销服务
     *
     * @param shopId 店铺id
     */
    private void checkShopIdAndSaveShopInfo(Long shopId) {
        boolean exists = distributeShopService.lambdaQuery()
                .select(DistributeShop::getId)
                .eq(DistributeShop::getShopId, shopId)
                .exists();
        if (exists) {
            return;
        }
        Option.of(shopRpcService.getShopInfoByShopId(shopId))
                .peek(
                        shopInfo -> distributeShopService.save(
                                new DistributeShop().setShopId(shopId)
                                        .setShopName(shopInfo.getName())
                                        .setShopLogo(shopInfo.getLogo())
                        )
                )
                .getOrElseThrow(() -> new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE,
                        SystemCode.PARAM_TYPE_ERROR.getMsg()));
    }

    @Override
    @Redisson(value = DistributeConstant.DISTRIBUTE_PRODUCT_BIND_LOCK, key = "#shopId")
    public void newProduct(Long shopId, ProductBindDTO productBind) {
        productBind.validParam(distributeConfHandleService.configMust().getLevel());
        Set<Long> productIds = productBind.getProductIds();
        /* 检查店铺id 并保存店铺信息到分销服务
         */
        this.checkShopIdAndSaveShopInfo(shopId);
        /* 检查商品信息
         */
        Map<ShopProductKey, Product> productBatch = goodsRpcService.getProductBatch(
                productIds.stream()
                        .map(productId -> new ShopProductKey().setShopId(shopId).setProductId(productId))
                        .collect(Collectors.toSet())
        );
        if (productBatch.size() != productIds.size()) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, SystemCode.PARAM_TYPE_ERROR.getMsg());
        }
        //ES中需要更新商品的分销状态的id集合
        List<DistributeProduct> distributeProducts = productBatch.entrySet()
                .stream()
                .map(
                        entry -> {
                            Product product = entry.getValue();
                            Long productId = entry.getKey().getProductId();
                            if (product == null || ProductStatus.SELL_ON != product.getStatus()) {
                                throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE,
                                        "商品不处于正常状态:" + productId);
                            }
                            if (distributeProductService.lambdaQuery()
                                    .eq(DistributeProduct::getShopId, shopId)
                                    .eq(DistributeProduct::getProductId, productId)
                                    .exists()) {
                                throw new GlobalException(SystemCode.DATA_EXISTED_CODE,
                                        StrUtil.format("商品名:{},已存在绑定关系", product.getName()));
                            }
                            return new DistributeProduct()
                                    .setShopId(shopId)
                                    .setProductId(productId)
                                    .setSales(0L)
                                    .setName(product.getName())
                                    .setProductType(product.getProductType())
                                    .setPic(product.getPic())
                                    .setSalePrices(product.getSalePrices())
                                    .setStatus(product.getStatus())
                                    .setDistributionStatus(DistributionStatus.IN_DISTRIBUTION)
                                    .setShareType(productBind.getShareType())
                                    .setOne(productBind.getOne())
                                    .setTwo(productBind.getTwo())
                                    .setThree(productBind.getThree());
                        }
                ).toList();
        boolean success = distributeProductService.saveBatch(distributeProducts);
        if (!success) {
            throw new GlobalException(SystemCode.DATA_ADD_FAILED_CODE, SystemCode.PARAM_TYPE_ERROR.getMsg());
        }
        //更新ES中商品的分销状态为 true
        searchRpcService.updateProduct(
                new ProductDistributed()
                        .setShopId(shopId)
                        .setProductIds(productBind.getProductIds())
                        .setIsDistributed(Boolean.TRUE)
        );
    }

    @Override
    @Redisson(value = DistributeConstant.DISTRIBUTE_PRODUCT_BIND_LOCK, key = "#shopId+':'+#productBind.productIds[0]")
    public void editProduct(Long shopId, Long bindId, ProductBindDTO productBind) {
        Set<Long> productIds = productBind.getProductIds();
        if (productIds.size() != 1) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, SystemCode.PARAM_TYPE_ERROR.getMsg());
        }
        Long productId = productIds.iterator().next();
        productBind.validParam(distributeConfHandleService.configMust().getLevel());

        /* 检查是否已存在绑定关系
         */
        if (distributeProductService.lambdaQuery()
                .select(DistributeProduct::getId)
                .eq(DistributeProduct::getShopId, shopId)
                .eq(DistributeProduct::getProductId, productId)
                .ne(DistributeProduct::getId, bindId)
                .exists()) {
            throw new GlobalException(SystemCode.DATA_EXISTED_CODE, SystemCode.PARAM_TYPE_ERROR.getMsg());
        }
        /* 检查商品信息
         */
        Product product = goodsRpcService.getProductInfo(shopId, productId);
        if (product == null) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, SystemCode.PARAM_VALID_ERROR.getMsg());
        }
        boolean success = distributeProductService.lambdaUpdate()
                .set(DistributeProduct::getProductId, productId)
                .set(DistributeProduct::getName, product.getName())
                .set(DistributeProduct::getPic, product.getPic())
                .set(DistributeProduct::getSalePrices, JSON.toJSONString(product.getSalePrices()))
                .set(DistributeProduct::getStatus, product.getStatus())
                .set(DistributeProduct::getShareType, productBind.getShareType())
                .set(DistributeProduct::getOne, productBind.getOne())
                .set(DistributeProduct::getTwo, productBind.getTwo())
                .set(DistributeProduct::getThree, productBind.getThree())
                .eq(DistributeProduct::getShopId, shopId)
                .eq(DistributeProduct::getId, bindId)
                .update();
        if (!success) {
            throw new GlobalException(SystemCode.DATA_UPDATE_FAILED_CODE, SystemCode.DATA_UPDATE_FAILED.getMsg());
        }
    }

    @Override
    public void deleteBatch(Long shopId, Set<Long> bindIds) {
        //获取分销商品关联
        List<DistributeProduct> list = distributeProductService.lambdaQuery()
                .in(DistributeProduct::getId, bindIds).list();
        distributeProductService.lambdaUpdate()
                .eq(DistributeProduct::getShopId, shopId)
                .in(DistributeProduct::getId, bindIds)
                .remove();

        searchRpcService.updateProduct(
                new ProductDistributed()
                        .setShopId(shopId)
                        .setProductIds(list.stream().map(DistributeProduct::getProductId).collect(Collectors.toSet()))
                        .setIsDistributed(Boolean.FALSE)
        );
    }

    /**
     * 批量取消分销商品
     *
     * @param shopId  店铺id
     * @param bindIds 分销商品绑定关系id集合
     */
    @Override
    public void cancelBatch(Long shopId, Set<Long> bindIds) {
        bindIds.forEach(bindId ->
                SystemCode.DATA_UPDATE_FAILED.falseThrow(
                        distributeProductService.lambdaUpdate()
                                .set(DistributeProduct::getDistributionStatus, DistributionStatus.CANCEL_DISTRIBUTION)
                                .eq(shopId != 0, DistributeProduct::getShopId, shopId)
                                .in(DistributeProduct::getId, bindIds)
                                .update()
                )
        );
    }

    /**
     * 重新分销
     *
     * @param bindId 分销商品绑定关系id
     */
    @Override
    public void againDistribute(Long shopId, Long bindId) {
        SystemCode.DATA_UPDATE_FAILED.falseThrow(
                distributeProductService.lambdaUpdate()
                        .eq(DistributeProduct::getShopId, shopId)
                        .eq(DistributeProduct::getId, bindId)
                        .ne(DistributeProduct::getStatus, ProductStatus.PLATFORM_SELL_OFF)
                        .set(DistributeProduct::getDistributionStatus, DistributionStatus.IN_DISTRIBUTION)
                        .update()
        );
    }

    @Override
    public void updateProductSkuPrice(ProductPriceUpdateDTO priceUpdate) {
        Set<Long> shopIds = priceUpdate.getShopIds();
        if (CollUtil.isEmpty(shopIds)) {
            return;
        }
        Long productId = priceUpdate.getProductId();
        Set<ShopProductKey> productKeys = distributeProductService.lambdaQuery()
                .select(DistributeProduct::getShopId)
                .eq(DistributeProduct::getProductId, productId)
                .in(DistributeProduct::getShopId, shopIds)
                .list()
                .stream()
                .map(product -> new ShopProductKey(product.getShopId(), productId))
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(shopIds)) {
            return;
        }
        Map<ShopProductKey, Product> productBatch = goodsRpcService.getProductBatch(productKeys);
        List<Long> salePrices = priceUpdate.getSkuPriceMap()
                .values()
                .stream()
                .map(ProductPriceUpdateDTO.SkuPriceDTO::getSalePrice)
                .sorted()
                .toList();
        log.debug("新商品价格\n{}", salePrices);
        //批量更新
        SqlHelper.executeBatch(
                sqlSessionFactory,
                mybatisLogging,
                sqlSession -> {
                    DistributeProductMapper mapper = sqlSession.getMapper(DistributeProductMapper.class);
                    productBatch.forEach(
                            (key, product) -> {
                                if (product == null) {
                                    return;
                                }
                                log.debug("商品信息\n{}", product);
                                product.setSalePrices(salePrices);
                                mapper.update(
                                        Wrappers.lambdaUpdate(DistributeProduct.class)
                                                .set(DistributeProduct::getSalePrices,
                                                        JSON.toJSONString(product.realSalePrice()))
                                                .eq(DistributeProduct::getProductId, productId)
                                                .eq(DistributeProduct::getShopId, key.getShopId())
                                );
                            }
                    );
                }
        );
    }

    @Override
    public IPage<DistributeProduct> productPage(ProductQueryDTO query) {
        List<OrderItem> orders = query.getOrders();
        if (CollUtil.isEmpty(orders)) {
            query.setOrders(List.of(OrderItem.desc("createTime")));
        }
        IPage<DistributeProduct> result = distributeProductService.productPage(query);
        List<DistributeProduct> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }
        Set<Long> productIds = records.stream().map(DistributeProduct::getProductId).collect(Collectors.toSet());
        //存在的商品ID
        Set<Long> existsProductIds = goodsRpcService.queryExistsProductIds(ISecurity.userMust().getShopId(),
                productIds);
        //查询代销商品的supplierId
        Map<ShopProductKey, Product> productMap = goodsRpcService.getProductBatch(
                records.stream()
                        .map(
                                product -> getShopProductKey(product.getShopId(), product.getProductId())
                        )
                        .collect(Collectors.toSet())
        );
        records.forEach(
                product -> {
                    product.setCanEdit(existsProductIds.contains(product.getProductId()));
                }
        );

        ISecurity.match()
                .when(
                        secureUser -> {
                            Map<ShopProductKey, DistributeProduct> keyMap = records.stream()
                                    .collect(
                                            Collectors.toMap(
                                                    product -> convertShopProductKey(product, productMap)
                                                    , v -> v
                                            )
                                    );
                            Map<ShopProductKey, ProductStatisticsVO> productStatisticsMap = storageRpcService.getProductStatisticsMap(
                                    keyMap.keySet(), false);
                            keyMap.forEach(
                                    (key, value) -> {
                                        if (Option.of(productStatisticsMap.get(key))
                                                .peek(statistics -> value.setStock(statistics.getRemainingStock()))
                                                .isEmpty()) {
                                            value.setStock(0L);
                                        }
                                    }
                            );
                        },
                        Roles.SUPER_ADMIN, Roles.SUPER_CUSTOM_ADMIN, Roles.ADMIN, Roles.CUSTOM_ADMIN
                );
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProductStatus(List<ProductUpdateStatusDTO> productUpdateStatus) {
        productUpdateStatus.forEach(
                shopProducts -> distributeProductService.lambdaUpdate()
                        .set(DistributeProduct::getStatus, shopProducts.getProductStatus())
                        .set(DistributionStatus.toDistributeStatus(shopProducts.getProductStatus()) != null,
                                DistributeProduct::getDistributionStatus, DistributionStatus.CANCEL_DISTRIBUTION)
                        .eq(DistributeProduct::getShopId, shopProducts.getShopId())
                        .in(DistributeProduct::getProductId, shopProducts.getProductIds())
                        .update()
        );
    }

    @Override
    public Map<ShopProductKey, DistributeProduct> shopProductConfMap(Set<ShopProductKey> shopProductKeys) {
        List<DistributeProduct> distributeProducts = distributeProductService.getShoppProductConfigs(shopProductKeys);
        if (CollUtil.isEmpty(distributeProducts)) {
            return Collections.emptyMap();
        }
        return distributeProducts.stream()
                .collect(
                        Collectors.toMap(
                                product -> new ShopProductKey().setShopId(product.getShopId())
                                        .setProductId(product.getProductId()),
                                product -> product
                        )
                );
    }

    @Override
    public void updateProductSales(Map<ShopProductKey, Long> productSales) {
        if (CollUtil.isEmpty(productSales)) {
            return;
        }
        productSales.forEach(
                (shopProductKey, sales) -> {
                    if (sales == null || sales <= 0) {
                        return;
                    }
                    AtomicLong volume = productSalesLockMap.computeIfAbsent(shopProductKey, key -> new AtomicLong());
                    volume.addAndGet(sales);
                }
        );
    }

    @Override
    public Long productPrecompute(ShopProductKey key, Long userId) {
        Option<DistributeConf> config = distributeConfHandleService.config();
        if (config.isEmpty()) {
            return null;
        }
        DistributeConf conf = config.get();
        Precompute precompute = conf.getPrecompute();
        //从不展示
        if (precompute == null || Precompute.NEVER == precompute) {
            return null;
        }
        //仅对分销商/分销员展示
        if (Precompute.DISTRIBUTOR == precompute) {
            if (userId == null) {
                return null;
            }
            Option<Distributor> distributorOption = distributorHandleService.getByUserId(userId);
            if (distributorOption.isEmpty()) {
                return null;
            }
            Distributor distributor = distributorOption.get();
            //不是分销商 且 上级分销商为空 表示 该用户不是分销员
            if (distributor.getIdentity() != DistributorIdentity.AFFAIRS && distributor.getOne() == null) {
                return null;
            }
        }
        Optional<DistributeProduct> productOptional = distributeProductService.lambdaQuery()
                .eq(DistributeProduct::getDistributionStatus, DistributionStatus.IN_DISTRIBUTION)
                .eq(DistributeProduct::getShopId, key.getShopId())
                .eq(DistributeProduct::getProductId, key.getProductId())
                .oneOpt();
        if (productOptional.isEmpty()) {
            return null;
        }
        DistributeProduct product = productOptional.get();
        //获取最大的金额
        List<Long> salePrices = product.getSalePrices();
        if (CollUtil.isEmpty(salePrices)) {
            return null;
        }
        Long maxPrice = salePrices.get(salePrices.size() - CommonPool.NUMBER_ONE);
        //计算分佣金额
        return DistributeUtil.getCurrentLevelBonus(CommonPool.NUMBER_ONE, maxPrice, product.getShareType(),
                product.getOne());
    }


    @Scheduled(initialDelay = 5, fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void updateProductSales() {
        if (CollUtil.isEmpty(productSalesLockMap)) {
            return;
        }
        Set<ShopProductKey> shopProductKeys = productSalesLockMap.keySet();
        Map<ShopProductKey, Long> productSalesMap = new HashMap<>(CommonPool.NUMBER_THIRTY);
        for (ShopProductKey key : shopProductKeys) {
            AtomicLong sales = productSalesLockMap.remove(key);
            if (sales == null || sales.get() <= 0) {
                continue;
            }
            productSalesMap.put(key, sales.get());
        }
        this.updateProductSalesBatch(productSalesMap);
    }

    /**
     * 容器销毁之前先同步数据
     */
    @Override
    public void destroy() {
        this.updateProductSales();
    }

    /**
     * 更新商品销量
     *
     * @param productSalesMap 商品销量map
     */
    private void updateProductSalesBatch(Map<ShopProductKey, Long> productSalesMap) {
        if (CollUtil.isEmpty(productSalesMap)) {
            return;
        }
        RedissonClient redissonClient = RedisUtil.getRedissonClient();
        RLock multiLock = redissonClient.getMultiLock(
                productSalesMap.keySet().stream()
                        .map(productId -> RedisUtil.key(DistributeConstant.PRODUCT_SALES_LOCK_KEY,
                                productId.getShopId(), productId.getProductId()))
                        .map(redissonClient::getLock)
                        .toArray(RLock[]::new)
        );
        multiLock.lock();
        try {
            SqlHelper.executeBatch(
                    sqlSessionFactory,
                    mybatisLogging,
                    sqlSession -> {
                        DistributeProductMapper mapper = sqlSession.getMapper(DistributeProductMapper.class);
                        productSalesMap.forEach(
                                (shopProductKey, sales) ->
                                        mapper.update(
                                                null,
                                                Wrappers.lambdaUpdate(DistributeProduct.class)
                                                        .eq(DistributeProduct::getShopId, shopProductKey.getShopId())
                                                        .eq(DistributeProduct::getProductId,
                                                                shopProductKey.getProductId())
                                                        .setSql(StrUtil.format(
                                                                DistributeConstant.PRODUCT_SALES_INCREMENT_SQL_TEMPLATE,
                                                                sales))
                                        )

                        );
                    }
            );
        } finally {
            multiLock.unlock();
        }
    }

}
