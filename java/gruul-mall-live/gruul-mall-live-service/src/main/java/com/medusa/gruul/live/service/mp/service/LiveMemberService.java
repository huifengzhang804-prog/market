package com.medusa.gruul.live.service.mp.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.medusa.gruul.live.api.dto.LiveUserDto;
import com.medusa.gruul.live.api.entity.LiveMember;
import com.medusa.gruul.live.service.model.dto.LiveMemberDto;

/**
 * @author miskw
 * @date 2022/11/8
 */
public interface LiveMemberService extends IService<LiveMember> {
    /**
     * 直播间成员列表
     * @param liveMemberDto
     * @return
     */
    IPage<LiveMember> getLiveMember(LiveMemberDto liveMemberDto);

    /**
     * 平台查询所有主播成员
     * @param dto
     * @return
     */
    IPage<LiveMember> getLiveUser(LiveUserDto dto);
}
