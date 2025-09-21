package com.medusa.gruul.live.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.live.api.enums.RoomStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author miskw
 * @date 2022/11/8
 * @describe 微信小程序直播间
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wechat_room")
public class WechatRoom extends BaseEntity {
    /**
     * 直播间名称
     */
    @TableField("room_name")
    private String roomName;

    /**
     * 开播时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 直播结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 直播状态 0->未开始;1->直播中;2->已结束;
     */
    @TableField("status")
    private RoomStatus status;

    /**
     * 背景图，填入mediaID
     */
    @TableField("coverImg")
    private String coverImg;

    /**
     * 分享图，填入mediaID
     */
    @TableField("shareImg")
    private String shareImg;

    /**
     * 购物直播频道封面图，填入mediaID
     */
    @TableField("feedsImg")
    private String feedsImg;

    /**
     * 直播间类型 【1: 推流，0：手机直播】
     */
    @TableField("type")
    private Integer type;

    /**
     * 【0：开启，1：关闭】（若关闭，观众端将隐藏点赞按钮，直播开始后不允许开启）
     */
    @TableField("is_praise")
    private Integer isPraise;

    /**
     * 【0：开启，1：关闭】（若关闭，观众端将隐藏商品货架，直播开始后不允许开启）
     */
    @TableField("is_close_shelves")
    private Integer isCloseShelves;

    /**
     * 【0：开启，1：关闭】（若关闭，观众端将隐藏评论入口，直播开始后不允许开启）
     */
    @TableField("is_evaluate")
    private Integer isEvaluate;

    /**
     * 【0：开启，1：关闭】默认关闭回放（直播开始后允许开启）
     */
    @TableField("is_playback")
    private Integer isPlayback;
    /**
     *  店铺标识符
     */
    @TableField("shop_id")
    private String shopId;
    /**
     * 推流地址
     */
    @TableField("streaming_address")
    private String streamingAddress;
    /**
     * 直播商品Id,JSON
     */
    @TableField("live_product_examine_id")
    private Long liveProductExamineId;
    /**
     * 主播Id
     */
    @TableField("live_member_id")
    private Long liveMemberId;

    /**
     * 小程序直播小程序码
     */
    @TableField("qrcode_url")
    private String qrcodeUrl;
}
