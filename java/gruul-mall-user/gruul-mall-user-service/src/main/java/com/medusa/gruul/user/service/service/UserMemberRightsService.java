package com.medusa.gruul.user.service.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.user.api.enums.RightsType;
import com.medusa.gruul.user.api.model.dto.MemberRightsDTO;
import com.medusa.gruul.user.service.mp.entity.MemberRights;

import java.util.List;

/**
 * 用户会员service
 *
 * @author xiaoq
 * @Description UserMemberRightsService.java
 * @date 2022-11-09 15:23
 */
public interface UserMemberRightsService {
    /**
     * 修改 会员权益信息
     *
     * @param memberRights 会员权益DTO
     */
    void updateMemberRights(MemberRightsDTO memberRights);

    /**
     * 新增会员权益信息
     *
     * @param memberRights 会员权益DTO
     */
    void saveMemberRights(MemberRightsDTO memberRights);

    /**
     * 会员权益开启或关闭
     *
     * @param rightsSwitch 权益开关状态
     * @param id 权益id
     */
    void memberRightsSwitch(Boolean rightsSwitch, Long id);

    /**
     * 会员权益删除
     *
     * @param ids 会员权益ids
     */
    void delMemberRights(Long[] ids);

    /**
     * 获取会员权益数据
     *
     * @param page 分页
     * @return Page<MemberRights>
     */
    Page<MemberRights> getMemberRightsList(Page<MemberRights> page);

    /**
     * 可用会员权益获取
     *
     * @return List<MemberRights
     */
    List<MemberRights> getUsableMemberRightsList();

    /**
     * 会员根据权益恢复默认值
     *
     * @param rightsType 权益类型
     * @return 会员权益
     */
    MemberRights memberRightsRestoreDefault(RightsType rightsType);
}
