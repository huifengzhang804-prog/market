package com.medusa.gruul.live.service.model.vo;

import com.medusa.gruul.live.api.enums.RoomStatus;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/11
 * @describe 直播间列表返回对象
 */
@Data
public class LiveInfoVo {

    /**
     * 直播间Id
     */
    private Long id;
    /**
     * 直播间名称
     */
    private String roomName;

    /**
     * 主播昵称
     */
    private String userName ;

    /**
     * 微信号
     */
    private String wechatNumber;

    /**
     *  直播状态 0->未开始;1->直播中;2->已结束;
     */
    private RoomStatus status;

    /**
     * 当type = 0 时有值,微信小程序直播间Id
     */
    private Long wechatRoomId;

    /**
     * 当type = 1 时有值,腾讯云直播间Id
     */
    private Long tencentCloudId;

    /**
     *  店铺标识符
     */
    private Long shopId;

    /**
     * 开播时间
     */
    private Long startTime;

    /**
     * 直播结束时间
     */
    private Long endTime;


}
