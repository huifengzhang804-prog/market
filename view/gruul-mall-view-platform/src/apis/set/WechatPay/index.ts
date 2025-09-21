import { get, put, post, del } from '@/apis/http'
import type { ApiParameters } from '@/views/set/components/WechatPay'
/**
 * 编辑(新增/修改) 支付商户信息
 */
export const doEditmerchant = (data: ApiParameters): Promise<any> => {
    delete data.alipayRootCert
    delete data.appCertPublicKey
    return post({ url: 'gruul-mall-payment/merchant/edit', data })
}
/**
 * 获取商户配置信息
 * @param {string} payType
 */
export const doGetMerchant = (payType: string): Promise<any> => {
    return get({ url: `gruul-mall-payment/merchant/get`, params: { payType } })
}
