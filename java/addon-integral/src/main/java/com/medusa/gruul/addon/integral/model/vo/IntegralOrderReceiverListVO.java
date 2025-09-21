package com.medusa.gruul.addon.integral.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 积分订单收获信息vo
 *
 * @author shishuqian
 * date 2023/2/3
 * time 16:38
 **/

@Getter
@Setter
@ToString
public class IntegralOrderReceiverListVO {

    /**
     * 收货人名称
     */
    private String name;

    /**
     * 收货人电话
     */
    private String mobile;


//    @TableField(typeHandler = Fastjson2TypeHandler.class)
    private List<String> area;

    /**
     * 收货人详细地址
     */
    private String address;
}
