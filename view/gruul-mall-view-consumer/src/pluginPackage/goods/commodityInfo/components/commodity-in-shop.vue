<template>
  <view class="shop">
    <u-image :width="152" :height="152" border-radius="10" :src="props.shopInfo.shopLogo" />
    <view style="margin-left: 20rpx; width: 74%">
      <view style="display: flex; justify-content: space-between" @click="handleNavToShop">
        <view class="shop__title">
          <text class="shop__name"> {{ props.shopInfo.shopName }}</text>
          <text
            v-if="props.shopInfo.shopType && props.shopInfo.shopType !== 'ORDINARY'"
            class="shop__tag"
            :style="{ background: props.shopInfo.shopType === 'SELF_OWNED' ? '#FB232F' : '#8239F6' }"
          >
            {{ shopType[props.shopInfo.shopType] }}
          </text>
        </view>
        <u-icon name="arrow-right" color="#000" size="28"></u-icon>
      </view>
      <view style="margin-top: 20rpx; font-size: 24rpx">
        综合体验 <u-rate :model-value="props.shopInfo.score" :count="5" :colors="colors" size="34" disabled></u-rate>
      </view>
      <view class="line">
        <u-icon :name="isCollect ? 'heart-fill' : 'heart'" color="rgb(242, 45, 32)" size="26rpx" />
        <text style="margin-left: 10rpx">{{ props.shopInfo.followCount }}人已关注 &nbsp;&nbsp;</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, type PropType } from 'vue'
import { doGetOrderConcernStatusByShopId } from '@/apis/concern'
import { useUserStore } from '@/store/modules/user'
import type { Shop } from '@/apis/shops/type'

const token = useUserStore().getterToken

const colors = ['rgb(255, 159, 159)', ' rgb(255, 134, 134)', 'rgb(255, 121, 121)', 'rgb(255, 105, 105)', 'rgb(255, 41, 41)']
const props = defineProps({
  shopInfo: {
    type: Object as PropType<Shop>,
    default: () => ({}),
  },
})
// 收藏状态
const isCollect = ref(false)
/**
 * 查询收藏状态
 * @param {Long} shopId
 */
const checkCollect = async (shopId: Long) => {
  isCollect.value = false
  if (token) {
    const { code, data, success, msg } = await doGetOrderConcernStatusByShopId(shopId)
    if (!success) return uni.showToast({ title: `${msg ? msg : `查询关注状态失败`}`, icon: 'none' })
    isCollect.value = data
  }
}
// 跳转入商品详情
const handleNavToShop = () => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${props.shopInfo.shopId}`,
  })
}

const shopType = {
  SELF_OWNED: '自营',
  PREFERRED: '优选',
}
</script>

<style scoped lang="scss">
@include b(shop) {
  display: flex;
  padding: 20rpx 20rpx;
  @include e(name) {
    font-size: 28rpx;
    max-width: 400rpx;
    font-weight: 500;
    @include utils-ellipsis(1);
  }
  @include e(tag) {
    width: 47rpx;
    text-align: center;
    height: 28rpx;
    background-color: #fd0505;
    color: #fff;
    border-radius: 4rpx;
    font-size: 18rpx;
    line-height: 28rpx;
    margin-left: 8rpx;
    flex-shrink: 0;
  }
  @include e(title) {
    display: flex;
    align-items: center;
  }
}
@include b(line) {
  display: flex;
  font-size: 24rpx;
  align-items: center;
  margin-top: 20rpx;
}
@include b(footer) {
  display: flex;
  font-size: 24rpx;
}
</style>
