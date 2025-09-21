import { AliyunConfig } from '@/views/set/components/fileSet'
import { del, get, patch, post, put } from '../http'

export const getAuth = () => {
    return get({ url: '/gruul-mall-uaa/uaa/menu/navigate' })
}
/**
 * 新增权限
 * @param {name:string,path:string,perm:string} data
 */
export const doNewPermissions = (data: any) => {
    return post({
        url: '/gruul-mall-uaa/uaa/menu',
        data,
    })
}
/**
 * 编辑权限
 * @param {menuId string}
 * @param {name:string,path:string,perm:string} data
 */
export const doEditPermissions = (menuId: string, data: any) => {
    return put({
        url: `/gruul-mall-uaa/uaa/menu/${menuId}`,
        data,
    })
}

/**
 * 删除权限
 * @param {string} menuId
 */
export const doDelPermissions = (menuId: string) => {
    return del({
        url: `/gruul-mall-uaa/uaa/menu/${menuId}`,
    })
}

/**
 * 设置oss
 */
export const doSaveOSS = (data: any) => {
    return post({
        url: 'gruul-mall-carrier-pigeon/oss/edit',
        data,
    })
}
/**
 * OSS配置列表查询
 */
export const getOSSList = (): Promise<any> => {
    return get({
        url: `gruul-mall-carrier-pigeon/oss/list`,
    })
}

/**
 * 删除OSS配置列表
 */
export const deleteOSSList = (type: string): Promise<any> => {
    return del({
        url: `gruul-mall-carrier-pigeon/oss/remove/${type}`,
    })
}

/**
 * 获取指定服务商的配置
 */
export const changeOSSType = (type: string): Promise<any> => {
    return put({
        url: `gruul-mall-carrier-pigeon/oss/using/${type}`,
    })
}
/**
 * 获取oss服务商配置
 * @param {string} type oss服务商
 */
export const doGetOSS = (type: string): Promise<any> => {
    return get({
        url: `gruul-mall-carrier-pigeon/oss/current/config/${type}`,
    })
}

/**
 * 设置基础信息
 */
export const doSaveBasicSet = (data: any) => {
    return post({
        url: 'gruul-mall-order/order/config/timeout',
        data,
    })
}
/**
 * 获取基础信息
 */
export const doGetBasicSet = () => {
    return get<{
        payTimeout: Long
        confirmTimeout: Long
        commentTimeout: Long
        afsAuditTimeout: Long
    }>({
        url: 'gruul-mall-order/order/config/timeout',
    })
}

/**
 * 短信启用指定服务商
 */
export const SmsEnabled = (type: string): Promise<any> => {
    return put({
        url: `gruul-mall-carrier-pigeon/sms/using/${type}`,
    })
}

/**
 * 删除指定服务商短信配置
 */
export const deleteSms = (type: string): Promise<any> => {
    return del({
        url: `gruul-mall-carrier-pigeon/sms/remove/${type}`,
    })
}

/**
 * 获取采购时间信息
 */
export const doGetSupplierBasicSet = () => {
    return get({
        url: 'addon-supplier/supplier/order/config/timeout',
    })
}
/**
 * 获取采购时间信息
 */
export const doPutSupplierBasicSet = (data: { payTimeout: string }) => {
    return put({
        url: 'addon-supplier/supplier/order/config/timeout',
        data,
    })
}
/**
 * 短信设置
 */
export const doSmsSet = (data: any) => {
    return post({
        url: `gruul-mall-carrier-pigeon/sms/saveAndEdit/current/config/${data.enableCaptcha}`,
        data: {
            ...data,
            enableCaptcha: undefined,
        },
    })
}

/**
 * 获取短信获取配置列表
 */
export const getSmsList = () => {
    return get<AliyunConfig[]>({
        url: `gruul-mall-carrier-pigeon/sms/list`,
    })
}

/**
 * 获取短信配置
 */
export const doGetSms = (type: string): Promise<any> => {
    return get({
        url: `gruul-mall-carrier-pigeon/sms/current/config/${type}`,
    })
}
/**
 * 获取消息配置列表
 */
export const doGetNewsList = (params: any): Promise<any> => {
    return get({
        url: '/gruul-mall-carrier-pigeon/pigeon/notice/platform',
        params,
    })
}

/**
 * 消息新增
 */
export const doPostNews = (data: any) => {
    return post({
        url: '/gruul-mall-carrier-pigeon/pigeon/notice',
        data,
    })
}
/**
 * 消息编辑
 */
export const doPutNews = (noticeId: any, data: any) => {
    return put({
        url: `/gruul-mall-carrier-pigeon/pigeon/notice/${noticeId} `,
        data,
    })
}
/**
 * 消息推送
 */
export const doPatchNews = (noticeId: any) => {
    return patch({
        url: `/gruul-mall-carrier-pigeon/pigeon/notice/${noticeId} `,
    })
}
/**
 * 消息删除
 */
export const doDelNews = (noticeId: any) => {
    return del({
        url: `/gruul-mall-carrier-pigeon/pigeon/notice/${noticeId} `,
    })
}
/**
 * @description 获取商品审核的配置信息
 * @returns
 */
export const doGetGoodsAuditSetting = () => {
    return get({ url: '/gruul-mall-goods/manager/product/audit/setting' })
}
/**
 * @description 修改商品审核配置信息
 * @param productAuditType
 * @returns
 */
export const doPutGoodsAuditSetting = (productAuditType = '') => {
    return put({ url: `/gruul-mall-goods/manager/product/audit/setting/${productAuditType}` })
}
/**
 * @description 获取积分订单超时时间
 * @returns
 */
export const doGetIntergalTimeoutSetting = () => {
    return get({ url: '/addon-integral/integral/order/time' })
}
/**
 * @description 修改积分订单超时时间
 * @param time 修改的时间
 * @returns
 */
export const doPutIntergalTimeoutSetting = (time: any) => {
    return put({ url: `addon-integral/integral/order/time/${time}` })
}

/**
 * @description 修改采购订单支付方式
 * @param data 支付方式
 * @returns
 */
export const doPutSupplierOrderPaymentMethod = (data: { paymentMethods: string[] }) => {
    return put({ url: `addon-supplier/supplier/order/config/payment/method`, data })
}
/**
 * @description 获取采购订单支付方式
 * @returns
 */
export const doGetSupplierOrderPaymentMethod = (): Promise<any> => {
    return get({ url: `addon-supplier/supplier/order/config/payment/method` })
}
