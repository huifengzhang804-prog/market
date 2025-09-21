<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { doGetConcernList } from '@/apis/concern'
import type { ConcernItem } from '@/pages/modules/home/components/payattention/types'
import { useUserStore } from '@/store/modules/user'

const $userStore = useUserStore()
const page = reactive({
  current: 1,
  size: 10,
  pages: 1,
  shopList: [] as ConcernItem[],
})
const initialListOptions = reactive({
  isLoading: false,
  isEmpty: true,
})

// onMounted(() => {
//     if ($userStore.getterToken) {
//         initConcernShopList()
//     }
// })

const handleScrolltolower = () => {
  initConcernShopList(true)
}
const navToShopInfo = (shopItem: ConcernItem) => {
  console.log(' navToShopInfonavToShopInfo', 1211)
  uni.navigateTo({ url: `/basePackage/pages/merchant/Index?shopId=${shopItem.shopId}` })
}
async function initConcernShopList(isLoad = false) {
  if (!isLoad) {
    // 刷新
    page.current = 1
    initialListOptions.isLoading = true
    try {
      page.shopList = await getConcernList()
    } finally {
      initialListOptions.isLoading = false
      initialListOptions.isEmpty = page.shopList?.length === 0
    }
  } else if (isLoad && page.current < page.pages) {
    // 更新
    page.current++
    page.shopList = page.shopList.concat(await getConcernList())
  }
}
async function getConcernList() {
  const { code, data } = await doGetConcernList(page.current, page.size)
  if (code !== 200) return []
  page.pages = data.pages
  return data.records
}
defineExpose({ initConcernShopList, initialListOptions })
</script>

<template>
  <scroll-view v-if="page.shopList?.length" scroll-x enhanced :show-scrollbar="false" class="payattention" @scrolltolower="handleScrolltolower">
    <view class="payattention__content">
      <view v-for="shopItem in page.shopList" :key="shopItem.shopId" class="sort" @click="navToShopInfo(shopItem)">
        <view class="sort__container">
          <image class="sort__container--img" :src="shopItem?.logo" mode="scaleToFill" />
        </view>
        <text class="sort__text">{{ (shopItem.shopName ? shopItem.shopName : '未知').slice(0, 4) }}</text>
      </view>
    </view>
  </scroll-view>
  <view v-else class="no-data">
    <image src="https://bgniao-small-file-1253272780.cos.ap-chengdu.myqcloud.com/gruul/%E7%94%BB%E6%9D%BF%201190.png" class="no-data__img" />
    <text class="no-data__text">您没有关注任何店铺！去逛逛~</text>
  </view>
</template>

<style lang="scss" scoped>
@include b(payattention) {
  padding: 32rpx 20rpx;
  white-space: nowrap;
  box-sizing: border-box;
  height: 214rpx;
  overflow: hidden;
  @include e(content) {
    display: flex;
    align-items: center;
  }
}
@include b(sort) {
  float: left;
  display: flex;
  align-items: center;
  flex-direction: column;
  margin-right: 32rpx;
  @include e(all) {
    width: 100rpx;
    height: 100rpx;
    background-color: #f00;
    border-radius: 50%;
  }
  @include e(container) {
    width: 100rpx;
    height: 100rpx;
    border-radius: 50%;
    @include flex();
    background-color: #f9f9f9;
    @include m(img) {
      width: 72rpx;
      height: 72rpx;
      border-radius: 50%;
    }
  }
  @include e(text) {
    font-size: 24rpx;
    line-height: 34rpx;
    color: #333;
    margin-top: 16rpx;
  }
}
// @include b(payattention) {
//     position: relative;
//     width: 100%;
//     white-space: nowrap;
//     box-sizing: border-box;
//     height: 208rpx;
//     padding: 28rpx 0;
//     padding-left: 100rpx;
//     &::before {
//         content: '全部';
//         position: absolute;
//         left: 0;
//         width: 135rpx;
//         height: 208rpx;
//         background: #f7f7f7;
//         z-index: 5;
//         bottom: 0;
//         line-height: 320rpx;
//         text-align: center;
//         font-size: 20rpx;
//         color: #838383;
//     }
//     &::after {
//         content: ' ';
//         position: absolute;
//         top: 30rpx;
//         left: 20rpx;
//         width: 94rpx;
//         height: 94rpx;
//         border-radius: 50%;
//         background: red;
//         z-index: 6;
//     }
// }
// @include b(payattention-hidden) {
//     &::before {
//         background: transparent;
//         font-size: 0;
//     }
//     &::after {
//         background: transparent;
//     }
// }
// @include b(sort) {
//     &:nth-child(1) {
//         margin-left: 80rpx;
//     }
//     display: inline-block;
//     margin: 0 40rpx;
//     @include e(img) {
//         width: 94rpx;
//         height: 94rpx;
//         border-radius: 50%;
//         margin-bottom: 10rpx;
//     }
//     @include e(text) {
//         @include utils-ellipsis(1);
//         width: 94rpx;
//         font-size: 20rpx;
//         color: #838383;
//         text-align: center;
//         // #ifdef MP-WEIXIN
//         margin-top: 12rpx;
//         // #endif
//     }
// }
@include b(no-data) {
  width: 710rpx;
  height: 466rpx;
  border-radius: 16rpx;
  box-shadow: 42rpx 54rpx 100rpx -16rpx rgba(212, 178, 178, 0.17);
  background-color: rgb(252, 253, 255);
  margin: 32rpx auto 0;
  @include flex();
  flex-direction: column;
  @include e(img) {
    width: 280rpx;
    height: 316rpx;
  }
  @include e(text) {
    color: #333;
    font-family: PingFang SC;
    font-size: 24rpx;
    font-weight: 400;
    margin-top: 24rpx;
  }
}
</style>
