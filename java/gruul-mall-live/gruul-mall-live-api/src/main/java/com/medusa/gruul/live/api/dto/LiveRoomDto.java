package com.medusa.gruul.live.api.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.medusa.gruul.live.api.enums.RoomStatus;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/16
 * @describe 平台查询商家直播间
 */
@Data
public class LiveRoomDto extends Page<Object> {
    /**
     * 直播间状态
     */
    private RoomStatus roomStatus;

    /**
     * 店铺名称和直播名称
     */
    private String keywords;


}
