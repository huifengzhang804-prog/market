package com.medusa.gruul.live.service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * @author miskw
 * @date 2022/11/11
 * @describe 创建直播间参数
 */
@Data
public class WxMaLiveRoomDto {
    /**
     * 直播间名字，最短3个汉字，最长17个汉字
     */
    @Length(min = 3, max = 17)
    private String name;
    /**
     * 背景图，填入mediaID
     */
    @NotBlank(message = "请上传背景图")
    private String coverImg;
    /**
     * 直播计划开始时间（开播时间需要在当前时间的10分钟后 并且 开始时间不能在 6 个月后）
     * 时间戳 （秒）
     */
    @NotNull(message = "请填写直播开始时间")
    private Long startTime;
    /**
     * 直播计划结束时间（开播时间和结束时间间隔不得短于30分钟，不得超过24小时）
     * 时间戳 （秒）
     */
    @NotNull(message = "请填写直播结束时间")
    private Long endTime;
    /**
     * 主播昵称，最短2个汉字，最长15个汉字，1个汉字相当于2个字符
     */
    @Length(min = 2, max = 15)
    private String anchorName;
    /**
     * 主播微信号
     */
    @NotBlank(message = "请填写主播微信号")
    private String anchorWechat;

    /**
     * 分享图，填入mediaID
     * 直播间分享图，图片规则：建议像素800*640，大小不超过1M；
     */
    @NotBlank(message = "请上传分享图")
    private String shareImg;
    /**
     * 购物直播频道封面图
     * 购物直播频道封面图，图片规则：建议像素800*800，大小不超过100KB；
     */
    @NotBlank(message = "请上传购物直播频道封面图")
    private String feedsImg;

    /**
     * 直播间类型 【1: 推流，0：手机直播】
     */
    @NotNull(message = "请选择直播类型")
    private Integer type;

    /**
     * 是否开启官方收录 【1: 开启，0：关闭】，默认开启收录
     */
    private Integer isFeedsPublic;

    /**
     * 是否关闭点赞 【0：开启，1：关闭】（若关闭，观众端将隐藏点赞按钮，直播开始后不允许开启）
     */
    @NotNull(message = "请选择是否关闭点赞")
    private Integer closeLike;
    /**
     * 是否关闭货架 【0：开启，1：关闭】（若关闭，观众端将隐藏商品货架，直播开始后不允许开启）
     */
    @NotNull(message = "请选择是否关闭货架")
    private Integer closeGoods;
    /**
     * 是否关闭评论 【0：开启，1：关闭】（若关闭，观众端将隐藏评论入口，直播开始后不允许开启）
     */
    @NotNull(message = "请选择是否关闭评论")
    private Integer closeComment;

    /**
     * 是否关闭回放 【0：开启，1：关闭】默认关闭回放（直播开始后允许开启）
     */
    private Integer closeReplay;

    /**
     * 是否关闭分享 【0：开启，1：关闭】默认开启分享（直播开始后不允许修改）
     */
    private Integer closeShare;

    /**
     * 是否关闭客服 【0：开启，1：关闭】 默认关闭客服（直播开始后允许开启）
     */
    private Integer closeKf;


    /**
     * oss背景图url
     */
    private String ossCoverImgUrl;
    /**
     * oss分享图url
     */
    private String ossShareImgUrl;
    /**
     * oss购物直播频道封面图url
     */
    private String ossFeedsImgUrl;


}
