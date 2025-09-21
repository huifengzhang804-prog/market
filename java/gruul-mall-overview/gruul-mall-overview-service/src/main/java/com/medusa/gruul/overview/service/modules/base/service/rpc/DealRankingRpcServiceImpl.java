package com.medusa.gruul.overview.service.modules.base.service.rpc;

import com.medusa.gruul.overview.api.entity.DealRanking;
import com.medusa.gruul.overview.api.enums.OverviewDealType;
import com.medusa.gruul.overview.api.model.SupplierDealRankingDTO;
import com.medusa.gruul.overview.api.rpc.DealRankingRpcService;
import com.medusa.gruul.overview.service.mp.operate.service.IDealRankingService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>deal ranking RPC接口实现类</p>
 * @author An.Yan
 */
@Service
@DubboService
@RequiredArgsConstructor
public class DealRankingRpcServiceImpl implements DealRankingRpcService {

    private final IDealRankingService dealRankingService;

    /**
     * 保存deal ranking数据
     *
     * @param dto {@link SupplierDealRankingDTO}
     */
    @Override
    public void saveDealRanking(SupplierDealRankingDTO dto) {
        List<DealRanking> dealRankingList = new ArrayList<>();
        dto.getOrderItems().forEach(orderItem -> {
            DealRanking dealRanking = new DealRanking();
            Long productId = orderItem.getProductId();
            Integer num = orderItem.getNum();
            Long freightPrice = orderItem.getFreightPrice();
            Long fixPrice = orderItem.getFixPrice();
            Long dealPrice = orderItem.getDealPrice();
            Long shopId = dto.getSupplierId();
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
}
