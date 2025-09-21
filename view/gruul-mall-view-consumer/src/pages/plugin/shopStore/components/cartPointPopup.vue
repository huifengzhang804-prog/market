<script setup lang="ts">
import { computed, ref, watch, type PropType } from 'vue'
import { ADD_RESS_TYPES } from '@/apis/address/model'
import { navigateToConfirmOrder } from '@/pages/modules/car'
import { cloneDeep } from 'lodash'
import type { GoodListType, StoragePackage } from '@/pages/modules/car/types'
import type { DistributionKeyType } from '@/apis/good/model'
import { doGetShopCarList } from '@/apis/shopCar'

const $props = defineProps({
  modelValue: {
    type: Boolean,
    default() {
      return false
    },
  },
  allSelectedGoods: {
    type: Array as PropType<StoragePackage[]>,
    default: () => [],
  },
})
const emit = defineEmits(['update:modelValue', 'changeshowStockModal'])
const expressGoods = computed(() => formatCartGoodsStorePoint($props.allSelectedGoods, ADD_RESS_TYPES.DISTRIBUTION.EXPRESS))
const intraCityGoods = computed(() => formatCartGoodsStorePoint($props.allSelectedGoods, ADD_RESS_TYPES.DISTRIBUTION.INTRA_CITY_DISTRIBUTION))
const cartPointGoods = computed(() => formatCartGoodsStorePoint($props.allSelectedGoods, ADD_RESS_TYPES.DISTRIBUTION.SHOP_STORE))
const unNeedGoods = computed(() => formatCartGoodsStorePoint($props.allSelectedGoods, ADD_RESS_TYPES.DISTRIBUTION.VIRTUAL))
const expressGoodsFlatMap = computed(() => expressGoods.value.flatMap((shop) => shop.products))

const visible = ref(false)

watch(
  () => $props.modelValue,
  (val) => {
    visible.value = val
    emit('update:modelValue', val)
  },
)

function handlePreviewImage(params: StoragePackage['products'], idx: number) {
  uni.previewImage({
    current: idx,
    urls: params.map((item) => item.image),
    indicator: 'number',
  })
}
/**
 * 格式化自提点所需数据
 * @param {*} params
 */
function formatCartGoodsStorePoint(params: StoragePackage[], key: DistributionKeyType) {
  const currentGoodsGroups = cloneDeep(params).map((shop) => {
    shop.products = shop.products.filter((item) => item.distributionMode.includes(key))
    shop.distributionMode = key
    return shop
  })
  return currentGoodsGroups.filter((item) => item.distributionMode === key).filter((item) => item.products.length > 0)
}

const handleSettleAccounts = async (allSelectedGoods: StoragePackage[], key: DistributionKeyType) => {
  allSelectedGoods.forEach((item) => (item.distributionMode = key))
  const res = await navigateToConfirmOrder(allSelectedGoods?.filter((item) => item.products?.length > 0))
  if (!res.success) {
    emit('changeshowStockModal', res)
  }
}

const isUnEmpty = (goods: StoragePackage[]) => !!goods.length && goods.every((item) => item.products.length)
</script>

<template>
  <!-- 配送方式弹窗 -->
  <u-popup :model-value="visible" closeable mode="bottom" border-radius="14" @close="emit('update:modelValue', false)">
    <view class="distribution">
      <view class="distribution__title">配送方式选择</view>
      <scroll-view scroll-y class="distribution__list">
        <!-- 快递配送 -->
        <view v-if="isUnEmpty(expressGoods)" class="distribution__item">
          <view class="distribution__item-head">
            <view class="distribution__item-head--left">快递配送</view>
            <view class="distribution__item-head--right">
              共 <span style="color: #f54319">{{ expressGoodsFlatMap.reduce((total, item) => total + item.num, 0) }}</span> 件
            </view>
          </view>
          <view class="distribution__item-content">
            <scroll-view scroll-x enhanced :show-scrollbar="false" class="distribution__item-content--left">
              <view v-for="(product, idx) in expressGoodsFlatMap" :key="idx" class="goods" @click="handlePreviewImage(expressGoodsFlatMap, idx)">
                <u-image :width="140" :height="140" :fade="true" class="goods__image" :src="product.image" />
                <view class="goods__num">x{{ product.num }}</view>
              </view>
            </scroll-view>
            <u-button
              type="error"
              shape="circle"
              :ripple="true"
              size="default"
              style="height: 60rpx; margin-left: auto; margin-right: 0; padding: 0 28rpx"
              @click="handleSettleAccounts(expressGoods, 'EXPRESS')"
            >
              去结算
            </u-button>
          </view>
        </view>
        <!-- 同城配送 -->
        <view v-if="isUnEmpty(intraCityGoods)" class="distribution__item">
          <view class="distribution__item-head--left">同城配送</view>
          <view v-for="(item, idx) in intraCityGoods" :key="idx" class="distribution__item-content--container">
            <template v-if="item.products.length">
              <view class="distribution__item-head">
                <view class="distribution__item-head--shopname">{{ item.shopName }}</view>
                <view class="distribution__item-head--right">
                  共 <span style="color: #f54319">{{ item.products.reduce((total, item) => total + item.num, 0) }}</span> 件
                </view>
              </view>
              <view class="distribution__item-content">
                <scroll-view scroll-x enhanced :show-scrollbar="false" class="distribution__item-content--left">
                  <view v-for="(product, index) in item.products" :key="index" class="goods" @click="handlePreviewImage(item.products, idx)">
                    <u-image :width="140" :height="140" :src="product.image" :fade="true" class="goods__image" />
                    <view class="goods__num">x{{ product.num }}</view>
                  </view>
                </scroll-view>
                <u-button
                  type="error"
                  shape="circle"
                  :ripple="true"
                  size="default"
                  style="height: 60rpx; margin-left: auto; margin-right: 0; padding: 0 28rpx"
                  @click="handleSettleAccounts([item], ADD_RESS_TYPES.DISTRIBUTION.INTRA_CITY_DISTRIBUTION)"
                >
                  去结算
                </u-button>
              </view>
            </template>
          </view>
        </view>
        <!-- 到店自提 -->
        <view v-if="isUnEmpty(cartPointGoods)" class="distribution__item">
          <view class="distribution__item-head--left">到店自提</view>
          <view v-for="(item, idx) in cartPointGoods" :key="idx">
            <template v-if="item.products.length">
              <view class="distribution__item-head">
                <view class="distribution__item-head--shopname">{{ item.shopName }}</view>
                <view class="distribution__item-head--right">
                  共 <span style="color: #f54319">{{ item.products.reduce((total, item) => total + item.num, 0) }}</span> 件
                </view>
              </view>
              <view class="distribution__item-content">
                <scroll-view scroll-x enhanced :show-scrollbar="false" class="distribution__item-content--left">
                  <view v-for="(product, index) in item.products" :key="index" class="goods" @click="handlePreviewImage(item.products, idx)">
                    <u-image :width="140" :height="140" :src="product.image" :fade="true" class="goods__image" />
                    <view class="goods__num">x{{ product.num }}</view>
                  </view>
                </scroll-view>
                <u-button
                  type="error"
                  shape="circle"
                  :ripple="true"
                  size="default"
                  style="height: 60rpx; margin-left: auto; margin-right: 0; padding: 0 28rpx"
                  @click="handleSettleAccounts([item], ADD_RESS_TYPES.DISTRIBUTION.SHOP_STORE)"
                >
                  去结算
                </u-button>
              </view>
            </template>
          </view>
        </view>
        <!-- 无需物流 -->
        <view v-if="isUnEmpty(unNeedGoods)" class="distribution__item">
          <view class="distribution__item-head--left">无需物流</view>
          <view v-for="(item, idx) in unNeedGoods" :key="idx">
            <template v-if="item.products.length">
              <view class="distribution__item-head">
                <view class="distribution__item-head--shopname">{{ item.shopName }}</view>
                <view class="distribution__item-head--right">
                  共 <span style="color: #f54319">{{ item.products.reduce((total, item) => total + item.num, 0) }}</span> 件
                </view>
              </view>
              <view class="distribution__item-content">
                <scroll-view scroll-x enhanced :show-scrollbar="false" class="distribution__item-content--left">
                  <view v-for="(product, index) in item.products" :key="index" class="goods" @click="handlePreviewImage(item.products, idx)">
                    <u-image :width="140" :height="140" :src="product.image" :fade="true" class="goods__image" />
                    <view class="goods__num">x{{ product.num }}</view>
                  </view>
                </scroll-view>
                <u-button
                  type="error"
                  shape="circle"
                  :ripple="true"
                  size="default"
                  style="height: 60rpx; margin-left: auto; margin-right: 0; padding: 0 28rpx"
                  @click="handleSettleAccounts([item], ADD_RESS_TYPES.DISTRIBUTION.VIRTUAL)"
                >
                  去结算
                </u-button>
              </view>
            </template>
          </view>
        </view>
      </scroll-view>
    </view>
  </u-popup>
</template>

<style scoped lang="scss">
@include b(ordermodal-title) {
  @include flex;
  font-weight: 600;
  text-align: center;
  line-height: 88rpx;
}
@include b(ordermodal-content) {
  padding: 40rpx;
  font-size: 28rpx;
  text-align: center;
  bottom: 1px solid red;
}
@include b(distribution) {
  background: white;
  padding: 30rpx 22rpx 0;
  @include e(list) {
    height: 1000rpx;
  }
  @include e(title) {
    text-align: center;
    font-size: 30rpx;
    font-weight: bold;
    margin-bottom: 26rpx;
  }

  @include e(item) {
    background: #f8f8f8;
    border-radius: 8rpx;
    padding: 26rpx;
    margin-bottom: 20rpx;
  }
  @include e(item-head) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    @include m(left) {
      font-size: 28rpx;
      font-weight: bold;
      color: #333;
      margin-bottom: 24rpx;
    }
    @include m(shopname) {
      font-size: 28rpx;
      color: #666666;
    }
    @include m(right) {
      color: #777777;
      font-size: 24rpx;
    }
  }
  @include e(item-content) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-top: 12rpx;
    @include m(container) {
      margin-bottom: 24rpx;
    }
    @include m(left) {
      width: 0;
      flex: 1;
      white-space: nowrap;
      margin-right: 26rpx;
    }
  }
}
@include b(goods) {
  display: inline-block;
  width: 140rpx;
  height: 140rpx;
  margin-right: 24rpx;
  position: relative;
  border-radius: 10rpx;
  overflow: hidden;
  @include e(image) {
  }
  @include e(num) {
    position: absolute;
    bottom: 0;
    right: 0;
    z-index: 1;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: rgba($color: #000000, $alpha: 0.5);
    color: white;
  }
}
</style>
