package com.medusa.gruul.addon.live.mp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.addon.live.mp.entity.BaseLive;
import com.medusa.gruul.addon.live.param.LiveRoomParam;
import com.medusa.gruul.addon.live.vo.LiveAnchorVO;
import com.medusa.gruul.addon.live.vo.LiveRoomVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 描述
 */
public interface BaseLiveMapper extends BaseMapper<BaseLive> {
    IPage<LiveAnchorVO> liveAnchorList(@Param("page") Page<LiveAnchorVO> page, @Param("userId") Long userId);


    IPage<LiveRoomVO> livePage(@Param("liveRoomParam") LiveRoomParam liveRoomParam);

    boolean isBeginLive(@Param("userId") Long userId);

    LiveRoomVO detail(@Param("id") Long id);

    LiveRoomVO randomView(@Param("id") Long id);

    BaseLive getLiveRoom(@Param("liveId") Long liveId, @Param("userId") Long userId);
}
