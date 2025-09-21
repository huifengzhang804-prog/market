import { get, post, put } from '../http'
import { R } from '@apis/http.type'
import { encryptAccept } from '@/utils/rsa'
import { GrantType, GrantTypes, SignResp, MineUserData } from './index.type'
import { CaptchaRequest, CaptchaResponse } from '@components/slide-captcha/Captcha'
import Storage from '@/utils/Storage'
import { MenuDTO } from '@/components/layout/layout'

const storage = new Storage()
export const getLoginPermShops = (username: string): Promise<any> => {
    return get({
        showLoading: false,
        url: `/gruul-mall-uaa/uaa/auth/shop/${username}`,
    })
}

/**
 * 获取滑块验证码
 */
export const doGetCaptchaSlider = (mobile: string | null, smsType) => {
    encryptAccept((encrypt) => {
        mobile = encrypt(mobile as string)
    })
    return post<CaptchaResponse>({ url: `gruul-mall-uaa/uaa/auth/captcha/slider/new`, data: { mobile, smsType } })
}

/**
 * @description: 获取验证码
 * @param {} data
 */
export const doPostSmsCode = (data: CaptchaRequest<{ mobile: string }>) => {
    encryptAccept((encrypt) => typeof data.form === 'string' && (data = { ...data, form: { mobile: encrypt(data.form) } }))
    return post({ url: `gruul-mall-uaa/uaa/auth/captcha/sms`, data })
}

/**
 * @description: login
 * @param {GrantType} grantType 认证方式类型
 * @param data 认证资料
 * @returns {*}
 */
export const signByUser = (grantType: GrantType, data: any): Promise<R<SignResp>> => {
    const grant = GrantTypes[grantType]
    //参数加密
    if (grant.encrypt) {
        encryptAccept((encrypt) => {
            grant.encryptFields?.forEach((field) => {
                data[field] = encrypt(data[field])
            })
        })
    }
    return post<SignResp>({
        showLoading: true,
        url: `/gruul-mall-uaa/login?grant_type=${grant.value}`,
        data,
    })
}
/**
 * 查询用户菜单导航
 */
export const doGetUserMenu = (): Promise<R<MenuDTO>> => {
    return new Promise((resolve, reject) => {
        get({
            url: 'gruul-mall-uaa/uaa/menu/navigate',
        })
            .then((res: any) => {
                if (res.code !== 200) {
                    resolve(res)
                    return
                }
                const data = res.data
                const menuMap = new Map<string, Set<string>>()
                const menuConf = data.menuConfig
                for (const key in menuConf) {
                    menuMap.set(key, new Set(menuConf[key]))
                }
                data.menuConfig = menuMap
                resolve(res)
            })
            .catch((err: any) => reject(err))
    })
}

export const myData = () => {
    return get<MineUserData>({
        url: 'gruul-mall-uaa/uaa/shop/admin/mine',
    })
}
/**
 * @description: 商家中心 查询账户信息
 */
export const doGetUserDataAccount = () => {
    return get({
        url: `gruul-mall-uaa/uaa/user/data/account`,
    })
}

/**
 * @description: 商家中心 发送重置密码短信验证码
 */
export const doGetMyResetPasswordSms = () => {
    return get({
        url: `gruul-mall-uaa/uaa/auth/reset/my/password/sms`,
    })
}
/**
 * @description: 商家中心 重置密码
 * @param {string} code
 * @param {string} password
 * @param {string} confirmPassword
 * @returns {*}
 */
export const doPutMyResetPassword = (code: string, password: string, confirmPassword: string) => {
    return put({
        url: `gruul-mall-uaa/uaa/auth/reset/my/password`,
        data: {
            code,
            password,
            confirmPassword,
        },
    })
}
/**
 * @description: 重置密码 发送重置密码短信验证码
 */
export const doGetResetPasswordSms = (mobile: string) => {
    return get({
        url: `gruul-mall-uaa/uaa/auth/reset/${mobile}/password/sms`,
    })
}
/**
 * @description: 重置密码
 * @param {string} code
 * @param {string} password
 * @param {string} confirmPassword
 * @param {string} mobile
 * @returns {*}
 */
export const doPutResetPassword = (code: string, password: string, confirmPassword: string, mobile: string) => {
    return put({
        url: `gruul-mall-uaa/uaa/auth/reset/password`,
        data: {
            code,
            password,
            confirmPassword,
            mobile,
        },
    })
}

/**
 * 保存用户菜单配置
 * @param data 菜单配置
 */
export const doPostSetUserMenuConfig = (data: Map<string, Set<string>>) => {
    const object = {}
    for (const [key, set] of data) {
        object[key] = [...set]
    }
    return post({
        url: 'gruul-mall-uaa/uaa/menu/navigate',
        data: object,
        showLoading: false,
    })
}

/**
 * 退出登录
 */
export const doPostLogout = () => {
    // 清除缓存
    storage.clear()
    return post({
        url: 'gruul-mall-uaa/logout',
        showLoading: false,
    })
}
/**
 * 根据模块类id获取配置信息
 * @param {string} params
 */
export const queryConfigByModule = (params: string): Promise<any> => {
    return get({ url: `gruul-mall-addon-platform/platform/config/query-config-by-module?modules=${params}` })
}
