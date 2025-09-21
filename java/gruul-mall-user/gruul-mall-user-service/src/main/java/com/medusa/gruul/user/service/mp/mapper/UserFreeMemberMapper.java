package com.medusa.gruul.user.service.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.user.service.model.vo.FreeMemberRightsVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.service.mp.entity.UserFreeMember;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据层
 *
 * @author xiaoq
 * @Description UserFreeMemberMapper.java
 * @date 2022-11-10 18:53
 */
public interface UserFreeMemberMapper extends BaseMapper<UserFreeMember> {

    /**
     * 查询免费会员列表
     *
     * @return  List<FreeMemberRightsVO>
     */
    List<FreeMemberRightsVO> queryFreeMemberList();

    /**
     * 获取最新的免费会员信息 by 用户成长值
     *
     * @param growthValue 用户成长值
     * @return FreeMemberRightsVO
     */
    FreeMemberRightsVO getCurrentMemberRankByGrowthValue(@Param("growthValue") Long growthValue);

    /**
     *  根据会员级别id 获取关联权益信息
     *
     * @param id 会员级别id
     * @return List<RelevancyRightsVO>
     */
    List<RelevancyRightsVO>  queryRelevancyRights(Long id);

    /**
     * 获取免费会员及权益vo
     *
     * @param id 会员级别id
     * @return FreeMemberRightsVO
     */
    FreeMemberRightsVO queryFreeMemberInfo(@Param("id") Long id);
}
