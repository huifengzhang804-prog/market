package com.medusa.gruul.overview.service.mp.statement.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.overview.api.entity.OverviewStatement;
import com.medusa.gruul.overview.service.model.StatementCountModel;
import com.medusa.gruul.overview.service.model.dto.OverviewStatementQueryDTO;
import com.medusa.gruul.overview.service.model.dto.PurchasePayoutQueryDTO;
import com.medusa.gruul.overview.service.model.vo.PurchasePayoutVO;
import com.medusa.gruul.overview.service.mp.statement.mapper.OverviewStatementMapper;
import com.medusa.gruul.overview.service.mp.statement.service.IOverviewStatementService;
import com.medusa.gruul.shop.api.model.vo.ShopInfoVO;
import com.medusa.gruul.shop.api.rpc.ShopRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 对账单 服务实现类
 * </p>
 *
 * @author WuDi
 * @since 2022-10-09
 */
@Service
@RequiredArgsConstructor
public class OverviewStatementServiceImpl extends ServiceImpl<OverviewStatementMapper, OverviewStatement> implements IOverviewStatementService {

    private final ShopRpcService shopRpcService;

    @Override
    public IPage<OverviewStatement> queryStatement(OverviewStatementQueryDTO query) {
        return baseMapper.queryStatement(query.getPage(), query);
    }

    @Override
    public List<StatementCountModel> statistics(OverviewStatementQueryDTO query) {
        return baseMapper.statistics(query);
    }

    /**
     * 根据订单号获取采购单
     *
     * @param orderNo 订单号
     * @return {@link OverviewStatement}
     */
    @Override
    public List<OverviewStatement> listOverviewStatementByOrderNo(String orderNo) {
        return this
                .lambdaQuery()
                .eq(OverviewStatement :: getOrderNo, orderNo)
                .eq(OverviewStatement :: getDeleted, Boolean.FALSE)
                .list();
    }

    /**
     * 分页查询采购支出
     * @param query {@link PurchasePayoutQueryDTO}
     * @return {@link PurchasePayoutVO}
     */
    @Override
    public IPage<PurchasePayoutVO> queryPurchasePayoutList(PurchasePayoutQueryDTO query) {
        query.setShopId(ISecurity.userMust().getShopId());
        IPage<PurchasePayoutVO> page = this.baseMapper.queryPurchasePayoutList(query);
        if (CollectionUtils.isEmpty(page.getRecords())) {
            return new Page<>();
        }
        Set<Long> supplierIds = page.getRecords()
                .stream()
                .map(PurchasePayoutVO::getSupplierId)
                .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(supplierIds)) {
            return page;
        }
        Map<Long, String> shopInfoMap = Optional.ofNullable(
                shopRpcService.getShopInfoByShopIdList(supplierIds)
                        .stream()
                        .collect(Collectors.toMap(ShopInfoVO :: getId, ShopInfoVO :: getName, (a, b) -> a))
                ).orElse(new HashMap<>());
        page.getRecords().forEach(e -> e.setSupplierName(shopInfoMap.get(e.getSupplierId())));
        return page;

    }
}
