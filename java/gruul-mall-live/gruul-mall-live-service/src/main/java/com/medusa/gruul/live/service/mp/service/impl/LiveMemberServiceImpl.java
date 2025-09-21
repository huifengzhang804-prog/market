package com.medusa.gruul.live.service.mp.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.medusa.gruul.common.security.resource.helper.ISecurity;
import com.medusa.gruul.live.api.dto.LiveUserDto;
import com.medusa.gruul.live.api.entity.LiveMember;
import com.medusa.gruul.live.service.model.dto.LiveMemberDto;
import com.medusa.gruul.live.service.mp.mapper.LiveMemberMapper;
import com.medusa.gruul.live.service.mp.service.LiveMemberService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


/**
 * @author miskw
 * @date 2022/11/8
 */
@Service
public class LiveMemberServiceImpl extends ServiceImpl<LiveMemberMapper, LiveMember> implements LiveMemberService {
    @Resource
    private LiveMemberMapper liveMemberMapper;

    /**
     * 直播间成员列表
     *
     * @param liveMemberDto
     * @return
     */
    @Override
    public IPage<LiveMember> getLiveMember(LiveMemberDto liveMemberDto) {
        Long shopId = ISecurity.userMust().getShopId();
        IPage<LiveMember> page = new Page<>(liveMemberDto.getCurrent(), liveMemberDto.getSize());
        LambdaQueryChainWrapper<LiveMember> liveMemberLambdaQueryWrapper = this.lambdaQuery();
        liveMemberLambdaQueryWrapper
                .eq(LiveMember::getShopId, shopId)
                .like(StrUtil.isNotBlank(liveMemberDto.getKeywords()), LiveMember::getWechatNumber, liveMemberDto.getKeywords())
                .orderByDesc(LiveMember::getCreateTime)
                .or()
                .eq(LiveMember::getShopId, shopId)
                .like(StrUtil.isNotBlank(liveMemberDto.getKeywords()), LiveMember::getUserName, liveMemberDto.getKeywords())
                .orderByDesc(LiveMember::getCreateTime);
        liveMemberLambdaQueryWrapper.page(page);
        return page;
    }

    /**
     * 平台查询所有主播成员
     *
     * @param dto
     * @return
     */
    @Override
    public IPage<LiveMember> getLiveUser(LiveUserDto dto) {
        IPage<LiveMember> page = new Page<>(dto.getCurrent(), dto.getSize());
        lambdaQuery()
                .like(StrUtil.isNotBlank(dto.getKeywords()), LiveMember::getUserName, dto.getKeywords())
                .orderByDesc(LiveMember::getCreateTime)
                .or()
                .like(StrUtil.isNotBlank(dto.getKeywords()), LiveMember::getWechatNumber, dto.getKeywords())
                .orderByDesc(LiveMember::getCreateTime)
                .or()
                .like(StrUtil.isNotBlank(dto.getKeywords()), LiveMember::getShopName, dto.getKeywords())
                .orderByDesc(LiveMember::getCreateTime)
                .page(page);
        return page;
    }
}
