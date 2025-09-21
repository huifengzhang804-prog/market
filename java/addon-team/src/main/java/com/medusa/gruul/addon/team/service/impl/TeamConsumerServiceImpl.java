package com.medusa.gruul.addon.team.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.team.model.count.TeamProductUserCount;
import com.medusa.gruul.addon.team.model.dto.TeamOrderPageDTO;
import com.medusa.gruul.addon.team.model.dto.TeamProductOrderPageDTO;
import com.medusa.gruul.addon.team.model.enums.TeamOrderStatus;
import com.medusa.gruul.addon.team.model.enums.TeamProductStatus;
import com.medusa.gruul.addon.team.model.vo.TeamActivityProductVO;
import com.medusa.gruul.addon.team.model.vo.TeamOrderPageVO;
import com.medusa.gruul.addon.team.model.vo.TeamProductVO;
import com.medusa.gruul.addon.team.mp.service.ITeamOrderService;
import com.medusa.gruul.addon.team.mp.service.ITeamProductService;
import com.medusa.gruul.addon.team.service.TeamConsumerService;
import com.medusa.gruul.addon.team.util.TeamUtil;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.storage.api.entity.StorageSku;
import com.medusa.gruul.storage.api.rpc.StorageRpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author 张治保
 * date 2023/3/15
 */
@Service
@RequiredArgsConstructor
public class TeamConsumerServiceImpl implements TeamConsumerService {

    private final StorageRpcService storageRpcService;
    private final ITeamOrderService teamOrderService;
    private final ITeamProductService teamProductService;

    @Override
    public TeamProductVO productTeamStatus(Long shopId, Long productId) {
        return TeamUtil.getProductOpenedTeam(shopId, productId);
    }


    @Override
    public IPage<TeamOrderPageVO> teamProductOrderPage(TeamProductOrderPageDTO query) {
        TeamProductVO teamProduct = this.productTeamStatus(query.getShopId(), query.getProductId());
        TeamProductStatus teamStatus = teamProduct.getTeamStatus();
        if (TeamProductStatus.OPEN != teamStatus) {
            throw new GlobalException(SystemCode.PARAM_VALID_ERROR_CODE, "商品拼团活动未开启");
        }
        return teamOrderService.orderPage(
                new TeamOrderPageDTO()
                        .setActivityId(teamProduct.getActivityId())
                        .setShopId(query.getShopId())
                        .setProductId(query.getProductId())
                        .setStatus(TeamOrderStatus.ING)
                        .setUserId(query.getUserId())
                        .setTeamNo(query.getTeamNo())
        );
    }

    @Override
    public IPage<TeamActivityProductVO> activityProductPage(Page<TeamActivityProductVO> query) {
        IPage<TeamActivityProductVO> result = teamProductService.activityProductPage(query);
        List<TeamActivityProductVO> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }
        Map<ActivityShopProductKey, TeamActivityProductVO> keyProductMap = records.stream()
                .collect(
                        Collectors.toMap(
                                record -> {
                                    ActivityShopProductKey key = new ActivityShopProductKey().setProductId(record.getProductId());
                                    key.setShopId(record.getShopId()).setActivityType(OrderType.TEAM).setActivityId(record.getActivityId());
                                    return key;
                                },
                                record -> record
                        )
                );

        Map<ActivityShopProductKey, Integer> keyUserNumMap = teamProductService.teamProductUserCounts(keyProductMap.keySet())
                .stream()
                .collect(
                        Collectors.toMap(
                                value -> {
                                    ActivityShopProductKey key = new ActivityShopProductKey().setProductId(value.getProductId());
                                    key.setShopId(value.getShopId()).setActivityType(OrderType.TEAM).setActivityId(value.getActivityId());
                                    return key;
                                },
                                TeamProductUserCount::getUserNum
                        )
                );
        Map<ActivityShopProductKey, List<StorageSku>> activityShopProductKeyListMap = storageRpcService.productSkuStockBatch(keyProductMap.keySet());
        keyProductMap.forEach(
                (key, value) -> {
                    List<StorageSku> storageSkus = activityShopProductKeyListMap.get(key);
                    boolean empty = CollUtil.isEmpty(storageSkus);
                    value.setUserNum(MapUtil.getInt(keyUserNumMap, key, CommonPool.NUMBER_ZERO));
                    value.setMinPrice(CollUtil.min(value.getPrices().stream().flatMap(List::stream).collect(Collectors.toSet())));
                    value.setStock(
                            empty ? 0L :
                                    storageSkus.stream()
                                            .map(StorageSku::getStock)
                                            .reduce(Long::sum)
                                            .orElse(0L)
                    );
                    value.setPrice(
                            empty ? 0L :
                                    CollUtil.max(
                                            storageSkus.stream().map(StorageSku::getPrice).collect(Collectors.toSet())
                                    )
                    );
                }
        );
        return result;
    }


}
