<script setup lang="ts">
import { reactive, toRefs, ref, computed } from 'vue'
import PayattentionSearch from './components/payattention-search.vue'
import PayattentionSort from './components/payattention-sort.vue'
import PayattentionList from './components/payattention-list.vue'
import { useUserStore } from '@/store/modules/user'
import { doGetConcernShopListInfo } from '@/apis/concern'
import { EMPTY_GB } from '@/constant'
import DefaultEmpty from '@/components/qszr-core/packages/components/q-empty/default-empty.vue'
import type { ConcernShopInfoItem } from '@/pages/modules/home/components/payattention/types'
// import { onShow } from '@dcloudio/uni-app'
import type { DecorateItemType } from '../../types'
import PayattentionRecommand from './components/payattention-recommand/index.vue'
import { watch } from 'vue'
import { useSettingStore } from '@/store/modules/setting'

const $props = defineProps({
  decorationList: {
    type: Array<DecorateItemType>,
    default: () => [],
  },
})
const payattentionSortRef = ref<InstanceType<typeof PayattentionSort> | null>(null)
const page = reactive({
  size: 10,
  current: 1,
  list: [] as ConcernShopInfoItem[],
  isShowEmpty: false,
  pages: 1,
  total: 0,
  orders: [],
})
const { list, isShowEmpty } = toRefs(page)
const customStyle = { color: '#F54319', width: '200rpx', height: '60rpx', fontSize: '12px', marginTop: '10rpx' }
// 装修是否存在搜索组件
const isExistSearchComp = computed(() => {
  const composeComp = $props.decorationList.find((item) => item.value === 'compose')
  if (composeComp && composeComp.formData?.search?.show) return true
  return Boolean($props.decorationList.filter((item) => item.value === 'search').length)
})

// 取消关注后返回刷新
// onShow(() => {
//     if (useUserStore().getterToken) {
//         initGetConcernShopListInfo()
//     }
// })
watch(
  () => useUserStore().getterToken,
  (val) => {
    initGetConcernShopListInfo()
    payattentionSortRef?.value?.initConcernShopList()
  },
)

const initialSwiperData = () => {
  if (useUserStore().getterToken) {
    initGetConcernShopListInfo()
    payattentionSortRef?.value?.initConcernShopList()
  }
}

/**
 * 获取关注列表失败
 */
async function initGetConcernShopListInfo(isLoad = false) {
  isShowEmpty.value = false
  if (!isLoad) {
    // 刷新
    page.current = 1
    list.value = await getConcernShopListInfo()
  } else if (isLoad && page.current < page.pages) {
    // 更新
    page.current++
    list.value = list.value.concat(await getConcernShopListInfo())
  }
  isShowEmpty.value = true
}
async function getConcernShopListInfo() {
  const { code, data, msg } = await doGetConcernShopListInfo(page.current, page.size)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取关注列表失败'}`, icon: 'none' })
    return []
  }
  page.pages = data.pages
  return data.records
}
const handleScrolltolower = () => {
  initGetConcernShopListInfo(true)
}
const handleSeeGoods = () => {
  useSettingStore().NAV_TO_INDEX('首页')
}
defineExpose({ initialSwiperData })

const payattentionListRef = ref<InstanceType<typeof PayattentionList>[]>([])
const cancelTooltip = () => {
  payattentionListRef.value.forEach((ele) => ele.cancelTooltip())
}
const showRecommand = computed(() => {
  return payattentionSortRef?.value?.initialListOptions?.isEmpty && payattentionSortRef?.value?.initialListOptions?.isLoading === false
})
</script>

<template>
  <scroll-view scroll-y style="height: 100%" @scrolltolower="handleScrolltolower" @tap="cancelTooltip">
    <PayattentionSearch v-if="isExistSearchComp" />
    <PayattentionSort ref="payattentionSortRef" />
    <PayattentionRecommand v-if="showRecommand" />
    <view class="payattention-bg">
      <PayattentionList v-for="item in list" :key="item.shopId" ref="payattentionListRef" :shop="item" />
      <template v-if="!showRecommand">
        <!-- #ifdef H5 -->
        <DefaultEmpty v-if="isShowEmpty && !list.length" height="calc(100vh - 500rpx)" title="暂无新品~" :background="EMPTY_GB.COLLECTION_EMPTY">
          <view class="m-top"><u-button shape="circle" plain :custom-style="customStyle" @click="handleSeeGoods">去逛逛</u-button></view>
        </DefaultEmpty>
        <!-- #endif -->
        <!-- #ifndef H5 -->
        <u-empty :show="isShowEmpty && !list.length" text="暂无新品~" :src="EMPTY_GB.COLLECTION_EMPTY" :margin-top="-100" :icon-size="400">
          <template #bottom>
            <view class="m-top"> <u-button shape="circle" plain :custom-style="customStyle" @click="handleSeeGoods">去逛逛</u-button></view>
          </template>
        </u-empty>
        <!-- #endif -->
      </template>
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
@include b(payattention-bg) {
  // background-color: #f6f6f6;
  padding: 0 20rpx;
}
</style>
