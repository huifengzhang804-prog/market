<script setup lang="ts">
import { ref, reactive, nextTick, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { doGetDistributeTeam } from '../apis'
import Auth from '@/components/auth/auth.vue'

const { divTenThousand } = useConvert()
const currentTabIndex = ref(0)

const tabList = ref<{ name: string; label: string }[]>([])
const pageConfig = reactive({
  current: 1,
  pages: 1,
  size: 10,
})
const disTeamlist = ref<any>([])
const total = ref(0)
const superior = ref('')

onLoad(({ referrer }: any) => {
  superior.value = referrer
})

const loadMore = () => {
  pageConfig.current++
  initTeamList()
}
initTeamList()

async function initTeamList() {
  const { code, data, msg } = await doGetDistributeTeam({ ...pageConfig, level: tabList.value[currentTabIndex.value]?.label || 'ONE' })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取分销商品失败'}`, icon: 'none' })
  }

  disTeamlist.value = [...disTeamlist.value, ...data.records]
  const dataTabList: { name: string; label: string }[] = []
  if (data.count1 > 0) dataTabList.push({ name: `一级(${data.count1})`, label: 'ONE' })
  if (data.count2 > 0) dataTabList.push({ name: `二级(${data.count2})`, label: 'TWO' })
  if (data.count3 > 0) dataTabList.push({ name: `三级(${data.count3})`, label: 'THREE' })
  tabList.value = dataTabList
  total.value = Number(data.count1) + Number(data.count2) + Number(data.count3)
}
const changeTabs = () => {
  pageConfig.current = 1
  disTeamlist.value = []
  nextTick(() => {
    initTeamList()
  })
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - uni.upx2px(96) - uni.upx2px(167)
})
</script>

<template>
  <view class="head">
    <view class="head__num">
      {{ total }}
      <span class="head__superior">上级 ：{{ superior }}</span>
    </view>
  </view>
  <view style="border-bottom: 1px solid rgb(231, 231, 231)">
    <u-tabs
      ref="uTabSwiper"
      v-model="currentTabIndex"
      :list="tabList"
      bg-color="#fff"
      active-color="#EE3729"
      :show-bar="false"
      height="96"
      swiper-width="750"
      :is-scroll="false"
      @change="changeTabs"
    />
  </view>

  <scroll-view scroll-y :style="{ height: `${height}px` }" @scrolltolower="loadMore">
    <view v-for="item in disTeamlist" :key="item.id" class="content-item">
      <image :src="item.avatar" class="content-item__pic"></image>
      <view class="content-item__right">
        <view class="content-item__right-name">{{ item.name ? item.name : item.nickname }}</view>
        <view class="content-item__right-info">
          <view class="content-item__right-info--money">消费:{{ divTenThousand(item.consumption).toFixed(2) }} </view>
          <view class="content-item__right-info--count">订单数:{{ item.orderCount }}</view>
          <view class="content-item__right-info--time">{{ item.createTime.slice(0, 10) }}</view>
        </view>
      </view>
    </view>
  </scroll-view>
  <Auth />
</template>

<style lang="scss" scoped>
@include b(head) {
  background: #fd5e37;
  color: #fff;
  font-weight: 700;
  height: 167rpx;
  padding: 0 21rpx;
  @include e(num) {
    font-size: 61rpx;
    line-height: 167rpx;
    text-align: center;
  }
  @include e(superior) {
    font-size: 26rpx;
    font-weight: 400;
    position: absolute;
    right: 21rpx;
    top: 12rpx;
  }
}
@include b(content-item) {
  height: 112rpx;
  display: flex;
  align-items: center;
  background-color: #fff;
  margin-bottom: 2rpx;
  padding: 6rpx 46rpx 7rpx 36rpx;
  @include e(pic) {
    height: 90rpx;
    width: 90rpx;
    border-radius: 50%;
    background: #000;
  }
  @include e(right) {
    color: #333;
    font-weight: bold;
    margin-left: 15rpx;
  }
  @include e(right-name) {
    font-size: 32rpx;
    margin-bottom: 20rpx;
  }
  @include e(right-info) {
    font-size: 22rpx;
    font-weight: normal;
    display: flex;
    align-items: center;
    justify-content: space-around;
    transform: translateY(-4rpx);
    @include m(money) {
      width: 210rpx;
    }
    @include m(count) {
      width: 210rpx;
    }
    @include m(time) {
      // width: 200rpx;
      color: #999999;
    }
  }
}
</style>
