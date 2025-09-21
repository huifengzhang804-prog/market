package com.medusa.gruul.live.api.vo;

import com.medusa.gruul.live.api.enums.RoomStatus;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/16
 */
@Data
public class LiveRoomVo {
    private Long id;
    /**
     * 店铺Id
     */
    private Long shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 直播间名称
     */
    private String roomName;

    /**
     * 直播状态 0->未开始;1->直播中;2->已结束;
     */
    private RoomStatus status;

    /**
     * 直播间类型 0->微信; 1->腾讯云
     */
    private Integer type;

    /**
     * 微信小程序直播间Id
     */
    private Long wechatRoomId;

    /**
     * 开播时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 主播昵称
     */
    private String anchorName;

    /**
     * 微信号
     */
    private String wechatNumber;

    /**
     * 背景图
     */
    private String coverImg;

}
