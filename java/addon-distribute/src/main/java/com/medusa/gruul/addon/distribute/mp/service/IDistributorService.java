package com.medusa.gruul.addon.distribute.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.distribute.model.dto.DistributorQueryDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorRankDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorTeamQueryDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorStatistics;
import com.medusa.gruul.addon.distribute.model.vo.UserRankVO;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;

/**
 * 分销商 服务类
 *
 * @author 张治保
 * @since 2022-11-16
 */
public interface IDistributorService extends IService<Distributor> {

    /**
     * 分页查询分销商
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<Distributor> distributorPage(DistributorQueryDTO query);

    /**
     * 根据用户id查询 分销商数据统计
     *
     * @param userId 用户id
     * @return 统计结果
     */
    DistributorStatistics affairsStatistics(Long userId);

    /**
     * 根据用户id分页查询用户分销客户
     *
     * @param query 分页查询条件
     * @return 查询结果
     */
    IPage<Distributor> distributorTeamPage(DistributorTeamQueryDTO query);


    /**
     * 分页查询分销用户排行榜
     *
     * @param query 分页查询条件
     * @return 查询结果
     */
    IPage<Distributor> rank(DistributorRankDTO query);


    /**
     * 查询当前用户分销排名
     *
     * @param userId 用户id
     * @param shopId 店铺id  可以为null  为null查询所有用户的排行 不为null查询店铺内的排行
     * @return 用户的排名与佣金
     */
    UserRankVO getUserRank(Long userId, Long shopId);

    /***
     * 修改分销商失效的分销金额
     * @param userId
     * @param amount
     */
    void changeBonusInvalidCommission(Long userId, Long amount);
}
