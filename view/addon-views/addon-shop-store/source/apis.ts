import { get, post, put, del } from '@/apis/http'

/**
 * 新增门店
 */
export const PostAddStore = (data?: any) => {
  return post({
    url: 'addon-shop-store/store/issue',
    data,
  })
}
/**
 * 修改店铺门店信息
 */
export const PostUpdateStore = (data?: any) => {
  return post({
    url: 'addon-shop-store/store/update',
    data,
  })
}
/**
 * 店铺门店列表
 */
export const GetStoreList = (params: any): Promise<any> => {
  return get({
    url: 'addon-shop-store/store/list',
    params,
  })
}
/**
 * 删除门店
 */
export const DelStore = (shopId: string, id: string) => {
  return del({
    url: `addon-shop-store/store/del/${shopId}/${id}`,
  })
}
/**
 * 店铺门店状态修改
 */
export const PutStatus = (status: string, data: any) => {
  return put({
    url: `addon-shop-store/store/update/${status}`,
    data,
  })
}
/**
 * 店铺门店详情
 */
export const getstoreDetail = (shopId: string, params: any) => {
  return get({
    url: `addon-shop-store/store/info/${shopId}`,
    params,
  })
}

/**
 * 店铺店员新增
 */
export const PostAddassIstant = (data?: any) => {
  return post({
    url: 'addon-shop-store/assistant/issue',
    data,
  })
}
/**
 * 店铺店员获取
 */
export const GetAssistantList = (data?: any) => {
  return get({
    url: 'addon-shop-store/assistant/list',
    data,
  })
}
/**
 * 给店员设置门店
 */
export const PutAssistant = (shopAssistantId: string, params: any) => {
  return put({
    url: `addon-shop-store/assistant/set/store/${shopAssistantId}`,
    params,
  })
}

/**
 * 删除店员信息
 */
export const DelAssistant = (shopAssistantId: string) => {
  return del({
    url: `addon-shop-store/assistant/del/${shopAssistantId}`,
  })
}
/**
 * 获取验证码
 */
export const doPostSmsCode = (data: any) => {
  const { captchaTrack, form, id, smsType } = data
  return post({ url: `gruul-mall-uaa/uaa/auth/captcha/sms/${smsType}`, data: { captchaTrack, form, id } })
}
