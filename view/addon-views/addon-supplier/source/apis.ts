import { get, post, put, del, patch } from '@/apis/http'
import { SendMessage, Page, ReceiverMessage } from '@/views/message/components/types'
import type { InvoiceRequest } from './components/page-components/purchaseOrder/type'
import { Obj, Long, L } from '@/utils/types'
import { AddressInfo } from './types/supplier'

export const getSupplierProductList = <T>(params?: any) => {
  return get<L<T>>({
    url: 'addon-supplier/supplier/manager/product/getSupplyList',
    params: { ...params },
  })
}

export const getSupplierDistributionCost = (data: any): Promise<any> => {
  return post({ url: 'gruul-mall-order/order/distribution/cost', data, showLoading: false })
}

export const purchaseCreateOrder = (data: any = {}) => {
  return post<Obj>({ url: 'addon-supplier/supplier/order', data })
}

export const getPurchaseOrderCreationResult = (mainNo: string) => {
  return get({ url: `addon-supplier/supplier/order/creation/${mainNo}` })
}

/**
 * 支付采购订单
 * @param data 支付信息
 */
export const payPurchaseOrder = (data: any = {}) => {
  return put({ url: 'addon-supplier/supplier/order/pay', data })
}
/**
 * 取消采购订单
 * @param orderNo 订单号
 */
export const cancelPurchaseOrder = (orderNo: string) => {
  return put({ url: `addon-supplier/supplier/order/close/${orderNo}` })
}
/**
 * 获取已发货包裹列表
 * @param orderNo 订单编号
 */
export const getDeliveryedPurchasePackages = (orderNo: string): Promise<any> => {
  return get({ url: `addon-supplier/supplier/order/delivery/${orderNo}` })
}
/**
 * 获取入库详情
 * @param orderNo 订单号
 */
export const getInStorageDetails = (orderNo: string): Promise<any> => {
  return get({ url: `addon-supplier/supplier/order/storage/${orderNo}` })
}

/**
 * 订单入库
 * @param data 入库数据
 */
export const putPurchaseOrderInStorage = (data: any) => {
  return put({ url: 'addon-supplier/supplier/order/storage', data })
}
/**
 * 完成采购订单入库
 * @param orderNo 订单号
 */
export const putFinishPurchaseOrderInStorage = (orderNo: string) => {
  return put({ url: `addon-supplier/supplier/order/complete/${orderNo}` })
}
/**
 * 获取待发布列表
 * @param params 查询参数
 */
export const getToBeReleaseList = (params: any) => {
  return get({ url: 'addon-supplier/supplier/order/publish', params })
}
/**
 * 获取待发布商品详情
 * @param productId 产品id号
 */
export const getToBeReleaseDetails = (productId: string, supplierId: string) => {
  return get<any>({ url: `addon-supplier/supplier/order/publish/product/${productId}`, params: { supplierId } })
}

/**
 * 添加聊天联系人
 * @param sender 发送人id号
 * @param targetId 收件人id
 */
export const postAddContact = (sender: Long, targetId: string) => {
  return post({ url: `gruul-mall-carrier-pigeon/pigeon/group-chat-rooms/${sender}/${targetId}` })
}
/**
 * 获取采购列表已发布的列表
 */
export const getPurchaseIssueList = (params: any): Promise<any> => {
  return get({ url: 'gruul-mall-goods/manager/product/purchase/issue/products', params })
}
/**
 * 修改下架状态
 */
export const putIssureListOnline = (id: string) => {
  return put({ url: `gruul-mall-goods/manager/product/purchase/issue/product/updateStatus/${id}` })
}

export const getSupplierSearchList = (params: any = {}): Promise<any> => {
  return get({ url: 'gruul-mall-shop/shop/info/getSupplierInfo', params })
}

// 用于获取店铺搜索列表
export const getShopSearchList = (params: any = {}) => {
  return get({ url: 'gruul-mall-shop/shop/info/getShopInfo', params })
}
/**
 * 获取采购单列表
 */
export const getPurchaseOrderList = (params: any = {}): Promise<any> => {
  return get({ url: 'addon-supplier/supplier/order', params })
}
/**
 * 获取采购单详情
 * @param orderNo 订单号
 */
export const getPurchaseOrderDetails = (orderNo: string): Promise<any> => {
  return get({ url: `addon-supplier/supplier/order/${orderNo}` })
}

/**
 * 物流轨迹
 * @param {*} companyCode 物流公司名字
 * @param {*} waybillNo 物流单号
 */
export const doGetLogisticsTrajectoryByWaybillNo = (companyCode: string, waybillNo: string): Promise<any> => {
  return get({ url: 'gruul-mall-freight/logistics/node', params: { companyCode, waybillNo } })
}

/**
 * 查询商品规格组与sku列表
 */
export const doGetCommoditySku = (shopId: Long, productId: any, isSupplier = false) => {
  return get({
    url: `gruul-mall-storage/storage/shop/${shopId}/product/${productId}?isSupplier=${isSupplier}`,
  })
}

/**
 * 获取平台类目列表
 */
export const doGetPlatformCategory = () => {
  return get<any>({
    url: 'gruul-mall-addon-platform/platform/shop/signing/category/choosable/list',
  })
}

/**
 * 获取物流服务收货地址
 */
export const doGetDeliveryAddress = () => {
  return get<L<AddressInfo>>({
    url: 'gruul-mall-shop/shop/logistics/address/list',
    params: {
      current: 1,
      size: 1000,
    },
  })
}

/**
 * 获取模板列表
 */
export const doGetLogisticsTemplateList = (current: number, size: number): Promise<any> => {
  return get({
    url: 'gruul-mall-freight/logistics/template/get/list',
    params: {
      current,
      size,
    },
  })
}

/**
 * 发布商品
 */
export const doReleaseGood = (data: any) => {
  return post({
    url: 'gruul-mall-goods/manager/product/issue',
    data,
  })
}

/*
 * 产品 特性列表 I
 */
export const doGetfeatureList = (params: any) => {
  return get({
    url: `gruul-mall-goods/manager/feature/list`,
    params,
  })
}

/**
 * 获取供应商列表
 */
export const doGetSupList = (params: any): Promise<any> => {
  return get({
    url: 'gruul-mall-goods/manager/supplier/list',
    params,
  })
}

/**
 * 获取分类列表
 */
export const doGetCategory = (params: any) => {
  return get<any>({
    url: 'gruul-mall-goods/goods/product/category',
    params,
  })
}

/**
 * 获取单个商品信息
 */
export const doGetSingleCommodity = (id: any, shopId: string) => {
  return get({
    url: `gruul-mall-goods/manager/product/get/${shopId}/${id}`,
  })
}

/*
 * 商品一键复制
 */
export const doGetCopyGoods = (params: any) => {
  return get({
    url: `gruul-mall-goods/api/copy/goods/detail`,
    params,
  })
}

/**
 * 获取品牌列表
 */
export const getBrandList = (data: any) => {
  return get({
    url: 'gruul-mall-search/search/brand/brandInfo',
    params: data,
  })
}

/**
 * 分页查询供应商列表
 */
export const getSupplierMessageUsers = (keywords: string, page: Page, senderType: number | null) => {
  return get({
    url: `/gruul-mall-carrier-pigeon/pigeon/group-chat-room-messages/chat-rooms`,
    params: { ...page, keywords, senderType },
    showLoading: false,
  })
}

/**
 * 分页查询供应商聊天记录
 */

export const getSupplierMessages = (receiverMessage: ReceiverMessage, page: Page) => {
  return get({
    url: `/gruul-mall-carrier-pigeon/pigeon/group-chat-room-messages/chat-room`,
    params: { ...receiverMessage, ...page },
  })
}

/**
 * 发送消息给供应商
 * @param userId 用户id
 * @param data 消息体
 */
export const sendSupplierMessages = (data: SendMessage) => {
  return post({
    url: `/gruul-mall-carrier-pigeon/pigeon/group-chat-room-messages/message`,
    data,
    showLoading: false,
  })
}
/**
 * 获取今日新增供应商数量
 */
export const doGetNewSupplierNumber = () => {
  return get({
    url: 'gruul-mall-shop/shop/supplierQuantity',
  })
}

/**
 * 获取供应商订单数量
 */
export const doGetsupplierOrderCount = () => {
  return get({
    url: 'addon-supplier/supplier-overview/supplier-purchase-order-number',
  })
}
/**
 * 获取代销设置信息
 */
export const doGetConsignmentSetting = () => {
  return get<any>({
    url: 'gruul-mall-goods/consignment/config',
  })
}
/**
 * 更新代销设置信息
 */
export const doPostUpdateConsignmentSetting = (data: any) => {
  return post({ url: 'gruul-mall-goods/consignment/config', data })
}
/**
 * 获取已铺货列表
 */
export const doGetShippedGoodsList = (params: any): Promise<any> => {
  return get({ url: 'gruul-mall-goods/consignment/pave/goods', params })
}
/**
 * 批量铺货
 */
export const doPostBatchDistribution = (data: any) => {
  return post({ url: 'gruul-mall-goods/consignment/pave/goods', data })
}
/**
 * 获取供应商商品信息
 * @param commodityId 商家id号
 * @param shopId 店铺id号
 */
export const doGetSupplierCommodityDetails = (commodityId: string, shopId: string) => {
  return get({ url: `addon-supplier/supplier/manager/product/get/${shopId}/${commodityId}` })
}
/**
 * 单项一键铺货
 */
export const doPostSingleDistribution = (data: any) => {
  return post({ url: 'gruul-mall-goods/consignment/single/pave/goods', data })
}
/**
 * 上架代销商品
 * @param productId 商品id号
 */
export const doPutShelfOnConsignmentGoods = (productId: string) => {
  return put({ url: `gruul-mall-goods/consignment/product/update/status/${productId}` })
}
/**
 * 获取采购商品详情信息
 * @param commodityId 商品id号
 */
export const doGetConsignmentCommidityInfo = (commodityId: string) => {
  return get<any>({ url: `gruul-mall-goods/manager/product/consignment/${commodityId}` })
}
/**
 * 提交编辑采购商品信息
 */
export const doPostUpdateConsignmentCommodityInfo = (data: any) => {
  return post({ url: 'gruul-mall-goods/manager/product/consignment/update', data })
}
/**
 * 分页查询发票抬头
 */
export const doGetInvoicePageInvoiceHeader = (pages?: Page): Promise<any> => {
  return get({
    url: `addon-invoice/invoice/invoice-headers/pageInvoiceHeader`,
    params: {
      ...pages,
      ownerType: 'SHOP',
    },
  })
}
/**
 * 设置默认抬头
 */
export const doPutDefaultInvoiceHeader = (data: any) => {
  return put({ url: `addon-invoice/invoice/invoice-headers/default-invoice-header`, data })
}
/**
 * 创建发票抬头
 */
export const doPostinvoiceHeader = (data: any) => {
  return post({ url: `addon-invoice/invoice/invoice-headers/invoice-header`, data })
}
/**
 * 删除发票抬头
 */
export const doDeleteInvoiceHeader = (invoiceHeaderId: string) => {
  return del({ url: `addon-invoice/invoice/invoice-headers/${invoiceHeaderId}` })
}
/**
 * 查询抬头详情
 */
export const doGetInvoiceHeaderDetail = (params: any): Promise<any> => {
  return get({ url: `addon-invoice/invoice/invoice-headers`, params })
}

const invoiceUrl = 'addon-invoice/invoice/'
/**
 * 申请开票
 */
export const doPostInvoiceRequest = (data: InvoiceRequest) => {
  return post({ url: `${invoiceUrl}invoiceRequest`, data })
}
/**
 * 获取发票设置
 */
export const doGetinvoiceSettings = (params: { invoiceSettingsClientType: 'SHOP' | 'SUPPLIER'; shopId: string }): Promise<any> => {
  return get({ url: `addon-invoice/invoice/invoiceSettings`, params })
}
/**
 * 查询发票详情
 */
export const doGetinvoiceDetail = (id: string) => {
  return get({ url: `addon-invoice/invoice/invoiceRequest/${id}` })
}
/**
 * 获取发票状态
 */
export const doGetInvoiceTryRequest = (params: any): Promise<any> => {
  return get({ url: `addon-invoice/invoice/invoiceRequest/pre-request`, params })
}
/**
 * 撤销开票
 */
export const doPutwithdraw = (Id: string) => {
  return put({ url: `addon-invoice/invoice/invoiceRequest/${Id}` })
}
/**
 * 重发发票
 */
export const doPostReSend = (data: any) => {
  return post({ url: 'addon-invoice/invoice/invoiceAttachment/re-send', data })
}
/**
 * 获取默认抬头
 */
export const doGetDefault = (): Promise<any> => {
  return get({ url: `addon-invoice/invoice/invoice-headers/getDefaultInvoiceHeader`, params: { invoiceHeaderOwnerType: 'SHOP' } })
}

/**
 * 获取采购支出
 */
export const doGetPurchaseExpenditure = (params: any) => {
  return get({
    url: 'gruul-mall-overview/overview/purchase-payout',
    params,
  })
}

/**
 * 根据店铺类型，查询商品标签
 */
export const doGetLabelList = (shopType: any) => {
  return get<any>({
    url: `gruul-mall-goods/goods/product/label/searchByShopType/${shopType}`,
    params: {
      size: 1000,
    },
  })
}

// 导出 采购订单
export const doPostSupplierExport = (data: any) => {
  return post({
    url: 'addon-supplier/supplier/order/export',
    data,
  })
}

// 供应商审核商品列表
export const doGetSupplierExamineGoods = (params: any = {}) => {
  return get<any>({ url: 'addon-supplier/supplier/manager/product/audit', params })
}

/**
 * 供应商商品下架
 * @param data 下架参数
 * @param status 状态
 */
export const doUpdateSupplierSellStatus = (data: any, status = '') => {
  return put({
    url: `addon-supplier/supplier/manager/product/updateStatus/${status}`,
    data,
  })
}
/**
 * 查询店铺余额
 */
export const doGetShopBalance = (): Promise<any> => {
  return get({
    url: 'gruul-mall-overview/overview/shop/balance',
  })
}
/**
 * 更新商品状态
 * @param {string} status
 */
export const doUpdateSellStatus = (keys: { shopId: string | number; productId: string }[], status: string) => {
  return put({
    url: `gruul-mall-goods/manager/product/updateStatus/${status}`,
    data: {
      keys,
    },
  })
}
/**
 * @description 获取采购订单支付方式
 * @returns
 */
export const doGetSupplierOrderPaymentMethod = (): Promise<any> => {
  return get({ url: `addon-supplier/supplier/order/config/payment/method` })
}
