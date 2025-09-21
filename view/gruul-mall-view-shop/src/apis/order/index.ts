import { L } from '@/utils/types'
import { get, put, post } from '../http'
import type {
    BatchDeliverBody,
    CourierLoctionInfo,
    HandleErrorForm,
    IcFreightFee,
    IcOrderDetail,
    Order,
    PrintTaskLink,
    ShopLogisticsAddress,
} from './types'
import type { OrderMessage } from '@/views/message/components/types'
import { ApiNotDelivey, ApiOrderReceiver } from '@/views/order/orderShipment/types'
import {
    Printer,
    PrinterDTO,
    PrinterEditDTO,
    PrinterPageParams,
    PrintTask,
    PrintTaskDTO,
    PrintTemplate,
    PrintTemplateDetail,
    PrintTemplateDTO,
    PrintTemplatePageParams,
} from '@/q-plugin/components/types'
import { ParamsDeliverWithNoString } from '@/views/order/orderShipment/components/notShipment.vue'

/**
 * 获取订单列表 usePackage 是否根据
 * @param params.usePackage true 按包裹查询
 * @param params.packageId 为空查询未发货 否则查询已发货
 */
export const doGetOrderList = (params: any) => {
    return get<L<Order>>({ url: `gruul-mall-order/order`, params })
}

/**
 * 通过orderNo获取订单详情
 */
export const doGetOrderDetails = (orderNo: string, params: any) => {
    return get<Order>({ url: `/gruul-mall-order/order/${orderNo}`, params })
}
/**
 * 通过orderNo获取未发货订单
 */
export const dogetOrderNotDetailsByOrderNo = (orderNo: string, shopOrderNo: string) => {
    return get<{ orderReceiver: ApiOrderReceiver; shopOrderItems: ApiNotDelivey[]; extra: Obj }>({
        url: `/gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/undelivered`,
    })
}

/**
 * 商品发货
 * @param {string} orderNo
 */
export const doPutOrderDetailsByOrderNo = (orderNo: string, data: ParamsDeliverWithNoString) => {
    return put({ url: `/gruul-mall-order/order/${orderNo}/deliver`, data })
}
/**
 * 商品批量发货
 * @param {string} orderNo
 */
export const doPostOrderBatchDeliver = (data: BatchDeliverBody[]) => {
    return put<string>({ url: `gruul-mall-order/order/batch/deliver`, data })
}
/**
 * 关闭店铺订单
 * @param {string} orderNo
 * @param {string} shopOrderNo
 */
export const doPutCloseShopOrder = (orderNo: string, shopOrderNo: string) => {
    return put({ url: `gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/close` })
}

//修改或者新增地址
export const setLogisticsDddress = (data: any) => {
    return post({
        url: 'gruul-mall-shop/shop/logistics/address/set',
        data,
    })
}
// 获取物流地址列表
export const doGetLogisticsList = (): Promise<any> => {
    return get<L<ShopLogisticsAddress>>({
        url: 'gruul-mall-shop/shop/logistics/address/list',
    })
}
/**
 * 获取采购单列表
 */
export const getPurchaseOrderList = (params: any = {}) => {
    return get<OrderMessage>({ url: 'addon-supplier/supplier/order', params })
}

/**
 * 获取平台发货配置信息
 */
export const doGetShopDeliveryConfig = (): Promise<any> => {
    return get({ url: '/gruul-mall-shop/shop/deliver' })
}

/**
 * 联系买家
 */
export const postAddContact = (shopId: Long, userId: string) => {
    return post({ url: `gruul-mall-carrier-pigeon/pigeon/platform-chat-rooms/${shopId}/${userId}` })
}

const deliverTypeOptionsMap = {
    IC_MERCHANT: '商家自行配送',
    IC_OPEN: 'UU 跑腿配送',
}
/**
 * 获取店铺可选择的同城配送方式
 */
export const doGetShopDeliveryMethod = async () => {
    const { code, data, msg } = await post<('IC_MERCHANT' | 'IC_OPEN')[]>({ url: `addon-ic/ic/shop/order/deliver/type` })
    if (code === 200 && data) {
        return {
            data: data.map((item) => {
                return {
                    label: deliverTypeOptionsMap[item],
                    value: item,
                }
            }),
            code,
            msg,
        }
    } else {
        return {
            data: [],
            code,
            msg,
        }
    }
}
/**
 * 批量获取配送单运费价格 当选择UU 跑腿作为配送方时可用
 */
export const doGetBatchDeliveryPrice = (data: string[]) => {
    return post<IcFreightFee>({ url: 'addon-ic/ic/shop/order/deliver/price', data })
}

type ICParament = { orderNo: string; shopId: Long }
/**
 * 获取同城配送订单信详情
 * @param {string} orderNo
 * @param {string} shopId
 */
export const doGetICOrder = (data: ICParament) => {
    return post<IcOrderDetail>({ url: `addon-ic/ic/shop/order/deliver/info`, data })
}

/**
 * 获取UU跑腿配送员最新信息和定位
 */
export const doGetUuptCourierInfo = (data: ICParament) => {
    return post<CourierLoctionInfo>({ url: `addon-ic/ic/shop/order/courier/uupt`, data })
}

/**
 * 同城单异常处理
 */
export const doHandleIcOrderError = (data: HandleErrorForm) => {
    return post({ url: `addon-ic/ic/shop/order/error`, data })
}

// ============================-----------------================
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
export const getPrintTemplate = (data: PrintTemplatePageParams) => {
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
