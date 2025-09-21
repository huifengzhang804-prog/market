<template>
  <view>
    <!-- #ifndef MP-WEIXIN -->
    <Coner :class="['goods-item__coner']" :tag-show="formData.showContent.tagShow" :tag-style="formData.showContent.tagStyle"></Coner>
    <!-- #endif  -->
    <view v-if="imgShow" class="goods-item__img" style="border-radius: 20rpx 20rpx 0 0">
      <!-- #ifndef H5 -->
      <image :src="cropImg(item.pic, 380, 380)" alt :lazy-load="true" mode="aspectFill" style="height: 100%"></image>
      <!-- #endif -->
      <!-- #ifdef H5 -->
      <lazy-load>
        <img :src="cropImg(item.pic, 380, 380)" mode="aspectFill" style="height: 100%" />
      </lazy-load>
      <!-- #endif -->
    </view>
    <view :class="['goods-item__foot']">
      <view :class="['goods-item__name1', goodsNameClass]">
        {{ item.productName }}
      </view>
      <view class="goods-item__bottom" style="margin: 0">
        <view class="goods-item__price">
          <view class="price__one">
            {{ Number(range(item.salePrices[0])).toFixed(2) }}
          </view>
        </view>
        <view @click.stop="$emit('navTo-detail')">
          <ShoppingButton :button-style="formData.showContent.buttonStyle"></ShoppingButton>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import Coner from './coner.vue'
import ShoppingButton from './shoppingButton.vue'
import { cropImg } from '@/utils'
import type { PropType } from 'vue'
import type { GoodFormData } from '../types'
import LazyLoad from '@/components/lazy-load/lazy-load.vue'

type GoodItemKey = 'productId' | 'productName' | 'salePrices' | 'salesVolume' | 'shopName' | 'pic' | 'shopId' | 'specs' | 'status'
type GoodItemType = Record<GoodItemKey, string>
const { range } = usePriceRange()
const $emit = defineEmits(['navTo-detail'])

defineProps({
  formData: {
    type: Object as PropType<GoodFormData>,
    default() {
      return {}
    },
  },
  classBugCard3: {
    type: String,
    default: '',
  },
  item: {
    type: Object as PropType<GoodItemType>,
    default: () => ({}),
  },
  goodsNameClass: {
    type: String,
    default: '',
  },
  goodsCornerMark: {
    type: Object as PropType<Record<'url' | 'class', string>>,
    default: () => ({}),
  },
  imgShow: {
    type: Boolean,
    default: true,
  },
})
</script>

<style lang="scss" scoped>
@include b(goods-item) {
  position: relative;
  background-color: #fff;

  @include when(circle) {
    border-radius: 12rpx;
    overflow: hidden;
  }

  @include when(shadow) {
    // box-shadow: 0 0 4px rgba($color: #000000, $alpha: 0.4);
    box-shadow: 0px 2rpx 112rpx 12rpx rgba(109, 109, 109, 0.1);
  }

  @include when(border) {
    border: 2rpx solid #eeeeee;
  }

  @include e(img) {
    height: 360rpx;
    background-color: #eeeeee;
    overflow: hidden;
    position: relative;

    image {
      height: 360rpx;
      width: 100%;
    }
  }

  @include e(name1) {
    overflow: hidden;
    text-overflow: ellipsis;
    padding-top: 10rpx;
    -webkit-line-clamp: 1;
    word-wrap: break-word;
    white-space: normal !important;
    -webkit-box-orient: vertical;
    display: -webkit-box;
    font-size: 32rpx;
  }

  @include e(foot) {
    padding: 0 14rpx;
    position: relative;

    .is-bold {
      font-weight: 800;
    }
  }

  @include e(bottom) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6rpx 0;
    margin-top: 20rpx;
  }

  @include e(price) {
    line-height: 74rpx;
    flex: 1;

    view {
      display: inline-block;
      line-height: 74rpx;
      height: 74rpx;
    }

    .price__one {
      font-size: 36rpx;
      color: #dd3c2b;
      &::before {
        content: '￥';
        font-size: 24rpx;
        font-weight: normal;
      }
    }

    .price__two {
      text-decoration: line-through;
      color: #bbbbbb;
      font-size: 20rpx;
      margin-left: 16rpx;
    }
  }

  @include e(icon) {
    display: inline-block;
    width: 56rpx;
    height: 56rpx;
    line-height: 56rpx;
    text-align: center;
    background-color: red;
    border-radius: 100%;
    color: #ffffff;
  }

  @include e(cart1) {
    float: right;
    height: 60rpx;
    width: 60rpx;
    background-color: rgba(252, 98, 63, 1);
    box-sizing: border-box;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-top: 10rpx;
  }

  @include e(cart) {
    float: right;
    height: 60rpx;
    width: 60px;
    box-sizing: border-box;
    border-radius: 50%;
    display: flex;
    justify-content: center;
    align-items: center;

    image {
      display: inline-block;
      width: 40rpx;
      height: 40rpx;
    }
  }

  @include e(cart1) {
    background-color: #fff;

    image {
      display: inline-block;
      width: 60rpx;
      height: 60rpx;
    }
  }

  @include e(cart2) {
    background: linear-gradient(164deg, rgba(243, 243, 243, 1), rgba(229, 56, 46, 1), rgba(253, 78, 38, 1));
    box-shadow: 0 4rpx 14rpx 0 rgba(255, 14, 0, 0.27);
    border-radius: 50%;
  }

  @include e(cart3) {
    border: 2rpx solid rgba(252, 98, 63, 1);
    width: auto;
    padding: 0 10rpx;
    color: rgba(252, 98, 63, 1);
    font-size: 24rpx;
    border-radius: 40rpx;
    height: 50rpx;
    line-height: 50rpx;
  }

  @include e(cart4) {
    border: 2rpx solid rgba(252, 98, 63, 1);
    background-color: rgba(252, 98, 63, 1);
    width: auto;
    padding: 0 10rpx;
    color: #fff;
    font-size: 24rpx;
    border-radius: 40rpx;
    height: 50rpx;
    line-height: 50rpx;
  }

  @include e(coner) {
    position: absolute;
    z-index: 12;

    image,
    text {
      display: block;
      width: 100%;
      height: 100%;
      position: absolute;
    }
  }

  @include e(coner1) {
    left: 0rpx;
    top: 0rpx;
    width: 76rpx;
    height: 44rpx;
    z-index: 7;
  }

  @include e(coner2) {
    left: 0;
    top: 0;
    width: 76rpx;
    height: 82rpx;
  }

  @include e(coner3) {
    left: 0rpx;
    top: 0rpx;
    width: 84rpx;
    height: 42rpx;
  }

  @include e(delivery) {
    padding-top: 4rpx;

    .i_box {
      border: 2rpx solid rgba(233, 56, 38, 1);
      border-radius: 34rpx;
      color: #e93826;
      font-size: 22rpx;
      display: inline-block;
      padding: 0 12rpx;
    }

    .base_info {
      padding: 2rpx 12rpx;
      background-color: #e93826;
      color: #fff;
      border-radius: 34rpx;
      display: none;
    }

    .hasTip {
      padding-left: 0;

      .base_info {
        display: inline-block;
        margin-right: 2rpx;
      }
    }
  }

  .goods-item__delivery3 {
    transform: scale(0.76);
    position: absolute;
    left: 4rpx;
    top: 100rpx;
  }

  .goods-buy__cart3 {
    width: 34rpx;
    height: 36rpx;
  }

  .buy-icon2 {
    margin-top: 16rpx;

    image {
      width: 38rpx;
      height: 38rpx;
    }
  }
}

.add__f-p {
  padding-bottom: 30rpx;
}
</style>
