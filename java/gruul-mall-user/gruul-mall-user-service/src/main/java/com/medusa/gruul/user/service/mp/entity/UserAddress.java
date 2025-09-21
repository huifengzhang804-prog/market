package com.medusa.gruul.user.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import com.vividsolutions.jts.geom.Point;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;

/**
 * @author 张治保
 * @since 2022-03-08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "t_user_address", autoResultMap = true)
public class UserAddress extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 物理定位
     */
    private Point location;

    /**
     * 省市区
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> area;

    /**
     * 详细地址
     */
    private String address;
    /**
     * 是否是默认地址
     */
    private Boolean isDefault;


}
