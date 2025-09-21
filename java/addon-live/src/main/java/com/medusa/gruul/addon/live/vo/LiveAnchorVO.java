package com.medusa.gruul.addon.live.vo;

import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 主播对应的直播间列表
 */
@Data
@Accessors(chain = true)
public class LiveAnchorVO {
    /**
     * 直播间id
     */
    private Long id;

    /**
     * 主播id
     */
    private Long anchorId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 直播标题
     */
    private String liveTitle;
    /**
     * 店铺id
     */
    private String shopId;
    /**
     * 直播间简介
     */
    private String liveSynopsis;
    /**
     * 直播间封面图
     */
    private String pic;
    /**
     * 推流地址
     */
    private String pushAddress;
    /**
     * 拉流地址
     */
    private String pullAddress;
    /**
     * 开播时间
     */
    private LocalDateTime beginTime;
    /**
     * 下播时间
     */
    private LocalDateTime endTime;
    /**
     * 直播间状态 0-->未开播；1-->直播中；2-->已结束;3-->违规下播
     */
    private LiveRoomStatus status;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
