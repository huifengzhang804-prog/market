<script setup lang="ts">
import { ref, type PropType, reactive } from 'vue'
import SellingInfo from '@pluginPackage/integral/mall/components/commodityInfo/selling-info.vue'
import { onLoad } from '@dcloudio/uni-app'
import formatRichText from '@/pluginPackage/utils/formatRichText'
import type { IntegralGoodsInfoType } from '@/apis/plugin/integral/model'

const $props = defineProps({
  info: {
    type: Object as PropType<IntegralGoodsInfoType>,
    default() {
      return {}
    },
  },
})

// 权益相关
const interestsConfig = reactive({
  // 发货
  delivery: {
    sendTime: '',
    // 预计收货时间
    receiveTime: '',
  },
  // 运费
  freight: '',
  service: '',
})
const skuChooseScreenWidth = reactive({
  screenWidth: 0,
  skuImgItemWidth: 0,
  translateX: 0,
  index: 0,
  distance: 0,
  lastIndex: 0,
})

onLoad(() => {
  uni.getSystemInfo({
    success: (res) => {
      skuChooseScreenWidth.screenWidth = res.screenWidth
    },
  })
})
</script>
<template>
  <view id="swiper_two">
    <!-- 常规商品展示价格 s-->
    <view class="line">
      <selling-info :info="$props.info" />
    </view>
    <!-- 常规商品展示价格 e-->
    <view class="goods-card">
      <u-cell-group :border="false">
        <u-cell-item v-if="interestsConfig.delivery.sendTime" :border-bottom="false">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">发货</text>
              <text class="goods-card--value">
                付款后，{{ interestsConfig.delivery.sendTime }}，预计{{ interestsConfig.delivery.receiveTime }}前送达
              </text>
            </view>
          </template>
        </u-cell-item>
        <u-cell-item v-show="interestsConfig.service" :border-bottom="false">
          <template #title>
            <view class="goods-card__content">
              <text class="goods-card--title">服务</text>
              <text class="goods-card--value">{{ interestsConfig.service }}</text>
            </view>
          </template>
        </u-cell-item>
      </u-cell-group>
    </view>
  </view>
  <template v-if="$props.info.detail">
    <view id="detail" class="goods-card">
      <view class="line-title">详情</view>
      <view class="introduction-title">
        <u-icon name="file-text"></u-icon>
        商品介绍
      </view>
      <view style="margin: 0 40rpx; padding-bottom: 20rpx">
        <rich-text :nodes="formatRichText($props.info.detail)"></rich-text>
      </view>
    </view>
  </template>
</template>
<style lang="scss" scoped>
@import '@/assets/css/mixins/mixins.scss';
@include b(swiper-sku) {
  padding: 4rpx;
  display: flex;
  flex-wrap: nowrap;
  @include e(image) {
    border-radius: 30rpx;
    transform: translateX(0);
    flex-shrink: 0;
    width: calc(20% - 4rpx);
    height: 150rpx;
    margin-right: 4rpx;
    border: 2px solid transparent;
    text-align: center;
    line-height: 150rpx;
    font-weight: 700;
    transition: transform 1s;
  }
}
@include b(line) {
  padding: 0 32rpx;
  padding-bottom: 30rpx;
  background: #fff;
  @include e(name) {
    font-size: 32rpx;
    line-height: 52rpx;
    color: #1e1c1c;
  }
  @include e(ctx) {
    @include flex(space-between);
    margin-top: 18rpx;
  }
  @include e(ctx-left) {
    font-size: 26rpx;
    @include flex();
    @include m(title) {
      font-weight: bold;
    }
    @include m(item) {
      color: #7c7c7c;
      margin-left: 30rpx;
      font-weight: bold;
    }
    @include m(red) {
      color: #fb375e;
    }
  }
  @include e(ctx-right) {
    color: #7c7c7c;
    & > .icon {
      margin-right: 8rpx;
    }
  }
  @include e(desc) {
    padding: 16rpx;
    background: rgb(228, 228, 228);
    border-radius: 10rpx;
    color: #6c6a6a;
    word-break: break-all;
  }
}
@include b(detail) {
  @include e(title) {
    margin-top: 20px;
    color: #5e5e5e;
    font-weight: bold;
    text-align: center;
  }
}
@include b(shopInfo) {
  @include flex();
  @include e(title) {
    width: 400rpx;
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    font-weight: 700;
    font-size: 28rpx;
    color: #1a1a1a;
  }
  @include e(img) {
    margin-right: 32rpx;
  }
  @include e(tips) {
    font-size: 24rpx;
    color: #9b9b9b;
  }
}
@include b(assess) {
  color: red;
}
@include b(parameter) {
  @include e(header) {
    text-align: center;
    line-height: 100rpx;
    font-size: 30rpx;
    color: #000;
    font-weight: bold;
  }
  @include e(item) {
    padding: 20rpx;
    font-size: 28rpx;
    border-bottom: 1px solid rgb(226, 224, 224);
    @include flex(flex-start);
    @include m(left) {
      color: #ccc;
      margin-right: 40rpx;
    }
  }
}
@include b(cell) {
  @include e(title) {
    font-size: 28rpx;
    font-weight: bold;
    color: #000;
  }
}

@include b(active-sku) {
  border-color: #e31436;
}
@include b(goods-card) {
  margin: 10rpx;
  border-radius: 16rpx;
  background-color: rgba(255, 255, 255, 1);
  @include e(content) {
    @include flex;
    font-size: 24rpx;
  }
  @include m(title) {
    width: 100rpx;
    color: #999999;
  }
  @include m(value) {
    padding-left: 16rpx;
    color: #333;
  }
  @include m(see) {
    display: inline-block;
    width: 150rpx;
    font-size: 28rpx;
  }
}
@include b(line-title) {
  font-weight: 700;
  padding: 20rpx 30rpx;
  @include flex;
  justify-content: flex-start;
  line-height: 25rpx;
  &::before {
    content: '';
    display: inline-block;
    width: 6rpx;
    height: 30rpx;
    margin-right: 16rpx;
    background: linear-gradient(180deg, rgba(227, 20, 54, 1) 0%, rgba(255, 233, 233, 1) 100%);
  }
}
@include b(introduction-title) {
  font-size: 24rpx;
  color: #101010;
  padding: 20rpx 30rpx;
}
@include b(parameter-box) {
  padding: 20rpx 30rpx 30rpx 30rpx;
}
@include b(parameter-item) {
  @include flex(space-between);
  margin-bottom: 10rpx;
  @include e(name) {
    width: 200rpx;
    height: 80rpx;
    background-color: rgba(249, 249, 249, 1);
    color: rgba(16, 16, 16, 1);
    font-size: 28rpx;
    text-align: center;
    margin-right: 8rpx;
    line-height: 80rpx;
  }
  @include e(parame) {
    flex: 1;
    height: 80rpx;
    line-height: 80rpx;
    background-color: rgba(249, 249, 249, 1);
    color: rgba(16, 16, 16, 1);
    font-size: 28rpx;
    text-align: center;
    margin-right: 8rpx;
  }
}
@include b(shipping-instructions) {
  padding: 30rpx;
  color: #666666;
  font-size: 24rpx;
}
</style>
