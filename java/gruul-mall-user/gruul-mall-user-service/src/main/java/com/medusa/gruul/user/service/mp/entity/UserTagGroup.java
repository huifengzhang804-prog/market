package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 会员所属标签组
 *
 * @author WuDi
 * @since 2022-09-14
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("t_user_tag_group")
public class UserTagGroup extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 会员标签id
     */
    private Long userTagId;


    /**
     * 店铺id
     */
    private Long shopId;


}
