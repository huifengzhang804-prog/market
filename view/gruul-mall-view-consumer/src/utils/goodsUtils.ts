import type { extra } from '@/pluginPackage/goods/commodityInfo/types'

const { divTenThousand } = useConvert()

// 处理属性参数回显需要数据
export const handleParams = (currentSpecs: extra['productAttributes']) => {
  if (Array.isArray(currentSpecs)) {
    const allfeatureValues = currentSpecs.flatMap((item: any) => item.featureValues) || []
    return allfeatureValues.map((item: any) => {
      const propertyPrice = divTenThousand(item.secondValue)
      return `${item.firstValue}${propertyPrice.lte(0) ? '' : '+'}${propertyPrice}元`
    })
  }
  return []
}
