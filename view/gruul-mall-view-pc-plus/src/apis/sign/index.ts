import { get, post, put, del, patch } from '../http'
import { R } from '@/apis/http.type'
import { encryptAccept } from '@/utils/rsa'
import { GrantType, GrantTypes, SignResp } from './index.type'
import { CaptchaRequest, CaptchaResponse } from '@/components/slide-captcha/Captcha'
import Storage from '@/libs/storage'

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
export const doSignByUser = (grantType: GrantType, data: any): Promise<R<SignResp>> => {
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
    url: `/gruul-mall-uaa/login?grant_type=${grant.value}`,
    data,
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
  })
}
