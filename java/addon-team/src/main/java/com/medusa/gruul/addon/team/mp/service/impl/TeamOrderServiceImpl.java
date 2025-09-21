package com.medusa.gruul.addon.team.mp.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.team.model.count.TeamOrderCount;
import com.medusa.gruul.addon.team.model.count.TeamProductInfoCount;
import com.medusa.gruul.addon.team.model.dto.TeamOrderPageDTO;
import com.medusa.gruul.addon.team.model.dto.TeamSummaryDTO;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamOrderPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderSummaryVO;
import com.medusa.gruul.addon.team.mp.entity.TeamOrder;
import com.medusa.gruul.addon.team.mp.mapper.TeamOrderMapper;
import com.medusa.gruul.addon.team.mp.service.ITeamOrderService;
import com.medusa.gruul.addon.team.mp.service.ITeamProductService;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 拼团订单表 服务实现类
 * </p>
 *
 * @author 张治保
 */
@Service
@RequiredArgsConstructor
public class TeamOrderServiceImpl extends ServiceImpl<TeamOrderMapper, TeamOrder> implements ITeamOrderService {

    private final ITeamProductService productService;

    @Override
    public List<TeamOrderCount> teamOrderCounts(Set<TeamKey> keys) {
        return baseMapper.teamOrderCounts(keys);
    }

    @Override
    public IPage<TeamOrderPageVO> orderPage(TeamOrderPageDTO query) {
        IPage<TeamOrderPageVO> result = baseMapper.orderPage(query);
        List<TeamOrderPageVO> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }
        if (Objects.nonNull(query.getUserId())) {
            // 获取团号
            Set<String> teamNos = records.stream().map(TeamOrderPageVO::getTeamNo).collect(Collectors.toSet());
            List<TeamOrder> list = lambdaQuery()
                    .select(TeamOrder::getTeamNo)
                    .in(TeamOrder::getTeamNo, teamNos)
                    .eq(TeamOrder::getUserId, query.getUserId())
                    .list();
            Set<String> joinedTeamIds = list.stream().map(TeamOrder::getTeamNo).collect(Collectors.toSet());
            for (TeamOrderPageVO record : records) {
                //如果已经加入的拼图 则不可以再次加入
                record.setCanJoin(!joinedTeamIds.contains(record.getTeamNo()));
            }
        }

        Map<ActivityShopProductKey, List<TeamOrderPageVO>> keyOrdersMap = records.stream()
                .collect(
                        Collectors.groupingBy(
                                record -> {
                                    ActivityShopProductKey key = new ActivityShopProductKey().setProductId(record.getProductId());
                                    key.setShopId(record.getShopId()).setActivityId(record.getActivityId());
                                    return key;
                                }
                        )
                );
        Set<TeamProductInfoCount> productsInfo = productService.productsInfo(keyOrdersMap.keySet());
        Map<ActivityShopProductKey, TeamProductInfoCount> keyInfoMap = productsInfo.stream()
                .collect(
                        Collectors.toMap(
                                info -> {
                                    ActivityShopProductKey key = new ActivityShopProductKey().setProductId(info.getProductId());
                                    key.setShopId(info.getShopId()).setActivityId(info.getActivityId());
                                    return key;
                                },
                                info -> info
                        )
                );
        keyOrdersMap.forEach(
                (key, orders) -> {
                    TeamProductInfoCount info = keyInfoMap.get(key);
                    orders.forEach(
                            order -> order.setProductName(info.getProductName()).setProductImage(info.getProductImage())
                    );
                }
        );
        return result;
    }

    @Override
    public TeamOrderSummaryVO getOrderSummary(TeamSummaryDTO summaryQuery) {
        return baseMapper.getOrderSummary(summaryQuery);
    }
}
