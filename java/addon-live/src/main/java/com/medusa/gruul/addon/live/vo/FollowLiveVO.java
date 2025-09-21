package com.medusa.gruul.addon.live.vo;

import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author miskw
 * @date 2023/6/6
 * @describe 关注直播间列表
 */
@Data
@Accessors(chain = true)
public class FollowLiveVO {
    /**
     * 直播间id
     */
    private Long liveId;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 店铺logo
     */
    private String shopLogo;
    /**
     * 店铺类型
     */
    private ShopType shopType;
    /**
     * 观看人数
     */
    private Integer viewership;
    /**
     * 直播主题
     */
    private String liveTitle;
    /**
     * 封面图
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
     * 直播间状态 0-->未开播；1-->直播中；2-->休息中;3-->已结束;4-->违规下播
     */
    private LiveRoomStatus status;
    /**
     * 是否预约
     */
    private Boolean isReservation;
    /**
     * 开播时间
     */
    private LocalDateTime beginTime;
    /**
     * 下播时间
     */
    private LocalDateTime endTime;
}
