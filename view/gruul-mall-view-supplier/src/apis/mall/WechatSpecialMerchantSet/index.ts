import { get, post, put, del, patch } from '../../http'
// 查询商户 特约商户申请状态    gruul-mall-payment/wx/service/merchant/
export const applicationStatus = () => {
    return get({
        url: 'gruul-mall-payment/wx/service/merchant',
        errorImmediately: false,
    })
}

// 创建特约商户申请单   gruul-mall-payment/wx/service/merchant/apply/create
export const createApplication = (data: any) => {
    return post({
        url: 'gruul-mall-payment/wx/service/merchant/apply/create',
        data,
    })
}
// 商户与申请单绑定   gruul-mall-payment/wx/service/merchant/apply/{applymentId}/bound   ContentTy
export const applyBound = (applymentId: string) => {
    return put({
        url: `gruul-mall-payment/wx/service/merchant/apply/${applymentId}/bound`,
        headers: { 'Content-Type': 'peapplication/x-www-form-urlencoded;charset=UTF-8' },
    })
}
// 特约商户确认
export const confirmApi = () => {
    return put({
        url: `gruul-mall-payment/wx/service/merchant/apply/confirm`,
    })
}
