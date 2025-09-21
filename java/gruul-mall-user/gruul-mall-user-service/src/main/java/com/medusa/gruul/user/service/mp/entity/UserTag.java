package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *
 * 会员标签
 *
 *
 * @author WuDi
 * @since 2022-09-14
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("t_user_tag")
public class UserTag extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 会员标签名称
     */
    private String tagName;

    /**
     * 店铺id
     */
    private Long shopId;


}
