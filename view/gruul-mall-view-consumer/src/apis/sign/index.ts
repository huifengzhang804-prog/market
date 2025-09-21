import type { Result } from '@/utils/types'
import api from '@/libs/request'
import { encryptAccept } from '@/utils/rsa'
import { GrantType, GrantTypes, type SignResp } from '@/apis/sign/type'
import type { CaptchaRequest, CaptchaResponse } from '@/components/slide-captcha/Captcha'
import storage from '@/utils/storage'

/**
 * 获取滑块验证码
 */
export const doGetCaptchaSlider = (mobile: string | null, smsType: string) => {
  encryptAccept((encrypt) => {
    mobile = encrypt(mobile as string)
  })
  const data = { mobile: mobile, smsType: smsType }
  return api.post<CaptchaResponse>(`gruul-mall-uaa/uaa/auth/captcha/slider/new`, data)
}

/**
 * 获取验证码
 */

export const doPostSmsCode = (data: CaptchaRequest<{ mobile: string }>) => {
  encryptAccept((encrypt) => typeof data.form === 'string' && (data = { ...data, form: { mobile: encrypt(data.form) } }))
  return api.post(`gruul-mall-uaa/uaa/auth/captcha/sms`, data)
}

// 获取分销短信验证码()
export const doPostSmsCodeFen = (data: CaptchaRequest<string>) => {
  return api.post(`gruul-mall-uaa/uaa/auth/captcha/sms/DISTRIBUTOR`, data)
}

/**
 * login
 * @param {GrantType} grantType 认证方式类型
 * @param data 认证资料
 */
export const doSignByUser = (grantType: GrantType, data: any) => {
  const grant = GrantTypes[grantType]
  //参数加密
  if (grant.encrypt) {
    encryptAccept((encrypt) => {
      grant.encryptFields?.forEach((field) => {
        data[field] = encrypt(data[field])
      })
    })
  }
  return api.post<SignResp>(`gruul-mall-uaa/login?grant_type=${grant.value}`, data, { 'Shop-Id': 0 })
}
/**
 * 店铺记录访客数
 */
export const doAddVistor = (shopId: Long) => {
  return api.post(
    'gruul-mall-shop/shop/visitor/add',
    {},
    {
      'Shop-Id': shopId,
    },
  )
}
/**
 * 获取会员中心信息
 * @param params
 */
export const doGetMemberCardInfo = () => {
  return api.get(`gruul-mall-user/user/member/card/info`)
}

/**
 * 退出登录
 */
export const doPostLogout = () => {
  storage.removeThis()
  return api.post('gruul-mall-uaa/logout')
}
