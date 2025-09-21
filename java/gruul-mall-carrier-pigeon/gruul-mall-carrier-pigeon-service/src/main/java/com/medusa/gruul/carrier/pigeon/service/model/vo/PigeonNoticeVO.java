package com.medusa.gruul.carrier.pigeon.service.model.vo;

import com.medusa.gruul.carrier.pigeon.api.enums.Channel;
import com.medusa.gruul.carrier.pigeon.api.enums.MsgType;
import com.medusa.gruul.carrier.pigeon.api.enums.NoticeType;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author 张治保
 * date 2022/4/26
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class PigeonNoticeVO {

    /**
     * id
     */
    private Long id;
    /**
     * 是否已读
     */
    private Boolean read;
    /**
     * 类型 0公告 1.提现工单通过审核 ,2提现工单未通过审核 ,3商品发布通过审核  4商品发布未通过审核 ,  5.优惠券已被平台下架
     */
    private NoticeType type;
    /**
     * 发送类型
     */
    private SendType sendType;
    /**
     * 发送渠道
     */
    private Channel channel;
    /**
     * 消息内容类型
     */
    private MsgType msgType;
    /**
     * 公告标题
     */
    private String title;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 跳转连接
     */
    private String url;
    /**
     * 公告内容
     */
    private String content;
    /**
     * 创建者用户id
     */
    private Long createBy;
    /**
     * 修改者用户id
     */
    private Long updateBy;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
