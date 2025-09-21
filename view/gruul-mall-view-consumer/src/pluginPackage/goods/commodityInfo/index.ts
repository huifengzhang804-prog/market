import type { ServicesType, extra } from '@/pluginPackage/goods/commodityInfo/types'
import { ServiceBarrier } from '@/apis/good/model'

const { divTenThousand } = useConvert()

interface ServiceHandle {
  name: string
  sendTime: () => string
  isSendTimeService: boolean
}
export const serviceHandler: Record<ServicesType, ServiceHandle> = {
  ALL_ENSURE: {
    name: ServiceBarrier['ALL_ENSURE'],
    sendTime: () => '',
    isSendTimeService: false,
  },
  FAKE_COMPENSATE: {
    name: ServiceBarrier['FAKE_COMPENSATE'],
    sendTime: () => '',
    isSendTimeService: false,
  },
  NO_FREIGHT: {
    name: ServiceBarrier['NO_FREIGHT'],
    sendTime: () => '',
    isSendTimeService: false,
  },
  SEVEN_END_BACK: {
    name: ServiceBarrier['SEVEN_END_BACK'],
    sendTime: () => '',
    isSendTimeService: false,
  },
  TWO_DAY_SEND: {
    name: ServiceBarrier['TWO_DAY_SEND'],
    sendTime: () => getEstimateDate(2),
    isSendTimeService: true,
  },
}
const getEstimateDate = (deliverDay: number) => {
  const dateUtil = new Date(new Date().getTime() + (deliverDay + 3) * 24 * 60 * 60 * 1000)
  return dateUtil.getMonth() + 1 + '月' + dateUtil.getDate() + '日'
}
// 处理选择校验
export const handleProductAttributes = (productAttributes: extra['productAttributes']) => {
  const length = productAttributes?.length
  if (!length) return true
  for (let i = 0; i < length; i++) {
    const item = productAttributes[i]
    if (item.isRequired && item.featureValues?.length === 0) {
      uni.showToast({
        title: `${item.featureName}为必选`,
        icon: 'none',
      })
      return false
    }
    if (!item.isMultiSelect && item.featureValues?.length > 1) {
      uni.showToast({
        title: `${item.featureName}为单选`,
        icon: 'none',
      })
      return false
    }
  }
  return true
}

// 处理属性参数全部统计的价格
export const handleProductAttributesPrice = (productAttributes: extra['productAttributes']) => {
  const allfeatureValues = productAttributes?.flatMap((item: any) => item.featureValues) || []
  const values = allfeatureValues.map((item: any) => item.secondValue)
  return values.reduce((pre: number, item: number) => +pre + +item, 0)
}
