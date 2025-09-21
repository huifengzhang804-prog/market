package com.medusa.gruul.afs.service.mp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.util.List;

/**
 * <p>
 * 订单优列表
 * </p>
 *
 * @author 张治保
 * @since 2022-07-19
 */
@Getter
@Setter
@Accessors(chain = true)
@ToString
@TableName(value = "t_afs_order_receiver", autoResultMap = true)
public class AfsOrderReceiver extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 售后工单号
     */
    private String afsNo;

    /**
     * 收货人名称
     */
    @TableField("`name`")
    private String name;

    /**
     * 收货人电话
     */
    private String mobile;

    /**
     * 省市区代码列表
     */
    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> area;

    /**
     * 收货人详细地址
     */
    private String address;


}
