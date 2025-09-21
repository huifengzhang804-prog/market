package com.medusa.gruul.addon.team.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.team.model.count.TeamProductCount;
import com.medusa.gruul.addon.team.model.count.TeamProductInfoCount;
import com.medusa.gruul.addon.team.model.count.TeamProductUserCount;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamActivityProductVO;
import com.medusa.gruul.addon.team.mp.entity.TeamProduct;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.storage.api.entity.StorageSku;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 拼团活动商品关联表 服务类
 * </p>
 *
 * @author 张治保
 */
public interface ITeamProductService extends IService<TeamProduct> {

    /**
     * 获取拼团商品统计数据
     *
     * @param teamKeys 拼团商品key集合
     * @return 拼团商品统计数据
     */
    List<TeamProductCount> teamProductCounts(Set<TeamKey> teamKeys);


    /**
     * 分页查询拼团活动商品列表
     *
     * @param query 分页参数
     * @return 分页结果数据
     */
    IPage<TeamActivityProductVO> activityProductPage(Page<TeamActivityProductVO> query);


    /**
     * 批量查询活动商品参与人数
     *
     * @param keys 活动商品key集合
     * @return 参与人数
     */
    Set<TeamProductUserCount> teamProductUserCounts(Set<ActivityShopProductKey> keys);

    /**
     * 批量查询活动商品信息
     *
     * @param keys keys
     * @return 商品名称和商品图片
     */
    Set<TeamProductInfoCount> productsInfo(Set<ActivityShopProductKey> keys);

    /**
     * 获取当前正在进行的拼团活动商品信息
     *
     * @param skuList skuList
     * @return 正在进行拼团活动的活动id和商品信息
     */
    List<TeamProduct> getCurrentTeamProduct(List<StorageSku> skuList);
}
