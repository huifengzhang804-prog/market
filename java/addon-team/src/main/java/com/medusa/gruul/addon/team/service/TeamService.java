package com.medusa.gruul.addon.team.service;

import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.mp.entity.TeamActivity;
import com.medusa.gruul.addon.team.mp.entity.TeamProduct;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author 张治保
 * date 2023/3/16
 */
public interface TeamService {

    /**
     * 开启拼团活动
     *
     * @param teamKey 拼团活动key
     */
    void openTeamActivity(TeamKey teamKey);

    /**
     * 开启拼团活动
     *
     * @param activity 拼团活动
     * @param products 拼团商品
     */
    void openTeamActivity(TeamActivity activity, List<TeamProduct> products);

    /**
     * 关闭拼团活动 退还库存
     *
     * @param teamKey 拼团活动key
     */
    void closeTeamActivity(TeamKey teamKey);


    /**
     * 关闭拼团活动 退还库存
     *
     * @param teamActivity 拼团活动数据
     */
    void closeTeamActivity(TeamActivity teamActivity);


    /**
     * 删除商品拼团活动缓存数据
     *
     * @param scoreProductKeysMap 商品分数key集合
     */
    void removeProductsActivityCache(Map<Long, Set<String>> scoreProductKeysMap);

    /**
     * 关闭拼团订单
     *
     * @param teamNo 拼团订单号
     */
    void closeTeamOrder(String teamNo);


}
