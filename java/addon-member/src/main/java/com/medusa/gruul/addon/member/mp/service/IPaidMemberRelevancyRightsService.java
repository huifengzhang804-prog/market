package com.medusa.gruul.addon.member.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.addon.member.mp.entity.PaidMemberRelevancyRights;
import com.medusa.gruul.user.api.model.vo.MemberBasicsRelevancyRightsVO;

import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-15 14:19
 */
public interface IPaidMemberRelevancyRightsService extends IService<PaidMemberRelevancyRights> {
    /**
     * 获取基础权益
     *
     * @param id 付费会员id
     * @return  List<MemberBasicsRelevancyRightsVO>
     */
    List<MemberBasicsRelevancyRightsVO> getRightsList(Long id);

    /**
     * 查询会员权益是否在使用中
     * @param rightId
     * @return true 使用中 false 未使用
     */
    Boolean queryMemberRightInUse(Long rightId);
}
