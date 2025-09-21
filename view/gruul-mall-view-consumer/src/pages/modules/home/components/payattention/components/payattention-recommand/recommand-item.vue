<template>
  <view class="recommand-item">
    <view class="recommand-item__shop" @click.prevent="jumpToShop(shopInfo.id)">
      <image class="shop-logo" :src="shopInfo.logo" />
      <view class="shop-info">
        <view class="shop-info__name">
          <text
            v-if="shopInfo.shopType !== 'ORDINARY'"
            class="shop-info__name--tag"
            :class="shopInfo.shopType === 'SELF_OWNED' ? 'label-red' : 'label-yellow'"
            >{{ ShopTypeEnum[shopInfo.shopType] }}</text
          >
          <text class="shop-info__name--main">{{ shopInfo.name }}</text>
          <view class="shop-info__name--focus" @click.stop="focusShop()">
            <q-icon name="icon-31guanzhu1" size="28rpx" />
            <text class="shop-info__name--focus--text">关注</text>
          </view>
        </view>
        <view v-if="showSaleInfo" class="shop-info__sale">
          <text v-if="shopInfo.salesVolume" class="shop-info__sale--info">销量 {{ shopInfo.salesVolume }}</text>
          <text v-if="shopInfo.initialDeliveryCharge === 0 || shopInfo.initialDeliveryCharge" class="shop-info__sale--info">
            <text>起送 </text>
            <text class="shop-info__sale--currency">￥</text>
            <text>{{ shopInfo.initialDeliveryCharge }}</text>
          </text>
          <text v-if="shopInfo.distance" class="shop-info__sale--info">距离 {{ shopInfo.distance }}km</text>
        </view>
        <view v-if="shopInfo.couponList?.length" class="shop-info__coupon">
          <view class="shop-info__coupon--container" :class="{ hidden: showMoreCoupon === false }">
            <view class="coupon-container">
              <text v-for="(coupon, couponIndex) in shopInfo.couponList" :key="couponIndex" class="shop-info__coupon--item">{{
                coupon.sourceDesc
              }}</text>
            </view>
          </view>
          <view class="shop-info__coupon--operator" :class="{ rotate: showMoreCoupon }" @click.stop="showMoreCoupon = !showMoreCoupon">
            <q-icon name="icon-xiajiantou" size="32rpx" />
          </view>
        </view>
      </view>
    </view>
    <scroll-view v-if="shopInfo.productList?.length" scroll-x enhanced :show-scrollbar="false" class="recommand-item__scroll">
      <view class="recommand-item__goods">
        <view
          v-for="(product, productIndex) in shopInfo.productList"
          :key="productIndex"
          class="goods-item"
          @click="jumpGoods(shopInfo.id, product.id)"
        >
          <image class="goods-item__cover" :src="product.pic" />
          <view class="goods-item__name">{{ product.name }}</view>
          <view class="goods-item__price">
            <text class="goods-item__price--currency">￥</text>
            <text>{{ divTenThousand(product.salePrice) }}</text>
          </view>
        </view>
        <view v-if="shopInfo.productList?.length > 4" class="goods-more" @click="jumpToShop(shopInfo.id)">查看更多</view>
      </view>
    </scroll-view>
  </view>
</template>

<script lang="ts" setup>
import qIcon from '@/components/q-icon/q-icon.vue'
import { type PropType, computed, onMounted, ref } from 'vue'
import { type RecommandShop, ShopTypeEnum } from './recommand'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'
import { doCancelAttentionAndAttention } from '@/apis/concern'

const $collectionDispatcherStore = useCollectionDispatcher()
const showMoreCoupon = ref(false)
const $props = defineProps({
  shopInfo: {
    type: Object as PropType<RecommandShop>,
    default: () => ({}),
  },
})
const { divTenThousand } = useConvert()
const showSaleInfo = computed(() => {
  return (
    $props.shopInfo.mode === 'O2O' &&
    (typeof $props.shopInfo.initialDeliveryCharge !== 'undefined' || typeof $props.shopInfo.distance !== 'undefined' || $props.shopInfo.salesVolume)
  )
})
const jumpToShop = (shopId: Long) => {
  uni.navigateTo({ url: `/basePackage/pages/merchant/Index?shopId=${shopId}` })
}
const focusShop = async () => {
  const { data, code, msg } = await doCancelAttentionAndAttention($props.shopInfo.name, $props.shopInfo.id, true, $props.shopInfo.logo)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : `关注失败`}`, icon: 'none' })
  uni.showToast({
    title: `关注成功`,
    icon: 'none',
  })
  $collectionDispatcherStore.updateShopData()
}
</script>

<style lang="scss" scoped>
@include b(recommand-item) {
  background-color: #fff;
  width: 710rpx;
  margin: 20rpx auto 0;
  border-radius: 16rpx;
  padding: 32rpx 20rpx;
  box-sizing: border-box;
  @include e(shop) {
    @include flex(flex-start, flex-start);
    @include b(shop-logo) {
      width: 120rpx;
      height: 120rpx;
      border-radius: 8rpx;
      flex-shrink: 0;
    }
    @include b(shop-info) {
      margin-left: 16rpx;
      flex: 1;
      @include e(sale) {
        margin-top: 4rpx;
        @include m(info) {
          margin-right: 20rpx;
          font-size: 24rpx;
          line-height: 32rpx;
          color: #666;
        }
        @include m(currency) {
          font-size: 20rpx;
        }
      }
      @include e(name) {
        @include flex(space-between, center);
        @include m(tag) {
          padding: 2rpx 8rpx;
          border-radius: 4rpx;
          color: #fff;
          font-size: 20rpx;
          line-height: 28rpx;
          margin-right: 12rpx;
        }
        @include m(main) {
          flex: 1;
          line-height: 44rpx;
          height: 44rpx;
          overflow: hidden;
        }
        @include m(focus) {
          margin-left: 12rpx;
          font-size: 24rpx;
          color: #fff;
          line-height: 32rpx;
          padding: 6rpx 20rpx;
          background-color: #f54319;
          border-radius: 74rpx;
          @include flex(center, center);
          display: inline-flex;
          flex-shrink: 0;
          @include m(text) {
            margin-left: 6rpx;
          }
        }
      }
      @include e(coupon) {
        margin-top: 8rpx;
        @include flex(space-between, flex-start);
        @include m(operator) {
          font-size: 32rpx;
          margin-left: 34rpx;
          flex-shrink: 0;
        }
        @include m(container) {
          @include b(coupon-container) {
            @include flex(flex-start, center);
            flex-wrap: wrap;
          }
        }
        @include m(item) {
          padding: 2rpx 4rpx;
          line-height: 28rpx;
          font-size: 20rpx;
          color: #f54319;
          border: 2rpx solid #f54319;
          border-radius: 4rpx;
          margin-right: 20rpx;
          margin-bottom: 6rpx;
          box-sizing: border-box;
        }
      }
      @include b(label-red) {
        background-color: #f54319;
      }
      @include b(label-yellow) {
        background-color: #fd9224;
      }
      @include b(hidden) {
        height: 40rpx;
        overflow: hidden;
      }
      @include b(rotate) {
        transform: rotate(180deg);
      }
    }
  }
  &__scroll {
    margin-top: 20rpx;
  }
  &__goods {
    @include flex(flex-start, flex-start);
    @include b(goods-item) {
      width: 196rpx;
      margin-right: 8rpx;
      @include e(cover) {
        width: 196rpx;
        height: 196rpx;
        border-radius: 8rpx;
      }
      @include e(name) {
        line-height: 40rpx;
        height: 80rpx;
        font-size: 28rpx;
        color: #333;
        overflow: hidden;
        margin-top: 12rpx;
      }
      @include e(price) {
        line-height: 40rpx;
        margin-top: 12rpx;
        font-size: 28rpx;
        color: #f54319;
        @include m(currency) {
          font-size: 20rpx;
        }
      }
    }
    @include b(goods-more) {
      border: 2rpx solid #f54319;
      border-radius: 8rpx;
      margin-left: 20rpx;
      font-size: 24rpx;
      line-height: 34rpx;
      width: 24rpx;
      padding: 52rpx 32rpx 28rpx;
      color: #f54319;
      box-sizing: content-box;
    }
  }
}
</style>
