package com.medusa.gruul.live.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Data;

/**
 * @author miskw
 * @date 2022/11/14
 * @describe 微信接口调用次数
 */
@Data
@TableName("t_wechat_call_number")
public class WechatCallNumber extends BaseEntity {
    /**
     * '添加直播商品次数'
     */
    @TableField("product_submit_num")
    private Integer product_submit_num;
    /**
     * '撤销审核直播商品次数'
     */
    @TableField("product_withdraw_num")
    private Integer product_withdraw_num;
    /**
     * '删除直播商品次数'
     */
    @TableField("product_delete_num")
    private Integer product_delete_num;

    /**
     * '删除直播间次数'
     */
    @TableField("live_delete_num")
    private Integer live_delete_num;

    /**
     * 删除直播间总次数
     */
    @TableField("live_delete_num_total")
    private Integer live_delete_num_total;
    /**
     * 撤销审核直播商品总次数
     */
    @TableField("product_withdraw_num_total")
    private Integer product_withdraw_num_total;
    /**
     * 添加直播商品总次数
     */
    @TableField("product_submit_num_total")
    private Integer product_submit_num_total;
    /**
     * 删除直播商品总次数
     */
    @TableField("product_delete_num_total")
    private Integer product_delete_num_total;
    /**
     * 当前时间
     */
    @TableField("current_time")
    private Integer current_time;
    /**
     * 租户Id 店铺标识符
     */
    @TableField("shop_id")
    private Long shop_id;

}
