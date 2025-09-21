package com.medusa.gruul.live.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.live.api.enums.RoomStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * @author miskw
 * @date 2022/11/8
 * @describe 直播间
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_live_room")
public class LiveRoom extends BaseEntity {
    /**
     * 直播间名称
     */
    @TableField("room_name")
    private String roomName;

    /**
     * 直播间类型 0->微信; 1->腾讯云
     */
    @TableField("type")
    private Integer type;
    /**
     * 背景图
     */
    @TableField("cover_img")
    private String coverImg;

    /**
     * 分享图
     */
    @TableField("share_img")
    private String shareImg;

    /**
     * 购物直播频道封面图
     */
    @TableField("feeds_img")
    private String feedsImg;

    /**
     * 直播状态 0->未开始;1->直播中;2->已结束;
     */
    @TableField("status")
    private RoomStatus status;

    /**
     * 当type = 0 时有值,微信小程序直播间Id
     */
    @TableField("wechat_room_id")
    private Long wechatRoomId;

    /**
     * 当type = 1 时有值,腾讯云直播间Id
     */
    @TableField("tencent_cloud_id")
    private Long tencentCloudId;

    /**
     * 店铺标识符
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 店铺名称
     */
    @TableField("shop_name")
    private String shopName;

    /**
     * 店铺logo
     */
    @TableField("shop_logo")
    private String shopLogo;

    /**
     * 开播时间
     */
    @TableField("start_time")
    private Long startTime;

    /**
     * 直播结束时间
     */
    @TableField("end_time")
    private Long endTime;


    /**
     * 推流地址
     */
    @TableField("streaming_address")
    private String streamingAddress;


    /**
     * 当type = 0 时有值,微信小程序直播间数据镜像
     */
    @TableField("wechat_room_json")
    private String wechatRoomJson;

    /**
     * 主播昵称
     */
    @TableField("anchor_name")
    private String anchorName;

    /**
     * 微信号
     */
    @TableField("wechat_number")
    private String wechatNumber;

    /**
     * 是否开启官方收录 【1: 开启，0：关闭】，默认开启收录
     */
    @TableField("is_feeds_public")
    private Integer isFeedsPublic;


    /**
     * 是否关闭点赞 【0：开启，1：关闭】（若关闭，观众端将隐藏点赞按钮，直播开始后不允许开启）
     */
    @TableField("close_like")
    private Integer closeLike;
    /**
     * 是否关闭货架 【0：开启，1：关闭】（若关闭，观众端将隐藏商品货架，直播开始后不允许开启）
     */
    @TableField("close_goods")
    private Integer closeGoods;
    /**
     * 是否关闭评论 【0：开启，1：关闭】（若关闭，观众端将隐藏评论入口，直播开始后不允许开启）
     */
    @TableField("close_comment")
    private Integer closeComment;


    /**
     * 是否关闭回放 【0：开启，1：关闭】默认关闭回放（直播开始后允许开启）
     */
    @TableField("close_replay")
    private Integer closeReplay;

    /**
     * 是否关闭分享 【0：开启，1：关闭】默认开启分享（直播开始后不允许修改）
     */
    @TableField("close_share")
    private Integer closeShare;

    /**
     * 是否关闭客服 【0：开启，1：关闭】 默认关闭客服（直播开始后允许开启）
     */
    @TableField("close_kf")
    private Integer closeKf;


}
