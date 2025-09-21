package com.medusa.gruul.user.service.mp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.user.service.model.vo.FreeMemberRightsVO;
import com.medusa.gruul.user.api.model.vo.RelevancyRightsVO;
import com.medusa.gruul.user.service.mp.entity.UserFreeMember;

import java.util.List;

/**
 * 服务层
 *
 * @author xiaoq
 * @Description IUserFreeMemberService.java
 * @date 2022-11-10 18:51
 */
public interface IUserFreeMemberService extends IService<UserFreeMember> {
    /**
     * 获取免费会员列表
     *
     * @return   List<FreeMemberRightsVO>
     */
    List<FreeMemberRightsVO> getFreeMemberList();


    /**
     * 获取用户所处会员级别
     *
     * @param growthValue 用户成长值
     * @return 免费级别会员权益VO
     */
    FreeMemberRightsVO getCurrentMemberRank(Long growthValue);


    /**
     * 获取会员级别关联权益
     *
     * @param id 会员级别id
     * @return 关联权益
     */
    List<RelevancyRightsVO> getRelevancyRights(Long id);

    /**
     * 根据会员级别id获取免费会员级别关联权益
     *
     * @param id 根据会员级别
     * @return FreeMemberRightsVO
     */
    FreeMemberRightsVO getFreeMemberInfo(Long id);
}
