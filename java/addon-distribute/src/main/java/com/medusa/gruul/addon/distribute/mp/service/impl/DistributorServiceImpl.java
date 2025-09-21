package com.medusa.gruul.addon.distribute.mp.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.addon.distribute.model.dto.DistributorQueryDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorRankDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorTeamQueryDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorStatistics;
import com.medusa.gruul.addon.distribute.model.vo.UserRankVO;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import com.medusa.gruul.addon.distribute.mp.mapper.DistributorMapper;
import com.medusa.gruul.addon.distribute.mp.service.IDistributorService;
import org.springframework.stereotype.Service;

/**
 * 分销商 服务实现类
 *
 * @author 张治保
 * @since 2022-11-16
 */
@Service
public class DistributorServiceImpl extends ServiceImpl<DistributorMapper, Distributor> implements IDistributorService {

    @Override
    public IPage<Distributor> distributorPage(DistributorQueryDTO query) {
        return baseMapper.distributorPage(query);
    }

    @Override
    public DistributorStatistics affairsStatistics(Long userId) {
        return baseMapper.affairsStatistics(userId);
    }

    @Override
    public IPage<Distributor> distributorTeamPage(DistributorTeamQueryDTO query) {
        return baseMapper.distributorTeamPage(query);
    }

    @Override
    public IPage<Distributor> rank(DistributorRankDTO query) {
        return baseMapper.rank(query);
    }

    @Override
    public UserRankVO getUserRank(Long userId, Long shopId) {
        return baseMapper.getUserRank(userId, shopId);
    }

    @Override
    public void changeBonusInvalidCommission(Long userId, Long amount) {
        baseMapper.changeBonusInvalidCommission(userId, amount);
    }
}
