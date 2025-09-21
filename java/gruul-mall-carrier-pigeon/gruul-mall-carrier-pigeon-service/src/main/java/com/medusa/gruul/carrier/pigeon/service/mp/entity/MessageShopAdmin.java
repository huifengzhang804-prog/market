package com.medusa.gruul.carrier.pigeon.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 店铺客服表
 * </p>
 *
 * @author 张治保
 * @since 2022-10-11
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_message_shop_admin")
public class MessageShopAdmin extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickname;


}
