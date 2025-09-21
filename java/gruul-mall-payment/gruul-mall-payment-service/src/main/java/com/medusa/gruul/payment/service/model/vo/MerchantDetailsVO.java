package com.medusa.gruul.payment.service.model.vo;

import com.medusa.gruul.common.model.enums.PayType;
import com.medusa.gruul.common.system.model.model.Platform;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 支付商户渠道配置VO
 *
 * @author xiaoq
 * @ Description 支付商户渠道配置VO
 * @date 2022-07-20 17:04
 */
@Data
@Accessors(chain = true)
public class MerchantDetailsVO {

    /**
     * 列表id
     */
    private String detailsId;

    /**
     * 支付渠道
     */
    private PayType payType;

    /**
     * 应用appid
     */
    private String appid;

    /**
     * 应用私钥
     */
    private String keyPrivate;

    /**
     * 支付宝公钥
     */
    private String keyPublic;
    /**
     * 商户号
     */
    private String mchId;
    /**
     * 支付证书
     */
    private String keyCert;

    /**
     * 主体名称
     */
    private String subjectName;


    /**
     * 支持的平台
     */
    private List<Platform> platforms;

}
