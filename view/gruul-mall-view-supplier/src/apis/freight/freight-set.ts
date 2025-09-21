import { get, put, post, del } from '@/apis/http'
/**
 * @description:快递设置新增/修改
 * @returns {*}
 */
export const doCourierUpdateAndEdit = (customer: string, key: string, secret: string, id?: string) => {
    return post({ url: 'gruul-mall-freight/logistics/settings/edit', data: { customer, id, key, secret } })
}
/**
 * @description:快递设置信息获取
 * @returns {*}
 */
export const doGetCourierInfo = (): Promise<any> => {
    return get({ url: 'gruul-mall-freight/logistics/settings/get' })
}
