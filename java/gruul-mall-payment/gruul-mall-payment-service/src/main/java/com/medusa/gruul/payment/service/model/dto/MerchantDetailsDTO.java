package com.medusa.gruul.payment.service.model.dto;

import cn.hutool.core.util.StrUtil;
import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.model.resp.SystemCode;
import com.medusa.gruul.common.system.model.model.Platform;
import com.medusa.gruul.global.model.o.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Set;

/**
 * 商户渠道配置DTO
 *
 * @author xiaoq
 * @since 2022-07-13 09:49
 */
@Data
public class MerchantDetailsDTO implements BaseDTO {

    /**
     * 列表id
     */
    private String detailsId;

    /**
     * 支付渠道枚举
     */
    @NotNull
    private PayType payType;

    /**
     * 主体名称
     */
    @NotBlank
    private String subjectName;

    /**
     * APP平台类型
     */
    @NotNull
    @NotEmpty
    private Set<Platform> platforms;

    /**
     * 应用appid
     */
    @NotBlank
    private String appid;

    /**
     * 商户号 微信支付需要
     */
    private String mchId;

    /**
     * 应用私钥
     */
    @NotBlank
    private String keyPrivate;

    /**
     * 支付宝公钥
     */
//    @NotBlank
    private String keyPublic;

    /**
     * 支付证书
     */
    @NotBlank
    private String keyCert;


    @Override
    public void validParam() {
        if (PayType.WECHAT == payType && StrUtil.isBlank(mchId)) {
            throw SystemCode.PARAM_VALID_ERROR.exception();
        }
    }
}
