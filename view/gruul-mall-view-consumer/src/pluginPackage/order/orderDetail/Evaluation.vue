<script lang="ts" setup>
import { ref } from 'vue'
import { doPostGetOrderEvaluate } from '@/apis/order'
import { onLoad } from '@dcloudio/uni-app'
import PreviewImage from '@/components/preview-image/preview-image.vue'
import Auth from '@/components/auth/auth.vue'
import storage from '@/utils/storage'

const pathQuery = ref({
  productId: '',
  skuId: '',
  shopId: '',
  specs: [],
})
const order = ref({
  avatar: '',
  comment: '',
  createTime: '',
  id: '',
  image: '',
  isExcellent: false,
  medias: [],
  name: '',
  nickname: '',
  orderNo: '',
  productId: '',
  rate: 5,
  shopId: '',
  skuId: '',
  specs: [''],
  updateTime: '',
  userId: '',
})
const handleStarsChange = () => {}

// @ts-ignore
onLoad(({ storageKey }) => {
  const item = storage.get(storageKey)
  if (!item) {
    return
  }
  const { orderNo, productId, skuId, shopId, specs } = item
  pathQuery.value.productId = productId
  pathQuery.value.skuId = skuId
  pathQuery.value.shopId = shopId
  pathQuery.value.specs = specs
  initOrderEvaluate(orderNo)
})

async function initOrderEvaluate(orderNo: string) {
  const { code, data } = await doPostGetOrderEvaluate(orderNo, pathQuery.value)
  if (code !== 200) {
    return
  }
  order.value = data
}
</script>

<template>
  <view class="form">
    <view class="form__container">
      <view class="form__content">
        <view>
          <u-image :border-radius="15" :height="100" :src="order.image" :width="100" />
        </view>
        <view class="form__content-right">
          <view class="u-line-1" style="width: calc(100vw - 140rpx)">{{ order.name }}</view>
          <view class="form__content-right--bottom">
            <text style="margin-right: 20rpx">评分</text>
            <u-rate v-model="order.rate" disabled gutter="32" size="40" @change="handleStarsChange" />
          </view>
        </view>
      </view>
      <view class="form__input">
        <view :border="true" maxlength="30" style="height: 170rpx; border: 1px solid #ccc; border-radius: 20rpx; padding: 20rpx">
          {{ order.comment }}
        </view>
        <view class="form__input--msg">{{ `${order.comment.length}/30` }}</view>
      </view>
      <!-- medias -->
      <template v-if="order.medias.length">
        <view class="form__upload-title">
          <view> 商品图片</view>
        </view>
        <!-- <u-grid :col="3" :border="false">
                    <u-grid-item v-for="src in order.medias" :key="src">
                        <u-image :width="200" :height="200" :border-radius="15" :src="src" />
                    </u-grid-item>
                </u-grid> -->
        <PreviewImage :images="order.medias" />
      </template>
    </view>
  </view>
  <Auth />
</template>

<style lang="scss" scoped>
@include b(form) {
  @include e(container) {
    background: #fff;
    margin-top: 20rpx;
    border-radius: 20rpx;
    padding: 10rpx;
  }
  @include e(upload) {
    display: flex;
    width: 214rpx;
    height: 214rpx;
    justify-content: center;
    align-items: center;
    background: #f3f4f6;
    border-radius: 4rpx;
    margin-top: 10rpx;
  }
  @include e(upload-title) {
    margin: 20rpx 0;
    color: #333;
    font-size: 26rpx;
    font-weight: 400;
  }
  @include e(input) {
    position: relative;
    margin-top: 20rpx;
    @include m(msg) {
      position: absolute;
      right: 10rpx;
      bottom: 10rpx;
      color: #c0c4cc;
    }
  }
  @include e(msg) {
    display: flex;
    padding: 26rpx;
    justify-content: flex-start;
    align-items: center;
    background: #ffebeb;
  }
  @include e(content) {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    @include e(content-right) {
      display: flex;
      flex-direction: column;
      height: 100rpx;
      justify-content: space-between;
      padding: 5rpx;
      margin-left: 20rpx;
      @include m(bottom) {
        display: flex;
        justify-content: flex-start;
      }
    }
  }
}

@include b(level) {
  position: relative;
  margin: 96rpx 18rpx 0 18rpx;
  height: 208rpx;
  background: #fff;
  border-radius: 20rpx;
  padding-top: 60rpx;
  text-align: center;
  font-size: 18rpx;
  color: #9a9a9a;
  @include e(image) {
    position: absolute;
    left: 50%;
    top: -40rpx;
    margin-left: -50rpx;
  }
  @include e(sku) {
  }
  @include e(assess) {
    margin-top: 30rpx;
  }
}

@include b(main) {
  position: relative;
  background: #fff;
  border-radius: 20rpx;
  padding: 52rpx 0 15rpx 24rpx;
  margin: 0 18rpx;
  border-top: 1px dashed #ccc;
  @include e(text) {
    position: absolute;
    top: -20rpx;
    left: 50%;
    transform: translateX(-50%) scale(0.8);
    font-size: 16rpx;
    color: #9a9a9a;
    text-align: center;
    width: 400rpx;
    background: #fff;
  }
  @include e(title) {
    font-size: 18rpx;
    color: #9a9a9a;
  }
  @include e(upload) {
    margin: 16rpx 0;
    @include m(slot) {
      padding: 54rpx 0;
      width: 664rpx;
      display: flex;
      justify-content: center;
      flex-direction: column;
      align-items: center;
      background: rgb(244, 245, 246);
      border-radius: 10rpx;
    }
    @include m(thereAre) {
      padding: 54rpx 0;
      width: 214rpx;
      display: flex;
      justify-content: center;
      flex-direction: column;
      align-items: center;
      background: rgb(244, 245, 246);
      border-radius: 20rpx;
    }
    @include m(hover) {
      background-color: rgb(235, 236, 238);
    }
    @include m(text) {
      font-size: 18rpx;
      color: #9a9a9a;
      text-align: center;
    }
  }
}
</style>
