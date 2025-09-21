package com.medusa.gruul.payment.service.common.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.egzosn.pay.common.bean.CertStoreType;
import com.egzosn.pay.common.util.sign.SignUtils;
import com.medusa.gruul.common.model.enums.PayType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author 张治保
 * @since 2025/1/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class MerchantConfig implements Serializable {

    /**
     * 列表id
     */
    @TableId(type = IdType.INPUT)
    private String detailsId;

    /**
     * 支付类型(支付渠道) 详情查看com.egzosn.pay.spring.boot.core.merchant.PaymentPlatform对应子类，aliPay 支付宝， wxPay微信..等等
     */
    private PayType payType;

    /**
     * 应用id
     */
    private String appid;

    /**
     * 商户id，商户号，合作伙伴id等等
     */
    private String mchId;

    /**
     * 当前面私钥公钥为证书类型的时候，这里必填，可选值:PATH,STR,INPUT_STREAM
     */
    private CertStoreType certStoreType;

    /**
     * 私钥或私钥证书
     */
    private String keyPrivate;

    /**
     * 公钥或公钥证书
     */
    private String keyPublic;

    /**
     * key证书,附加证书使用，如SSL证书，或者银联根级证书方面
     */
    private String keyCert;

    /**
     * 私钥证书或key证书的密码
     */
    private String keyCertPwd;

    /**
     * 异步回调
     */
    private String notifyUrl;

    /**
     * 同步回调地址，大部分用于付款成功后页面转跳
     */
    private String returnUrl;

    /**
     * 签名方式,目前已实现多种签名方式详情查看com.egzosn.pay.common.util.sign.encrypt。MD5,RSA等等
     */
    private SignUtils signType;

    /**
     * 收款账号，暂时只有支付宝部分使用，可根据开发者自行使用
     */
    private String seller;

    /**
     * 子appid
     */
    private String subAppId;

    /**
     * 子商户id
     */
    private String subMchId;

    /**
     * 编码类型，大部分为utf-8
     */
    private String inputCharset;

    /**
     * 是否为测试环境: 0 否，1 测试环境
     */
    private Boolean isTest;


    /**
     * 主体名称
     */
    private String subjectName;
}
