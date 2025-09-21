interface FormCn {
    [key: string]: {
        name: string
        placeholder: string
        info?: string
    }
}

export const enumServerFormCn: FormCn = {
    qcloudDomain: {
        name: '域名',
        placeholder: '腾讯云绑定的域名',
        info: 'DomainName',
    },
    description: {
        name: '说明',
        placeholder: '请输入说明',
    },
    // 前缀
    qcloudPrefix: {
        name: '路径前缀',
        placeholder: '不设置默认为空',
        info: 'Path',
    },
    // appid
    qcloudAppId: {
        name: '应用ID',
        placeholder: '腾讯云Appid',
        info: 'Appid',
    },
    // SecretId"
    qcloudSecretId: {
        name: '密钥ID',
        placeholder: '腾讯云Secretld',
        info: 'Secretld',
    },
    // SecretKey
    qcloudSecretKey: {
        name: '密钥Key',
        placeholder: '腾讯云SecretKey',
        info: 'SecretKey',
    },
    // BucketName"
    qcloudBucketName: {
        name: '存储桶名称',
        placeholder: '腾讯云BucketName',
        info: 'BucketName',
    },
    // 腾讯云COS所属地区
    qcloudRegion: {
        name: '存储桶所属地区',
        placeholder: '如：sh（可选填，华南：gz华北：tj华东：sh）',
        info: 'Bucket所属地区',
    },
    // 华为appid
    quidwayAccessKeyId: {
        name: '应用ID',
        placeholder: '华为appid',
        info: 'Appid',
    },
    // SecretId
    quidwayAccessKeySecret: {
        name: '访问密钥',
        placeholder: '华为云AccessKeySecret',
        info: 'AccessKeySecret',
    },
    // endPoint
    quidwayEndpoint: {
        name: '端点',
        placeholder: '华为云Endpoint',
        info: 'Endpoint',
    },
    // bucketname
    obsBucketName: {
        name: '存储桶名称',
        placeholder: '华为云BucketName',
        info: 'BucketName',
    },
    // 域名
    quidwayDomain: {
        name: '域名',
        placeholder: '华为云绑定的域名',
        info: 'DomainName',
    },
    // 前缀
    quidwayPrefix: {
        name: '路径前缀',
        placeholder: '不设置默认为空',
        info: 'Path',
    },
    // 域名
    qiniuDomain: {
        name: '域名',
        placeholder: '七牛云绑定的域名',
        info: 'DomainName',
    },
    // 前缀
    qiniuPrefix: {
        name: '路径前缀',
        placeholder: '不设置默认为空',
        info: 'Path',
    },
    // AccessKey
    qiniuAccessKey: {
        name: '访问密钥',
        placeholder: '七牛云AccessKey',
        info: 'AccessKey',
    },
    // SecretKey
    qiniuSecretKey: {
        name: '密钥Key',
        placeholder: '七牛云SecretKey',
        info: 'SecretKey',
    },
    // 桶
    qiniuBucketName: {
        name: '空间名',
        placeholder: '七牛云空间名',
        info: 'SpaceName',
    },
    // 域名
    aliyunDomain: {
        name: '域名',
        placeholder: '阿里云绑定的域名',
        info: 'DomainName',
    },
    // 前缀
    aliyunPrefix: {
        name: '路径前缀',
        placeholder: '不设置默认为空',
        info: 'Path',
    },
    // endPoint
    aliyunEndPoint: {
        name: '端点',
        placeholder: '阿里云EndPoint',
        info: 'EndPoint',
    },
    // AccessKeyId
    aliyunAccessKeyId: {
        name: '访问密钥ID',
        placeholder: '阿里云AccessKeyId',
        info: 'AccessKeyId',
    },
    // AccessKeySecret
    aliyunAccessKeySecret: {
        name: '访问密钥Key',
        placeholder: '阿里云AccessKeySecret',
        info: 'AccessKeySecret',
    },
    // BucketName"
    aliyunBucketName: {
        name: '存储桶名称',
        placeholder: '阿里云BucketName',
        info: 'BucketName',
    },

    minioAccessKey: {
        name: 'key',
        placeholder: 'minio AccessKey',
        info: 'key',
    }, //key
    minioSecretKey: {
        name: '秘钥',
        placeholder: 'minio SecretKey',
        info: 'Secret',
    }, //秘钥
    minioEndPoint: {
        name: '端点',
        placeholder: 'minio EndPoint',
        info: 'Endpoint',
    }, //端点
    minioBucketName: {
        name: '存储桶',
        placeholder: 'minio bucket',
        info: 'bucket',
    }, //bucket
    minioDomain: {
        name: '域名',
        placeholder: 'minio 域名',
        info: 'DomainName',
    }, //域名
    localDomain: {
        name: '域名',
        placeholder: '本地域名',
        info: 'DomainName',
    },
    localStoragePath: {
        name: '存储路径',
        placeholder: '本地存储路径',
        info: 'BucketPath',
    },
}
/**
 * 短信设置列表
 */
export interface AliyunConfig {
    createTime: string
    deleted: boolean
    description: string
    id: string
    providerAppId: string
    providerAppSecret: string
    signature: string
    smsType: string
    type: string
    updateTime: string
    using: boolean
    version: number
}
/**
 * OSS设置列表
 */
export interface TencentConfig {
    createTime: string
    description: string
    id: string
    type: 'TENCENT' | 'QUIDWAY' | 'QINIUO' | 'ALIYUN' | 'MINIO' | 'LOCAL'
    updateTime: string
    using: boolean
}
