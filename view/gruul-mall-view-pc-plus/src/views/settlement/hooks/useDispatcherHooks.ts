import { useAppStore } from '@/store/modules/app'
import { AppPluginName, ChainHandleParam } from '../plugins/types'
import { ORDER_TYPE, OrderParamsType } from '../types'
import Storage from '@/libs/storage'

const storage = new Storage()
const useDispatcherHooks = () => {
  const benefitMap: Record<string, AppPluginName> = {
    vip: 'addon-member',
    full: 'addon-full-reduction',
    coupon: 'addon-coupon',
  }
  const orderBenefit = ref({
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
  const orderType = ref<keyof typeof ORDER_TYPE>('COMMON')
  // 进入确定订单更新权益
  getBenefit()
  getActivity()
  function getBenefit() {
    const temp = {
      vip: false,
      full: false,
      coupon: false,
    }
    const plugins = useAppStore().getPluginList
    Object.keys(benefitMap).forEach((item) => {
      if (plugins.includes(benefitMap[item])) {
        temp[item] = true
      }
    })
    const activityParam = joinActivity() as OrderParamsType
    if (activityParam) {
      const stackable = activityParam.stackable
      if (stackable) {
        Object.keys(stackable).forEach((item) => {
          // @ts-ignore
          if (temp[item]) {
            // @ts-ignore
            temp[item] = stackable[item]
          }
        })
      }
    }
    orderBenefit.value = temp
  }
  /**
   * @description:是否参与活动
   */
  function joinActivity() {
    const commodityList = storage.getItem('commodityInfo')
    if (commodityList.length && commodityList[0].activityParam) {
      return commodityList[0].activityParam
    }
    return null
  }
  /**
   * @description:获取优惠享参数
   */
  function getDiscountParam(discounts: ChainHandleParam[]) {
    const discountList = JSON.parse(JSON.stringify(discounts))
    const discount: any = {}
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
   * @description: 获取活动参数
   */
  function getActivity() {
    const activityParam = joinActivity() as OrderParamsType
    if (activityParam) {
      activity.value = {
        activityId: activityParam.activityId,
        extra: activityParam.extra,
      }
      // @ts-ignore
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

export default useDispatcherHooks
