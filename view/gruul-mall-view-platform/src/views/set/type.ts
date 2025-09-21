/**
 * 基础设置form类型
 * @param {number} normalOrderOvertime 未支付订单超时时间
 * @param {number} confirmOvertime 已发货自动关闭时间
 * @param {number} commentOvertime 已完成自动关闭售后时间
 * @param {number} finishOvertime; 申请售后时间
 * @param {number} merchantConfirmOvertime; 售后审核时间
 * @param {number} key 快宝id
 * @param {number} customer 快宝值
 * @param {number} ratio 积分抵扣比例百分比
 * @param {number} ceiling 顶底抵扣上限额度百分比
 */
interface BasicFormType {
    normalOrderOvertime: number
    confirmOvertime: number
    commentOvertime: number
    finishOvertime: number
    merchantConfirmOvertime: number
    key: string
    customer: string
    ratio: number
    ceiling: number
}
/**
 * 添加物流公司from类型
 */
export interface LogisticsForm {
    logisticsCompanyCode: string
    logisticsCompanyName: string
    logisticsCompanyStatus: string
    printTempNo: string
}
export interface LogisticsFormID extends LogisticsForm {
    id: string
}
export interface Row {
    createTime: string
    deleted: boolean
    id: string
    logisticsCompanyCode: string
    logisticsCompanyName: string
    logisticsCompanyStatus: string
    updateTime: string
    version: number
}
