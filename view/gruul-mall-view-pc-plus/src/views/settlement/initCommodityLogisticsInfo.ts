import useMember from '@/composables/useMember'
const { includeBenefit } = useMember()
/**
 * @description: 组装获取运费数据
 * @param {StoragePackage} shopProducsList
 * @param {string} areaCode
 * @returns {*}
 */
export function initCommodityLogisticsInfo(shopProducsList: any, receiverAreaCode: string[], address: string) {
  const params = {
    receiverAreaCode,
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
function priceKeyContorl(param: any) {
  let key = 'salePrice'
  if (param?.activityParam?.type) {
    const priceKey = {
      PACKAGE: 'price',
      SPIKE: 'price',
      TEAM: 'price',
      BARGAIN: 'price',
    }
    key = priceKey[param.activityParam.type]
  }
  return key
}
/**
 * @description 重组请求运费的shopFreights数据源
 * @param shopProducsList
 * @param priceKey
 * @returns
 */
function buildShopProductsList(shopProducsList: any, priceKey: 'salePrice' | 'price') {
  const returnShopProductsList = []
  const freightsArray: any[] = []
  shopProducsList.forEach((shopProduct: { shopId: any; products: any }) => {
    const { shopId, products } = shopProduct
    const map = new Map<string, { templateId: string; skuInfos: any; shopId: string; sellType: string }>()
    products.forEach(
      (product: { [x: string]: any; freightTemplateId?: any; num?: any; skuId?: any; weight?: any; sellType?: any; supplierId?: any }) => {
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
      },
    )
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
