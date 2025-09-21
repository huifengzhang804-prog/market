package com.medusa.gruul.addon.supplier.modules.overview.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.medusa.gruul.addon.supplier.model.dto.SupplierGoodsTradeStaticDTO;
import com.medusa.gruul.addon.supplier.model.dto.SupplierNewProductCountDTO;
import com.medusa.gruul.addon.supplier.model.dto.SupplierTradeStaticDTO;
import com.medusa.gruul.addon.supplier.model.enums.DateRangeType;
import com.medusa.gruul.addon.supplier.model.enums.OrderStatus;
import com.medusa.gruul.addon.supplier.model.enums.PackageStatus;
import com.medusa.gruul.addon.supplier.model.enums.SupplierError;
import com.medusa.gruul.addon.supplier.model.vo.SupplierGoodsTradeAmountTopVO;
import com.medusa.gruul.addon.supplier.model.vo.SupplierGoodsTradeStaticVO;
import com.medusa.gruul.addon.supplier.model.vo.SupplierGoodsTradeTopVO;
import com.medusa.gruul.addon.supplier.model.vo.ToDoListVO;
import com.medusa.gruul.addon.supplier.modules.overview.service.ISupplierOverviewService;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierGoods;
import com.medusa.gruul.addon.supplier.mp.entity.SupplierOrder;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierGoodsService;
import com.medusa.gruul.addon.supplier.mp.service.ISupplierOrderService;
import com.medusa.gruul.addon.supplier.utils.DateUtils;
import com.medusa.gruul.carrier.pigeon.api.rpc.PigeonChatStatisticsRpcService;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.SellType;
import com.medusa.gruul.common.module.app.shop.ShopMode;
import com.medusa.gruul.common.module.app.shop.ShopStatus;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.order.api.model.ConsignmentOrderStatisticDTO;
import com.medusa.gruul.order.api.model.ConsignmentProductTradeStatisticDTO;
import com.medusa.gruul.order.api.model.ConsignmentProductTradeTopDTO;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>供应商经营概况服务实现类</p>
 *
 * @author An.Yan
 */
@Service
@RequiredArgsConstructor
public class SupplierOverviewServiceImpl implements ISupplierOverviewService {

    private final PigeonChatStatisticsRpcService pigeonChatStatisticsRpcService;
    private final ISupplierGoodsService supplierGoodsService;
    private final ISupplierOrderService supplierOrderService;
    private final ShopRpcService shopRpcService;
    private final OrderRpcService orderRpcService;


    /**
     * 按照店铺统计当天新增咨询数
     *
     * @param supplierShopId 供应商店铺ID
     * @return 当天新增咨询数
     */
    @Override
    public Integer getInquiryNumberByShopId(Long supplierShopId) {
        // Pigeon服务已经做了必要的检查,所以这里不需要检查店铺信息
        return pigeonChatStatisticsRpcService.getInquiryNumber(supplierShopId);
    }

    /**
     * 取时间范围内新增的商品数量
     *
     * @param shopId        店铺ID
     * @param dateRangeType 日期范围
     * @return {@link Integer}新增的商品数量
     */
    @Override
    public Integer getNewCreatedProductCount(Long shopId, DateRangeType dateRangeType) {
        checkShop(shopId);
        return supplierGoodsService.countNewProduct(new SupplierNewProductCountDTO(shopId, dateRangeType));
    }

    /**
     * 统计今日待办数据
     *
     * @param shopId 店铺ID
     * @return {@link ToDoListVO}
     */
    @Override
    public ToDoListVO getToDoList(Long shopId, DateRangeType dateRangeType) {
//        SupplierToDoListDTO queryDTO = new SupplierToDoListDTO(shopId, dateRangeType);
//        checkShop(shopId);
        // 获取代销单统计数据
        ConsignmentOrderStatisticDTO consignmentDTO = orderRpcService.countConsignmentOrderStatistic(shopId);

        // 获取采购单统计数据
//        result.setPendingPaymentOrders(result.getPendingPaymentOrders() + consignmentDTO.getPendingPaymentOrders());
//        result.setPendingDeliveredOrders(result.getPendingDeliveredOrders() + consignmentDTO.getPendingDeliveredOrders());
//        result.setPendingReceivedOrders(result.getPendingReceivedOrders() + consignmentDTO.getPendingReceivedOrders());
        ToDoListVO result = new ToDoListVO();
        //待支付订单数
        result.setPendingPaymentOrders(
                supplierOrderService.lambdaQuery()
                        .eq(SupplierOrder::getSupplierId, shopId)
                        .eq(SupplierOrder::getType, SellType.PURCHASE)
                        .eq(SupplierOrder::getStatus, OrderStatus.UNPAID)
                        .count() + consignmentDTO.getPendingPaymentOrders()
        );
        //待发货订单数
        result.setPendingDeliveredOrders(
                supplierOrderService.lambdaQuery()
                        .eq(SupplierOrder::getSupplierId, shopId)
                        .eq(SupplierOrder::getType, SellType.PURCHASE)
                        .eq(SupplierOrder::getStatus, OrderStatus.PAID)
                        .exists(
                                """
                                        SELECT  1 FROM t_supplier_order_item b
                                        WHERE t_supplier_order.`no` = b.order_no
                                        AND b.package_status = {0}
                                        """,
                                PackageStatus.WAITING_FOR_DELIVER.getValue()
                        )
                        .count() + consignmentDTO.getPendingDeliveredOrders()
        );

        //待收货订单数量
        result.setPendingReceivedOrders(
                supplierOrderService.lambdaQuery()
                        .eq(SupplierOrder::getSupplierId, shopId)
                        .eq(SupplierOrder::getType, SellType.PURCHASE)
                        .eq(SupplierOrder::getStatus, OrderStatus.PAID)
                        .exists(
                                """
                                        SELECT  1 FROM t_supplier_order_item b
                                        WHERE t_supplier_order.`no` = b.order_no
                                        AND b.package_status = {0}
                                        """,
                                PackageStatus.WAITING_FOR_RECEIVE.getValue()
                        )
                        .count() + consignmentDTO.getPendingReceivedOrders()
        );

        return result;
    }

    /**
     * 获取供应商时间范围内商品交易量&交易额
     *
     * @param dto {@link SupplierTradeStaticDTO}
     * @return {@link SupplierGoodsTradeStaticVO}
     */
    @Override
    public List<SupplierGoodsTradeStaticVO> getSupplierGoodsTradeStatic(SupplierTradeStaticDTO dto) {

        // 获取供应商采购商品交易统计数据
        List<SupplierGoodsTradeStaticVO> supplierGoodsTradeStatic = supplierOrderService.getSupplierGoodsTradeStatic(dto);

        // 获取供应商代销商品交易统计数据
        List<ConsignmentProductTradeStatisticDTO> consignmentStatistic = orderRpcService.countConsignmentProductTradeStatistic(dto.getShopId(),
                DateUtils.convert2Date(dto.getBeginTime(), DateUtils.DATE_FORMATTER),
                DateUtils.convert2Date(dto.getEndTime(), DateUtils.DATE_FORMATTER));

        // key: 交易日期; value: 统计对象
        Map<String, ConsignmentProductTradeStatisticDTO> consignmentMap = consignmentStatistic
                .stream()
                .collect(Collectors.toMap(ConsignmentProductTradeStatisticDTO::getXDate, a -> a, (a, b) -> a));

        // 以交易日期为维度,加上代销数据
        supplierGoodsTradeStatic.forEach(item -> {
            ConsignmentProductTradeStatisticDTO consignmentItem = consignmentMap.get(item.getXDate());
            if (consignmentItem == null) {
                return;
            }
            item.setTradeAmount(item.getTradeAmount() + consignmentItem.getTradeAmount());
            item.setTradeNumber(item.getTradeNumber() + consignmentItem.getTradeNumber());
        });
        return supplierGoodsTradeStatic;
    }

    /**
     * 获取供应商商品交易量TOP数据
     *
     * @param dto {@link SupplierGoodsTradeStaticDTO}
     * @return {@link SupplierGoodsTradeTopVO}
     */
    @Override
    public List<SupplierGoodsTradeTopVO> getSupplierGoodsTradeNumTopList(SupplierGoodsTradeStaticDTO dto) {
        dto.settingAndCheckAttributes();
        checkShop(dto.getSupplierId());

        // 获取采购商品TOP数据
        List<SupplierGoodsTradeTopVO> topList = this.supplierOrderService.getSupplierGoodsTradeNumTopList(dto);

        // 获取代销商品TOP数据
        List<ConsignmentProductTradeTopDTO> consignmentProduct = orderRpcService.countConsignmentProductTradeTop(dto.getSupplierId(),
                DateUtils.convert2DateTime(dto.getBeginTime(), DateUtils.DATE_TIME_FORMATTER),
                DateUtils.convert2DateTime(dto.getEndTime(), DateUtils.DATE_TIME_FORMATTER));
        topList.addAll(consignmentProduct
                .stream()
                .map(item -> new SupplierGoodsTradeTopVO()
                        .setProductId(item.getProductId())
                        .setNumber(item.getTradeNumber())
                )
                .toList()
        );
        if (CollectionUtil.isEmpty(topList)) {
            return Lists.newArrayList();
        }
        // 设置product name && rank
        AtomicInteger rank = new AtomicInteger(CommonPool.NUMBER_ONE);


        // 按照金额大小取5条
        topList = topList
                .stream()
                .sorted(Comparator.comparing(SupplierGoodsTradeTopVO::getNumber).reversed())
                .limit(CommonPool.NUMBER_TEN).collect(Collectors.toList());
        List<Long> productIds = topList.stream().map(SupplierGoodsTradeTopVO::getProductId).toList();
        List<SupplierGoods> supplierGoods = supplierGoodsService.listByIds(productIds);
        if (CollectionUtil.isNotEmpty(supplierGoods)) {
            Map<Long, SupplierGoods> collect = supplierGoods.stream()
                    .collect(Collectors.toMap(SupplierGoods::getId, Function.identity()));
            topList.forEach(e -> {
                e.setRank(rank.getAndIncrement());
                SupplierGoods goods = collect.get(e.getProductId());
                if (Objects.nonNull(goods)) {
                    e.setProductName(goods.getSupplierGoodsName());
                    e.setPic(goods.getProduct().getPic());
                }
            });
        }

        return topList;
    }

    /**
     * 获取供应商商品交易金额TOP数据
     *
     * @param dto {@link SupplierGoodsTradeStaticDTO}
     * @return {@link SupplierGoodsTradeAmountTopVO}
     */
    @Override
    public List<SupplierGoodsTradeAmountTopVO> getSupplierGoodsTradeAmountTopList(SupplierGoodsTradeStaticDTO dto) {
        dto.settingAndCheckAttributes();
        checkShop(dto.getSupplierId());
        // 获取采购商品TOP数据
        List<SupplierGoodsTradeAmountTopVO> topList = this.supplierOrderService.getSupplierGoodsTradeAmountTopList(dto);

        // 获取代销商品TOP数据
        List<ConsignmentProductTradeTopDTO> consignmentProduct = orderRpcService.countConsignmentProductTradeTop(dto.getSupplierId(),
                DateUtils.convert2DateTime(dto.getBeginTime(), DateUtils.DATE_TIME_FORMATTER),
                DateUtils.convert2DateTime(dto.getEndTime(), DateUtils.DATE_TIME_FORMATTER));
        topList.addAll(consignmentProduct
                .stream()
                .map(item -> new SupplierGoodsTradeAmountTopVO()
                        .setAmount(item.getTradeAmount())
                        .setProductId(item.getProductId())
                )
                .toList()
        );
        if (CollectionUtil.isEmpty(topList)) {
            return Lists.newArrayList();
        }
        List<Long> productIds = topList.stream().map(SupplierGoodsTradeAmountTopVO::getProductId).toList();
        List<SupplierGoods> supplierGoods = supplierGoodsService.listByIds(Sets.newHashSet(productIds));
        Map<Long, SupplierGoods> supplierGoodsMap = Maps.newHashMap();
        if (CollectionUtil.isNotEmpty(supplierGoods)) {
            supplierGoodsMap = supplierGoods.stream()
                    .collect(Collectors.toMap(BaseEntity::getId, x -> x));
        }
        AtomicReference<Integer> rank = new AtomicReference<>(CommonPool.NUMBER_ONE);
        // 按照金额大小取10条
        Map<Long, SupplierGoods> finalSupplierGoodsMap = supplierGoodsMap;
        topList = topList
                .stream()
                .sorted(Comparator.comparing(SupplierGoodsTradeAmountTopVO::getAmount).reversed())
                .limit(CommonPool.NUMBER_TEN)
                .peek(e -> {
                    e.setRank(rank.getAndSet(rank.get() + CommonPool.NUMBER_ONE));
                    SupplierGoods temp = finalSupplierGoodsMap.get(e.getProductId());
                    if (Objects.nonNull(temp)) {
                        e.setProductName(temp.getSupplierGoodsName());
                        e.setPic(temp.getProduct().getPic());
                    }

                })
                .collect(Collectors.toList());
        return topList;
    }

    /**
     * 获取所有供应商采购单数量
     *
     * @return {@link Long}
     */
    @Override
    public Long getPurchaseOrderCount() {
        return this.supplierOrderService.lambdaQuery().count();
    }

    /**
     * 检查店铺信息
     *
     * @param shopId 店铺ID
     */
    private void checkShop(Long shopId) {
        ShopInfoVO shopInfo = shopRpcService.getShopInfoByShopId(shopId);
        SupplierError.SUPPLIER_NOT_EXIST.trueThrow(shopInfo == null ||
                ShopMode.SUPPLIER != shopInfo.getShopMode() ||
                ShopStatus.NORMAL != shopInfo.getStatus());
    }
}
