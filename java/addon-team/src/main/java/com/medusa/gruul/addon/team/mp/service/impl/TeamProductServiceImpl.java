package com.medusa.gruul.addon.team.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.team.model.count.TeamProductCount;
import com.medusa.gruul.addon.team.model.count.TeamProductInfoCount;
import com.medusa.gruul.addon.team.model.count.TeamProductUserCount;
import com.medusa.gruul.addon.team.model.key.TeamKey;
import com.medusa.gruul.addon.team.model.vo.TeamActivityProductVO;
import com.medusa.gruul.addon.team.mp.entity.TeamProduct;
import com.medusa.gruul.addon.team.mp.mapper.TeamProductMapper;
import com.medusa.gruul.addon.team.mp.service.ITeamProductService;
import com.medusa.gruul.common.model.base.ActivityShopProductKey;
import com.medusa.gruul.storage.api.entity.StorageSku;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 拼团活动商品关联表 服务实现类
 * </p>
 *
 * @author 张治保
 */
@Service
public class TeamProductServiceImpl extends ServiceImpl<TeamProductMapper, TeamProduct> implements ITeamProductService {

    @Override
    public List<TeamProductCount> teamProductCounts(Set<TeamKey> teamKeys) {
        return baseMapper.teamProductCounts(teamKeys);
    }

    @Override
    public IPage<TeamActivityProductVO> activityProductPage(Page<TeamActivityProductVO> query) {
        return baseMapper.activityProductPage(query);
    }

    @Override
    public Set<TeamProductUserCount> teamProductUserCounts(Set<ActivityShopProductKey> keys) {
        return baseMapper.teamProductUserCounts(keys);
    }

    @Override
    public Set<TeamProductInfoCount> productsInfo(Set<ActivityShopProductKey> keys) {
        return baseMapper.productsInfo(keys);
    }

    /**
     * 获取当前正在进行的拼团活动商品信息
     *
     * @param skuList skuList
     * @return 正在进行拼团活动的活动id和商品信息
     */
    @Override
    public List<TeamProduct> getCurrentTeamProduct(List<StorageSku> skuList) {
        return baseMapper.getCurrentTeamProduct(skuList);
    }
}
