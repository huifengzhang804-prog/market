package com.medusa.gruul.live.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.medusa.gruul.live.api.enums.LiveRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author miskw
 * @date 2022/11/8
 * @describe 直播成员
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_live_member")
public class LiveMember extends BaseEntity {
    /**
     * 主播昵称
     */
    @TableField("user_name")
    private String userName ;
    /**
     * 微信号
     */
    @TableField("wechat_number")
    private String wechatNumber;
    /**
     * 主播头像
     */
    @TableField("avatar_url")
    private String avatarUrl;
    /**
     *  店铺标识符
     */
    @TableField("shop_id")
    private Long shopId;

    /**
     * 店铺名称
     */
    @TableField("shop_name")
    private String shopName;

    /**
     * 用户角色[1-管理员，2-主播，3-运营者]
     */
    @TableField("role")
    private LiveRole role;
}
