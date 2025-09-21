package com.medusa.gruul.live.service.model.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/12
 * @Describe 获取微信直播列表
 */

@Data
public class WeChatLiveInfosDto extends Page<Object> {
    /**
     * 直播间名称
     */
    private String roomName;
    /**
     * 主播昵称
     */
    private String anchorName;
    /**
     * 直播状态
     */
    private Integer liveStatus;
    /**
     * 开播时间左区间
     */
    private Long beginRoomLeft;
    /**
     * 开播时间右区间
     */
    private Long endRoomRight;

}
