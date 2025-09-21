package com.medusa.gruul.carrier.pigeon.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 消息店铺关联表
 * </p>
 *
 * @author 张治保
 * @since 2022-04-26
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_pigeon_shop_message")
public class PigeonShopMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 店铺id
     */
    private Long shopId;

    /**
     * 消息id
     */
    private Long messageId;

    /**
     * 是否已读 0 维度 1已读
     */
    @TableField("`read`")
    private Boolean read;


}
