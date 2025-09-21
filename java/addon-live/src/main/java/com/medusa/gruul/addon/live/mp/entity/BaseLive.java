package com.medusa.gruul.addon.live.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.addon.live.enums.LiveRoomStatus;
import com.medusa.gruul.common.module.app.shop.ShopType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 直播间基础信息
 */
@Getter
@Setter
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("t_base_live")
public class BaseLive extends BaseEntity {
    /**
     * 主播id
     */
    private Long anchorId;
    /**
     * 主播名称
     */
    private String anchorNickname;
    /**
     * 店铺id
     */
    private Long shopId;
    /**
     * 店铺名称
     */
    private String shopName;
    /**
     * 店铺类型
     */
    private ShopType shopType;
    /**
     * 店铺logo
     */
    private String shopLogo;
    /**
     * 直播主题
     */
    private String liveTitle;
    /**
     * 直播简介
     */
    private String liveSynopsis;
    /**
     * 封面图
     */
    private String pic;
    /**
     * 推流stream name
     */
    private String streamName;
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
     * 直播间状态 0-->未开播；1-->直播中2-->已结束;3-->违规下播
     */
    @TableField("`status`")
    private LiveRoomStatus status;

}
