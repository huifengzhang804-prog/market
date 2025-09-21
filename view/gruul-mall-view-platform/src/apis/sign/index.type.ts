import type { Menu } from '@/components/layout/layout'

export enum GrantType {
    /**
     * 无感刷新
     * 移除请求头中的Authorization
     * 请求参数 {value:refresh_token}
     */
    REFRESH_TOKEN = 'refresh_token',
    /**
     * 密码登录
     */
    PASSWORD = 'password',
    /**
     * 短信验证码登录
     */
    SMS_CODE = 'sms_code',
    /**
     * 微信授权码登录
     */
    WECHAT_CODE = 'wechat_code',
    /**
     * 切换店铺登录
     */
    SWITCH_SHOP = 'switch_shop',
}

/**
 * 认证授权类型 配置值的类型
 */
interface GrantTypeValue {
    // 认证方式
    value: GrantType
    // 是否加密
    encrypt: boolean
    // 需要加密的字段 如果需要加密 请在此处添加字段
    encryptFields?: string[]
}

/**
 * 认证授权类型 已经对应的配置
 */
export const GrantTypes: Record<GrantType, GrantTypeValue> = {
    [GrantType.REFRESH_TOKEN]: { value: GrantType.REFRESH_TOKEN, encrypt: false },
    [GrantType.PASSWORD]: { value: GrantType.PASSWORD, encrypt: true, encryptFields: ['username', 'password'] },
    [GrantType.SMS_CODE]: { value: GrantType.SMS_CODE, encrypt: true, encryptFields: ['mobile', 'code'] },
    [GrantType.WECHAT_CODE]: { value: GrantType.WECHAT_CODE, encrypt: true, encryptFields: ['loginCode', 'mobileCode'] },
    [GrantType.SWITCH_SHOP]: { value: GrantType.SWITCH_SHOP, encrypt: false },
}

/**
 * 大数字 类型 当后端返回的数字过大时 会以字符串格式返回 否则返回数字
 * todo 应该作为全局类型使用 会有比较多的地方会用到 比如 价格 各种 id等
 */
export type Long = number | string

/**
 * 登录返回的开放数据的格式
 */
export interface SignRespOpen {
    //当前登录用户所属的店铺 id 平台默认为 0
    shopId: Long

    //当前登录用户 id
    userId: Long
}

/**
 * 返回的 Token数据的格式
 */
export interface TokenResp {
    //token 值
    value: string
    //token 过期具体的时间
    expireAt: string
    //token 过期时间 单位秒
    expiresIn: Long
}

/**
 * 登录返回的数据格式
 */
export interface SignResp extends TokenResp {
    // 开放数据
    open: SignRespOpen
    // 刷新 token
    refreshToken: TokenResp
}

/**
 * 我的资料数据格式
 */
export interface MineUserData {
    /**
     * 店铺id
     */
    shopId: string
    /**
     * 用户id
     */
    userId: string
    /**
     * 昵称
     */
    nickname: string
    /**
     * 手机号
     */
    mobile: string
    /**
     * 邮箱
     */
    email: string
}

export interface MenuDTO {
    isAdmin: boolean
    menuConfig: { [key: string]: any }
    menus: Menu[]
}
