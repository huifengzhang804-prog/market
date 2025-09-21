package com.medusa.gruul.overview.service.modules.operate.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.common.system.model.ISystem;
import com.medusa.gruul.goods.api.entity.Product;
import com.medusa.gruul.goods.api.rpc.GoodsRpcService;
import com.medusa.gruul.order.api.entity.ShopOrderItem;
import com.medusa.gruul.order.api.rpc.OrderRpcService;
import com.medusa.gruul.overview.api.entity.DealRanking;
import com.medusa.gruul.overview.api.enums.OverviewDealType;
import com.medusa.gruul.overview.service.model.param.DealRankingParam;
import com.medusa.gruul.overview.service.model.vo.DealStatisticsVO;
import com.medusa.gruul.overview.service.model.vo.SalableProductTypeVO;
import com.medusa.gruul.overview.service.model.vo.SalableShopVO;
import com.medusa.gruul.overview.service.modules.operate.service.ManageDealRankingService;
import com.medusa.gruul.overview.service.mp.operate.service.IDealRankingService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author xiaoq
 * @Description
 * @date 2022-10-20 14:18
 */
@Service
@RequiredArgsConstructor
public class ManageDealRankingServiceImpl implements ManageDealRankingService {

    private final IDealRankingService dealRankingService;

    private final ShopRpcService shopRpcService;

    private final GoodsRpcService goodsRpcService;

    private final OrderRpcService orderRpcService;

    /**
     * 交易排行信息入库
     *
     * @param shopOrderItems 完成订单内的商品信息
     */
    @Override
    public void dealRankingChange(List<ShopOrderItem> shopOrderItems) {
        List<DealRanking> dealRankingList = new ArrayList<>();
        shopOrderItems.forEach(shopOrderItem -> {
            DealRanking dealRanking = new DealRanking();
            Long productId = shopOrderItem.getProductId();
            Integer num = shopOrderItem.getNum();
            Long freightPrice = shopOrderItem.getFreightPrice();
            Long fixPrice = shopOrderItem.getFixPrice();
            Long dealPrice = shopOrderItem.getDealPrice();
            Long shopId = shopOrderItem.getShopId();
            dealRanking.setOverviewDealType(OverviewDealType.PRODUCT)
                    .setProductId(productId)
                    .setShopId(shopId)
                    .setRealTradeVolume(Long.valueOf(num))
                    .setRealTradingVolume((num * dealPrice) + fixPrice + freightPrice)
                    .setDate(LocalDate.now());
            dealRankingList.add(dealRanking);
        });
        dealRankingService.saveBatch(dealRankingList);
    }

    @Override
    public List<SalableShopVO> getSalableShop(DealRankingParam dealRankingParam) {
        disposeDealRankingParam(dealRankingParam);
        List<SalableShopVO> salableShop = dealRankingService.getSalableShop(dealRankingParam);

        // 获取代销商品热卖店铺统计数据
        List<SalableShopVO> hotSoldConsignmentProduct = Optional
                .ofNullable(orderRpcService
                        .countConsignmentProductHotSoldShopList(dealRankingParam.getStartDateTime(), dealRankingParam.getEndDateTime()))
                .orElse(new ArrayList<>())
                .stream()
                .map(item -> new SalableShopVO()
                        .setShopId(item.getShopId())
                        .setRealTradingVolume(item.getBusinessVolume())).collect(Collectors.toList());

        if (CollUtil.isEmpty(salableShop) && CollUtil.isEmpty(hotSoldConsignmentProduct)) {
            return Collections.emptyList();
        }

        // 根据交易额排序并取TOP10
        salableShop.addAll(hotSoldConsignmentProduct);

        // 根据shop group by并累计金额
        salableShop = Optional.of(salableShop.stream().collect(
                        Collectors.groupingBy(SalableShopVO::getShopId,
                                Collectors.collectingAndThen(
                                        Collectors.toList(),
                                        item -> new SalableShopVO()
                                                .setShopId(item.get(0).getShopId())
                                                .setShopName(item.get(0).getShopName())
                                                .setRealTradingVolume(item.stream().map(
                                                        SalableShopVO::getRealTradingVolume).reduce(0L, Long::sum)
                                                )
                                )
                        )
                ).values()
        ).map(e -> e.stream()
                .sorted(Comparator.comparing(SalableShopVO::getRealTradingVolume).reversed())
                .limit(10)
                .collect(Collectors.toList())
        ).orElse(new ArrayList<>());

        // 设置店铺name
        List<Long> shopIds = salableShop.stream().map(SalableShopVO::getShopId).toList();
        if (!CollectionUtils.isEmpty(shopIds)) {
            List<ShopInfoVO> shopInfoByShopIdList = shopRpcService.getShopInfoByShopIdList(new HashSet<>(shopIds));
            Map<Long, ShopInfoVO> collect = shopInfoByShopIdList.stream().collect(Collectors.toMap(ShopInfoVO::getId, x->x));
            salableShop.forEach(bean ->{
                ShopInfoVO shopInfoVO = collect.get(bean.getShopId());
                if (Objects.nonNull(shopInfoVO)) {
                    bean.setShopName(shopInfoVO.getName());
                    bean.setShopLogo(shopInfoVO.getLogo());
                }
            });
        }
        return salableShop;
    }

    @Override
    public List<DealStatisticsVO> getDealStatistics(DealRankingParam dealRankingParam) {
        disposeDealRankingParam(dealRankingParam);
        dealRankingParam.setShopId(ISystem.shopIdOpt().get());
        return dealRankingService.getDealStatistics(dealRankingParam);
    }

    @Override
    public List<SalableProductTypeVO> getSalableProductMoney(DealRankingParam dealRankingParam) {
        dealRankingParam.setShopId(ISystem.shopIdOpt().get());
        disposeDealRankingParam(dealRankingParam);
        List<SalableProductTypeVO> salableProductMoney = dealRankingService.getSalableProductMoney(dealRankingParam);
        if (CollUtil.isEmpty(salableProductMoney)) {
            return Collections.emptyList();
        }
        salableProductMoney.forEach(bean -> {
            Product product = goodsRpcService.getConditionProductInfo(bean.getShopId(), bean.getProductId());
            if (Objects.isNull(product)) {
                product = goodsRpcService.getProductBySupplierIdAndProductId(bean.getShopId(), bean.getProductId());
            }
            if (product == null) {
                return;
            }
            String productName = product.getName();
            bean.setProductName(productName);
            bean.setPic(product.getPic());
        });
        return salableProductMoney;
    }

    @Override
    public List<SalableProductTypeVO> getSalableProductNum(DealRankingParam dealRankingParam) {
        disposeDealRankingParam(dealRankingParam);
        dealRankingParam.setShopId(ISystem.shopIdOpt().get());
        List<SalableProductTypeVO> salableProductNum = dealRankingService.getSalableProductNum(dealRankingParam);
        if (CollUtil.isEmpty(salableProductNum)) {
            return Collections.emptyList();
        }
        // 获取代销商品销量TOP10并组装数据
        List<SalableProductTypeVO> consignmentProduct = Optional
                .ofNullable(orderRpcService.countConsignmentProductHotSoldList(dealRankingParam.getStartDateTime(), dealRankingParam.getEndDateTime()))
                .orElse(new ArrayList<>())
                .stream()
                .filter(e -> dealRankingParam.getShopId().equals(e.getShopId()))
                .map(
                        item -> new SalableProductTypeVO()
                                .setProductId(item.getProductId())
                                .setShopId(item.getShopId())
                                .setRealTradeVolume(Long.valueOf(item.getNum()))
                                .setRealTradingVolume(item.getDealPrice())
                )
                .toList();
        salableProductNum.addAll(consignmentProduct);

        // 按照店铺+商品ID group by并对销量求和
        salableProductNum = Optional.of(salableProductNum
                        .stream()
                        .collect(
                                Collectors.groupingBy(item -> item.getProductId() + "#" + item.getShopId(),
                                        Collectors.collectingAndThen(Collectors.toList(),
                                                item -> new SalableProductTypeVO()
                                                        .setProductId(item.get(0).getProductId())
                                                        .setShopId(item.get(0).getShopId())
                                                        .setRealTradeVolume(
                                                                item.stream().map(SalableProductTypeVO::getRealTradeVolume).reduce((long)CommonPool.NUMBER_ZERO, Long::sum)
                                                        )
                                                        .setRealTradingVolume(
                                                                item.stream().map(SalableProductTypeVO::getRealTradingVolume).reduce((long)CommonPool.NUMBER_ZERO, Long::sum)
                                                        )
                                        )
                                )
                        ))
                .map(e -> e.values()
                        .stream()
                        .sorted(Comparator.comparing(SalableProductTypeVO::getRealTradeVolume).reversed())
                        .limit(10)
                        .collect(Collectors.toList())
                )
                .orElse(new ArrayList<>());

        // 设置商品name&店铺
        salableProductNum.forEach(bean -> {
            Product shopProduct = goodsRpcService.getConditionProductInfo(bean.getShopId(), bean.getProductId());
            Product supplierProduct =null;
            if (Objects.isNull(shopProduct)) {
                 supplierProduct = goodsRpcService.getProductBySupplierIdAndProductId(bean.getShopId(), bean.getProductId());
            }
            if (shopProduct == null && supplierProduct == null) {
                return;
            }
            ShopInfoVO shopInfoVO = Optional
                    .ofNullable(shopRpcService.getShopInfoByShopId(bean.getShopId()))
                    .orElse(new ShopInfoVO());
            Product product = shopProduct == null ? supplierProduct : shopProduct;
            String productName = product.getName();
            bean.setPic(product.getPic());
            bean.setProductName(productName);
            bean.setShopName(shopInfoVO.getName());
            //bean.setSalePrice(product.getSalePrice());
        });
        return salableProductNum;
    }

    /**
     * 移动端商家后台 店铺指定月份的交易概况
     *
     * @param dealRankingParam 指定月份
     * @return 店铺指定月份的交易概况
     */
    @Override
    public DealStatisticsVO getMobileDealStatistics(DealRankingParam dealRankingParam) {
        dealRankingParam.setShopId(ISecurity.userMust().getShopId());
        String tempMonth = dealRankingParam.getCurrentMonth() + "-01";
        dealRankingParam.setStartDate(DateUtil.beginOfMonth(DateUtil.parse(tempMonth)).toLocalDateTime().toLocalDate());
        dealRankingParam.setEndDate(DateUtil.endOfMonth(DateUtil.parse(tempMonth)).toLocalDateTime().toLocalDate());
        return dealRankingService.getMobileDealStatistics(dealRankingParam);
    }


    /**
     * 日期处理
     *
     * @param dealRankingParam 交易排行查询param
     */
    private void disposeDealRankingParam(DealRankingParam dealRankingParam) {
        LocalDate today = LocalDate.now();
        switch (dealRankingParam.getDateType()) {
            case TODAY:
                dealRankingParam.setStartDate(today);
                dealRankingParam.setEndDate(today);
                break;
            case NEARLY_A_WEEK:
                dealRankingParam.setStartDate(today.minusDays(CommonPool.NUMBER_SEVEN));
                dealRankingParam.setEndDate(today);
                break;
            case NEARLY_A_MONTH:
                dealRankingParam.setStartDate(today.minusMonths(CommonPool.NUMBER_ONE));
                dealRankingParam.setEndDate(today);
                break;
            default:
        }
    }
}
