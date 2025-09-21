package com.medusa.gruul.payment.service.properties;

import com.github.binarywang.wxpay.constant.WxPayConstants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * 支付Property
 *
 * @author xiaoq
 * Description PayProperty.java
 * date 2022-07-28 09:32
 */
@Data
@ConfigurationProperties(prefix = "gruul.pay")
public class PaymentProperty {

    /**
     * 前端回调页面
     */
    private String returnUrl;

    /**
     * 是否启用服务商模式
     */
    private boolean enableServiceMode = true;

    /**
     * 微信服务商支付配置
     */
    @NestedConfigurationProperty
    private WxServiceConf wxServiceConf = new WxServiceConf();

    /**
     * 微信支付服务商配置
     *
     * @author 张治保
     */
    @Getter
    @Setter
    @ToString
    public static class WxServiceConf {

        /**
         * 微信小程序 appid 必填 小程序/h5支付时使用
         */
        private String appid;

        /**
         * (商户进件时使用) 微信开放平台支付app的 appid app支付使用
         */
        private String openAppid;

        /**
         * (商户进件时使用) 网站域名 native支付使用
         */
        private String domain;

        /**
         * 服务商商户号
         */
        private String mchId;

        /**
         * 自营特约商户号
         */
        private String selfSubMchid;

        /**
         * 签名类型，目前支持HMAC-SHA256和MD5，默认为MD5
         */
        private String signType = WxPayConstants.SignType.MD5;

        /**
         * api v3秘钥
         */
        private String apiV3Key;
        /**
         * p12证书路径
         */
        private String keyPath = "classpath:cert/apiclient_cert.p12";

        /**
         * apiclient_cert.pem证书文件的绝对路径或者以classpath:开头的类路径.
         */
        private String privateCertPath = "classpath:cert/apiclient_cert.pem";

        /**
         * apiclient_key.pem证书文件的绝对路径或者以classpath:开头的类路径.
         */
        private String privateKeyPath = "classpath:cert/apiclient_key.pem";


    }
}
