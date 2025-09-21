package com.medusa.gruul.addon.distribute.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.distribute.model.dto.DistributorQueryDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorRankDTO;
import com.medusa.gruul.addon.distribute.model.dto.DistributorTeamQueryDTO;
import com.medusa.gruul.addon.distribute.model.vo.DistributorStatistics;
import com.medusa.gruul.addon.distribute.model.vo.UserRankVO;
import com.medusa.gruul.addon.distribute.mp.entity.Distributor;
import org.apache.ibatis.annotations.Param;

/**
 * 分销商 Mapper 接口
 *
 * @author 张治保
 * @since 2022-11-16
 */
public interface DistributorMapper extends BaseMapper<Distributor> {

    /**
     * 分页查询分销商
     *
     * @param query 查询条件
     * @return 分页查询结果
     */
    IPage<Distributor> distributorPage(@Param("query") DistributorQueryDTO query);


    /**
     * 根据用户id查询 分销商数据统计
     *
     * @param userId 用户id
     * @return 统计结果
     */
    DistributorStatistics affairsStatistics(@Param("userId") Long userId);


    /**
     * 根据用户id分页查询用户分销客户
     *
     * @param query 分页查询条件
     * @return 查询结果
     */
    IPage<Distributor> distributorTeamPage(@Param("query") DistributorTeamQueryDTO query);

    /**
     * 分页查询分销用户排行榜
     *
     * @param query 分页查询条件
     * @return 查询结果
     */
    IPage<Distributor> rank(@Param("query") DistributorRankDTO query);

    /**
     * 查询当前用户分销排名
     *
     * @param userId 用户id
     * @param shopId 店铺id
     * @return 用户的排名与佣金
     */
    UserRankVO getUserRank(@Param("userId") Long userId, @Param("shopId") Long shopId);

    /**
     * 增加失效佣金
     *
     * @param userId
     * @param amount
     */
    void changeBonusInvalidCommission(@Param("userId") Long userId,
                                      @Param("amount") Long amount);
}
