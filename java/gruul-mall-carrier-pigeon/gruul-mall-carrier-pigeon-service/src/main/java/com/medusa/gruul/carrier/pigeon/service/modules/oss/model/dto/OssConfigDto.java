package com.medusa.gruul.carrier.pigeon.service.modules.oss.model.dto;

import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.enums.StorageType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author whh
 * @description
 * @data: 2020/4/11
 */
@Data
public class OssConfigDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 当前使用类型 1：七牛  2：阿里云  3：腾讯云 4.华为 5 MinIO 6 本地存储
     */
    @NotNull(message = "服务商类型不能为空")
    private StorageType type;
    /**
     * 说明
     */
    @NotNull(message = "说明内容不能为空")
    private String description;

    /**
     * 七牛绑定的域名
     */
    private String qiniuDomain;
    /**
     * 七牛路径前缀
     */
    private String qiniuPrefix;
    /**
     * 七牛ACCESS_KEY
     */
    private String qiniuAccessKey;
    /**
     * 七牛SECRET_KEY
     */
    private String qiniuSecretKey;
    /**
     * 七牛存储空间名
     */
    private String qiniuBucketName;

    /////////////////////////////////////////////////////////////

    /**
     * 阿里云绑定的域名
     */
    private String aliyunDomain;
    /**
     * 阿里云路径前缀
     */
    private String aliyunPrefix;
    /**
     * 阿里云EndPoint
     */
    private String aliyunEndPoint;
    /**
     * 阿里云AccessKeyId
     */
    private String aliyunAccessKeyId;
    /**
     * 阿里云AccessKeySecret
     */
    private String aliyunAccessKeySecret;
    /**
     * 阿里云BucketName
     */
    private String aliyunBucketName;

    /////////////////////////////////////////////////////////////////////
    /**
     * 腾讯云绑定的域名
     */
    private String qcloudDomain;
    /**
     * 腾讯云路径前缀
     */
    private String qcloudPrefix;
    /**
     * 腾讯云AppId
     */
    private Integer qcloudAppId;
    /**
     * 腾讯云SecretId
     */
    private String qcloudSecretId;
    /**
     * 腾讯云SecretKey
     */
    private String qcloudSecretKey;
    /**
     * 腾讯云BucketName
     */
    private String qcloudBucketName;
    /**
     * 腾讯云COS所属地区
     */
    private String qcloudRegion;

    ///////////////////////////////////////////////////////////////////
    /**
     * 华为云的 Access Key Id
     */
    private String quidwayAccessKeyId;


    /**
     * 华为云的 Access Key Secret
     */
    private String quidwayAccessKeySecret;

    /**
     * 华为云连接的地址节点
     */
    private String quidwayEndpoint;

    /**
     * 创建的桶的名称
     */
    private String obsBucketName;

    /**
     * 华为云绑定的域名
     */
    private String quidwayDomain;

    /**
     * 华为云路径前缀
     */
    private String quidwayPrefix;

    //////////////////////////////////////////////////////////////////////////

    /*  minio 存储配置*/
    /**
     * accessKey
     */
    private String minioAccessKey;
    /**
     * secretKey
     */
    private String minioSecretKey;
    /**
     * endpoint
     */
    private String minioEndPoint;
    /**
     * bucket名称
     */
    private String minioBucketName;
    /**
     * 访问域名
     */
    private String minioDomain;

    ///////////////////////////////////////////////////////////////////////////

    /**
     * 本地访问域名
     */
    private String localDomain;


}
