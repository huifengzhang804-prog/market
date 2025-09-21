<script setup lang="ts">
import QNav from '@/components/q-nav/q-nav.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { doGetGroupProductList } from '@pluginPackage/group/apis'
import { useStatusBar } from '@/hooks/useStatusBar'
import type { ApiGroupAndComItem, ApiGroupStatus } from '@/apis/plugin/group/model'
import { GROUP_STATUS } from '@/apis/plugin/group/model'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { onLoad } from '@dcloudio/uni-app'
import { computed, reactive, ref } from 'vue'

onLoad(() => {
  uni.$emit('updateTitle')
})
const groupList = ref<ApiGroupAndComItem[]>([])
const { divTenThousand } = useConvert()
const statusBarHeight = useStatusBar()
const sysHeight = uni.getSystemInfoSync().windowHeight
const navBarHeight = uni.upx2px(88)
const bgHeight = uni.upx2px(80)
const scrollViewHeight = computed(() => {
  return sysHeight - statusBarHeight.value - navBarHeight - bgHeight
})
const pageConfig = reactive({
  current: 1,
  size: 10,
  pages: 0,
})
const statusClass = {
  OPENING: 'pre',
  OPEN: 'ing',
  FINISHED: 'finish',
  NO: '',
}
const getStatus = (status: ApiGroupStatus) => {
  return GROUP_STATUS[status]
}
const getStatusClass = (status: ApiGroupStatus) => {
  return statusClass[status]
}

initGroupList()

const handleNav = (row: ApiGroupAndComItem) => {
  const { shopId, productId } = row
  jumpGoods(shopId, productId)
}
const scrolltolower = () => {
  initGroupList(true)
}
async function initGroupList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    groupList.value = await requestGroupList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    groupList.value = groupList.value.concat(await requestGroupList())
  }
}
async function requestGroupList() {
  const { code, data } = await doGetGroupProductList(pageConfig.current, pageConfig.size)
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取列表失败',
    })
  }
  if (data) {
    pageConfig.pages = data.pages
    return data.records
  } else {
    return []
  }
}
</script>

<template>
  <q-nav title="组团购" bg-color="#FC413C" color="#fff" />
  <view class="group__bg">
    <view class="group__bg--title">抱团买实惠·好价不等人</view>
  </view>
  <scroll-view class="group__content" scroll-y :style="{ height: scrollViewHeight + 'px' }" @scrolltolower="scrolltolower">
    <view v-for="item in groupList" :key="item.activityId" class="group__item">
      <view>
        <image :src="item.productImage" class="group__item-image"></image>
      </view>
      <view class="group__item-right">
        <view class="group__item-right--title">{{ item.productName }}</view>
        <view class="group__item-right--center">
          <q-icon name="icon-wanchenggouxuan" size="13px" color="#e9c675" />
          <text style="margin-left: 10rpx">{{ item.userNum }}人已参团</text>
        </view>
        <view class="group__item-right-bottom">
          <view class="group__item-right-bottom--tag">团</view>
          <view>
            <text class="group__item-right-bottom--price">￥{{ divTenThousand(item.minPrice).toString() }}</text>
            <text class="group__item-right-bottom--marking">￥{{ divTenThousand(item.price).toString() }}</text>
          </view>
          <view :class="['group__item-right-bottom--btn', getStatusClass(item.status)]" @click="handleNav(item)">{{ getStatus(item.status) }}</view>
        </view>
      </view>
    </view>
  </scroll-view>
</template>

<style lang="scss" scoped>
@include b(group) {
  @include e(bg) {
    font-size: 24rpx;
    color: #fff;
    height: 320rpx;
    overflow: hidden;
    background: linear-gradient(180deg, #fc413c, #fb4443, #f15e85, #f6f6f6, #f6f6f6);
    @include m(title) {
      text-align: center;
    }
  }
  @include e(content) {
    margin-top: -240rpx;
    padding: 0 28rpx;
    box-sizing: border-box;
  }
  @include e(item) {
    height: 240rpx;
    background-color: #fff;
    border-radius: 16rpx;
    @include flex;
    margin-bottom: 20rpx;
  }
  @include e(item-image) {
    width: 184rpx;
    height: 184rpx;
    margin-right: 16rpx;
  }
  @include e(item-right) {
    display: flex;
    flex-direction: column;
    @include m(title) {
      width: 430rpx;
      font-size: 28rpx;
      color: #323232;
      @include utils-ellipsis;
    }
    @include m(center) {
      width: 200rpx;
      height: 40rpx;
      line-height: 40rpx;
      text-align: center;
      color: #fff5df;
      font-size: 24rpx;
      border-radius: 28rpx;
      background: linear-gradient(90deg, #d72a1b, #dc4b2e);
      margin: 18rpx 0;
    }
  }
  @include e(item-right-bottom) {
    width: 412rpx;
    height: 50rpx;
    background: #ffe7e5;
    border-radius: 8rpx;
    @include flex(flex-start);
    position: relative;
    @include m(tag) {
      width: 50rpx;
      height: 50rpx;
      background: #f5d99c;
      line-height: 50rpx;
      text-align: center;
      font-size: 30rpx;
      color: #f54319;
      border-radius: 8rpx 8rpx 24rpx 8rpx;
      font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
      font-weight: bold;
      font-style: italic;
    }
    @include m(price) {
      font-size: 32rpx;
      color: #dd3324;
      font-weight: bold;
      margin-left: 6rpx;
      margin-right: 18rpx;
    }
    @include m(marking) {
      font-size: 24rpx;
      color: #a1a1a1;
      text-decoration: line-through;
    }
    @include m(btn) {
      font-size: 24rpx;
      width: 112rpx;
      height: 48rpx;
      line-height: 48rpx;
      text-align: center;
      position: absolute;
      bottom: 0;
      right: -30rpx;
      color: #fff5df;
      border-radius: 34rpx;
    }
  }
}
@include b(sellOut) {
  background: #a7a7a7;
}
@include b(ing) {
  background: linear-gradient(90deg, #e94927, #dd3324);
}
@include b(pre) {
  background: linear-gradient(90deg, #56e373, #4cc17e);
}
@include b(finish) {
  background: #a7a7a7;
  color: #fff;
}
</style>
