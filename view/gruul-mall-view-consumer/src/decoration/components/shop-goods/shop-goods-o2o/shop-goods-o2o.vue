<!-- eslint-disable vue/no-parsing-error -->
<template>
  <view v-if="shopList.length > 0" class="shop-container">
    <view v-for="shop in shopList" :key="shop?.id" class="shop">
      <view class="shop__title" @click="toShopDetails(shop)">
        <view class="shop__title-image">
          <!-- #ifndef H5 -->
          <image
            :src="cropImg(shop?.logo, getCropPx(112), getCropPx(112))"
            mode="aspectFit"
            style="width: 100%; height: 100%"
            :lazy-load="true"
          ></image>
          <!-- #endif -->
          <!-- #ifdef H5 -->
          <lazy-load>
            <img :src="cropImg(shop?.logo, getCropPx(112), getCropPx(112))" mode="aspectFit" style="width: 100%; height: 100%" />
          </lazy-load>
          <!-- #endif -->
        </view>
        <view style="flex: 1; padding-left: 16rpx; height: 90rpx; display: flex; flex-direction: column; justify-content: space-between">
          <view style="margin-bottom: 4rpx">
            <view class="shop-info__name">
              <text
                v-if="shop?.shopType && shop?.shopType !== 'ORDINARY'"
                class="shop__tag"
                :style="{ background: shop?.shopType === 'SELF_OWNED' ? '#fd0505' : '#7728f5' }"
              >
                {{ shopType[shop?.shopType] }}
              </text>
              <text class="shop-info__name--main">{{ shop?.name }}</text>
              <view v-if="shop?.hasOwnProperty('isFollow')" @click.stop="focusShop(shop)">
                <view v-if="!shop.isFollow" class="shop-info__name--focus">
                  <q-icon name="icon-31guanzhu1" size="28rpx" />
                  <text class="shop-info__name--focus--text">关注</text>
                </view>
                <view v-else class="shop-info__name--cancel">
                  <text class="shop-info__name--cancel--text">取消关注</text>
                </view>
              </view>
            </view>
          </view>
          <view style="display: flex; font-size: 24rpx; color: #666">
            <text style="margin-right: 25rpx; color: #fa3534">5.0分</text>
            <text style="margin-right: 25rpx">起送￥{{ shop?.initialDeliveryCharge || 0 }}</text>
            <text v-if="shop?.icDistributeInfo" style="margin-right: 25rpx">{{
              shop?.icDistributeInfo?.inDeliveryRange && shop?.icDistributeInfo?.showMsg
                ? shop?.icDistributeInfo?.showMsg
                : shop?.icDistributeInfo?.minLimit === 0 && !shop?.icDistributeInfo?.inDeliveryRange
                  ? '免费配送'
                  : shop?.icDistributeInfo?.minLimit > 0 && !shop?.icDistributeInfo?.inDeliveryRange
                    ? '配送费￥' + divTenThousand(shop?.icDistributeInfo?.minLimit)
                    : ''
            }}</text>
            <text v-if="shop.distance">距离 {{ new Decimal(shop.distance).toFixed(2) || 0 }}㎞</text>
            <!-- <text>月销 {{ shop?.salesVolume }}</text> -->
          </view>
          <!-- 优惠券 -->
          <!-- <view class="coupon-list">
            <view v-for="(item, index) in shop.couponList" :key="index" :data-index="index" class="coupon-button">{{ item.sourceDesc }}</view>
          </view> -->
        </view>
      </view>
      <view class="goods-list">
        <!-- <view class="coupon-button">{{ shop.couponList[0].sourceDesc }}</view> -->
        <view v-if="shop?.couponList?.length > 0" class="coupon-banner">
          <view v-if="shop?.couponList[0]?.data.discount" class="coupon-amount">{{ shop?.couponList[0]?.data.discount }}折</view>
          <view v-if="shop?.couponList[0]?.data.amount" class="coupon-amount">￥{{ shop?.couponList[0]?.data.amount / 10000 }}</view>
          <view class="coupon-scope"> {{ shop?.couponList[0]?.sourceDesc }} </view>
          <view v-if="shop?.couponList[0]?.data.productType === 'ALL'" class="coupon-desc">全部商品</view>
          <view v-if="shop?.couponList[0]?.data.productType === 'SHOP_ALL'" class="coupon-desc">全店通用</view>
          <view v-if="shop?.couponList[0]?.data.productType === 'ASSIGNED'" class="coupon-desc">指定商品可用</view>
          <view v-if="shop?.couponList[0]?.data.productType === 'ASSIGNED_NOT'" class="coupon-desc">指定商品不生效</view>
          <view class="coupon-xuxian"></view>
          <view class="coupon-btn" @click="handleClickCoupon(shop)">领超值券</view>
        </view>
        <view :style="{ width: shop?.couponList?.length > 0 ? '77.2%' : '100%', height: '280rpx', paddingRight: '20rpx' }">
          <scroll-view scroll-x enhanced :show-scrollbar="false">
            <!-- 商品列表 -->
            <view style="display: flex">
              <view
                v-for="(item, index) in shop?.goodsList"
                :key="index"
                class="goods-item"
                @click="handleClickItem({ shopId: shop?.id, productId: item?.id })"
              >
                <image :src="item?.logo" style="width: 153rpx; height: 153rpx; border-radius: 8rpx" />
                <view class="goods-info">
                  <view class="goods-name">{{ item?.name }}</view>
                  <view class="goods-price">
                    <span class="price-prefix">¥</span>
                    <span class="price-value">{{ item?.price }}</span>
                  </view>
                </view>
              </view>
            </view>
          </scroll-view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { nextTick, onMounted, onUnmounted, ref, watch } from 'vue'
import { formData } from '../shop-goods-default'
import { doGetCommodityByShop, doGetShopListByDistance } from '@/apis/o2oshop'
import type { ApiSearchShop } from './types'
import { cropImg, getCropPx } from '@/utils'
import { Decimal } from 'decimal.js-light'
import { useLocationStore } from '@/store/modules/location'
import { storeToRefs } from 'pinia'
import { doCancelAttentionAndAttention } from '@/apis/concern'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'
import LazyLoad from '@/components/lazy-load/lazy-load.vue'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { doPostConsumerCollectCoupon } from '@/apis/plugin/coupon/index'
import { onShow } from '@dcloudio/uni-app'

const $collectionDispatcherStore = useCollectionDispatcher()
const { divTenThousand } = useConvert()
interface IProps {
  decorationProperties: typeof formData
}
const props = withDefaults(defineProps<IProps>(), {
  decorationProperties: () => formData,
})
const { getterLocation } = storeToRefs(useLocationStore())

const shopList = ref<ApiSearchShop[]>([])

const shopType = {
  SELF_OWNED: '自营',
  PREFERRED: '优选',
}

/**
 * 监听滚动到店铺
 */
uni.$on('scrolltoO2OShop', async () => {
  if (props.decorationProperties.showStyle === 'automatic' || props.decorationProperties.showStyle === 'appoint') {
    await nextTick()
    initialShopList()
  }
})

/**
 * 初始化店铺列表
 */
const initialShopList = async (flag = false) => {
  let shopIds = props.decorationProperties.shopInfo.map((item) => item.shop?.id)
  if (props.decorationProperties.showStyle === 'automatic' || props.decorationProperties.showStyle === 'appoint') {
    // 按距离
    const requestPromise = doGetShopListByDistance({
      sortAsc: true,
      latitude: getterLocation.value?.coordinates?.[1],
      longitude: getterLocation.value?.coordinates?.[0],
      showHeaderShopIds: props.decorationProperties.showStyle === 'appoint' ? shopIds : undefined,
      moreCount: +props.decorationProperties.firstFew,
    })
    const result = await requestPromise
    if (flag) {
      shopList.value = result.data
    } else {
      shopList.value = shopList.value.concat(result.data) || []
    }
  }
}
/**
 * 跳转店铺详情
 * @param shop 店铺信息
 */
const toShopDetails = (shop: ApiSearchShop) => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shop?.id}`,
  })
}

/**
 * 跳转商品详情
 * @param e 商品信息
 */
const handleClickItem = (e: { productId: Long; shopId: Long }) => {
  jumpGoods(e.shopId, e.productId)
}

/**
 * 获取商品列表
 */
async function getGoodList() {
  const shopIds = shopList.value.map((item) => item?.id)
  const result = await doGetCommodityByShop(shopIds)
  if (result.code !== 200) {
    return uni.showToast({ icon: 'none', title: `${result.msg || '获取商品列表失败'}` })
  }
  const resultData = result.data
  resultData?.forEach((shopResult: any) => {
    const shopId = shopResult.shopId
    const index = shopList.value.findIndex((item) => item?.id === shopId)
    const goodsList = (shopResult?.productSaleVolumes || []).map((product: any) => ({
      id: product.productId,
      name: product.productName,
      logo: product.pic,
      price: divTenThousand(product.productPrice) as unknown as string,
    }))
    if (index > -1) {
      shopList.value[index].goodsList = goodsList
    }
  })
}

/**
 * 初始化数据
 */
const initialData = async (flag = false) => {
  await initialShopList(flag)
  getGoodList()
}

/**
 * 监听位置变化
 */
watch(
  () => getterLocation.value,
  () => {
    initialData(true)
  },
  {
    // #ifndef H5
    immediate: true,
    // #endif
    deep: true,
  },
)

/**
 * 关注店铺
 * @param shopInfo 店铺信息
 */
const focusShop = async (shopInfo: ApiSearchShop) => {
  shopInfo.isFollow = !shopInfo.isFollow
  const { data, code, msg } = await doCancelAttentionAndAttention(shopInfo?.name, shopInfo?.id, shopInfo.isFollow, shopInfo?.logo)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : `关注失败`}`, icon: 'none' })
  uni.showToast({
    title: `${!shopInfo.isFollow ? '已取消' : '已关注'}`,
    icon: 'none',
  })
}

/**
 * 领券
 * @param shop 店铺信息
 */
const handleClickCoupon = async (shop: ApiSearchShop) => {
  const res = await doPostConsumerCollectCoupon(shop.id, shop.couponList[0].id)
  if (res.code !== 200) return uni.showToast({ title: `${res.msg ? res.msg : `领券失败`}`, icon: 'none' })
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${shop?.id}`,
  })
}

/**
 * 只要重新放回到当前页面，就重新加载数据
 */
onShow(async () => {
  await initialShopList(true)
  getGoodList()
})

/**
 * 挂载
 */
onMounted(() => {
  $collectionDispatcherStore.addCommoditySubscriber(initialData)
  $collectionDispatcherStore.addShopSubscriber(initialData)
})

/**
 * 卸载
 */
onUnmounted(() => {
  $collectionDispatcherStore.removeCommoditySubscriber(initialData)
  $collectionDispatcherStore.removeShopSubscriber(initialData)
})
</script>

<style scoped lang="scss">
@include b(shop-container) {
  padding: 20rpx 20rpx 20rpx;
  min-height: 524rpx;
  background-color: #f6f6f6;
}
@include b(shop) {
  font-size: 28rpx;
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  background-color: #fff;

  @include e(title) {
    padding: 32rpx 20rpx 0;
    display: flex;
    .shop-info__name {
      @include flex(space-between, center);
      @include m(main) {
        flex: 1;
        color: #000;
        font-size: 32rpx;
        font-weight: 700;
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
        margin-right: 8rpx;
        flex-shrink: 0;
      }
      @include m(focus) {
        margin-left: 12rpx;
        font-size: 24rpx;
        color: #fff;
        border: 1px solid #f54319;
        background-color: #f54319;
        line-height: 36rpx;
        padding: 6rpx 20rpx;
        border-radius: 74rpx;
        @include flex(center, center);
        display: inline-flex;
        flex-shrink: 0;
        @include m(text) {
          margin-left: 6rpx;
        }
      }
      @include m(cancel) {
        margin-left: 12rpx;
        font-size: 24rpx;
        color: #333;
        border: 1px solid #c9c9c9;
        line-height: 36rpx;
        padding: 6rpx 20rpx;
        border-radius: 74rpx;
        @include flex(center, center);
        display: inline-flex;
        flex-shrink: 0;
        @include m(text) {
          margin-left: 6rpx;
        }
      }
    }
  }
  @include e(title-image) {
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 90rpx;
    height: 90rpx;
    border-radius: 8rpx;
    overflow: hidden;
    flex-shrink: 0;
  }
}

@include b(active) {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 52rpx;
  width: 725rpx;
  border: 1px solid #fcfcfc;
  border-radius: 0 0 16rpx 16rpx;
  background-color: #fff;
  border-top-color: transparent;
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
.more-icon {
  position: absolute;
  right: 10%;
  transform: rotate(90deg);
  transition: all 0.2s;
}
.coupon-list {
  display: flex;
  align-items: center;
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  max-width: 290px;
}
@keyframes fadeInDown_h2 {
  0% {
    -webkit-transform: translate3d(0, -100%, 0);
    transform: translate3d(0, -100%, 0);
  }

  to {
    -webkit-transform: translateZ(0);
    transform: translateZ(0);
  }
}

.coupon-button {
  position: relative;
  border: 0.4px solid rgb(245, 67, 25);
  border-radius: 2px;
  background: rgb(255, 255, 255);
  padding: 2rpx 8rpx;
  overflow: hidden;
  display: inline-block;
  font-size: 20rpx;
  margin-right: 20rpx;
  margin-top: 4rpx;
  color: #f54319;
}

@include b(goods-list) {
  padding: 20rpx;
  white-space: nowrap;
  width: 100%;
  height: 280rpx;
  display: flex;

  @include b(coupon-banner) {
    display: flex;
    flex-direction: column;
    min-width: 23%;
    border-radius: 8rpx;
    color: #fb232f;
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    margin-right: 20rpx;
    background:
      radial-gradient(circle at left bottom, transparent 8px, #fbf1ef 0) top left / 50% 70% no-repeat,
      radial-gradient(circle at left top, transparent 8px, #fbf1ef 0) bottom left / 50% 31% no-repeat,
      radial-gradient(circle at right bottom, transparent 8px, #fbf1ef 0) top right / 50% 70% no-repeat,
      radial-gradient(circle at right top, transparent 8px, #fbf1ef 0) bottom right / 50% 31% no-repeat;

    > div {
      margin-top: 10px;
      font-family: Arial, sans-serif;
      font-weight: 550; // 正常字重
      font-size: 14px;
    }

    .coupon-amount {
      margin-top: 10rpx;
      font-weight: 500;
    }

    .coupon-scope {
      font-family: Arial, sans-serif;
      font-weight: 350; // 正常字重
      padding: 0 3px;
      font-size: 24rpx;
      margin-top: 10rpx;
    }

    .coupon-desc {
      width: 82%;
      // 一行展示超出隐藏
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      font-size: 24rpx;
      margin-top: 10rpx;
      font-weight: 500;
      text-align: center;
    }

    .coupon-btn {
      font-size: 24rpx;
      padding: 2rpx 8rpx;
      background-color: #fb232f;
      color: #fff;
      border-radius: 8rpx;
      font-family: Arial, sans-serif;
      font-weight: 400; // 正常字重
      position: absolute;
      bottom: 15rpx;
    }
    .coupon-xuxian {
      width: 82%;
      height: 1px;
      border-top: 1px dashed #fb232f;
      position: absolute;
      bottom: 72rpx;
    }
  }

  .goods-item {
    width: 153rpx;
    display: flex;
    flex-direction: column;
    margin-right: 20rpx;

    .goods-info {
      flex: 1;
      height: 46rpx;
      display: flex;
      flex-direction: column;
      padding: 0 3rpx;
      .goods-name {
        font-size: 28rpx;
        color: #000;
        overflow: hidden;
        white-space: nowrap;
        font-family: Arial, sans-serif;
        font-weight: 300; // 正常字重
        margin: 9rpx 0 8rpx 0;
      }

      .goods-price {
        font-size: 30rpx;
        color: #fb232f;
        font-family: Arial, sans-serif;
        font-weight: 500; // 正常字重

        .price-prefix {
          font-size: 20rpx;
          margin-right: 2rpx;
        }
      }
    }
  }

  @include e(item) {
    width: 153rpx;
    display: inline-block;
  }
}
</style>
