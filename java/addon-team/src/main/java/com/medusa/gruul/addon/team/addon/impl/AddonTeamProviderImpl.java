package com.medusa.gruul.addon.team.addon.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.medusa.gruul.addon.team.addon.AddonTeamProvider;
import com.medusa.gruul.addon.team.model.OrderTeamCache;
import com.medusa.gruul.addon.team.model.constant.TeamConst;
import com.medusa.gruul.addon.team.model.dto.TeamSkuDTO;
import com.medusa.gruul.addon.team.model.enums.TeamMode;
import com.medusa.gruul.addon.team.model.enums.TeamOrderStatus;
import com.medusa.gruul.addon.team.model.enums.TeamProductStatus;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamProductVO;
import com.medusa.gruul.addon.team.mp.entity.TeamOrder;
import com.medusa.gruul.addon.team.mp.service.ITeamOrderService;
import com.medusa.gruul.addon.team.service.TeamService;
import com.medusa.gruul.addon.team.util.TeamUtil;
import com.medusa.gruul.common.addon.provider.AddonProvider;
import com.medusa.gruul.common.addon.provider.AddonProviders;
import com.medusa.gruul.common.model.base.ShopProductSkuKey;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.common.model.enums.OrderType;
import com.medusa.gruul.common.redis.util.RedisUtil;
import com.medusa.gruul.global.model.constant.Services;
import com.medusa.gruul.global.model.exception.GlobalException;
import com.medusa.gruul.global.model.o.RangeDateTime;
import com.medusa.gruul.goods.api.constant.GoodsConstant;
import com.medusa.gruul.goods.api.model.vo.ActivityDetailVO;
import com.medusa.gruul.order.api.addon.OrderAddonConstant;
import com.medusa.gruul.order.api.addon.activity.ActivityParam;
import com.medusa.gruul.order.api.addon.activity.ActivityResp;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author 张治保
 * date 2023/3/16
 */
@Service
@DubboService
@AddonProviders
@RequiredArgsConstructor
public class AddonTeamProviderImpl implements AddonTeamProvider {

    private final ITeamOrderService teamOrderService;
    private final TeamService teamService;


    private Integer userNum(TeamProductVO teamProduct, JSONObject extra) {
        TeamMode mode = teamProduct.getMode();
        if (TeamMode.COMMON == mode) {
            return teamProduct.getUsers().get(0);
        }
        Integer userNum = extra.getInteger(TeamConst.SELECTED_USERS_FIELD);
        if (userNum == null || !teamProduct.getUsers().contains(userNum)) {
            throw new GlobalException("请选择正确拼团人数");
        }
        return userNum;
    }


    private TeamProductVO getTeamProduct(Long activityId, Long shopId, Long productId) {
        TeamProductVO teamProduct = TeamUtil.getProductOpenedTeam(shopId, productId);
        if (TeamProductStatus.OPEN != teamProduct.getTeamStatus()) {
            throw new GlobalException("该商品暂未开启拼团");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(teamProduct.getStartTime())) {
            throw new GlobalException("活动未开始");
        }
        if (!activityId.equals(teamProduct.getActivityId())) {
            throw new GlobalException("该活动已结束");
        }
        return teamProduct;
    }

    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "activityBudget")
    public ActivityResp teamBudget(ActivityParam param) {
        //校验参数,并取出用户选择的参团人数
        validParam(param);
        //获取活动信息
        ShopProductSkuKey key = param.getSkuKeyMap().entrySet().iterator().next().getKey();
        TeamProductVO teamProduct = this.getTeamProduct(param.getActivityId(), key.getShopId(), key.getProductId());
        //获取团号
        //获取商品价格
        Long skuPrice = getSkuPrice(
                teamProduct.getSkus(),
                key,
                teamProduct.currentNumIndex(
                        this.userNum(teamProduct, param.getExtra())
                )
        );
        return new ActivityResp()
                .setSkuKeyPriceMap(Map.of(key, skuPrice))
                .setStackable(teamProduct.getStackable());
    }


    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_ORDER, supporterId = OrderAddonConstant.ORDER_DISCOUNT_SUPPORT_ID, methodName = "activity")
    public ActivityResp teamOrder(ActivityParam param) {
        //校验参数,并取出用户选择的参团人数
        validParam(param);
        //获取sku
        Map.Entry<ShopProductSkuKey, Integer> entry = param.getSkuKeyMap().entrySet().iterator().next();
        //获取活动信息
        ShopProductSkuKey key = entry.getKey();
        TeamProductVO teamProduct = this.getTeamProduct(param.getActivityId(), key.getShopId(), key.getProductId());

        Integer userNum = this.userNum(teamProduct, param.getExtra());
        //获取团号
        String teamNo = getTeamNo(param, key, param.getActivityId());
        //获取商品价格
        Long skuPrice = getSkuPrice(
                teamProduct.getSkus(),
                key,
                teamProduct.currentNumIndex(userNum)
        );
        ActivityResp resp = new ActivityResp()
                .setSkuKeyPriceMap(Map.of(key, skuPrice))
                .setStackable(teamProduct.getStackable());
        resp.getExtra().set(TeamConst.NO_FIELD, teamNo);
        //缓存订单信息与对应团号
        RedisUtil.setCacheMap(
                RedisUtil.key(TeamConst.ORDER_TEAM_CACHE_KEY, param.getOrderNo()),
                new OrderTeamCache()
                        .setTeamNo(teamNo)
                        .setProductNum(entry.getValue())
                        .setActivityId(teamProduct.getActivityId())
                        .setUserNum(userNum)
                        .setSkuKey(key)
                        .setSkuPrice(skuPrice),
                teamProduct.getStackable().getPayTimeout().plusMinutes(CommonPool.NUMBER_THREE)
        );
        return resp;
    }

    @Override
    @AddonProvider(filter = FILTER, service = Services.GRUUL_MALL_GOODS, supporterId = GoodsConstant.ADDON_SUPPORT_ID, methodName = "activity")
    public ActivityDetailVO activity(Long activityId, Long userId, ShopProductSkuKey key) {
        Long shopId = key.getShopId();
        Long productId = key.getProductId();
        TeamProductVO teamProduct = TeamUtil.getProductOpenedTeam(shopId, productId);
        if (TeamProductStatus.NO == teamProduct.getTeamStatus()) {
            teamService.openTeamActivity(new TeamKey().setShopId(shopId));
        }
        teamProduct = TeamUtil.getProductOpenedTeam(shopId, productId);
        if (TeamProductStatus.NO == teamProduct.getTeamStatus()) {
            return null;
        }
        Long skuId = key.getSkuId();
        TeamSkuDTO teamSku = teamProduct.getSkus()
                .stream()
                .filter(sku -> sku.getSkuId().equals(skuId))
                .findFirst()
                .orElse(null);

        return new ActivityDetailVO()
                .setType(OrderType.TEAM)
                .setActivityId(teamProduct.getActivityId())
                .setActivityPrice(teamSku == null ? null : teamSku.getPrices().get(CommonPool.NUMBER_ZERO))
                .setTime(
                        new RangeDateTime()
                                .setStart(teamProduct.getStartTime())
                                .setEnd(teamProduct.getEndTime())
                )
                .setStackable(teamProduct.getStackable());

    }

    private String getTeamNo(ActivityParam param, ShopProductSkuKey skuKey, Long activityId) {
        String teamNo;
        //不为空需要 对团号进行校验 是否可以继续使用
        if (StrUtil.isNotEmpty(teamNo = param.getExtra().getString(TeamConst.NO_FIELD))) {
            checkTeamNo(param.getUserId(), skuKey, activityId, teamNo);
        }
        return StrUtil.isEmpty(teamNo) ? TeamUtil.teamNoGenerate() : teamNo;
    }

    /**
     * 检查当前团是否已满 或者已过期 或者不存在 或者当前用户已经参加过该团
     *
     * @param teamNo 团号
     */
    private void checkTeamNo(Long userId, ShopProductSkuKey skuKey, Long activityId, String teamNo) {
        if (StrUtil.isEmpty(teamNo)) {
            return;
        }
        List<TeamOrder> teamOrders = teamOrderService.lambdaQuery()
                .eq(TeamOrder::getActivityId, activityId)
                .eq(TeamOrder::getShopId, skuKey.getShopId())
                .eq(TeamOrder::getProductId, skuKey.getProductId())
                .eq(TeamOrder::getTeamNo, teamNo)
                .eq(TeamOrder::getStatus, TeamOrderStatus.ING)
                .list();
        if (CollUtil.isEmpty(teamOrders)) {
            throw new GlobalException("该团已不存在或该团已满员");
        }
        if (teamOrders.stream().anyMatch(teamOrder -> teamOrder.getUserId().equals(userId))) {
            throw new GlobalException("您已参加过该团");
        }

    }

    /**
     * 获取当前团sku价格
     *
     * @param skus   sku列表
     * @param skuKey sku key
     * @param index  价格索引
     * @return 价格
     */
    private Long getSkuPrice(List<TeamSkuDTO> skus, ShopProductSkuKey skuKey, int index) {
        return skus.stream()
                .filter(sku -> sku.getSkuId().equals(skuKey.getSkuId()))
                .findFirst()
                .map(sku -> sku.getPrices().get(index))
                .orElseThrow(() -> new GlobalException("sku价格设置错误"));

    }

    /**
     * 校验参数
     *
     * @param param 参数
     */
    private void validParam(ActivityParam param) {
        Long activityId = param.getActivityId();
        if (activityId == null) {
            throw new GlobalException("活动id不能为空");
        }
        Map<ShopProductSkuKey, Integer> skuKeyMap = param.getSkuKeyMap();
        if (skuKeyMap == null || skuKeyMap.isEmpty()) {
            throw new GlobalException("sku不能为空");
        }
        if (skuKeyMap.size() > 1) {
            throw new GlobalException("sku只能有一个");
        }
    }
}
