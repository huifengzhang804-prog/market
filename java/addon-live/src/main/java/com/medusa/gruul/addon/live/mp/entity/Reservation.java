package com.medusa.gruul.addon.live.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2023/6/5
 * @describe 用户预约直播间
 */
@Data
@Accessors(chain = true)
@TableName("t_reservation")
@EqualsAndHashCode(callSuper = true)
public class Reservation extends BaseEntity {
    //店铺id
    private Long shopId;
    //直播间id
    private Long liveId;
    //用户id
    private Long userId;
    //微信openid
    private String openid;
    //消息是否推送
    private Boolean isPush;
}
