package com.medusa.gruul.live.service.mp.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medusa.gruul.live.api.entity.LiveRoom;
import org.apache.ibatis.annotations.Param;


/**
 * @author miskw
 * @date 2022/11/8
 */
public interface LiveRoomMapper extends BaseMapper<LiveRoom> {

    /**
     * 定时修改直播间开播状态
     * @param currentTime
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    Integer liveRoomReviewStart(@Param("currentTime") long currentTime);

    /**
     * 定时修改直播间【已结束】状态
     * @param currentTime
     * @return
     */
    @InterceptorIgnore(tenantLine = "true")
    Integer liveRoomReviewEnd(@Param("currentTime") long currentTime);
}
