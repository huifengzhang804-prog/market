package com.medusa.gruul.user.service.service;

import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;
import com.medusa.gruul.user.service.model.vo.FreeMemberRightsVO;
import com.medusa.gruul.user.service.model.dto.UserFreeMemberDTO;
import com.medusa.gruul.user.service.mp.entity.UserFreeMember;

import java.util.List;

/**
 * 免费会员服务层
 *
 * @author xiaoq
 * @Description FreeMemberService.java
 * @date 2022-11-10 18:43
 */
public interface FreeMemberService {
    /**
     * 免费会员新增
     *
     * @param userFreeMemberDTO 免费会员dto
     */
    void addFreeMember(UserFreeMemberDTO userFreeMemberDTO);

    /**
     * 免费会员更新
     *
     * @param userFreeMemberDTO 免费会员dto
     */
    void updateFreeMember(UserFreeMemberDTO userFreeMemberDTO);

    /**
     * 免费会员删除
     *
     * @param id 会员id
     */
    void delFreeMember(Long id);

    /**
     * 免费会员列表信息
     *
     *@return List<免费级别会员权益VO>
     */
    List<FreeMemberRightsVO> getFreeMemberList();


    /**
     * 获取初始会员信息
     *
     * @return 初始会员信息
     */
    UserFreeMember  getInitialFreeMemberInfo();

    /**
     *
     * 获取所处免费会员级别
     *
     * @param growthValue 成长值
     * @return  会员基础信息及关联数据
     */
    FreeMemberRightsVO getCurrentMemberRank(Long growthValue);

    /**
     * 根据id 获取会员级别详细信息
     *
     * @param id 免费会员级别id
     * @return 会员基础信息及关联数据
     */
    FreeMemberRightsVO getFreeMemberInfo(Long id);

    /**
     * 获取下一级别或当前级别会员级别信息
     *
     * @param rankCode 当前会员级别
     * @return 下一级别或当前级别会员级别信息
     */
    UserFreeMember getNextLevelMemberInfo(Integer rankCode);

    /**
     * 免费会员设置标签
     *
     * @param dto 会员标签dto
     */
    void addFreeMemberLabel(MemberLabelDTO dto);
}
