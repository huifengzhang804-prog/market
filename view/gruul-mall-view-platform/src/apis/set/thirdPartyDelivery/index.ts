import { get, put, post, del } from '@/apis/http'
/**
 * 设置 uupt 开放平台配置
 */
export const doUuptOpenConfigUpdateAndEdit = (data: { appid: string; appKey: string; openId: string }) => {
    return post({ url: 'addon-ic/ic/uupt/open/config', data })
}
/**
 * 获取 uupt 开放平台配置
 */
export const doGetUuptOpenConfigInfo = () => {
    return post({ url: 'addon-ic/ic/uupt/open/config/get' })
}
