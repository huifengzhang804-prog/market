package com.medusa.gruul.addon.supplier.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.medusa.gruul.addon.supplier.addon.SupplierLogisticsAddonSupport;
import com.medusa.gruul.addon.supplier.model.SupplierConst;
import com.medusa.gruul.addon.supplier.model.bo.OrderExtra;
import com.medusa.gruul.addon.supplier.model.bo.OrderItemExtra;
import com.medusa.gruul.addon.supplier.model.bo.Receiver;
import com.medusa.gruul.addon.supplier.model.dto.OrderCreateDTO;
import com.medusa.gruul.addon.supplier.model.dto.OrderSupplierDTO;
import com.medusa.gruul.addon.supplier.model.dto.OrderSupplierSkuDTO;
import com.medusa.gruul.addon.supplier.model.enums.*;
import com.medusa.gruul.addon.supplier.model.vo.OrderCreateVO;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderConfigService;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderCreateService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrderItem;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierGoodsService;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderItemService;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderService;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.DistributionMode;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.common.security.model.bean.SecureUser;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.global.model.helper.CompletableTask;
import com.medusa.gruul.global.model.o.ExceptionData;
import com.medusa.gruul.goods.api.model.enums.ProductStatus;
import com.medusa.gruul.order.api.addon.freight.FreightParam;
import com.medusa.gruul.order.api.addon.freight.PlatformFreightParam;
import com.medusa.gruul.order.api.addon.freight.ShopFreightParam;
import com.medusa.gruul.order.api.addon.freight.SkuInfoParam;
import com.medusa.gruul.order.api.enums.OrderCloseType;
import com.medusa.gruul.order.api.enums.OrderRabbit;
import com.medusa.gruul.order.api.pojo.OrderInfo;
import com.medusa.gruul.order.api.pojo.SkuStock;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.bo.OrderStockBO;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.rpc.StorageOrderRpcService;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 张治保
 * date 2023/7/19
 */
@Service
@RequiredArgsConstructor
public class SupplierOrderCreateServiceImpl implements SupplierOrderCreateService {

    private final Executor globalExecutor;
    private final RabbitTemplate rabbitTemplate;
    private final ShopRpcService shopRpcService;
    private final StorageRpcService storageRpcService;
    private final StorageOrderRpcService storageOrderRpcService;
    private final ISupplierGoodsService supplierGoodsService;
    private final ISupplierOrderService supplierOrderService;
    private final ISupplierOrderItemService supplierOrderItemService;
    private final SupplierOrderConfigService supplierOrderConfigService;
    private final SupplierLogisticsAddonSupport supplierLogisticsAddonSupport;


    @Override
    public OrderCreateVO createOrder(OrderCreateDTO orderCreate) {
        SecureUser<?> secureUser = ISecurity.userMust();
        //生成主单号
        String mainNo = RedisUtil.no(SupplierConst.MAIN_NO_PREFIX, SupplierConst.MAIN_NO_KEY);
        //获取所有供应商信息
        Tuple2<Set<ShopProductKey>, OrderStockBO> productKeyAndSkuKey = getProductKeyAndSkuKey(secureUser.getId(), mainNo, orderCreate.getSuppliers());
        //查询商品任务
        CompletableFuture<Map<ShopProductKey, SupplierGoods>> productMapTask = CompletableFuture.supplyAsync(() -> this.goodsMap(productKeyAndSkuKey._1(), orderCreate.getSellType()));
        //查询 sku并扣减 sku 库存任务
        CompletableFuture<Map<ActivityShopProductSkuKey, StorageSku>> skuMapTask = CompletableFuture.supplyAsync(() -> {
            storageOrderRpcService.reduceSkuStock(productKeyAndSkuKey._2());
            return storageRpcService.skuBatch(productKeyAndSkuKey._2().getSkuKeyStSvMap().keySet());
        });
        return Try.ofSupplier(
                () -> {
                    //生成订单数据
                    Set<SupplierOrder> orders = generateOrders(mainNo, secureUser, orderCreate, productMapTask, skuMapTask);
                    OrderCreateVO result = new OrderCreateVO()
                            .setMainNo(mainNo)
                            .setOrderNoMap(orders.stream().collect(Collectors.toMap(SupplierOrder::getSupplierId, SupplierOrder::getNo)));
                    //计算运费
                    this.updateFreight(orders, orderCreate.getReceiver());
                    //缓存订单创建标识
                    RedisUtil.setCacheObject(RedisUtil.key(SupplierConst.ORDER_CREATE_RESULT_PREFIX, mainNo), CommonPool.NUMBER_ONE, CommonPool.NUMBER_FIVE, TimeUnit.MINUTES);
                    //异步保存订单数据
                    globalExecutor.execute(() -> this.saveOrders(orders));
                    return result;
                }
        ).onFailure(
                exception ->
                        rabbitTemplate.convertAndSend(
                                OrderRabbit.ORDER_CREATE_FAILED.exchange(),
                                OrderRabbit.ORDER_CREATE_FAILED.routingKey(),
                                new OrderInfo()
                                        .setCloseType(OrderCloseType.FULL)
                                        .setActivityType(OrderType.COMMON)
                                        .setActivityId(0L)
                                        .setBuyerId(secureUser.getId())
                                        .setOrderNo(mainNo)
                                        .setSkuStocks(
                                                productKeyAndSkuKey._2()
                                                        .getSkuKeyStSvMap()
                                                        .entrySet()
                                                        .stream()
                                                        .map(
                                                                entry ->
                                                                {
                                                                    ActivityShopProductSkuKey key = entry.getKey();
                                                                    return new SkuStock()
                                                                            .setShopId(key.getShopId())
                                                                            .setProductId(key.getProductId())
                                                                            .setSkuId(key.getSkuId())
                                                                            .setNum(-(int) entry.getValue().getStock());
                                                                }
                                                        ).toList()
                                        )

                        )
        ).get();
    }

    private void updateFreight(Set<SupplierOrder> orders, Receiver receiver) {
        Map<String, BigDecimal> freightMap = new HashMap<>(MapUtil.emptyIfNull(this.getFreightMap(orders, receiver)));
        orders.stream().flatMap(order -> order.getOrderItems().stream())
                .forEach(
                        item -> {
                            String key = item.getSupplierId() + StrPool.COLON + item.getExtra().getTemplateId();
                            BigDecimal freight = freightMap.get(key);
                            if (freight != null && freight.signum() > 0) {
                                freightMap.remove(key);
                                item.setFreightPrice(freight.multiply(BigDecimal.valueOf(CommonPool.UNIT_CONVERSION_TEN_THOUSAND)).longValue());
                            }
                        }
                );
    }

    @Override
    public boolean createResult(String mainNo) {
        //缓存重不存在该key 表示订单已创建成功
        return !BooleanUtil.isTrue(RedisUtil.getRedisTemplate().hasKey(RedisUtil.key(SupplierConst.ORDER_CREATE_RESULT_PREFIX, mainNo)));
    }


    private Map<String, BigDecimal> getFreightMap(Set<SupplierOrder> orders, Receiver receiver) {
        //渲染运费计算参数
        PlatformFreightParam param = new PlatformFreightParam();
        param.setArea(receiver.getArea());
        param.setAddress(receiver.getAddress());
        param.setFreeRight(Boolean.FALSE);
        List<ShopFreightParam> shopFreights = new ArrayList<>();
        for (SupplierOrder order : orders) {
            ShopFreightParam shopFreightParam = new ShopFreightParam();
            shopFreightParam.setShopId(order.getSupplierId());
            Map<Long, List<SupplierOrderItem>> templateIdItemsMap = order.getOrderItems()
                    .stream()
                    .collect(Collectors.groupingBy(item -> item.getExtra().getTemplateId()));
            shopFreightParam.setFreights(
                    templateIdItemsMap.entrySet().stream()
                            .map(
                                    entry -> {
                                        FreightParam freightParam = new FreightParam();
                                        freightParam.setTemplateId(entry.getKey());
                                        freightParam.setSkuInfos(
                                                entry.getValue()
                                                        .stream()
                                                        .map(item -> {
                                                            SkuInfoParam skuInfo = new SkuInfoParam();
                                                            skuInfo.setPrice(BigDecimal.valueOf(item.getDealPrice()));
                                                            skuInfo.setNum(item.getNum());
                                                            skuInfo.setSkuId(item.getSkuId());
                                                            skuInfo.setWeight(item.getExtra().getWeight());
                                                            return skuInfo;
                                                        }).toList()
                                        );
                                        return freightParam;
                                    }
                            ).toList()
            );
            shopFreights.add(shopFreightParam);
        }
        param.setShopFreights(shopFreights);
        //获取运费计算结果
        return DistributionMode.EXPRESS.getFunction().apply(innerMode -> supplierLogisticsAddonSupport.distributionCost(innerMode, param));
    }

    private void saveOrders(Set<SupplierOrder> orders) {
        List<SupplierOrderItem> orderItems = new ArrayList<>();
        orders.forEach(
                order -> {
                    List<SupplierOrderItem> currentItems = order.getOrderItems();
                    orderItems.addAll(currentItems);
                    order.setPayAmount(currentItems.stream().mapToLong(item -> item.getNum() * item.getSalePrice() + item.getFixPrice() + item.getFreightPrice()).sum());
                }
        );
        supplierOrderService.saveBatch(orders);
        supplierOrderItemService.saveBatch(orderItems);
        SupplierOrder order = orders.iterator().next();
        //发送订单超时未支付 mq
        rabbitTemplate.convertAndSend(
                SupplierRabbit.SUPPLIER_ORDER_AUTO_PAID_TIMEOUT_CLOSE.exchange(),
                SupplierRabbit.SUPPLIER_ORDER_AUTO_PAID_TIMEOUT_CLOSE.routingKey(),
                orders.stream().map(SupplierOrder::getNo).collect(Collectors.toSet()),
                message -> {
                    message.getMessageProperties().setHeader(MessageProperties.X_DELAY, Duration.ofSeconds(order.getExtra().getPayTimeout()).toMillis());
                    return message;
                }
        );
        RedisUtil.delete(RedisUtil.key(SupplierConst.ORDER_CREATE_RESULT_PREFIX, order.getMainNo()));
    }

    private Set<SupplierOrder> generateOrders(String mainNo, SecureUser<?> secureUser, OrderCreateDTO orderCreate, CompletableFuture<Map<ShopProductKey, SupplierGoods>> productMapTask, CompletableFuture<Map<ActivityShopProductSkuKey, StorageSku>> skuMapTask) {
        //批量获取订单数据 并校验是否可以下单
        return orderCreate.getSuppliers().stream()
                .map(
                        supplierParam -> {
                            Long supplierId = supplierParam.getSupplierId();
                            SupplierOrder order = newOrder(mainNo, secureUser, supplierId, orderCreate);
                            return order.setOrderItems(
                                    orderItems(order, supplierParam.getProductSkus(), productMapTask, skuMapTask)
                            );
                        }
                ).collect(Collectors.toSet());
    }


    /**
     * 获取商品 并 检查商品是否可正常使用
     *
     * @param productKey     商品key
     * @param productMapTask 获取商品map的任务
     * @return SupplierGoods 商品信息
     */
    private SupplierGoods getCheckProduct(ShopProductKey productKey, CompletableFuture<Map<ShopProductKey, SupplierGoods>> productMapTask) {
        SupplierGoods goods = CompletableTask.getOrThrowException(productMapTask).get(productKey);

        if (goods == null) {
            throw SupplierError.PRODUCT_NOT_EXIST.dataEx(ExceptionData.of(productKey));
        }
        if (ProductStatus.SELL_ON != goods.getSupplierProductStatus()) {
            throw SupplierError.PRODUCT_STATUS_ERROR.dataEx(ExceptionData.of(productKey, goods.getSupplierProductStatus()));
        }
        return goods;
    }

    private StorageSku getCheckSku(ActivityShopProductSkuKey skuKey, OrderSupplierSkuDTO sku, CompletableFuture<Map<ActivityShopProductSkuKey, StorageSku>> skuMapTask) {
        StorageSku storageSku = CompletableTask.getOrThrowException(skuMapTask).get(skuKey);
        if (storageSku == null) {
            throw SupplierError.SKU_NOT_EXIST.dataEx(ExceptionData.of(skuKey));
        }
        Integer num = sku.getNum();
        if (num < storageSku.getMinimumPurchase()) {
            throw SupplierError.NOT_MEET_MINIMUM.dataEx(ExceptionData.of(skuKey, storageSku.getMinimumPurchase()));
        }
        return storageSku;
        //前面缓存中已经扣减过库存 做过校验 这里 没有必要再次校验库存
//        if (StockType.LIMITED == storageSku.getStockType() && num > storageSku.getStock()) {
//            throw SupplierError.INSUFFICIENT_STOCK.dataEx(ExceptionData.of(skuKey, storageSku.getStock()));
//        }


    }

    /**
     * 渲染订单子项列表
     *
     * @param order       订单信息
     * @param productSkus 商品信息
     * @return List<com.medusa.gruul.order.api.entity.SupplierOrderItem> 订单子项列表
     */
    private List<SupplierOrderItem> orderItems(SupplierOrder order, Map<Long, Set<OrderSupplierSkuDTO>> productSkus, CompletableFuture<Map<ShopProductKey, SupplierGoods>> productMapTask, CompletableFuture<Map<ActivityShopProductSkuKey, StorageSku>> skuMapTask) {
        Long supplierId = order.getSupplierId();
        String orderNo = order.getNo();
        return productSkus.entrySet()
                .stream()
                .flatMap(
                        entry -> {
                            Long productId = entry.getKey();
                            ShopProductKey productKey = new ShopProductKey().setShopId(supplierId).setProductId(productId);
                            //检查商品是否可正常使用
                            SupplierGoods goods = this.getCheckProduct(productKey, productMapTask);
                            //保存当前商品信息
                            order.getProducts().put(productId, goods.getProduct());
                            String productName = goods.getSupplierGoodsName();
                            return entry.getValue()
                                    .stream()
                                    .map(sku -> this.toOrderItem(goods.getProduct().getFreightTemplateId(), orderNo, productName, this.getCheckSku(sku.getSkuKey(), sku, skuMapTask), sku));
                        }
                ).toList();
    }

    /**
     * 构建订单子项
     *
     * @param orderNo     订单号
     * @param productName 商品名称
     * @param storageSku  商品sku
     * @param sku         订单商品信息
     * @return com.medusa.gruul.order.api.entity.SupplierOrderItem 订单子项
     */
    private SupplierOrderItem toOrderItem(Long templateId, String orderNo, String productName, StorageSku storageSku, OrderSupplierSkuDTO sku) {
        return new SupplierOrderItem()
                .setOrderNo(orderNo)
                .setSupplierId(storageSku.getShopId())
                .setPackageStatus(PackageStatus.WAITING_FOR_DELIVER)
                .setProductId(storageSku.getProductId())
                .setProductName(productName)
                .setSkuId(sku.getSkuId())
                .setNum(sku.getNum())
                .setImage(storageSku.getImage())
                .setSpecs(CollUtil.emptyIfNull(storageSku.getSpecs()))
                .setSalePrice(storageSku.getSalePrice())
                .setDealPrice(storageSku.getSalePrice())
                .setFixPrice(0L)
                .setFreightPrice(0L)
                .setExtra(new OrderItemExtra().setTemplateId(templateId).setWeight(storageSku.getWeight()));
    }

    private SupplierOrder newOrder(String mainNo, SecureUser<?> secureUser, Long supplierId, OrderCreateDTO orderCreate) {
        return new SupplierOrder()
                .setNo(RedisUtil.no(SupplierConst.NO_PREFIX, SupplierConst.NO_KEY))
                .setSupplierId(supplierId)
                .setType(orderCreate.getSellType())
                .setStatus(OrderStatus.UNPAID)
                .setShopId(secureUser.getShopId())
                .setShopUserId(secureUser.getId())
                .setPurchasePhone(secureUser.getMobile())
                .setPayAmount(0L)
                .setMainNo(mainNo)
                //发票状态 初始未申请开票
                .setInvoiceStatus(InvoiceStatus.INVOICE_NOT_START)
                .setExtra(
                        new OrderExtra()
                                .setRemark(orderCreate.getRemark())
                                .setReceiver(orderCreate.getReceiver())
                                .setPayTimeout(supplierOrderConfigService.orderTimeout().getPayTimeout())

                );
    }

    /**
     * 获取商品key和skuKey 同时校验供应商是否可用
     *
     * @param mainNo    主订单号
     * @param suppliers 订单供应商参数
     * @return 商品key和skuKey
     */
    private Tuple2<Set<ShopProductKey>, OrderStockBO> getProductKeyAndSkuKey(Long userId, String mainNo, Set<OrderSupplierDTO> suppliers) {
        //供应商 id 对应选购的商品信息
        Map<Long, Map<Long, Set<OrderSupplierSkuDTO>>> supplierProductsMap = suppliers.stream()
                .collect(Collectors.toMap(OrderSupplierDTO::getSupplierId, OrderSupplierDTO::getProductSkus));

        Set<ShopProductKey> productKeys = new HashSet<>();
        OrderStockBO orderStockBO = new OrderStockBO()
                .setNo(mainNo)
                .setUserId(userId)
                .setSkuKeyStSvMap(MapUtil.newHashMap());
        if (CollUtil.isEmpty(supplierProductsMap)) {
            return Tuple.of(productKeys, orderStockBO);
        }
        //查询供应商信息
        Map<Long, ShopInfoVO> shopMap = CollUtil.emptyIfNull(shopRpcService.getShopInfoByShopIdList(supplierProductsMap.keySet()))
                .stream()
                .collect(Collectors.toMap(ShopInfoVO::getId, v -> v));
        for (Map.Entry<Long, Map<Long, Set<OrderSupplierSkuDTO>>> shopProducts : supplierProductsMap.entrySet()) {
            Long supplierId = shopProducts.getKey();
            Map<Long, Set<OrderSupplierSkuDTO>> products = shopProducts.getValue();
            //校验供应商是否可用
            ShopInfoVO shopInfo = shopMap.get(supplierId);
            if (shopInfo == null || ShopMode.SUPPLIER != shopInfo.getShopMode() || ShopStatus.NORMAL != shopInfo.getStatus()) {
                throw SupplierError.SUPPLIER_NOT_EXIST.dataEx(ExceptionData.of(supplierId));
            }
            //查询商品信息
            for (Map.Entry<Long, Set<OrderSupplierSkuDTO>> productSkus : products.entrySet()) {
                Long productId = productSkus.getKey();
                Set<OrderSupplierSkuDTO> skus = productSkus.getValue();
                //查询商品信息
                ShopProductKey productKey = new ShopProductKey().setProductId(productId).setShopId(supplierId);
                productKeys.add(productKey);
                //查询商品sku信息
                for (OrderSupplierSkuDTO sku : skus) {
                    ActivityShopProductSkuKey skuKey = (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                            .setSkuId(sku.getSkuId())
                            .setProductId(productId).setShopId(supplierId)
                            .setActivityType(OrderType.COMMON)
                            .setActivityId(0L);
                    sku.setSkuKey(skuKey);
                    Integer num = sku.getNum();
                    orderStockBO.getSkuKeyStSvMap()
                            .put(
                                    skuKey,
                                    new StSvBo()
                                            .setStock(-num)
                                            .setSales(num)
                            );
                }
            }
        }
        return Tuple.of(productKeys, orderStockBO);
    }

    private Map<ShopProductKey, SupplierGoods> goodsMap(Set<ShopProductKey> productKeys, SellType sellType) {
        LambdaQueryChainWrapper<SupplierGoods> selectQuery = supplierGoodsService.lambdaQuery()
                .select(SupplierGoods::getId, SupplierGoods::getShopId, SupplierGoods::getSupplierGoodsName, SupplierGoods::getSupplierProductStatus, SupplierGoods::getProduct)
                .eq(SupplierGoods::getSellType, sellType);
        selectQuery.and(
                query -> productKeys.forEach(
                        productKey -> query.or(
                                orQuery -> orQuery.eq(SupplierGoods::getId, productKey.getProductId())
                                        .eq(SupplierGoods::getShopId, productKey.getShopId())
                        )

                )
        );
        return selectQuery.list()
                .stream()
                .collect(
                        Collectors.toMap(
                                goods -> new ShopProductKey().setProductId(goods.getId()).setShopId(goods.getShopId()),
                                goods -> goods
                        )
                );

    }
}
