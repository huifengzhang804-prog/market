package com.medusa.gruul.addon.member.service;

import com.medusa.gruul.addon.member.model.dto.PaidMemberDTO;
import com.medusa.gruul.addon.member.model.vo.PaidMemberRightsVO;
import com.medusa.gruul.user.api.model.dto.MemberLabelDTO;

import java.util.List;

/**
 * 管理端付费会员接口层
 *
 * @author xiaoq
 * @Description
 * @date 2022-11-15 11:33
 */
public interface ManagePaidMemberService {
    /**
     * 付费会员信息
     *
     * @param paidMember 付费会员信息
     */
    void addPaidMember(PaidMemberDTO paidMember);

    /**
     * 付费会员修改
     *
     * @param paidMember 付费会员信息
     */
    void updatePaidMember(PaidMemberDTO paidMember);

    /**
     * 付费会员级别删除
     *
     * @param id 付费会员
     */
    void delPaidMember(Long id);

    /**
     * 会员级别信息
     *
     * @return List<PaidMemberRightsVO>
     */
    List<PaidMemberRightsVO> getPaidMemberList();

    /**
     * 会员级别权益处理
     *
     * @param list 付费会员权益VO
     */
    void disposeRights(List<PaidMemberRightsVO> list);


    /**
     * 根据付费会员级别id获取付费会员权益信息
     *
     * @param id 付费会员级别id
     * @return PaidMemberRightsVO
     */
    PaidMemberRightsVO getPaidMemberInfo(Long id);

    /**
     * 付费会员标签设置
     *
     * @param dto 标签信息
     */
    void savePaidMemberLabel(MemberLabelDTO dto);
}
