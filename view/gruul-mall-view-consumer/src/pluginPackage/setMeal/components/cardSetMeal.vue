<script setup lang="ts">
import type { PropType } from 'vue'
import type { SetMealBasicInfoVO } from '@/apis/good/model'
import Countdown from '@/pages/plugin/secKill/components/decorationComponents/countdown.vue'

const props = defineProps({
  setMealList: {
    type: Array<SetMealBasicInfoVO>,
    default: () => [],
  },
  shopId: {
    type: String as PropType<Long>,
    default: '',
  },
})

const jumpDetial = (id: Long) => {
  uni.navigateTo({
    url: `/pluginPackage/setMeal/views/setMealDetial/setMealDetial?setMealId=${id}&shopId=${props.shopId}`,
  })
}
</script>
<template>
  <view v-if="$props.setMealList?.length" class="goods-card">
    <text class="goods-card__title">优惠套餐</text>
    <view style="padding: 0 24rpx">
      <scroll-view class="scroll-card" scroll-x enhanced :show-scrollbar="false" scroll-left="120">
        <view v-for="item in $props.setMealList" :key="item.setMealId" class="scroll-card__item" @click="jumpDetial(item.setMealId)">
          <view class="item-card">
            <image class="item-card__img" :src="item.setMealMainPicture" />
            <div class="item-card__detali">
              <text>{{ item.setMealName }}</text>
              <!-- <text style="color: rgba(222, 50, 36, 1)">最多可省￥{{ divTenThousand(item.saveAtLeast) }}</text> -->
              <text style="color: rgba(153, 153, 153, 1); display: flex; align-items: center"
                ><text>距离结束：</text><Countdown :startTime="item.endTime" :bg="false" class="time"
              /></text>
              <text class="setMealDescription">{{ item.setMealDescription }}</text>
            </div>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(goods-card) {
  margin: 10rpx;
  overflow: hidden;
  border-radius: 16rpx;
  background-color: rgba(255, 255, 255, 1);
  @include e(title) {
    font-weight: 700;
    padding: 20rpx 32rpx;
    @include flex;
    justify-content: flex-start;
    line-height: 25rpx;
  }
}
@include b(scroll-card) {
  white-space: nowrap;
  width: 100%;
  height: 288rpx;
  margin-top: 18rpx;
  @include e(item) {
    display: inline-block;
    width: 100%;
    height: 256rpx;
    text-align: center;
    font-size: 36rpx;
    margin: 12rpx;
  }
}
@include b(item-card) {
  @include flex;
  width: 670rpx;
  justify-content: left;
  background-color: rgba(243, 243, 243, 1);
  padding: 20rpx;
  @include e(img) {
    width: 212rpx;
    height: 212rpx;
  }
  @include e(detali) {
    flex: 1;
    height: 212rpx;
    margin-left: 24rpx;
    display: flex;
    flex-direction: column;
    color: rgba(51, 51, 51, 1);
    font-size: 24rpx;
    text {
      text-align: left;
      margin-bottom: 15rpx;
      padding: 10rxp 0;
    }
    .setMealDescription {
      color: #666;
      font-size: 22rpx;
      line-height: 32rpx;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
    }
  }
}
.time {
  color: red !important;
  font-size: 24rpx;
}
</style>
