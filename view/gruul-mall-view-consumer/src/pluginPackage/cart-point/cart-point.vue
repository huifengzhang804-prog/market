<script setup lang="ts">
import { computed, ref } from 'vue'
import { navigateToConfirmOrder } from '@/pages/modules/car'
import StockModal from '@/pages/modules/car/components/stock-modal.vue'
import { cloneDeep } from 'lodash'
import type { StoragePackage } from '@/pages/modules/car/types'
import { onLoad } from '@dcloudio/uni-app'
import storage from '@/utils/storage'
import type { DistributionKeyType } from '@/apis/good/model'

const allSelectedGoods = ref([])
const intraCityGoods = computed(() => formatCartGoodsStorePoint(allSelectedGoods.value, 'INTRA_CITY_DISTRIBUTION'))
const cartPointGoods = computed(() => formatCartGoodsStorePoint(allSelectedGoods.value, 'SHOP_STORE'))
const showStockModal = ref(false)
const stockModalData = ref({})
function totleNum(goods: StoragePackage[]) {
  return goods.flatMap((shop) => shop.products).length
}
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
  return cloneDeep(params).map((shop) => {
    shop.products = shop.products.filter((item) => item.distributionMode.includes(key))
    return shop
  })
}

const handleSettleAccounts = async (allSelectedGoods: StoragePackage[], key: DistributionKeyType) => {
  allSelectedGoods.forEach((item) => (item.distributionMode = key))
  const res = await navigateToConfirmOrder(allSelectedGoods)
  if (!res.success) {
    stockModalData.value = res
    showStockModal.value = true
    return
  }
  if (!res) {
    stockModalData.value = res
  }
}
onLoad(() => {
  allSelectedGoods.value = storage.get('carList')
})
</script>

<template>
  <stock-modal v-model="showStockModal" :stock-modal-data="stockModalData" />
  <!-- 配送方式弹窗 -->
  <view class="distribution">
    <view class="distribution__explain">不同配送方式的商品，不支持同步结算。</view>
    <!-- 快递配送 -->
    <view style="padding: 0 20rpx">
      <scroll-view scroll-y class="distribution__list">
        <view v-if="intraCityGoods.every((item) => item.products.length)" class="distribution__item">
          <view class="distribution__item-head">
            <view class="distribution__item-head--left"> 同城配送</view>
            <view class="distribution__item-head--right">
              共 <span style="color: #f54319">{{ intraCityGoods[0].products.reduce((pre, cur) => pre + cur.num, 0) }}</span> 件
            </view>
          </view>
          <view v-for="(item, idx) in intraCityGoods" :key="idx">
            <template v-if="item.products.length">
              <view class="distribution__item-content">
                <scroll-view scroll-x enhanced :show-scrollbar="false" class="distribution__item-content--left">
                  <view v-for="(product, index) in item.products" :key="index" class="goods" @click="handlePreviewImage(item.products, idx)">
                    <u-image :width="132" :height="132" :src="product.image" :fade="true" class="goods__image" />
                    <view class="goods__num">x{{ product.num }}</view>
                  </view>
                </scroll-view>
                <u-button
                  type="error"
                  :ripple="true"
                  size="default"
                  style="height: 60rpx; margin: 0; width: 174rpx; height: 83rpx; border-radius: 20rpx"
                  @click="handleSettleAccounts([item], 'INTRA_CITY_DISTRIBUTION')"
                >
                  去结算
                </u-button>
              </view>
            </template>
          </view>
        </view>
        <view v-if="cartPointGoods.every((item) => item.products.length)" class="distribution__item">
          <view class="distribution__item-head">
            <view class="distribution__item-head--left"> 到店自提 </view>
            <view class="distribution__item-head--right">
              共 <span style="color: #f54319">{{ cartPointGoods[0].products.reduce((pre, cur) => pre + cur.num, 0) }}</span> 件
            </view>
          </view>
          <view v-for="(item, idx) in cartPointGoods" :key="idx">
            <template v-if="item.products.length">
              <view class="distribution__item-content">
                <scroll-view scroll-x enhanced :show-scrollbar="false" class="distribution__item-content--left">
                  <view v-for="(product, index) in item.products" :key="index" class="goods" @click="handlePreviewImage(item.products, idx)">
                    <u-image :width="132" :height="132" :src="product.image" :fade="true" class="goods__image" />
                    <view class="goods__num">x{{ product.num }}</view>
                  </view>
                  <!-- <view
                                        v-for="(product, index) in item.products"
                                        :key="index"
                                        class="goods"
                                        @click="handlePreviewImage(item.products, idx)"
                                    >
                                        <u-image :width="132" :height="132" :src="product.image" :fade="true" class="goods__image" />
                                        <view class="goods__num">x{{ product.num }}</view>
                                    </view> -->
                </scroll-view>
                <u-button
                  type="error"
                  :ripple="true"
                  size="default"
                  style="height: 60rpx; margin: 0; width: 174rpx; height: 83rpx; border-radius: 20rpx"
                  @click="handleSettleAccounts([item], 'SHOP_STORE')"
                >
                  去结算
                </u-button>
              </view>
            </template>
          </view>
        </view>
      </scroll-view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(distribution) {
  @include e(list) {
    height: 600rpx;
  }
  @include e(title) {
    text-align: center;
    font-size: 30rpx;
    font-weight: bold;
  }
  @include e(explain) {
    background: #fff2f2;
    padding: 26rpx 32rpx;
    font-size: 28rpx;
    color: #333;
  }
  @include e(item) {
    background: #fff;
    border-radius: 20rpx;
    padding: 20rpx;
    margin-top: 20rpx;
  }
  @include e(item-head) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    @include m(left) {
      font-size: 30rpx;
    }
    @include m(right) {
      color: #777777;
      font-size: 24rpx;
      width: 150rpx;
      text-align: center;
    }
  }
  @include e(item-content) {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-top: 20rpx;
    @include m(left) {
      height: 136rpx;
      width: 435rpx;
      white-space: nowrap;
    }
  }
}
@include b(goods) {
  display: inline-block;
  width: 132rpx;
  height: 132rpx;
  margin-top: 2rpx;
  margin-right: 30rpx;
  position: relative;
  box-shadow: 0px 0px 9rpx -6rpx;
  @include e(num) {
    position: absolute;
    bottom: 0;
    right: 0;
    z-index: 1;
  }
}
</style>
