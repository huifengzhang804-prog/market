<!-- eslint-disable no-redeclare -->
<!-- eslint-disable no-import-assign -->
<script setup lang="ts">
import { ref, watchEffect, withDefaults } from 'vue'
// #ifndef H5
// @ts-ignore
import ShopGoodsO2o from './shop-goods-o2o/shop-goods-o2o.vue'
// @ts-ignore
import Shop from './components/shop.vue'
// #endif
// #ifdef H5
const ShopGoodsO2o = defineAsyncComponent(() => import('./shop-goods-o2o/shop-goods-o2o.vue'))
const Shop = defineAsyncComponent(() => import('./components/shop.vue'))
// #endif
import ShopSwiper from './components/shop-swiper.vue'
import { formData } from './shop-goods-default'
import { defineAsyncComponent } from 'vue'
import { jumpGoods } from '@/utils/navigateToShopInfo'

interface IProps {
  decorationProperties: typeof formData
}
withDefaults(defineProps<IProps>(), {
  decorationProperties: () => formData,
})

const current = ref(0)
// 切换current
const handleChange = (e: any) => {
  current.value = e.detail.current
}

const jumpShop = (shopId: Long = '') => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shopId}`,
  })
}
</script>
<template>
  <view v-if="!['is-style-one', 'is-style-two'].includes(decorationProperties.showStyle) || decorationProperties.shopInfo?.[0]?.shop?.id">
    <view v-if="['is-style-one', 'is-style-two'].includes(decorationProperties.showStyle)" class="shop-title">{{ decorationProperties.title }}</view>
    <view v-if="decorationProperties.showStyle === 'is-style-one'" class="shop-goods">
      <ShopSwiper :shopAbstract="decorationProperties.shopInfo" />
    </view>

    <view v-else-if="decorationProperties.showStyle === 'is-style-two'">
      <swiper
        class="shop-goods-swiper"
        :style="{ height: '280rpx' }"
        :indicator-dots="false"
        :pagination="false"
        :autoplay="true"
        :circular="true"
        :vertical="false"
        :interval="3000"
        :duration="500"
        :previous-margin="0 + 'px'"
        :next-margin="0 + 'px'"
      >
        <swiper-item v-for="(item, index) in decorationProperties.shopInfo" :key="item.shop.id" :data-index="index">
          <view class="shop-goods-swiper-item">
            <view class="shop-goods-swiper-img">
              <u-image :src="item.shop.advertisement" mode="aspectFit" height="278rpx" width="100%" border-radius="20rpx" />
              <view class="shop-goods-swiper-mask"></view>
            </view>
            <view class="shop-goods-swiper-name-box" @click.stop="jumpShop(item.shop.id)">
              <text class="shop-goods-swiper-name">{{ item.shop.name }}</text>
              <q-icon name="icon-bg-left" size="17rpx" class="shop-goods-swiper-shopCar" />
            </view>
            <text class="shop-goods-swiper-promotion">{{ item.shop.promotion }}</text>
            <view class="shop-goods-swiper-goods">
              <view v-for="good in item.goods" :key="good.id" class="shop-goods-swiper-goods-item" @click.stop="jumpGoods(item.shop.id, good.id)">
                <image :src="good.logo" mode="scaleToFill" style="width: 100%; height: 61%; border-radius: 20rpx" />
                <view class="shop-goods-swiper-goods-item-info">
                  <view class="shop-goods-swiper-goods-item-info-name">{{ good.name.length > 5 ? good.name.slice(0, 4) : good.name }}</view>
                  <view class="shop-goods-swiper-goods-item-info-price">
                    <text style="font-size: 22rpx">￥</text>
                    <text v-if="String(good.price)?.includes('.')">{{ String(good.price).split('.')[0] }}.</text>
                    <text v-if="String(good.price)?.includes('.')" style="font-size: 12px">{{ String(good.price).split('.')[1] }}</text>
                    <text v-else>{{ good.price }}</text>
                  </view>
                </view>
              </view>
            </view>
          </view>
        </swiper-item>
      </swiper>
    </view>
    <view v-else-if="decorationProperties.showStyle === 'is-style-three'">
      <Shop v-for="item in decorationProperties.shopInfo" :key="item.shop.id" :shop-item="item" />
    </view>
    <view v-else>
      <ShopGoodsO2o :decoration-properties="decorationProperties" />
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(shop-goods) {
  padding: 0 20rpx 20rpx;
  background-color: #fff;
  @include e(active) {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 52rpx;
    width: 725rpx;
    border: 1px solid #fcfcfc;
    border-radius: 0 0 16rpx 16rpx;
    background-color: #fff;
    border-top: 1px dotted #eee;
  }
}

.shop-goods-swiper {
  width: calc(100vw);
  background-color: #fff;

  .shop-goods-swiper-item {
    padding: 0 20rpx;
    display: flex;
    position: relative;

    .shop-goods-swiper-name-box {
      position: absolute;
      left: 16rpx;
      bottom: 55rpx;
      color: #fff;
      font-size: 24rpx;
      padding: 0 16rpx;
      width: 270rpx;
      display: flex;
      align-items: center;

      .shop-goods-swiper-name {
        display: inline-block;
        max-width: 240rpx;
        font-family: Yu Gothic;
        font-weight: 600;
        font-size: 36rpx;
        // 一行展示超出...
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin-right: 5rpx;
      }

      .shop-goods-swiper-shopCar {
        transform: rotate(180deg);
      }
    }

    .shop-goods-swiper-promotion {
      position: absolute;
      bottom: 26rpx;
      left: 16rpx;
      width: 278rpx;
      color: #fff;
      font-size: 24rpx;
      padding: 0 16rpx;
    }

    .shop-goods-swiper-img {
      width: 278rpx;
      height: 278rpx;
      border-radius: 20rpx;
      position: relative;
    }

    .shop-goods-swiper-mask {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      border-radius: 20rpx;
      height: 278rpx;
      background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.3));
      pointer-events: none;
    }

    .shop-goods-swiper-goods {
      flex: 1;
      display: flex;
      justify-content: start;

      .shop-goods-swiper-goods-item {
        width: 31%;
        margin-left: 3%;
        display: flex;
        flex-direction: column;
        background-color: #fafafa;
        border-radius: 20rpx;
        position: relative;

        .shop-goods-swiper-goods-item-info {
          height: 39%;
          display: flex;
          flex-direction: column;
          padding: 0 10rpx;

          .shop-goods-swiper-goods-item-info-name {
            margin: 8rpx 0;
            text-align: center;
            font-size: 26rpx;
            font-family: Arial, sans-serif;
            font-weight: 400;
            // 一行展示超出隐藏
            overflow: hidden;
            text-overflow: clip;
            white-space: nowrap;
            color: #333;
          }
          .shop-goods-swiper-goods-item-info-price {
            color: #000;
            font-size: 26rpx;
            font-family: Arial, sans-serif;
            font-weight: 550;
            text-align: center;
          }
        }
      }
    }
  }
}

.goodsBigImg {
  width: calc(100% - 2px);
  height: 700rpx;
  border: 1px solid #eee;
  margin-bottom: -10rpx;
  // #ifdef H5
  object-fit: fill;
  // #endif
}
.point {
  width: 14rpx;
  height: 14rpx;
  background: #ff3a3a;
  border-radius: 7rpx;
  opacity: 0.4;
  margin-left: 20rpx;
}
.current {
  opacity: 1;
}
.swiper {
  height: 480rpx;
}
.shop-title {
  padding: 20rpx;
  color: rgb(51, 51, 51);
  font-size: 32rpx;
  font-weight: 500;
  background-color: #fff;
}
</style>
