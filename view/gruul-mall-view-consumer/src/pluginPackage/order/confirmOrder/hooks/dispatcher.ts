import { ref, watch } from 'vue'
import { useAppStore } from '@/store/modules/app'
import storage from '@/utils/storage'
import type { AppPluginName } from '@/apis/sys/model'
import type { OrderParamsType } from '@/pluginPackage/goods/commodityInfo/types'
import type { ChainHandleParam } from '@/pages/plugin/types'
import type { OrderType } from '@/pluginPackage/order/confirmOrder/types'

type BenefitTypes = 'vip' | 'full' | 'coupon'
export default function dispatcherForConfirmOrder() {
  const benefitMap: Record<BenefitTypes, AppPluginName> = {
    vip: 'addon-member',
    full: 'addon-full-reduction',
    coupon: 'addon-coupon',
  }
  const orderBenefit = ref<OrderParamsType['stackable']>({
    vip: true,
    full: true,
    coupon: true,
  })
  // 订单优惠项
  const discountSet = ref<ChainHandleParam[]>([])
  // 传递下单接口参数discounts
  const discounts = ref({})
  // 参与活动对象
  const activity = ref({})
  // 订单类型
  const orderType = ref<OrderType>('COMMON')
  // 进入确定订单更新权益
  getBenefit()
  getActivity()
  /**
   * 获取权益
   */
  function getBenefit() {
    const temp = {
      vip: false,
      full: false,
      coupon: false,
    }
    const plugins = useAppStore().getPluginList
    Object.keys(benefitMap).forEach((item) => {
      if (plugins.includes(benefitMap[item as BenefitTypes])) {
        temp[item as BenefitTypes] = true
      }
    })
    console.log('temp获取权益', temp)
    const activityParam = joinActivity() as OrderParamsType

    if (activityParam) {
      const stackable = activityParam.stackable
      Object.keys(stackable).forEach((item) => {
        if (temp[item as BenefitTypes]) {
          temp[item as BenefitTypes] = stackable[item as BenefitTypes]
        }
      })
    }
    orderBenefit.value = temp
  }
  /**
   * 是否参与活动
   */
  function joinActivity() {
    const commodityList = storage.get('commodityInfo')
    if (commodityList.length && commodityList[0].activityParam) {
      return commodityList[0].activityParam
    }
    return null
  }
  /**
   * 获取优惠享参数
   */
  function getDiscountParam(discounts: ChainHandleParam[]) {
    const discountList = JSON.parse(JSON.stringify(discounts))
    const discount: Obj = {}
    if (discountList.length) {
      for (let i = 0; i < discountList.length; i++) {
        const currentItem = discountList[i]
        if (currentItem.preferTreatment && JSON.stringify(currentItem.preferTreatment) !== '{}') {
          Object.keys(currentItem.preferTreatment).forEach((item) => {
            if (item !== 'addon-member') {
              // 过滤会员
              if (!discount[item]) {
                discount[item] = {}
              }
              if (item === 'FULL') {
                const obj = {
                  [currentItem.preferTreatment[item].id]: currentItem.preferTreatment[item].discountId,
                }
                discount[item][currentItem.shopId] = obj
                return
              }
              discount[item][currentItem.shopId] = currentItem.preferTreatment[item].discountId
            }
          })
        }
      }
    }
    return discount
  }
  /**
   * 获取活动参数
   */
  function getActivity() {
    const activityParam = joinActivity() as OrderParamsType
    if (activityParam) {
      activity.value = {
        activityId: activityParam.activityId,
        extra: activityParam.extra,
      }
      orderType.value = activityParam.type
    }
  }
  watch(discountSet, (newVal) => {
    discounts.value = getDiscountParam(newVal)
  })
  return {
    orderBenefit,
    discountSet,
    discounts,
    activity,
    orderType,
  }
}
