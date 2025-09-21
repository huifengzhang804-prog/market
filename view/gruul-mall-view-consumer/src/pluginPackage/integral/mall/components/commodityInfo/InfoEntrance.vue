<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount, onUnmounted } from 'vue'
import { onHide, onLoad } from '@dcloudio/uni-app'
import HeadNav from '@pluginPackage/integral/mall/components/commodityInfo/head-nav.vue'
import commodityLine from '@pluginPackage/integral/mall/components/commodityInfo/commodityLine.vue'
import commodityBar from '@pluginPackage/integral/mall/components/commodityInfo/commodity-bar.vue'
import STORAGE from '@/utils/storage'
import { doGetIntegralGoodsInfo } from '@pluginPackage/integral/api'
import { doGetDefaultAddress } from '@/apis/address'
import type { DoPostIntegralOrderCreateParameter } from '@pluginPackage/integral/api'
import type { IntegralGoodsInfoType } from '@/apis/plugin/integral/model'
// 领取优惠
import { useUserStore } from '@/store/modules/user'
import Auth from '@/components/auth/auth.vue'
import useAddressDispatcher from '@/store/dispatcher/useAddressDispatcher'
import { GeometryType } from '@/apis/address/type'
const $useAddressDispatcher = useAddressDispatcher()

const { divTenThousand } = useConvert()
const goodInfo = ref<IntegralGoodsInfoType>({
  albumPics: [],
  detail: '',
  createTime: '',
  freightPrice: 0,
  id: '',
  integralPrice: '',
  integralProductAttributes: [],
  name: '',
  pic: '',
  status: 'SELL_ON',
  stock: 0,
  price: '',
  salePrice: '',
  virtualSalesVolume: 0,
})
const popStatus = ref(false)
const receiver = ref<DoPostIntegralOrderCreateParameter['receiver']>({
  area: [] as unknown as [string, string, string?],
  address: '',
  name: '',
  mobile: '',
  location: {
    type: GeometryType.Point,
    coordinates: [0, 0],
  },
})

onLoad(async (res) => {
  uni.$emit('updateTitle')
  if (res) {
    const { goodId, shopId } = res
    initIntegralGoodsInfo(goodId)
    initDefaultAddress()
  }
})

// 页面离开关闭弹窗
onHide(() => {
  popStatus.value = false
})

async function initDefaultAddress() {
  let dataReceiver: DoPostIntegralOrderCreateParameter['receiver'] = {
    area: [] as unknown as [string, string, string?],
    address: '',
    name: '',
    mobile: '',
    location: {
      type: GeometryType.Point,
      coordinates: [0, 0],
    },
  }
  try {
    const { data, code } = await doGetDefaultAddress()
    if (code === 200 && data) {
      const { area, address, name, mobile }: { area: [string, string, string?]; address: string; name: string; mobile: string } = data
      dataReceiver = { ...dataReceiver, area, address, name, mobile }
    } else {
      // uni.showToast({ icon: 'none', title: '获取地址信息失败' })
    }
  } finally {
    receiver.value = dataReceiver
  }
}
onMounted(() => $useAddressDispatcher.addCartSubscriber(initDefaultAddress))
onUnmounted(() => $useAddressDispatcher.removeCartSubscriber(initDefaultAddress))

async function initIntegralGoodsInfo(goodId: Long) {
  const { code, data, msg } = await doGetIntegralGoodsInfo(goodId)
  if (code !== 200) {
    uni.showToast({
      title: `${msg || '获取商品信息失败'}`,
      icon: 'none',
    })
    return
  }
  if (data) {
    initInfo(data).then(() => {
      data.albumPics = swiperList(data)
      goodInfo.value = data
    })
  }
}
const swiperList = (data: IntegralGoodsInfoType) => {
  if (!data.albumPics) {
    return []
  } else if (typeof data.albumPics === 'string') {
    const arr = data.albumPics.split(',')
    return arr
  }
  return data.albumPics
}

// 商品不存在或已下架直接跳页面
async function initInfo(data: IntegralGoodsInfoType) {
  if (data.status !== 'SELL_ON') {
    uni.showToast({
      title: '商品不可用',
      icon: 'none',
      success: () => {
        const time = setTimeout(() => {
          uni.redirectTo({ url: '/basePackage/pages/abnormalGoods/AbnormalGoods' })
          clearTimeout(time)
        }, 1500)
      },
    })
    return Promise.reject('商品不可用')
  }
}
/**
 * 积分兑换
 */
const handleIntegralExchange = () => {
  STORAGE.set(`integralOrderInfo`, goodInfo.value)
  uni.navigateTo({
    url: `/pluginPackage/integral/mall/components/confirmOrder/confirmOrder`,
  })
}
</script>
<template>
  <head-nav id="nav" :good-info="goodInfo" />
  <u-swiper id="swiper" type="image" :list="goodInfo.albumPics" mode="number" height="750" border-radius="0" :autoplay="false" />
  <commodity-line :info="goodInfo" />
  <commodity-bar :integral="goodInfo.integralPrice" @exchange="handleIntegralExchange" />
  <Auth />
</template>

<style lang="scss" scoped>
@include b(spec) {
  box-sizing: border-box;
  padding: 20rpx 30rpx 0 30rpx;
  overflow: hidden;
  @include e(info) {
    width: 100%;
    display: flex;
    box-sizing: border-box;
    align-items: center;
    margin-bottom: 40rpx;
    @include m(image) {
      margin-right: 20rpx;
    }
  }
  @include e(product) {
    @include m(price) {
      font-size: 34rpx;
      font-weight: bold;
      color: #fa5555;
      &::before {
        content: '￥';
        font-weight: normal;
        display: inline-block;
        font-size: 26rpx;
      }
    }
  }
  @include e(product-line) {
    display: flex;
    align-items: flex-end;
    font-size: 26rpx;
    @include m(stock) {
      height: 30rpx;
      line-height: 30rpx;
      margin: 20rpx 20rpx 0 0;
    }
    @include m(limit) {
      color: #fa5555;
    }
  }
  @include e(btn) {
    width: 100%;
    background: #f54319;
    color: #fff;
    text-align: center;
    line-height: 80rpx;
    height: 80rpx;
    border-radius: 60rpx;
    margin: 20rpx 0;
  }
  @include e(number) {
    @include flex(space-between);
  }
}
</style>
