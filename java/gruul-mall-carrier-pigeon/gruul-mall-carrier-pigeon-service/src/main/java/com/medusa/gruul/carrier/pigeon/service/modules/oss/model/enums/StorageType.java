package com.medusa.gruul.carrier.pigeon.service.modules.oss.model.enums;


import cn.hutool.core.text.StrPool;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.OssConstant;
import com.medusa.gruul.carrier.pigeon.service.modules.oss.model.config.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileStorageProperties;
import org.dromara.x.file.storage.core.FileStorageServiceBuilder;
import org.dromara.x.file.storage.core.platform.FileStorage;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Description: 存储类型type
 *
 * @author xiaoq
 * @since 2022-05-19 17:05
 */
@Getter
@RequiredArgsConstructor
public enum StorageType {
    /**
     * 七牛
     */
    QINIUO("QINIUO", QiniouStorageConfig.class,
            (type, conf) -> {
                QiniouStorageConfig config = (QiniouStorageConfig) conf;
                FileStorageProperties.QiniuKodoConfig qiniuKodoConfig = new FileStorageProperties.QiniuKodoConfig();
                qiniuKodoConfig.setAccessKey(config.getQiniuAccessKey())
                        .setSecretKey(config.getQiniuSecretKey())
                        .setBucketName(config.getQiniuBucketName())
                        .setDomain(ensureEndsWithSlash(config.getQiniuDomain()))
                        .setBasePath(ensureEndsWithSlash(config.getQiniuPrefix()))
                        .setPlatform(type.name());
                return FileStorageServiceBuilder.buildQiniuKodoFileStorage(List.of(qiniuKodoConfig), null);
            }
    ),
    /**
     * 阿里
     */
    ALIYUN("ALIYUN", AliyunStorageConfig.class,
            (type, conf) -> {
                AliyunStorageConfig config = (AliyunStorageConfig) conf;
                FileStorageProperties.AliyunOssConfig aliyunOssConfig = new FileStorageProperties.AliyunOssConfig();
                aliyunOssConfig.setPlatform(type.name());
                aliyunOssConfig.setAccessKey(config.getAliyunAccessKeyId());
                aliyunOssConfig.setSecretKey(config.getAliyunAccessKeySecret());
                aliyunOssConfig.setEndPoint(config.getAliyunEndPoint());
                aliyunOssConfig.setBucketName(config.getAliyunBucketName());
                aliyunOssConfig.setDomain(ensureEndsWithSlash(config.getAliyunDomain()));
                aliyunOssConfig.setBasePath(ensureEndsWithSlash(config.getAliyunPrefix()));
                //构建阿里云存储配置
                return FileStorageServiceBuilder.buildAliyunOssFileStorage(List.of(aliyunOssConfig), null);
            }
    ),
    /**
     * 腾讯
     */
    TENCENT("TENCENT", TencentStorageConfig.class,
            (type, conf) -> {
                TencentStorageConfig config = (TencentStorageConfig) conf;
                FileStorageProperties.TencentCosConfig tencentCosConfig = new FileStorageProperties.TencentCosConfig();
                tencentCosConfig.setSecretId(config.getQcloudSecretId())
                        .setSecretKey(config.getQcloudSecretKey())
                        .setRegion(config.getQcloudRegion())

                        .setBucketName(config.getQcloudBucketName().concat(StrPool.DASHED).concat(String.valueOf(config.getQcloudAppId())))
                        .setDomain(ensureEndsWithSlash(config.getQcloudDomain()))
                        .setBasePath(ensureEndsWithSlash(config.getQcloudPrefix()))
                        .setPlatform(type.name());
                return FileStorageServiceBuilder.buildTencentCosFileStorage(List.of(tencentCosConfig), null);
            }
    ),

    /**
     * 华为
     */
    QUIDWAY("QUIDWAY", QuidwayStorageConfig.class,
            (type, conf) -> {
                QuidwayStorageConfig config = (QuidwayStorageConfig) conf;
                //增加
                FileStorageProperties.HuaweiObsConfig huaweiObsConfig = new FileStorageProperties.HuaweiObsConfig();
                huaweiObsConfig.setAccessKey(config.getQuidwayAccessKeyId())
                        .setSecretKey(config.getQuidwayAccessKeySecret())
                        .setEndPoint(config.getQuidwayEndpoint())
                        .setBucketName(config.getObsBucketName())
                        .setDomain(ensureEndsWithSlash(config.getQuidwayDomain()))
                        .setBasePath(ensureEndsWithSlash(config.getQuidwayPrefix()))
                        .setPlatform(type.name());
                //构建华为云存储配置
                return FileStorageServiceBuilder.buildHuaweiObsFileStorage(List.of(huaweiObsConfig), null);
            }
    ),

    /**
     * MinIO
     */
    MINIO("MINIO", MinIoStorageConfig.class,
            (type, conf) -> {
                MinIoStorageConfig config = (MinIoStorageConfig) conf;
                FileStorageProperties.MinioConfig minioConfig = new FileStorageProperties.MinioConfig();
                minioConfig.setAccessKey(config.getMinioAccessKey())
                        .setSecretKey(config.getMinioSecretKey())
                        .setBucketName(config.getMinioBucketName())
                        .setEndPoint(config.getMinioEndPoint())
                        .setDomain(ensureEndsWithSlash(config.getMinioDomain()))
                        .setPlatform(type.name());
                return FileStorageServiceBuilder.buildMinioFileStorage(List.of(minioConfig), null);
            }
    ),

    /**
     * 本地存储
     */
    LOCAL("LOCAL", LocalStorageConfig.class,
            (type, conf) -> {
                LocalStorageConfig config = (LocalStorageConfig) conf;
                FileStorageProperties.LocalPlusConfig localPlusConfig = new FileStorageProperties.LocalPlusConfig();
                //域名后面追加映射path
                localPlusConfig.setDomain(
                                ensureEndsWithSlash(config.getLocalDomain())
                                        .concat(OssConstant.LOCAL_OSS_REQUEST_PATH)
                        )
                        .setStoragePath(ensureEndsWithSlash(config.getLocalStoragePath()))
                        .setPlatform(type.name());
                return FileStorageServiceBuilder.buildLocalPlusFileStorage(List.of(localPlusConfig));
            }
    ),

    ;
    @EnumValue
    private final String value;
    /**
     * 对应的配置类型
     */
    private final Class<? extends StorageConf> storageConfClass;

    /**
     * 转换函数 将自定义配置转换成对应的x-file-storage需要的配置
     */
    private final BiFunction<StorageType, StorageConf, List<? extends FileStorage>> convert;


    /**
     * 检查字符串是否以斜杠("/")结尾，如果不是则添加斜杠。
     *
     * @param str 原始字符串
     * @return 修改后的字符串
     */
    private static String ensureEndsWithSlash(String str) {
        // 检查传入的字符串是否为null或空串，若是，则直接返回一个斜杠("/")
        if (str == null || str.isEmpty()) {
            return StringUtils.EMPTY;
        }

        // 如果字符串已经以斜杠("/")结尾，则直接返回原字符串
        if (str.endsWith(StrPool.SLASH)) {
            return str;
        }

        // 否则，在字符串末尾添加一个斜杠并返回
        return str.concat(StrPool.SLASH);
    }
}
