package com.medusa.gruul.carrier.pigeon.service.modules.sms.mp.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.Fastjson2TypeHandler;
import com.medusa.gruul.carrier.pigeon.service.modules.sms.handler.SmsClientConf;
import com.medusa.gruul.common.mp.model.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author miskw
 * @date 2022/12/12
 * @describe 短信记录
 */

@Getter
@Setter
@TableName("t_sms_record")
@Accessors(chain = true)
public class SmsRecord extends BaseEntity {


    /**
     * 发送的目标手机号
     */
    @TableField(value = "`mobiles`", typeHandler = Fastjson2TypeHandler.class)
    private Set<String> mobiles;

    /**
     * 发送短信的客户端配置
     */
    @TableField(value = "`conf`", typeHandler = Fastjson2TypeHandler.class)
    private SmsClientConf conf;

    /**
     * 短信参数
     */
    @TableField(value = "`param`", typeHandler = Fastjson2TypeHandler.class)
    private JSONObject param;

    /**
     * 错误信息
     */
    @TableField(value = "`err`", typeHandler = Fastjson2TypeHandler.class)
    private JSONObject err;


}
