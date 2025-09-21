package com.medusa.gruul.addon.supplier.modules.order.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.google.common.collect.Maps;
import com.medusa.gruul.addon.supplier.model.SupplierConst;
import com.medusa.gruul.addon.supplier.model.bo.OrderTimeNodes;
import com.medusa.gruul.addon.supplier.model.bo.PublishProductKey;
import com.medusa.gruul.addon.supplier.model.dto.*;
import com.medusa.gruul.addon.supplier.model.enums.*;
import com.medusa.gruul.addon.supplier.model.vo.*;
import com.medusa.gruul.addon.supplier.modules.order.handler.DeliverTypeHandler;
import com.medusa.gruul.addon.supplier.modules.order.service.SupplierOrderHandleService;
import com.medusa.gruul.addon.supplier.mp.entity.*;
import com.medusa.gruul.addon.supplier.mp.service.*;
import com.medusa.gruul.common.fastjson2.FastJson2;
import com.medusa.gruul.common.model.base.ActivityShopProductSkuKey;
import com.medusa.gruul.common.model.base.ProductSkuKey;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.mp.model.SqlHelper;
import com.medusa.gruul.common.redis.annotation.Redisson;
import com.medusa.gruul.common.security.model.enums.Roles;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.web.handler.Handler;
import com.medusa.gruul.common.web.util.SpringUtils;
import com.medusa.gruul.global.model.o.Final;
import com.medusa.gruul.global.model.o.KeyValue;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.model.dto.ProductExtraDTO;
import com.medusa.gruul.overview.api.model.PurchaseOrderReq;
import com.medusa.gruul.overview.api.model.SupplierDealRankingDTO;
import com.medusa.gruul.overview.api.rpc.DealRankingRpcService;
import com.medusa.gruul.overview.api.rpc.OverviewRpcService;
import com.medusa.gruul.service.uaa.api.rpc.UaaRpcService;
import com.medusa.gruul.service.uaa.api.vo.UserInfoVO;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import com.medusa.gruul.storage.api.bo.StSvBo;
import com.medusa.gruul.storage.api.dto.SkuCopy;
import com.medusa.gruul.storage.api.dto.StoragesCopyDTO;
import com.medusa.gruul.storage.api.enums.StorageRabbit;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import io.vavr.Tuple;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2023/7/24
 */
@Service
@RequiredArgsConstructor
public class SupplierOrderHandleServiceImpl implements SupplierOrderHandleService {

    private final RabbitTemplate rabbitTemplate;
    private final ShopRpcService shopRpcService;
    private final StorageRpcService storageRpcService;
    private final OverviewRpcService overviewRpcService;
    private final ISupplierOrderService supplierOrderService;
    private final ISupplierOrderItemService supplierOrderItemService;
    private final ISupplierOrderPackageService supplierOrderPackageService;
    private final ISupplierGoodsPublishService supplierGoodsPublishService;
    private final ISupplierStorageRecordService supplierStorageRecordService;
    private final DealRankingRpcService dealRankingRpcService;
    private final UaaRpcService uaaRpcService;
    private SupplierOrderHandleService supplierOrderHandleService;



    @Override
    public IPage<SupplierOrder> orderPage(OrderQueryPageDTO query) {


        IPage<SupplierOrder> result = supplierOrderService.orderPage(query);
        if (result.getRecords().isEmpty()) {
            return result;
        }
        //统计所有查询出来的订单号
        Set<String> orderNos = result.getRecords().stream().map(SupplierOrder::getNo).collect(Collectors.toSet());
        //构建查询条件
        LambdaQueryChainWrapper<SupplierOrderItem> itemQueryWrapper = supplierOrderItemService.lambdaQuery()
                .select(SupplierOrderItem::getSkuId,
                        SupplierOrderItem::getId, SupplierOrderItem::getImage, SupplierOrderItem::getSpecs,
                        SupplierOrderItem::getOrderNo, SupplierOrderItem::getSupplierId, SupplierOrderItem::getSalePrice,
                        SupplierOrderItem::getDealPrice, SupplierOrderItem::getNum, SupplierOrderItem::getFreightPrice,
                        SupplierOrderItem::getProductId, SupplierOrderItem::getProductName, SupplierOrderItem::getPackageStatus
                )
                .in(SupplierOrderItem::getOrderNo, orderNos)
                .eq(query.getSupplierId() != null, SupplierOrderItem::getSupplierId, query.getSupplierId());

        //查询订单已入库的数据
        Map<String, Integer> stockInCountInfo = supplierStorageRecordService.queryStockInCountInfo(orderNos);
        //根据订单号分组
        Map<String, List<SupplierOrderItem>> orderNoSupplierOrdersMap = itemQueryWrapper.list()
                .stream()
                .collect(Collectors.groupingBy(SupplierOrderItem::getOrderNo));
        //设置到每个订单中
        result.getRecords().forEach(order -> order.setOrderItems(orderNoSupplierOrdersMap.getOrDefault(order.getNo(), List.of())));
        Set<Long> userIds = result.getRecords().stream().map(SupplierOrder::getShopUserId).collect(Collectors.toSet());
        Map<Long, UserInfoVO> userInfoMap = uaaRpcService.getUserDataBatchByUserIds(userIds);
        //查询店铺信息
        Set<Long> shopIds = result.getRecords().stream().map(SupplierOrder::getShopId)
                .collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(shopIds)) {
            List<ShopInfoVO> shopInfoList = shopRpcService.getShopInfoByShopIdList(shopIds);
            if (CollectionUtil.isNotEmpty(shopInfoList)) {
                Map<Long, ShopInfoVO> shopInfoVOMap = shopInfoList.stream()
                        .collect(Collectors.toMap(ShopInfoVO::getId, x -> x, (a, b) -> a));
                result.getRecords().forEach(order -> {
                    ShopInfoVO shopInfoVO = shopInfoVOMap.get(order.getShopId());
                    if (shopInfoVO != null) {
                        order.setShopName(shopInfoVO.getName());
                        order.setShopContractNumber(shopInfoVO.getContractNumber());
                    }


                });
            }
        }
        //查询发票信息

        result.getRecords().forEach(order -> {
            order.setHasProductStorage(stockInCountInfo.getOrDefault(order.getNo(), 0) > 0);
            order.setStockInCount(stockInCountInfo.getOrDefault(order.getNo(), 0));
            UserInfoVO userInfoVO = userInfoMap.get(order.getShopUserId());
            if (Objects.nonNull(userInfoVO)) {
                String name = StringUtils.isEmpty(userInfoVO.getNickname()) ? userInfoVO.getUsername() : userInfoVO.getNickname();
                order.setPurchaseNickName(name);
            }
        });
        if (Objects.isNull(query.getStatus())|| OrderQueryStatus.WAITING_FOR_DELIVER.equals(query.getStatus())||
                OrderQueryStatus.WAITING_FOR_PUTIN.equals(query.getStatus())
        ) {
            result.getRecords().forEach(order -> {
                Integer waitForDeliveryCount = 0;
                Integer hasDeliveryCount = 0;
                for (SupplierOrderItem orderItem : order.getOrderItems()) {
                    if (PackageStatus.WAITING_FOR_DELIVER.equals(orderItem.getPackageStatus())) {
                            //待发货
                        waitForDeliveryCount+=orderItem.getNum();
                    }else {
                        hasDeliveryCount+=orderItem.getNum();
                    }
                }
                order.setWaitForDeliveryCount(waitForDeliveryCount);
                order.setHasDeliveryCount(hasDeliveryCount);
            });

        }
        return result;
    }

    @Override
    public SupplierOrder order(OrderDetailQueryDTO query) {
        SupplierOrder order = supplierOrderService.order(query);
        if (order == null) {
            return null;
        }
        //查询已入库数量
        SupplierStorageRecord record = supplierStorageRecordService.lambdaQuery()
                .eq(SupplierStorageRecord::getOrderNo, query.getOrderNo())
                .eq(query.getSupplierId() != null, SupplierStorageRecord::getSupplierId, query.getSupplierId())
                .eq(query.getShopId() != null, SupplierStorageRecord::getShopId, query.getShopId())
                .one();
        Map<ProductSkuKey, Integer> skuRecords = new HashMap<>(record == null || record.getSkuRecords() == null ? Map.of() : record.getSkuRecords());
        if (record != null && record.getSkuRecords() != null) {
            order.getOrderItems()
                    .forEach(orderItem -> {
                        Integer num = skuRecords.remove(new ProductSkuKey().setProductId(orderItem.getProductId()).setSkuId(orderItem.getSkuId()));
                        orderItem.setUsed(num == null ? 0 : num);
                    });

        }
        //供应商端需要额外查询店铺信息
        ISecurity.match().when(
                (user) -> {
                    ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(order.getShopId());
                    if (shopInfo == null) {
                        return;
                    }
                    order.setExtraInfo(
                            new JSONObject()
                                    .fluentPut("shopName", shopInfo.getName())
                                    .fluentPut("shopPhone", shopInfo.getContractNumber())
                    );
                },
                Roles.SUPPLIER_ADMIN, Roles.SUPPLIER_CUSTOM_ADMIN
        );
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = SupplierConst.ORDER_STATUS_UPDATE_LOCK_PREFIX, batchParamName = "#orderNos", key = "#item")
    public void updateOrderStatus(boolean isSystem, Set<String> orderNos, OrderStatus currentStatus, OrderStatus nextStatus) {
        int updateCount = supplierOrderService.getBaseMapper()
                .update(
                        null,
                        Wrappers.lambdaUpdate(SupplierOrder.class)
                                .set(SupplierOrder::getStatus, nextStatus)
                                .eq(SupplierOrder::getStatus, currentStatus)
                                .in(SupplierOrder::getNo, orderNos)
                );
        if (isSystem) {
            return;
        }
        if (updateCount != orderNos.size()) {
            throw SupplierError.ORDER_STATUS_CHANGED.exception();
        }
        if (!nextStatus.isClosed()) {
            return;
        }
        //如果是订单关闭操作 则退换库存
        List<SupplierOrderItem> items = supplierOrderItemService.lambdaQuery()
                .select(
                        SupplierOrderItem::getOrderNo,
                        SupplierOrderItem::getSupplierId, SupplierOrderItem::getProductId,
                        SupplierOrderItem::getSkuId, SupplierOrderItem::getNum
                )
                .in(SupplierOrderItem::getOrderNo, orderNos)
                .list();
        Map<String, Map<ActivityShopProductSkuKey, StSvBo>> orderSkuStocksMap = new HashMap<>();
        for (SupplierOrderItem orderItem : items) {
            Map<ActivityShopProductSkuKey, StSvBo> skuStocks = orderSkuStocksMap.computeIfAbsent(orderItem.getOrderNo(), k -> new HashMap<>());
            StSvBo stSvBo = skuStocks.computeIfAbsent(
                    (ActivityShopProductSkuKey) new ActivityShopProductSkuKey()
                            .setSkuId(orderItem.getSkuId())
                            .setProductId(orderItem.getProductId())
                            .setShopId(orderItem.getSupplierId())
                            .setActivityId(0L)
                            .setActivityType(OrderType.COMMON),
                    key -> new StSvBo()
            );
            Integer num = orderItem.getNum();
            //加库存 减销量
            stSvBo.incrementStock(num).incrementSales(-num);
        }
        rabbitTemplate.convertAndSend(StorageRabbit.RETURN_STOCK.exchange(), StorageRabbit.RETURN_STOCK.routingKey(), orderSkuStocksMap);
    }

    @Override
    @Redisson(name = SupplierConst.ORDER_STATUS_UPDATE_LOCK_PREFIX, key = "#orderPay.orderNo")
    @Transactional(rollbackFor = Exception.class)
    public void orderPay(Long shopId, OrderPayDTO orderPay) {
        orderPay.validParam();
        String orderNo = orderPay.getOrderNo();
        SupplierOrder order = supplierOrderService.lambdaQuery()
                .select(SupplierOrder::getStatus, SupplierOrder::getSupplierId, SupplierOrder::getProducts)
                .eq(SupplierOrder::getNo, orderNo)
                .eq(SupplierOrder::getShopId, shopId)
                .one();
        if (order == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (order.getStatus() != OrderStatus.UNPAID) {
            throw SupplierError.ORDER_STATUS_CHANGED.exception();
        }
        boolean success = supplierOrderService.lambdaUpdate()
                .setSql(SqlHelper.renderJsonSetSql("extra", Tuple.of("pay", JSON.toJSONString(orderPay), null)))
                .set(SupplierOrder::getTimeNodes, JSON.toJSONString(new OrderTimeNodes().setPayTime(LocalDateTime.now())))
                .eq(SupplierOrder::getNo, orderNo)
                .eq(SupplierOrder::getStatus, OrderStatus.UNPAID)
                .update();
        SupplierError.ORDER_STATUS_CHANGED.falseThrow(success);
        OrderPayType payType = orderPay.getPayType();
        supplierOrderHandleService.updateOrderStatus(false, Set.of(orderNo), OrderStatus.UNPAID, payType.getSuccessStatus());
        if (payType == OrderPayType.BALANCE) {
            Long supplierId = order.getSupplierId();
            List<SupplierOrderItem> items = supplierOrderItemService.lambdaQuery()
                    .select(SupplierOrderItem::getProductId, SupplierOrderItem::getDealPrice,
                            SupplierOrderItem::getNum, SupplierOrderItem::getFreightPrice)
                    .eq(SupplierOrderItem::getOrderNo, orderNo)
                    .eq(SupplierOrderItem::getSupplierId, supplierId)
                    .list();
            if (CollUtil.isEmpty(items)) {
                return;
            }
            Final<Long> freight = new Final<>(0L);
            //根据 productId 分组 计算每个 productId 对应的金额
            Map<Long, Integer> productNumMap = new HashMap<>();
            Map<Long, Long> productIdAmountMap = items.stream()
                    .collect(Collectors.groupingBy(SupplierOrderItem::getProductId))
                    .entrySet()
                    .stream()
                    .collect(
                            Collectors.toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue()
                                            .stream()
                                            .map(item -> {
                                                Integer number = productNumMap.get(item.getProductId());
                                                number = number == null ? CommonPool.NUMBER_ZERO : number;
                                                productNumMap.put(item.getProductId(),
                                                        number + item.getNum());
                                                freight.set(freight.get() + item.getFreightPrice());
                                                return item.getDealPrice() * item.getNum();
                                            })
                                            .reduce(0L, Long::sum)
                            )
                    );
            PurchaseOrderReq req = new PurchaseOrderReq();
            req.setOrderNo(orderNo);
            req.setBusinessShopId(shopId);
            req.setSupplierShopId(supplierId);
            req.setItems(
                    productIdAmountMap.entrySet()
                            .stream()
                            .map(entry -> {
                                PurchaseOrderReq.PurchaseOrderGoodsItem item = new PurchaseOrderReq.PurchaseOrderGoodsItem();
                                item.setAmount(entry.getValue());
                                item.setProductId(entry.getKey());
                                item.setNum(productNumMap.get(entry.getKey()));
                                Product product = order.getProducts().get(entry.getKey());
                                Long rate = 0L;
                                ProductExtraDTO extra;
                                if (product != null && (extra = product.getExtra()) != null) {
                                    rate = extra.getCustomDeductionRatio() != null ? extra.getCustomDeductionRatio() : rate;
                                }
                                item.setRate(rate);
                                return item;
                            })
                            .collect(Collectors.toList())
            );
            req.setFreight(freight.get());
            overviewRpcService.generateStatementOfPurchase(req);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = SupplierConst.ORDER_STATUS_UPDATE_LOCK_PREFIX, key = "#audit.orderNo")
    public void orderPayAudit(Long supplierId, OrderPayAuditDTO audit) {
        SupplierOrder order = supplierOrderService.lambdaQuery()
                .eq(SupplierOrder::getNo, audit.getOrderNo())
                .eq(SupplierOrder::getSupplierId, supplierId)
                .one();
        if (order == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (order.getStatus() != OrderStatus.PAYMENT_AUDIT) {
            throw SupplierError.ORDER_STATUS_CHANGED.exception();
        }
        supplierOrderHandleService.updateOrderStatus(Boolean.FALSE, Set.of(audit.getOrderNo()), OrderStatus.PAYMENT_AUDIT, audit.getSuccess() ? OrderStatus.PAID : OrderStatus.AUDIT_FAIL_CLOSED);

        // 调用overview rpc 更新real_ranking
        List<SupplierOrderItem> orderItems = supplierOrderItemService
                .lambdaQuery()
                .eq(SupplierOrderItem::getOrderNo, order.getNo())
                .list();
        if (CollectionUtils.isEmpty(orderItems)) {
            return;
        }
        SupplierDealRankingDTO supplierDealRankingDTO = new SupplierDealRankingDTO()
                .setSupplierId(order.getSupplierId())
                .setShopId(order.getShopId()).setNo(order.getNo())
                .setOrderItems(
                        orderItems.stream()
                                .map(
                                        item -> new SupplierDealRankingDTO.SupplierOrderItem()
                                                .setDealPrice(item.getDealPrice())
                                                .setNum(item.getNum())
                                                .setFixPrice(item.getFixPrice())
                                                .setFreightPrice(item.getFreightPrice())
                                                .setProductId(item.getProductId())
                                                .setSalePrice(item.getSalePrice())
                                )
                                .collect(Collectors.toList()));
        dealRankingRpcService.saveDealRanking(supplierDealRankingDTO);
    }

    @Override
    public void closeOrder(OrderMatchQueryDTO orderMatch, OrderStatus closeStatus) {
        boolean exists = supplierOrderService.lambdaQuery()
                .eq(SupplierOrder::getNo, orderMatch.getOrderNo())
                .eq(orderMatch.getSupplierId() != null, SupplierOrder::getSupplierId, orderMatch.getSupplierId())
                .eq(orderMatch.getShopId() != null, SupplierOrder::getShopId, orderMatch.getShopId())
                .exists();
        if (!exists) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        supplierOrderHandleService.updateOrderStatus(false, Set.of(orderMatch.getOrderNo()), OrderStatus.UNPAID, closeStatus);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = SupplierConst.ORDER_STATUS_UPDATE_LOCK_PREFIX, key = "#delivery.orderNo")
    public void orderDelivery(Long supplierId, OrderDeliveryDTO delivery) {
        delivery.validParam();
        String orderNo = delivery.getOrderNo();
        SupplierOrder order = supplierOrderService.lambdaQuery()
                .select(SupplierOrder::getStatus, SupplierOrder::getExtra)
                .eq(SupplierOrder::getNo, orderNo)
                .eq(SupplierOrder::getSupplierId, supplierId)
                .one();
        if (order == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (OrderStatus.PAID != order.getStatus()) {
            throw SupplierError.ORDER_NOT_PAID.exception();
        }
        Handler<Object> handler = SpringUtils.getBean(DeliverTypeHandler.class, delivery.getDeliverType());
        handler.handle(supplierId, delivery, order.getExtra());
    }

    @Override
    public void orderDeliveryBatch(Long supplierId, Set<OrderDeliveryDTO> deliveries) {
        deliveries.forEach(delivery -> supplierOrderHandleService.orderDelivery(supplierId, delivery));
    }

    @Override
    public BigDecimal splitDeliveryItems(String orderNo, Long packageId, Set<OrderDeliveryItemDTO> items) {
        Map<Long, OrderDeliveryItemDTO> itemIdDeliverMap = items.stream().collect(Collectors.toMap(OrderDeliveryItemDTO::getItemId, v -> v));
        List<SupplierOrderItem> orderItems = supplierOrderItemService.lambdaQuery()
                .eq(SupplierOrderItem::getOrderNo, orderNo)
                .eq(SupplierOrderItem::getPackageStatus, PackageStatus.WAITING_FOR_DELIVER)
                .in(SupplierOrderItem::getId, itemIdDeliverMap.keySet())
                .list();
        if (orderItems.size() != items.size()) {
            throw SupplierError.ORDER_STATUS_CHANGED.exception();
        }
        BigDecimal totalWeight = BigDecimal.ZERO;
        //是否有未发货商品
        List<SupplierOrderItem> saveOrUpdateItems = new ArrayList<>();
        for (SupplierOrderItem orderItem : orderItems) {
            Long itemId = orderItem.getId();
            OrderDeliveryItemDTO deliveryItem = itemIdDeliverMap.get(itemId);
            int num = deliveryItem.getNum();
            int currentNum = orderItem.getNum();
            if (num > currentNum) {
                throw SupplierError.SPLIT_NUMBER_ERROR.exception();
            }
            int leftNum = currentNum - num;
            if (leftNum > 0) {
                saveOrUpdateItems.add(orderItem.copyItem(leftNum));
            }
            totalWeight = totalWeight.add(orderItem.getExtra().getWeight().multiply(new BigDecimal(num)));
            saveOrUpdateItems.add(
                    orderItem.setNum(num)
                            .setPackageStatus(PackageStatus.WAITING_FOR_RECEIVE)
                            .setPackageId(packageId)
            );
        }
        supplierOrderItemService.saveOrUpdateBatch(saveOrUpdateItems);
        //更新发货时间
        supplierOrderService.lambdaUpdate()
                .eq(SupplierOrder::getNo, orderNo)
                .setSql(SqlHelper.renderJsonSetSql("time_nodes", Tuple.of("deliveryTime", LocalDateTime.now().format(FastJson2.DATE_FORMATTER))))
                .update();
        return totalWeight;
    }

    @Override
    public List<SupplierOrderPackage> deliveryPackages(OrderMatchQueryDTO deliveryQuery) {
        return supplierOrderPackageService.deliveryPackages(deliveryQuery);
    }

    @Override
    public OrderStorageVO storageQuery(OrderMatchQueryDTO query) {
        OrderStorageVO orderStorage = supplierOrderService.orderStorage(query);
        if (orderStorage == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        SupplierStorageRecord record = supplierStorageRecordService.lambdaQuery()
                .eq(SupplierStorageRecord::getOrderNo, query.getOrderNo())
                .eq(query.getSupplierId() != null, SupplierStorageRecord::getSupplierId, query.getSupplierId())
                .eq(query.getShopId() != null, SupplierStorageRecord::getShopId, query.getShopId())
                .one();
        Map<ProductSkuKey, Integer> skuRecords = record == null ? Collections.emptyMap() : record.getSkuRecords();
        for (OrderStorageProductVO product : orderStorage.getProducts()) {
            for (OrderStorageProductSkuVO sku : product.getSkus()) {
                sku.setUsed(skuRecords.getOrDefault(new ProductSkuKey().setProductId(product.getProductId()).setSkuId(sku.getSkuId()), 0));
            }
        }
        return orderStorage.setRemark(record == null ? null : record.getRemark());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Redisson(name = SupplierConst.PRODUCT_STORAGE_LOCK_PREFIX, key = "#storage.orderNo")
    public void storage(OrderStorageDTO storage) {
        String orderNo = storage.getOrderNo();
        //查询订单信息与发货商品总数
        OrderStorageVO orderStorage = supplierOrderService.orderStorage(new OrderMatchQueryDTO().setOrderNo(orderNo).setShopId(storage.getShopId()));
        if (orderStorage == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        //供应商 id
        Long supplierId = orderStorage.getSupplierId();
        //查询历史入库记录
        SupplierStorageRecord record = supplierStorageRecordService.lambdaQuery()
                .eq(SupplierStorageRecord::getOrderNo, storage.getOrderNo())
                .eq(SupplierStorageRecord::getShopId, storage.getShopId())
                .one();
        if (record == null) {
            record = new SupplierStorageRecord()
                    .setOrderNo(storage.getOrderNo())
                    .setShopId(storage.getShopId())
                    .setSupplierId(supplierId)
                    .setSkuRecords(new HashMap<>(CommonPool.NUMBER_EIGHT));
        }
        record.setRemark(storage.getRemark());

        Map<ProductSkuKey, OrderStorageProductSkuVO> skuKeyStorageMap = orderStorage.skuKeyStorageMap();
        Set<KeyValue<ProductSkuKey, Integer>> skuStorages = storage.getSkuStorages();
        //校验已入库数 和当前入库数只和是否超过购买数量
        for (KeyValue<ProductSkuKey, Integer> skuStorage : skuStorages) {
            ProductSkuKey currentKey = skuStorage.getKey();
            OrderStorageProductSkuVO storageSkuRecord = skuKeyStorageMap.get(currentKey);
            if (storageSkuRecord == null) {
                throw SupplierError.STORAGE_SKU_RECORD_NOT_EXIST.dataEx(currentKey);
            }
            Integer value = skuStorage.getValue();
            //总入库数
            int totalStorage = value + record.getSkuRecords().getOrDefault(currentKey, 0);
            if (totalStorage > storageSkuRecord.getNum()) {
                throw SupplierError.STORAGE_NUM_SET_ERROR.dataEx(currentKey);
            }
            record.getSkuRecords().put(currentKey, totalStorage);
        }
        //更新入库记录
        supplierStorageRecordService.saveOrUpdate(record);
        //检查是否订单里的所有商品都已入库 都已入库 更新为已完成
        if (checkOrderStorage(skuKeyStorageMap, record.getSkuRecords())) {
            //更新收货时间
            supplierOrderService.lambdaUpdate()
                    .eq(SupplierOrder::getNo, orderNo)
                    .setSql(SqlHelper.renderJsonSetSql("time_nodes", Tuple.of("receiveTime", LocalDateTime.now().format(FastJson2.DATE_FORMATTER))))
                    .update();
            //更新商品状态
            supplierOrderItemService.lambdaUpdate()
                    .set(SupplierOrderItem::getPackageStatus, PackageStatus.COMPLETED)
                    .eq(SupplierOrderItem::getOrderNo, orderNo)
                    .update();
        }
        //生成发布商品发布数据
        this.generateProductPublish(storage.getShopId(), supplierId, skuStorages.stream().map(kv -> kv.getKey().getProductId()).collect(Collectors.toSet()));
        //rpc sku copy
        storageRpcService.storageCopy(
                new StoragesCopyDTO()
                        .setTransactionId(orderNo)
                        .setTargetShopId(storage.getShopId())
                        .setTargets(
                                skuStorages.stream()
                                        .map(
                                                current -> new SkuCopy()
                                                        .setSourceKey(new ShopProductSkuKey().setShopId(supplierId).setProductId(current.getKey().getProductId()).setSkuId(current.getKey().getSkuId()))
                                                        .setNum(current.getValue())
                                                        .setProductName(skuKeyStorageMap.get(current.getKey()).getProductName())
                                        ).collect(Collectors.toSet())
                        )
        );
    }

    /**
     * 检查是否订单里的所有商品都已入库
     */
    private boolean checkOrderStorage(Map<ProductSkuKey, OrderStorageProductSkuVO> skuKeyStorageMap, Map<ProductSkuKey, Integer> skuRecords) {
        for (Map.Entry<ProductSkuKey, OrderStorageProductSkuVO> skuStorageEntry : skuKeyStorageMap.entrySet()) {
            ProductSkuKey key = skuStorageEntry.getKey();
            OrderStorageProductSkuVO value = skuStorageEntry.getValue();
            //该 sku
            Integer used = skuRecords.get(key);
            if (used == null) {
                return false;
            }
            if (value.getNum() > used) {
                return false;
            }
        }
        return true;
    }

    /**
     * 生成发布商品发布数据
     *
     * @param shopId     店铺id
     * @param supplierId 供应商id
     * @param productIds 商品id集合
     */
    private void generateProductPublish(Long shopId, Long supplierId, Set<Long> productIds) {
        List<SupplierGoodsPublish> published = supplierGoodsPublishService.lambdaQuery()
                .eq(SupplierGoodsPublish::getShopId, shopId)
                .eq(SupplierGoodsPublish::getSupplierId, supplierId)
                .in(SupplierGoodsPublish::getProductId, productIds)
                .list();
        LocalDateTime now = LocalDateTime.now();
        Set<Long> existsProductIds = CollUtil.emptyIfNull(published).stream()
                .map(SupplierGoodsPublish::getProductId)
                .collect(Collectors.toSet());
        List<SupplierGoodsPublish> newPublish = CollUtil.subtract(productIds, existsProductIds)
                .stream()
                .map(productId -> new SupplierGoodsPublish()
                        .setShopId(shopId)
                        .setSupplierId(supplierId)
                        .setProductId(productId)
                        .setPublished(Boolean.FALSE)
                        .setCreateTime(now)
                ).toList();
        supplierGoodsPublishService.saveBatch(newPublish);

    }

    @Override
    @Redisson(name = SupplierConst.ORDER_STATUS_UPDATE_LOCK_PREFIX, key = "#orderMatch.orderNo")
    public void orderComplete(OrderMatchQueryDTO orderMatch) {
        String orderNo = orderMatch.getOrderNo();
        SupplierOrder order = supplierOrderService.lambdaQuery()
                .select(SupplierOrder::getStatus)
                .eq(SupplierOrder::getNo, orderNo)
                .eq(SupplierOrder::getShopId, orderMatch.getShopId())
                .one();
        if (order == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (OrderStatus.PAID != order.getStatus()) {
            throw SupplierError.ORDER_NOT_PAID.exception();
        }
        List<SupplierOrderItem> items = supplierOrderItemService.lambdaQuery()
                .select(SupplierOrderItem::getPackageStatus)
                .eq(SupplierOrderItem::getOrderNo, orderNo)
                .list();
        //判断是已全部完成 如果已全部完成则提示订单已完成
        if (items.stream().allMatch(current -> PackageStatus.COMPLETED == current.getPackageStatus())) {
            throw SupplierError.ORDER_STATUS_CHANGED.exception();
        }
        //判断是否有已发货商品 有一件已发货发货商品就可以 把订单更新为已完成
        if (items.stream().allMatch(current -> PackageStatus.WAITING_FOR_DELIVER == current.getPackageStatus())) {
            throw SupplierError.ORDER_CANNOT_COMPLETE.exception();
        }
        //更新收货时间
        supplierOrderService.lambdaUpdate()
                .eq(SupplierOrder::getNo, orderNo)
                .setSql(SqlHelper.renderJsonSetSql("time_nodes", Tuple.of("receiveTime", LocalDateTime.now().format(FastJson2.DATE_FORMATTER))))
                .update();
        //更新商品状态
        supplierOrderItemService.lambdaUpdate()
                .set(SupplierOrderItem::getPackageStatus, PackageStatus.COMPLETED)
                .eq(SupplierOrderItem::getOrderNo, orderNo)
                .update();
    }

    @Override
    public void orderRemarkBatch(OrderRemarkBatchDTO remark) {
        supplierOrderService.lambdaUpdate()
                .set(SupplierOrder::getRemark, remark.getRemark())
                .in(SupplierOrder::getNo, remark.getOrderNos())
                .eq(SupplierOrder::getSupplierId, remark.getSupplierId())
                .update();
    }

    @Override
    public IPage<PublishProductVO> publishPage(PublishProductQueryDTO query) {
        IPage<PublishProductVO> result = supplierOrderService.publishPage(query);
        if (result.getTotal() == 0) {
            return result;
        }
        Map<PublishProductKey, PublishProductVO> productMap = result.getRecords().stream()
                .collect(
                        Collectors.toMap(
                                product -> new PublishProductKey()
                                        .setSupplierId(product.getSupplierId())
                                        .setShopId(product.getShopId())
                                        .setProductId(product.getProductId()),
                                v -> v
                        )
                );
        List<PublishProductKey> list = supplierOrderService.getPublishNum(productMap.keySet());

        //提取订单号
        Set<String> orderNos = list.stream().map(PublishProductKey::getOrderNo)
                .collect(Collectors.toSet());
        //查询订单已经入库数量
        Map<String, Integer> stockInCountInfo = supplierStorageRecordService.queryStockInCountInfo(orderNos);

        Map<PublishProductKey, Long> productKeyNumMap = Maps.newHashMap();
        //商品待发货数量
        Map<Long,Long> productDeliverCountMap = Maps.newHashMap();
        Map<Long, Long> productKeyStockeInMap = supplierStorageRecordService.queryProductStockInCountInfo(orderNos);
        for (PublishProductKey publishProductKey : list) {
            productDeliverCountMap.putIfAbsent(publishProductKey.getProductId(),0L);
            if (PackageStatus.WAITING_FOR_DELIVER.equals(publishProductKey.getPackageStatus())) {
                productDeliverCountMap.computeIfPresent(publishProductKey.getProductId(),
                        (k,v)->v+publishProductKey.getCount()
                );
            }
            PublishProductKey key = new PublishProductKey();
            key.setSupplierId(publishProductKey.getSupplierId());
            key.setShopId(publishProductKey.getShopId());
            key.setProductId(publishProductKey.getProductId());
            //获取后从Map中移除 防止重复计算
            stockInCountInfo.remove(publishProductKey.getOrderNo());
            if (productKeyNumMap.containsKey(key)) {
                productKeyNumMap.put(key, productKeyNumMap.get(key) + publishProductKey.getCount());
            } else {
                productKeyNumMap.put(key, Long.valueOf(publishProductKey.getCount()));
            }
        }
        //填充总采购数量
        //填充已入库的数量
        productMap.forEach(
                (key, value) -> {
                    value.setNum(productKeyNumMap.getOrDefault(key, 0L));
                    value.setWaitForDeliveryCount(productDeliverCountMap.getOrDefault(key.getProductId(),0L));
                    value.setHasDeliveryCount(value.getNum()-value.getWaitForDeliveryCount());
                    value.setStockInCount(productKeyStockeInMap.getOrDefault(key.getProductId(), 0L));

                }
        );

        Set<Long> supplierIds = result.getRecords().stream().map(PublishProductVO::getSupplierId)
                .filter(Objects::nonNull).collect(
                        Collectors.toSet());
        if (CollectionUtil.isNotEmpty(supplierIds)) {
            List<ShopInfoVO> shopInfoByShopIdList = shopRpcService.getShopInfoByShopIdList(
                    supplierIds);
            Map<Long, ShopInfoVO> shopInfoVOMap = shopInfoByShopIdList.stream()
                    .collect(Collectors.toMap(ShopInfoVO::getId, Function.identity()));
            result.getRecords().forEach(bean -> {
                if (shopInfoVOMap.containsKey(bean.getSupplierId())) {
                    bean.setSupplierContractNumber(shopInfoVOMap.get(bean.getSupplierId()).getContractNumber());
                }
            });
        }

        return result;
    }

    @Override
    public PublishProductGetVO publishProductGet(PublishProductGetDTO publishProductGet) {
        Long shopId = publishProductGet.getShopId();
        Long supplierId = publishProductGet.getSupplierId();
        Long productId = publishProductGet.getProductId();
        SupplierGoodsPublish publish = supplierGoodsPublishService.lambdaQuery()
                .select(SupplierGoodsPublish::getPublished)
                .eq(SupplierGoodsPublish::getShopId, shopId)
                .eq(SupplierGoodsPublish::getSupplierId, supplierId)
                .eq(SupplierGoodsPublish::getProductId, productId)
                .one();
        if (publish == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        if (publish.getPublished()) {
            throw SupplierError.PRODUCT_PUBLISHED.exception();
        }
        SupplierOrder order = supplierOrderService.lambdaQuery()
                .select(SupplierOrder::getProducts)
                .eq(SupplierOrder::getSupplierId, supplierId)
                .eq(SupplierOrder::getShopId, shopId)
                .eq(SupplierOrder::getStatus, OrderStatus.PAID)
                .exists("SELECT 1 FROM t_supplier_order_item WHERE order_no = t_supplier_order.no AND product_id = {0}", productId)
                .last(SqlHelper.SQL_LIMIT_1)
                .one();
        if (order == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        Map<Long, Product> products = order.getProducts();
        if (products == null) {
            throw SystemCode.DATA_NOT_EXIST.exception();
        }
        return new PublishProductGetVO()
                .setSupplierId(supplierId)
                .setProductId(productId)
                .setProduct(products.get(productId));
    }

    @Autowired
    public void setSupplierOrderHandleService(SupplierOrderHandleService supplierOrderHandleService) {
        this.supplierOrderHandleService = supplierOrderHandleService;
    }
}
