import { doGetFreightCalculation } from '@/apis/order'
import { ElMessage } from 'element-plus'
import type { StoragePackage } from '@/views/shoppingcart/types'
import type { ReceiverAreaDataParams, SkuInfos, LogisticsFreights } from '@/views/detail/types'
import useMember from '@/composables/useMember'
/**
 * @description: 整理运费所需参数
 * @returns {*}
 */
export function formatGetFreightParams(
  receiverAreaCode: string[],
  shopId: string,
  weight: number,
  skuId: string,
  price: string,
  logisticsId: string,
) {
  return {
    receiverAreaCode,
    shopFreights: [{ shopId, freights: [{ skuInfos: [{ skuId, weight, price, logisticsId, num: 1 }] }] }], //
  }
}
/**
 * @description: 获取运费
 * @param {ReceiverAreaDataParams} params
 * @returns {*}
 */
export async function GetFreightCalculation(params: ReceiverAreaDataParams) {
  let freight = ''
  const { data: res, code, msg } = await doGetFreightCalculation(params)
  if (code !== 200) {
    ElMessage.error(msg || '运费获取失败')
    return freight
  }
  for (const key in res) {
    freight = res[key]
  }
  return freight
}
const { includeBenefit } = useMember()
/**
 * @description: 组装获取运费数据
 */
export function initCommodityLogisticsInfo(shopProducsList: StoragePackage[], area: [string, string, string?], address: string) {
  const params: ReceiverAreaDataParams = {
    area,
    address,
    //freeRight 是否有会员免运费
    freeRight: includeBenefit('LOGISTICS_DISCOUNT'),
    shopFreights: [{ shopId: '', freights: [{ templateId: '', skuInfos: [{ weight: '', price: '', num: '', skuId: '' }] }] }],
    distributionMode: [],
  }
  const priceKey = priceKeyContorl(shopProducsList[0]) as 'salePrice' | 'price'
  const shopFreights = buildShopProductsList(shopProducsList, priceKey)
  params.shopFreights = shopFreights
  return params
}

// 商品详情立即购买和购物车的商品未参与活动 默认取 salePrice
// 活动商品根据需求取对应 price 获取运费
function priceKeyContorl(param: StoragePackage) {
  let key = 'salePrice'
  if (param?.activityParam?.type) {
    const priceKey = {
      PACKAGE: 'price',
      SPIKE: 'price',
      TEAM: 'price',
      BARGAIN: 'price',
      COMMON: 'price',
    }
    key = priceKey[param.activityParam.type]
  }
  return key
}
// 扩展两个字段便于后续数据处理
interface SkuInfosExtendsInterface extends SkuInfos {
  sellType: string
  supplierId?: string
}
/**
 * @description 重组请求运费的shopFreights数据源
 * @param shopProducsList
 * @param priceKey
 * @returns
 */
function buildShopProductsList(shopProducsList: StoragePackage[], priceKey: 'salePrice' | 'price') {
  const returnShopProductsList: ReceiverAreaDataParams['shopFreights'] = []
  const freightsArray: any[] = []
  shopProducsList.forEach((shopProduct) => {
    const { shopId, products } = shopProduct
    const map = new Map<string, { templateId: string; skuInfos: SkuInfosExtendsInterface[]; shopId: string; sellType: string }>()
    products.forEach((product) => {
      const { freightTemplateId: templateId, num, skuId, weight, sellType, supplierId } = product
      const price = product[priceKey]
      if (!map.get(templateId)) {
        map.set(templateId, {
          templateId,
          skuInfos: [{ price, num, skuId, weight, sellType, supplierId }],
          shopId,
          sellType,
        })
        return
      }
      map.get(templateId)?.skuInfos.push({ price, num, skuId, weight, sellType, supplierId })
    })
    for (const key of map) {
      freightsArray.push({ ...key[1], shopId })
    }
  })
  const newMap = new Map<string, any>()
  freightsArray.forEach((item) => {
    const skuInfo = item?.skuInfos
    const shopId = item?.shopId
    const templateId = item?.templateId
    skuInfo.forEach((sku: any) => {
      const { price, num, skuId, weight, sellType, supplierId } = sku
      const mapKey = `${shopId}-${sellType}-${supplierId}-${templateId}`
      if (!newMap.get(mapKey)) {
        newMap.set(mapKey, {
          templateId,
          sellType,
          supplierId,
          skuInfos: [{ price, num, skuId, weight }],
        })
        return
      }
      const skuInfo = newMap.get(mapKey)?.skuInfos
      const idx = skuInfo.findIndex((item: any) => item.skuId === skuId)
      if (idx > -1) {
        skuInfo[idx].num += num
      } else {
        skuInfo.push({ price, num, skuId, weight })
      }
    })
  })
  for (const key of newMap) {
    const [shopId, sellType, supplierId, templateId] = key[0].split('-')
    const { skuInfos } = key[1]
    returnShopProductsList.push({
      shopId: sellType === 'CONSIGNMENT' ? supplierId : shopId,
      freights: [
        {
          templateId,
          skuInfos,
        },
      ],
    })
  }
  return returnShopProductsList
}
