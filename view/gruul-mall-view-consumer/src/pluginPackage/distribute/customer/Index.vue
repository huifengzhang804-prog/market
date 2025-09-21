<script setup lang="ts">
import { reactive, computed } from 'vue'
import QNav from '@/components/q-nav/q-nav.vue'
import { useStatusBar } from '@/hooks/useStatusBar'
import { doGetDistributeCustomer } from '../apis'
import type { ApiDisCustomerItem } from '@/apis/plugin/distribute/model'
import Auth from '@/components/auth/auth.vue'
import { onLoad } from '@dcloudio/uni-app'

onLoad(() => {
  uni.$emit('updateTitle')
})
type CustomInfoType = {
  current: number
  size: number
  pages: number
  status: string
  list: ApiDisCustomerItem[]
}

const { divTenThousand } = useConvert()
const customerInfo = reactive<CustomInfoType>({
  list: [],
  current: 1,
  size: 10,
  pages: 1,
  status: 'nomore',
})
const scrollHeight = computed(() => {
  const sysInfo = uni.getSystemInfoSync()
  const navHeight = uni.upx2px(88)
  const statusHeight = useStatusBar()
  return `${sysInfo.screenHeight - navHeight - statusHeight.value}px`
})

initCustomer()

const handleScrollBottom = () => {
  initCustomer(true)
}
async function initCustomer(isLoad = false) {
  customerInfo.status = 'loading'
  if (!isLoad) {
    // 刷新
    customerInfo.current = 1
    customerInfo.list = await reqCustomer()
  } else if (isLoad && customerInfo.current < customerInfo.pages) {
    // 更新
    customerInfo.current++
    customerInfo.list = customerInfo.list.concat(await reqCustomer())
  }
}
async function reqCustomer() {
  const { current, size } = customerInfo
  const { code, data, msg } = await doGetDistributeCustomer({ current, size })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取分销客户失败'}`, icon: 'none' })
    customerInfo.status = 'loadmore'
    return []
  }
  if (data.current >= data.pages) {
    customerInfo.status = 'nomore'
  } else {
    customerInfo.status = 'loadmore'
  }
  customerInfo.pages = data.pages
  return data.records
}
</script>

<template>
  <q-nav title="分销客户" bg-color="#3D3C41" color="#fff" />
  <scroll-view scroll-y :style="{ height: scrollHeight }" @scrolltolower="handleScrollBottom">
    <view v-for="item in customerInfo.list" :key="item.code" class="cus">
      <view class="cus__info">
        <image class="cus__info--img" mode="widthFix" :src="item.avatar" />
        <view class="cus__info--name">
          <view class="cus__info--mb">{{ item.name || item.nickname }}</view>
          <view class="color5c">注册时间：{{ item.createTime }}</view>
        </view>
      </view>
      <view class="cus__order">
        <view>消费{{ divTenThousand(item.consumption) }}元</view>
        <view>{{ item.orderCount }}个订单</view>
      </view>
    </view>
    <u-loadmore v-if="customerInfo.list.length" :status="customerInfo.status" />
  </scroll-view>
  <Auth />
</template>

<style lang="scss" scoped>
@include b(cus) {
  padding: 0 30rpx;
  background: #fff;
  margin: 10rpx 0;
  @include e(info) {
    padding: 36rpx 0;
    @include flex(flex-start);
    border-bottom: 2rpx solid #eeedee;
    @include m(img) {
      width: 100rpx;
      height: 100rpx;
      margin-right: 10rpx;
    }
    @include m(name) {
      font-size: 30rpx;
    }
    @include m(mb) {
      margin-bottom: 36rpx;
    }
  }
  @include e(order) {
    height: 108rpx;
    @include flex(space-between);
    font-size: 30rpx;
  }
}
.color5c {
  color: #5c5c5c;
}
</style>
