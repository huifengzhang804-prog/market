package com.medusa.gruul.live.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.live.api.enums.RoomStatus;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/11
 * @Describe 接收直播间列表参数
 */
@Data
public class LiveInfoDto extends Page<Object> {
    /**
     * 直播间名称
     */
    private String liveRoomName;
    /**
     * 主播昵称
     */
    private String liveMemberName;
    /**
     * 直播间状态
     */
    private RoomStatus status;
    /**
     * 开播左区间
     */
    private Long beginLeftTime;
    /**
     * 开播右区间
     */
    private Long beginRightTime;
    /**
     * shopId
     */
    private Long shopId;

}
