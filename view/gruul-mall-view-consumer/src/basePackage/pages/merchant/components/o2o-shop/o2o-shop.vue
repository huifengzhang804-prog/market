<template>
  <view style="padding-bottom: 20rpx; background-color: #fff">
    <view style="position: fixed; top: 0; left: 0; right: 0; background-color: #fff; z-index: 666">
      <q-nav :title="shopInfo.name" />
    </view>
    <view :style="{ height }" />

    <view v-for="(item, index) in decorateList" :key="index">
      <CusSwiper v-if="item.value === 'swiper'" :decoration-properties="item.formData" />
      <view v-if="item.value === 'classification'">
        <view style="padding: 20rpx 28rpx">
          <view class="title">
            <u-image :src="shopInfo.logo" border-radius="12" width="96rpx" height="96rpx" mode="scaleToFill" @click="goToShopInfo" />
            <view style="flex: 1; margin-left: 12rpx">
              <view class="title__name">
                <view style="display: flex; align-items: center">
                  <view class="title__name--text">
                    {{ shopInfo.name }}
                  </view>
                  <text
                    v-if="shopInfo.shopType && shopInfo.shopType !== 'ORDINARY'"
                    class="title__name--tag"
                    :style="{ background: shopInfo.shopType === 'SELF_OWNED' ? '#fd0505' : '#7728f5' }"
                  >
                    {{ shopType[shopInfo.shopType] }}
                  </text>
                </view>
                <view @click="handleConcern">
                  <q-icon v-if="isConcern" name="icon-guanzhu1" size="40rpx" color="red" />
                  <q-icon v-else name="icon-guanzhu-zhihui" size="40rpx" />
                </view>
              </view>
              <view class="title__info">
                月销{{ salesVolumeToStr(shopInfo.sales) }}
                <text style="margin: 0 20rpx">起送￥{{ divTenThousand(shopInfo?.minLimitPrice || 0).toNumber() }}</text>
                <text v-if="shopInfo.distance">距离{{ Number(shopInfo.distance).toFixed(2) }}km</text>
              </view>
            </view>
          </view>
          <view class="tip">
            <view class="tip__text">公告：{{ shopInfo.newTips }}</view>
            <q-icon
              name="icon-wenhao"
              color="#4f4f4f"
              size="28rpx"
              style="width: 30rpx; height: 30rpx; position: relative; top: 1px"
              @click="shippingFeePrompt = true"
            />
          </view>
        </view>
        <view style="border-top: 1rpx solid #c0c0c0; height: 30rpx" />
        <Classification :shopInfo="shopInfo" :decorationProperties="item.formData" />
      </view>
    </view>
    <CarBar ref="carBarRef" :deliveryMoney="divTenThousand(shopInfo?.minLimitPrice || 0).toNumber()" :shopInfo="shopInfo" />
    <u-popup v-model="shippingFeePrompt" mode="bottom" border-radius="20" height="auto" closeable>
      <view class="shipping-fee-prompt">
        <view class="shipping-fee-prompt__title">同城配送说明</view>
        <view class="shipping-fee-prompt__content"> &emsp; &emsp; 同城配送按重量收费时以结算运费为准，因 各商品重量不同列表只展示基础配送费。</view>
      </view>
    </u-popup>
  </view>
</template>

<script setup lang="ts">
import { type PropType, ref, watch, provide, computed } from 'vue'
import CusSwiper from '@decoration/components/swiper/swiper.vue'
import Classification from './classification/classification.vue'
import CarBar from './components/car-bar.vue'
import { doGetEnablePageByType } from '@/apis/decoration/shop/index'
import QNav from '@/components/q-nav/q-nav.vue'
import { useStatusBar } from '@/hooks'
import type { GetShopInfoRes } from '@/apis/o2oshop/model'
import { doCancelAttentionAndAttention } from '@/apis/concern'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'

const { divTenThousand } = useConvert()
const $collectionDispatcherStore = useCollectionDispatcher()
interface ProperType {
  formData: any
  icon: string
  id: string
  label: string
  value: string
}
const props = defineProps({
  shopInfo: {
    type: Object as PropType<GetShopInfoRes & { id: Long }>,
    default: () => ({}),
  },
})
const shippingFeePrompt = ref(false)
let statusBarHeight = useStatusBar()
const { salesVolumeToStr } = useConvert()
const isConcern = ref(props.shopInfo.isFollow)
const height = ref('')

// #ifndef H5
height.value = `${statusBarHeight.value}px`
// #endif

//  #ifdef H5
height.value = `${statusBarHeight.value + 44}px`
// #endif

// 购物车
const carBarRef = ref<InstanceType<typeof CarBar>>()
// 更新购物车数据
const carBarRefresh = () => {
  carBarRef.value?.initCarData()
}
// 传递更新购物车数据方法
provide('refreshCar', carBarRefresh)

const shopType = {
  SELF_OWNED: '自营',
  PREFERRED: '优选',
}

// 列表数据
const decorateList = ref<ProperType[]>([])
/**
 * 商铺首页获取装修数据
 */
async function initDecoration() {
  let endpointType = 'H5_APP'
  // #ifdef MP-WEIXIN
  endpointType = 'WECHAT_MINI_APP'
  // #endif
  // #ifndef MP-WEIXIN
  endpointType = 'H5_APP'
  // #endif
  const { code, data } = await doGetEnablePageByType(props.shopInfo.id, endpointType, 'SHOP_HOME_PAGE')
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取装修失败',
    })
    return
  }
  if (data) {
    decorateList.value = JSON.parse(data.properties)
  }
}

initDecoration()

const goToShopInfo = () => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/StoreInformation?shopId=${props.shopInfo.id}`, // 目标页面的路径
  })
}
/**
 * 关注
 */
const handleConcern = async () => {
  var value = isConcern.value
  const { code, msg } = await doCancelAttentionAndAttention(props.shopInfo.name, props.shopInfo.id, !value, props.shopInfo.logo)
  if (code !== 200)
    return uni.showToast({
      title: `${msg ? msg : `${value ? '取消' : '关注'}失败`}`,
      icon: 'none',
    })
  uni.showToast({
    title: `${value ? '已取消' : '已关注'}`,
    icon: 'none',
  })
  isConcern.value = !value
  $collectionDispatcherStore.updateShopData()
}
</script>

<style scoped lang="scss">
@include b(content) {
  margin: 24rpx;
  height: 100%;
  width: 555rpx;
  box-sizing: border-box;
  @include e(head) {
    padding: 20rpx 0;
    @include flex(flex-start);
    @include m(name) {
      margin-left: 20rpx;
    }
  }
  @include e(item) {
    margin-top: 32rpx;
    @include flex(flex-start);
    align-items: flex-start;
    @include m(label) {
      width: 150rpx;
      flex-shrink: 0;
    }
    @include m(info) {
      @include utils-ellipsis(4);
      color: #666;
    }
    @include m(newTips) {
      color: #f29100;
    }
  }
  @include e(btn) {
    position: fixed;
    bottom: 30rpx;
    left: 50%;
    transform: translateX(-50%);
  }
}
@include b(title) {
  display: flex;
  @include e(name) {
    display: flex;
    justify-content: space-between;
    align-items: center;
    @include m(text) {
      font-size: 32rpx;
      font-weight: 700;
      max-width: 400rpx;
      @include utils-ellipsis(1);
    }
    @include m(tag) {
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
      position: relative;
      top: 2rpx;
    }
  }
  @include e(info) {
    margin-top: 20rpx;
    color: #999;
    font-size: 24rpx;
  }
}

@include b(tip) {
  margin-top: 16rpx;
  color: #ff5c53;
  font-size: 24rpx;
  display: flex;
  @include e(text) {
    flex: 1;
  }
}

@include b(shipping-fee-prompt) {
  padding: 20rpx;
  @include e(title) {
    font-size: 36rpx;
    width: 100%;
    text-align: center;
    margin-top: 10rpx;
  }
  @include e(content) {
    font-size: 28rpx;
    margin-top: 30rpx;
    line-height: 54rpx;
    margin-bottom: 10rpx;
  }
}
</style>
