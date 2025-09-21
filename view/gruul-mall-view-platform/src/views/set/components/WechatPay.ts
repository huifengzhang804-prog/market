/**
 * 编辑(新增/修改) 支付商户信息
 * @param {string} appid
 * @param {string} detailsId
 * @param {string} keyCert 关键证书
 * @param {string} keyPrivate 关键密钥
 * @param {string} keyPublic 公钥
 * @param {string} mchId 商户号
 * @param {PAYTYPE} payType 支付类型
 * @param {PAYTYPE} platforms 支付平台
 */
export interface ApiParameters {
    subjectName: string
    appid: string
    detailsId: string
    keyCert: string
    keyPrivate: string
    keyPublic: string
    mchId: string
    payType: keyof typeof PAYTYPE
    platforms: Array<PLATFORMS>
    appCertPublicKey?: string
    alipayRootCert?: string
}

/**
 * 支付类型
 * @param BALANCE 平衡
 * @param WECHAT 微信
 * @param ALIPAY 支付宝
 */
export enum PAYTYPE {
    BALANCE,
    WECHAT,
    ALIPAY,
}

/**
 * 支付平台
 * @param WECHAT_MINI_APP 微信的迷你应用程序
 * @param   WECHAT_MP 微信
 * @param   PC PC
 * @param   H5 H5
 * @param   IOS IOS
 * @param   ANDROID 安卓
 * @param   HARMONY 鸿蒙
 */
export enum PLATFORMS {
    WECHAT_MINI_APP = 'WECHAT_MINI_APP',
    WECHAT_MP = 'WECHAT_MP',
    PC = 'PC',
    H5 = 'H5',
    IOS = 'IOS',
    ANDROID = 'ANDROID',
    HARMONY = 'HARMONY',
}

/**
 * 平台
 */
const platforms: Record<PLATFORMS, string> = {
    WECHAT_MINI_APP: '微信小程序',
    WECHAT_MP: '微信公众号',
    PC: 'PC端',
    H5: 'H5',
    IOS: 'IOS',
    ANDROID: '安卓',
    HARMONY: '鸿蒙',
}
export { platforms }
