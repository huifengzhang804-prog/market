import { computed, provide, reactive, ref, type Ref, unref } from 'vue'
import { doGetGroupInfo, doGetGroupList } from '@pluginPackage/group/apis'
import type { ApiGroupInfo, ApiGroupListType, GroupDispatcherType, RequestGroupListParams } from '@/apis/plugin/group/model'
import type { OrderParamsType } from '@/pluginPackage/goods/commodityInfo/types'
import type { Fn } from '@/utils/types'
import type { StorageSku, ProductResponse } from '@/apis/good/model'

type HookCallback = (params: OrderParamsType) => void
export type HooksOfGroupType = (choosedSku: Ref<StorageSku>, callback?: HookCallback) => GroupDispatcherType

/**
 * 获取拼团信息
 */
export default function hooksOfGroup(choosedSku: Ref<StorageSku>, goodsInfo: ProductResponse, currentSkuArr: any, callback?: HookCallback) {
  const groupInfo = ref<ApiGroupInfo>({
    activityId: '',
    effectTimeout: 15,
    endTime: '',
    huddle: false,
    mode: 'COMMON',
    preheat: false,
    preheatHours: 1,
    skus: [],
    stackable: { payTimeout: 180, coupon: false, vip: false, full: false },
    startTime: '',
    teamStatus: 'FINISHED',
    users: [],
    myTeams: [],
  })
  const groupList = ref<ApiGroupListType[]>([])
  // 拼团列表分页
  const pageConfig = reactive({
    current: 1,
    size: 4,
    total: 0,
  })
  // 当前选中团下标
  const groupIndex = ref(0)
  const existGroup = computed(() => {
    return groupInfo.value.teamStatus === 'OPEN' || groupInfo.value.teamStatus === 'OPENING'
  })
  /**
   * 获取活动skuId
   */
  const getSkuIds = computed<Long[]>(() => {
    if (groupInfo.value && groupInfo.value.skus.length) {
      return groupInfo.value.skus.map((item) => {
        return item.skuId
      })
    } else {
      return []
    }
  })
  /**
   * 商品是否参与拼团活动
   */
  const isJoinForGroup = computed(() => {
    const exist = groupInfo.value.skus.find((item) => {
      return item.skuId === unref(choosedSku).id || currentSkuArr.value.find((obj: any) => Object.is(obj.id, item.skuId))
    })
    return Boolean(exist)
  })
  const goodInfoRef = ref(goodsInfo)
  const doRequest = async function (params: { shopId: Long; goodId: Long; extra: any; oldExtra?: any }, goodInfo: ProductResponse) {
    goodInfoRef.value = goodInfo
    const { shopId, goodId, extra } = params
    const groupConfig = await requestGroupInfo(shopId, goodId, extra.teamNo)
    if (groupConfig && groupConfig.teamStatus === 'OPEN') {
      // groupListInfo 已经开团的列表 自己已经拼团的话看不到
      const groupListInfo = await requestGroupList({ shopId, goodId })
      if (groupListInfo) {
        groupList.value = groupListInfo.records
        pageConfig.total = groupListInfo.total
        pageConfig.current = groupListInfo.current
        pageConfig.size = groupListInfo.size
      }
    }
    groupInfo.value = groupConfig
    const { payTimeout, ...others } = groupConfig.stackable
    callback?.({
      productId: goodId,
      type: 'TEAM',
      activityId: groupConfig.activityId,
      extra: params.oldExtra || {},
      stackable: others,
    })
  } as unknown as Fn
  /**
   * 获取拼团信息
   */
  async function requestGroupInfo(shopId: Long, goodId: Long, teamNo?: string) {
    const { code, data = null } = await doGetGroupInfo(shopId, goodId, teamNo)
    if (code && code !== 200) {
      uni.showToast({
        icon: 'none',
        title: '获取拼团信息失败',
      })
    }
    return data
  }
  /**
   * 获取拼团列表
   */
  async function requestGroupList(options: RequestGroupListParams) {
    const { shopId, goodId, current = 1, size = 4 } = options
    let groupListInfo = null
    const { code, data } = await doGetGroupList(shopId, goodId, current, size)
    if (code && code === 200) {
      groupListInfo = data
    } else {
      uni.showToast({
        icon: 'none',
        title: '获取拼团列表失败',
      })
    }
    return groupListInfo
  }
  /**
   * 获取拼团价格
   */
  const getPrice = () => {
    if (existGroup.value && isJoinForGroup.value) {
      const choosed = groupInfo.value.skus.find((item) => {
        return item.skuId === unref(choosedSku).id
      })
      const groupPrice = groupInfo.value.skus.find((item) => {
        return item.skuId === choosed?.skuId
      })
      return groupPrice ? groupPrice.prices[groupIndex.value] : choosedSku.value.salePrice
    } else {
      return null
    }
  }
  const exportSlice: GroupDispatcherType = {
    groupInfo,
    groupList,
    existGroup,
    isJoinForGroup,
    doRequest,
    requestGroupInfo,
    getPrice,
    requestGroupList,
    getSkuIds,
    groupIndex,
  }
  provide('groupProvide', exportSlice)
  return exportSlice
}
