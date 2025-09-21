package com.medusa.gruul.addon.live.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.medusa.gruul.addon.live.mp.entity.AnchorFollow;
import com.medusa.gruul.addon.live.param.FollowLiveParam;
import com.medusa.gruul.addon.live.vo.FollowLiveVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
public interface AnchorFollowMapper extends BaseMapper<AnchorFollow> {
    IPage<FollowLiveVO> followLiveList(@Param("followLiveParam") FollowLiveParam followLiveParam, @Param("userId") Long userId);

    IPage<FollowLiveVO> discoverLiveList(@Param("followLiveParam") FollowLiveParam followLiveParam, @Param("userId") Long userId);
}
