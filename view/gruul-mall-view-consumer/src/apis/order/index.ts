import api from '@/libs/request'
import type { CourierLoctionInfo, IcOrderDetail, OrderShopsDTORequest } from './types'
import type { ApiOrder } from '@/pluginPackage/order/orderList/types'

/**
 * 生成价格预算
 */
export const doPostBudget = (data: OrderShopsDTORequest) => {
  return api.post('gruul-mall-order/order/budget', data)
}
/**
 * 生成订单
 */
export const doGenerateOrders = (data: any) => {
  return api.post('gruul-mall-order/order', data)
}
/**
 * 获取订单信息
 */
export const doGetOrder = (params: any) => {
  return api.get('gruul-mall-order/order', params)
}
/**
 * 根据模块类id获取配置信息
 */
export const queryConfigByModule = (params: string): Promise<any> => {
  return api.get(`gruul-mall-addon-platform/platform/config/query-config-by-module?modules=${params}`)
}

type ICParament = { orderNo: string; shopId: Long }
/**
 * 获取同城配送订单信详情
 * @param {string} orderNo
 * @param {string} shopId
 */
export const doGetICOrder = (data: ICParament) => {
  return api.post<IcOrderDetail>(`addon-ic/ic/shop/order/deliver/info`, data)
}
/**
 * 获取UU跑腿配送员最新信息和定位
 * @param {string} orderNo
 * @param {string} shopId
 */
export const doGetUuptCourierInfo = (data: ICParament) => {
  return api.post<CourierLoctionInfo>(`addon-ic/ic/shop/order/courier/uupt`, data)
}
/**
 * 获取订单详情信息
 * @param {string} orderNo
 * @param {Long} shopId 传该值已支付
 */
export const doGetOrderInfo = (orderNo: string, shopId?: Long, packageId?: string, usePackage = false) => {
  const params: any = { usePackage }
  if (packageId) {
    params.packageId = packageId
  }
  if (shopId) {
    params.shopId = shopId
  }
  return api.get<ApiOrder>(`gruul-mall-order/order/${orderNo}`, params)
}
/**
 * 查询未支付的订单支付信息
 */
export const doGetOrderPayment = (orderNo: any) => {
  return api.get(`gruul-mall-order/order/${orderNo}/payment`)
}
/**
 * 查询创建情况
 */
export const doGetOrderCreateConditions = (orderNo: any) => {
  return api.get(`gruul-mall-order/order/${orderNo}/creation`)
}
type PayType = { orderNo: string; payType: string; rebate: boolean }
/**
 * 支付
 * @param {any} data
 */
export const doPostOrderPayPage = (data: PayType) => {
  return api.post(`gruul-mall-order/order/pay/page`, data)
}
/**
 * 查询第一个已发货的包裹
 * @param {string} orderNo
 * @param {string} shopOrderNo
 * @param {string} packageId
 */
export const doGetFirstDeliveryPage = (orderNo: string, shopOrderNo: string, packageId: string) => {
  return api.get(`gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered/01`, { packageId })
}

/**
 * 查询所有已发货的包裹
 * @param {string} orderNo
 * @param {string} shopOrderNo
 */
export const doGetDeliveryPackage = (orderNo: string, shopOrderNo: string, shopId: Long) => {
  return api.get(`gruul-mall-order/order/${orderNo}/shopOrder/${shopOrderNo}/delivered/package`, { shopId })
}
/**
 * 物流轨迹
 * @param {*} companyCode 物流公司名字
 * @param {*} waybillNo 物流单号
 */
export const doGetLogisticsTrajectoryByWaybillNo = (companyCode: string, waybillNo: string, shopId: Long, recipientsPhone?: string) => {
  return api.get(
    'gruul-mall-freight/logistics/node',
    {
      companyCode,
      waybillNo,
      recipientsPhone,
    },
    shopId ? { 'Shop-Id': shopId } : {},
  )
}
/**
 * 查询运费
 */
export const doGetFreightCalculation = (data: any) => {
  return api.post('gruul-mall-order/order/distribution/cost', data)
}
/**
 * 查询订单是否支付完成
 * @param {string} outTradeNo 订单号
 */
export const doGetOrderIsPaySuccess = (outTradeNo: string) => {
  return api.get(`gruul-mall-payment/merchant/pay/order/status`, { outTradeNo })
}
/**
 * 订单修改地址
 * @param {object} params
 */
export const doPutOrderReceiver = (
  orderNo: string,
  params: { shopOrderNo?: string; name: string; mobile: string; area: [string, string, string?]; address: string },
) => {
  return api.put(`gruul-mall-order/order/${orderNo}/receiver`, params)
}
/**
 * 取消订单
 * @param {string} orderNo 订单号
 */
export const doPutCloseOrderByOrderNo = (orderNo: string) => {
  return api.put(`gruul-mall-order/order/${orderNo}/close`)
}

/**
 * 批量根据店铺id查询店铺交易设置
 * @param {Long} ids
 */
export const doGetOrderSettingsDealBatch = (ids: Long[]) => {
  return api.get(`gruul-mall-order/order/config/form/batch`, { shopIds: ids.join() })
}

/**
 * 创建开通会员订单
 */

export const doPostActiveMember = (data: any) => {
  return api.post(`addon-member/paid/member/pay`, data)
}

/**
 * 查询订单商品项
 */
export const doGetShopOrderItem = (orderNo: string, itemId: string) => {
  return api.get(`gruul-mall-order/order/${orderNo}/item/${itemId}`)
}
/**
 * 查询订单评价信息
 */
export const doPostGetOrderEvaluate = (
  orderNo: string,
  data: {
    shopId: Long
    productId: Long
    skuId: Long
    specs: string[]
  },
) => {
  return api.post(`gruul-mall-order/order/evaluate/${orderNo}`, data)
}

interface ShopProduct {
  key: { shopId: Long; productId: Long; skuId: Long }
  // num  购买数量
  num: string
}

type OrderValid = {
  //orderType 普通商品   秒杀       拼团        砍价      套餐
  orderType: 'COMMON' | 'SPIKE' | 'TEAM' | 'BARGAIN' | 'PACKAGE'
  activityId: string
  products: Array<ShopProduct>
  distributionMode: 'EXPRESS'
}
/**
 * 检验订单异常
 */
const doPostOrderValid = (data: OrderValid) => {
  return api.post('gruul-mall-order/order/valid', data)
}
export const toConfirmOrderValid = async (params: Array<any>) => {
  const orderValid: OrderValid = {
    orderType: params[0]?.activityParam?.type || 'COMMON',
    activityId: params[0]?.activityParam?.activityId || '',
    products: [],
    distributionMode: params[0]?.distributionMode || 'EXPRESS',
  }
  const returnValue = {
    success: true,
    data: {},
  }
  params.forEach((item) => {
    item.products.forEach((ite: any) => {
      const { skuId, id: productId, num } = ite
      const shopProduct = { key: { shopId: item.shopId, productId, skuId }, num }
      orderValid.products.push(shopProduct)
    })
  })
  const { code, msg, data } = await doPostOrderValid(orderValid)
  switch (code) {
    case 200:
      return returnValue
    case 30038:
      {
        const shopDisable = params
          .filter((shops) => data.includes(shops.shopId))
          .map((item) => item.shopName)
          .join(',')
        returnValue.success = false
        returnValue.data = { title: '店铺已禁用', content: `${shopDisable}` }
      }
      return returnValue
    case 30040:
      returnValue.success = false
      returnValue.data = { title: '店铺已禁用' }
      return returnValue
    case 30034:
      returnValue.success = false
      returnValue.data = { title: '提示', content: msg }
      return returnValue
    case 30039:
      switch (true) {
        // notExists, 不存在的 sku key 集合格式 [{shopId,productId,skuId},{shopId,productId,skuId}]
        case data.notExists.length > 0:
          returnValue.success = false
          returnValue.data = { title: '规格不存在', content: formatContent(params, data.notExists).join(',') }
          break
        case data.stockNotEnough.length > 0:
          returnValue.success = false
          returnValue.data = {
            title: '库存不足',
            content: formatContent(params, data.stockNotEnough, 'stockNotEnough').join(','),
          }
          break
        case data.limitNotEnough.length > 0:
          //    limitNotEnough:, 超限购的商品 数据格式[{key,limit:限购数,bought:已购数},{key,limit:限购数,bought:已购数}]、
          //    Key可能是两种格式商品限购KEY格式为 {shopId,productId}，sku 限购 KEY 格式为{shopId,productId,skuId}
          returnValue.success = false
          returnValue.data = {
            title: '超限购',
            content: formatContent(params, data.limitNotEnough, 'limitNotEnough').join(','),
          }
          break
        default:
          break
      }
      return returnValue
    default:
      return returnValue
  }
}

function formatContent(arr1: Array<any>, arr2: Array<any>, key?: string) {
  const content: Array<string> = []
  for (let i = 0; i < arr1.length; i++) {
    const paraItem = arr1[i]
    for (let j = 0; j < arr2.length; j++) {
      const notItem = arr2[j]
      paraItem.products.forEach((product: Obj) => {
        const isShop = paraItem.shopId === (notItem.shopId || notItem.key.shopId)
        const isProduct = product.productId === (notItem.productId || notItem.key.productId)
        const isSkuId = product.skuId === (notItem.skuId || notItem.key.skuId)
        const specs = product.specs ? product.specs : []
        let productKey = `${product.productName + ' 规格:' + specs.join(',')}`
        const limitNotEnoughIsSku = !notItem.key?.skuId || isSkuId
        if (key === 'limitNotEnough' && isShop && isProduct && limitNotEnoughIsSku) {
          //   notItem.key.skuId  不存在是商品限购 存在是规格限购
          const str = `商品:${productKey} 此${notItem.key.skuId ? '规格' : '商品'}限购${notItem.limit}件,已购${notItem.bought}件`
          content.push(str)
          return
        }
        if (isShop && isProduct && isSkuId) {
          if (key === 'stockNotEnough') {
            productKey += `库存仅剩${notItem.stock}件`
          }
          content.push(productKey)
        }
      })
    }
  }
  return content
}
