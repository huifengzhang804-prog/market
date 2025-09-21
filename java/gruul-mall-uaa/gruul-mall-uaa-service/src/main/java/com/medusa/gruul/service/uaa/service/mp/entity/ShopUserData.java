package com.medusa.gruul.service.uaa.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * <p>
 * 管理员(平台/店铺)资料查询表
 * </p>
 *
 * @author 张治保
 * @since 2022-04-27
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("t_shop_user_data")
public class ShopUserData extends BaseEntity {

    @Serial
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
     * 昵称
     */
    private String nickname;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;


}
