package com.medusa.gruul.carrier.pigeon.api.sms;

import cn.hutool.core.map.MapUtil;
import com.medusa.gruul.carrier.pigeon.api.sms.enums.SmsTemplateType;
import com.medusa.gruul.common.model.constant.CommonPool;
import com.medusa.gruul.global.model.constant.RegexPools;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Description: 短信发送dto
 *
 * @author xiaoq
 * @since 2022-03-23 14:51
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SmsSendParam implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 短信模板类型
     */
    @NotNull
    private SmsTemplateType templateType;

    /**
     * 接收短信的手机号集合 腾讯云最大限制 200 个，阿里云最大限制 1000 个
     * 所以最好不要超过 200 个
     */
    @NotNull
    private Set<@Pattern(regexp = RegexPools.MOBILE_TEL) String> mobiles;

    /**
     * 短信发送参数 注意顺序 如果模板有顺序要求
     */
    @NotNull
    private Map<String, String> params = MapUtil.newHashMap(true);


    /**
     * 注意顺序
     * 添加 短信验证码参数 如果是短信验证码 则调用此方法 添加 ，参数为 code
     *
     * @param smsCode 验证码
     * @return this
     */
    public SmsSendParam smsCode(String smsCode) {
        return param(CommonPool.CODE, smsCode);
    }

    /**
     * 添加短信发送参数 注意顺序
     *
     * @param key   模板 key
     * @param value 模板值
     * @return this
     */
    public SmsSendParam param(String key, Object value) {
        params.put(key, value.toString());
        return this;
    }

}
