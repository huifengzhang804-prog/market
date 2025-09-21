<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import QEmpty from '@/components/qszr-core/packages/components/q-empty/q-empty.vue'
import { EMPTY_GB } from '@/constant'
import { doGetUserIntegralDetailInfo } from '@pluginPackage/integral/api'
import type { IntegralDetailInfo } from '@/pluginPackage/integral/api/types'
import Auth from '@/components/auth/auth.vue'

const pageConfig = reactive({
  size: 15,
  current: 1,
  pages: 1,
})
const list = ref<IntegralDetailInfo[]>([])

const gainIntegralCn = {
  DAY_LOGIN: '每日登入',
  INTEGRAL_PRODUCT_EXCHANGE: '积分商品兑换',
  DAY_SHARE: '每日分享',
  INTEGRAL_CLEAR: '积分清空',
  DAY_SIGN_IN: '每日签到',
  SYSTEM_RECHARGE: '系统充值',
  SYSTEM_DEDUCT: '系统扣除',
  ORDER_CONSUMPTION: '订单消费',
  ORDER_CANCEL: '订单取消',
  INTEGRAL_CONSUME: '消费所得',
}

initUserIntegralDetailInfo()

/**
 * 积分明细列表
 */
async function initUserIntegralDetailInfo(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    list.value = await getUserIntegralDetailInfo()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    list.value = list.value.concat(await getUserIntegralDetailInfo())
  }
}
async function getUserIntegralDetailInfo() {
  const { code, data, msg } = await doGetUserIntegralDetailInfo(pageConfig)
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取积分明细列表失败'}`, icon: 'none' })
    return []
  }
  if (!data) {
    return []
  }
  pageConfig.pages = data.pages
  return data.records
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value
})
</script>

<template>
  <template v-if="list.length">
    <scroll-view scroll-y :style="{ height: `${height}px` }" @scrolltolower="initUserIntegralDetailInfo(true)">
      <view v-for="(item, index) in list" :key="index" class="integral_record">
        <view>
          {{ gainIntegralCn[item.gainIntegralType] }} <text v-if="item.particulars">（{{ item.particulars }}）</text>
        </view>
        <view class="integral_record--time">{{ item.createTime }}</view>
        <text class="integral_record--num"> {{ item.changeType === 'INCREASE' ? '+' : '-' }}{{ item.variationIntegral }} </text>
      </view>
    </scroll-view>
  </template>
  <q-empty v-else :background="EMPTY_GB.MESSAGE_EMPTY" title="暂无明细" />
  <Auth />
</template>

<style scoped lang="scss">
page {
  background: #fff;
}
@include b(integral_record) {
  font-size: 26rpx;
  position: relative;
  @include flex();
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-evenly;
  height: 164rpx;
  text-indent: 1em;
  border-bottom: 1px solid #f4f4f5;
  @include m(time) {
    color: #999999;
  }
  @include m(num) {
    font-size: 36rpx;
    color: #f76025;
    position: absolute;
    right: 30rpx;
    top: 50%;
    transform: translate(0, -50%);
  }
}
</style>
