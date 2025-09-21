import { R } from '@apis/http.type'
import { get, post, put } from '../http'
import { encryptAccept } from '@/utils/rsa'
import { GrantType, GrantTypes, SignResp, MineUserData } from './index.type'
import { CaptchaRequest, CaptchaResponse } from '@components/slide-captcha/Captcha'
import Storage from '@/utils/Storage'

const storage = new Storage()
/**
 * 获取滑块验证码
 */
export const doGetCaptchaSlider = (mobile: string | null, smsType: string) => {
    encryptAccept((encrypt) => {
        mobile = encrypt(mobile as string)
    })
    return post<CaptchaResponse>({ url: `gruul-mall-uaa/uaa/auth/captcha/slider/new`, data: { mobile, smsType } })
}

/**
 * 获取验证码
 * @param {} data
 */
export const doPostSmsCode = (data: CaptchaRequest<{ mobile: string }>) => {
    encryptAccept((encrypt) => typeof data.form === 'string' && (data = { ...data, form: { mobile: encrypt(data.form) } }))
    return post({ url: `gruul-mall-uaa/uaa/auth/captcha/sms`, data })
}

/**
 *  login
 * @param {GrantType} grantType 认证方式类型
 * @param data 认证资料
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

export const myData = () => {
    return get<MineUserData>({
        url: 'gruul-mall-uaa/uaa/shop/admin/mine',
    })
}
/**
 * 商家中心 查询账户信息
 */
export const doGetUserDataAccount = (): Promise<any> => {
    return get({
        url: `gruul-mall-uaa/uaa/user/data/account`,
    })
}

/**
 * 商家中心 发送重置密码短信验证码
 */
export const doGetMyResetPasswordSms = () => {
    return get({
        url: `gruul-mall-uaa/uaa/auth/reset/my/password/sms`,
    })
}
/**
 * 商家中心 重置密码
 * @param {string} code
 * @param {string} password
 * @param {string} confirmPassword
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
 * 重置密码 发送重置密码短信验证码
 */
export const doGetResetPasswordSms = (mobile: string) => {
    return get({
        url: `gruul-mall-uaa/uaa/auth/reset/${mobile}/password/sms`,
    })
}
/**
 * 重置密码
 * @param {string} code
 * @param {string} password
 * @param {string} confirmPassword
 * @param {string} mobile
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
 * 查询用户菜单导航
 */
export const doGetUserMenu = (): Promise<R<any>> => {
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

/**
 * 保存用户菜单配置
 *
 * @param data 菜单配置
 */
export const doPostSetUserMenuConfig = (data: Map<string, Set<string>>) => {
    const object: Obj = {}
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
