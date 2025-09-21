package com.medusa.gruul.addon.member.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.addon.member.mp.entity.PaidMemberRelevancyRights;
import com.medusa.gruul.user.api.model.vo.MemberBasicsRelevancyRightsVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-15 14:20
 */
public interface PaidMemberRelevancyRightsMapper extends BaseMapper<PaidMemberRelevancyRights> {

    /**
     * 获取基础权益
     *
     * @param id 付费会员id
     * @return  List<MemberBasicsRelevancyRightsVO>
     */
    List<MemberBasicsRelevancyRightsVO> queryRightsList(@Param("id") Long id);

    /**
     * 查询会员全因被使用的记录数
     * @param rightId
     * @return
     */
    Integer queryMemberRightInUse(@Param("rightId") Long rightId);
}
