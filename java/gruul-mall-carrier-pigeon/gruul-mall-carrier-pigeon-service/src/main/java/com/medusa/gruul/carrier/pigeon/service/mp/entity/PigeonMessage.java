package com.medusa.gruul.carrier.pigeon.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.carrier.pigeon.api.enums.Channel;
import com.medusa.gruul.carrier.pigeon.api.enums.MsgType;
import com.medusa.gruul.carrier.pigeon.api.enums.NoticeType;
import com.medusa.gruul.carrier.pigeon.api.enums.SendType;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息表
 * </p>
 *
 * @author 张治保
 * @since 2022-04-26
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_pigeon_message")
public class PigeonMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 是否已推送
     */
    private Boolean pushed;

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


}
