<script setup lang="ts">
import { ref, unref, onMounted, onUnmounted, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { doGetbuyersEvaluation } from '@/apis/assess'
import UserInfo from '@pluginPackage/order/appraise/components/user-info.vue'
import AppraiseContainer from './components/appraise-container.vue'
import QEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import { EMPTY_GB } from '@/constant'
import { useUserStore } from '@/store/modules/user'
import type { ApiUserData } from '@/store/modules/user/state'
import type { ApiEvaluate } from '@pluginPackage/order/appraise/types'
import Auth from '@/components/auth/auth.vue'
type UserInfoType = Pick<ApiUserData, 'avatar' | 'gender' | 'nickname' | 'userId'>

// 评价列表数据
const evaluateList = ref<ApiEvaluate[]>([])
const userInfo = ref<UserInfoType>({
  avatar: '',
  gender: 'UNKNOWN',
  nickname: '12',
  userId: '1',
})
const isShowEmpty = ref(false)
const $userStore = useUserStore()
const pageConfig = ref({ current: 1, size: 10, pages: 1 })
const loadStatus = ref(false)

onLoad(async () => {
  const { info } = $userStore.getterUserInfo
  userInfo.value = info
})
onMounted(async () => {
  await initEvaluateList()
  console.log('evaluateList-----', evaluateList.value)
  isShowEmpty.value = !evaluateList.value.length
})

onUnmounted(() => {
  isShowEmpty.value = false
})

async function initEvaluateList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.value.current = 1
    evaluateList.value = await initEvaluateListFn()
  } else if (isLoad && pageConfig.value.current < pageConfig.value.pages) {
    // 更新
    pageConfig.value.current++
    evaluateList.value = evaluateList.value.concat(await initEvaluateListFn())
  }
}
async function initEvaluateListFn() {
  const { code, data, msg } = await doGetbuyersEvaluation(unref(pageConfig))
  if (code !== 200) {
    isShowEmpty.value = true
    uni.showToast({ title: `${msg ? msg : '获取列表失败'}`, icon: 'none' })
    return []
  }
  if (pageConfig.value.current === data.pages && evaluateList.value.length) {
    loadStatus.value = true
  }
  pageConfig.value.pages = data.pages
  return data.records
}

function formatSku(sku?: string | string[]) {
  if (!sku) {
    return []
  }
  return Array.isArray(sku) ? sku : [sku]
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value
})
</script>

<template>
  <scroll-view scroll-y :style="`height:${height}px`" @scrolltolower="initEvaluateList(true)">
    <view>
      <user-info v-if="evaluateList.length" :name="userInfo.nickname" :src="userInfo.avatar" />
      <appraise-container
        v-for="order in evaluateList"
        :key="order.id"
        style="margin: 16rpx 0 0 0"
        :time="order.createTime"
        :rate="order.rate"
        :comment="order.comment"
        :shop-reply="order.shopReply"
        :images="order.medias"
        :sku="formatSku(order.specs)"
        :name="order.name"
        :image="order.image"
      />
    </view>
    <!-- #ifndef MP-WEIXIN -->
    <q-empty v-if="!evaluateList.length && isShowEmpty" background="" title="暂无评价内容~" />
    <!-- #endif -->
    <!-- #ifdef MP-WEIXIN -->
    <u-empty
      v-if="!evaluateList.length && isShowEmpty"
      :src="EMPTY_GB.EVALUATION_EMPTY"
      text="暂无评价内容~"
      font-size="30"
      icon-size="550"
      margin-top="200"
    />
    <!-- #endif -->
    <u-loadmore v-if="loadStatus" margin-top="30" />
    <Auth />
  </scroll-view>
</template>

<style scoped lang="scss">
page {
  background: #f6f6f6;
  padding: 20rpx 20rpx 0 20rpx;
}
</style>
