import { get, post, put } from '@/apis/http'
import type {
  IntraCityDistributionConfig,
  PrintTask,
  PrintTaskDTO,
  PrintTaskLink,
  PrintTemplate,
  PrintTemplateDTO,
  PrintTemplateDetail,
  Printer,
  PrinterDTO,
  PrinterEditDTO,
  PrinterPageParams,
} from './types/types'
import type { L } from '@/utils/types'
import type { Long } from '@/apis/good'

/**
 * 添加同城配送配置
 */
export const saveNewIntraCity = (data: any) => {
  return post({
    url: 'addon-ic/ic/shop/config',
    data,
  })
}
/**
 * 获取同城配送信息
 */
export const getIntraCityInfo = () => {
  return post<IntraCityDistributionConfig>({
    url: 'addon-ic/ic/shop/config/get',
  })
}

/**
 * 查询 店铺 信息
 */
export const doGetShopInfowhile = (shopId: string): Promise<any> => {
  return get({ url: `gruul-mall-shop/shop/info/${shopId}` })
}

export interface ShopUuptStatus {
  platformActivated: boolean // 平台是否已激活
  cityOpen?: boolean // 当前店铺城市是否已开放
  activated?: boolean // 是否已激活
  balance?: number // 账户余额
  frozen?: number // 冻结金额
}

/**
 * 查询店铺 uupt 账号激活状态
 */
export const doGetShopUuptStatus = () => {
  return post<ShopUuptStatus>({ url: `addon-ic/ic/uupt/shop/status` })
}

interface QueryCaptcha {
  mobile: string
  captcha?: string
  smsCode?: string
}
export interface QueryCaptchaResult {
  needCaptcha: boolean
  base64Captcha?: string
  msg?: string
}

/**
 * 商家发送短信验证码进行授权
 */
export const doPostSmsCaptcha = (data: Omit<QueryCaptcha, 'smsCode'>) => {
  return post<QueryCaptchaResult>({ url: `addon-ic/ic/uupt/shop/sms`, data })
}
/**
 * 商家用户授权
 */
export const doAuthSms = (data: Omit<QueryCaptcha, 'captcha'>) => {
  return post<{ msg: string }>({ url: `addon-ic/ic/uupt/shop/auth`, data })
}
/**
 * 获取 uupt 充值二维码
 */
export const getRechargeQrCode = () => {
  return post<{ h5Qrcode: string; pcUrl: string }>({ url: `addon-ic/ic/uupt/shop/recharge` })
}

/**
 * 打印机分页查询
 */
export const getPrinter = (data: PrinterPageParams) => {
  return post<L<Printer>>({ url: `gruul-mall-order/order/printer/page`, data })
}
/**
 * 添加打印机
 */
export const doAddPrinter = (data: PrinterDTO) => {
  return post({ url: `gruul-mall-order/order/printer/save`, data })
}
/**
 * 编辑打印机
 */
export const doUpdatePrinter = (data: PrinterEditDTO) => {
  return post({ url: `gruul-mall-order/order/printer/edit`, data })
}
/**
 * 删除打印机 已绑定打印任务的打印机不可删除
 */
export const doDelPrinter = (data: { printerId: Long }) => {
  return post({ url: `gruul-mall-order/order/printer/delete`, data })
}

// ===============================================================================
/**
 * 打印模板分页查询
 */
export const getPrintTemplate = (data: PrinterPageParams) => {
  return post<L<PrintTemplate>>({ url: `gruul-mall-order/order/print/template/page`, data })
}
/**
 * 添加或编辑打印模板
 */
export const doUpdatePrintTemplate = (data: PrintTemplateDTO) => {
  return post({ url: `gruul-mall-order/order/print/template`, data })
}
/**
 * 删除打印模板
 */
export const doDelPrintTemplate = (data: { id: Long }) => {
  return post({ url: `gruul-mall-order/order/print/template/delete`, data })
}
/**
 * 根据 模板id查询模板详情
 */
export const doGetPrintTemplateDetail = (data: { id: Long }) => {
  return post<PrintTemplateDetail>({ url: `gruul-mall-order/order/print/template/detail`, data })
}

// ===============================================================================
/**
 * 打印任务分页查询
 */
export const getPrintTask = (data: PrinterPageParams) => {
  return post<L<PrintTask>>({ url: `gruul-mall-order/order/print/task/page`, data })
}
/**
 * 添加或编辑打印任务
 */
export const doUpdatePrintTask = (data: PrintTaskDTO) => {
  return post({ url: `gruul-mall-order/order/print/task`, data })
}
/**
 * 删除打印任务
 */
export const doDelPrintTask = (data: { id: Long }) => {
  return post({ url: `gruul-mall-order/order/print/task/delete`, data })
}
/**
 * 商家手动打印订单
 */
export const shopManualPrintOrder = (data: { orderNo: string; link: keyof typeof PrintTaskLink }) => {
  return post({ url: `gruul-mall-order/order/print/task/print`, data })
}
