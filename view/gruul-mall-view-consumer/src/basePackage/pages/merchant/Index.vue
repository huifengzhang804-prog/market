<script lang="ts" setup>
import { onLoad, onShow } from '@dcloudio/uni-app'
import NavTabbar from '@decoration/components/nav-tabbar/nav-tabbar.vue'
import Classification from '@decoration/components/classification/classification.vue'
import Home from '@/basePackage/pages/merchant/components/home.vue'
import { useNavBack } from '@/hooks/useNavBack'
import { doGetEnablePageByType } from '@/apis/decoration/shop'
import { doGetShopInfo } from '@/apis/good'
import { doAddVistor } from '@/apis/sign'
import { useUserStore } from '@/store/modules/user'
import type { LinkType, NavBarType } from '@decoration/components/types'
import O2oShop from './components/o2o-shop/o2o-shop.vue'
import Auth from '@/components/auth/auth.vue'
import { GeometryType } from '@/apis/address/type'
import { storeToRefs } from 'pinia'
import { useLocationStore } from '@/store/modules/location'
import type { GetShopInfoRes } from '@/apis/o2oshop/model'
import { computed, ref, watch } from 'vue'

const { getterLocation } = storeToRefs(useLocationStore())
// 切换导航控制的swiperindex
const contentSwiperId = ref('')
const shopInfo = ref<GetShopInfoRes & { id: Long }>({
  id: '',
  name: '',
  logo: '',
  companyName: '',
  no: '',
  contractNumber: '',
  status: 'NORMAL',
  address: '',
  location: { type: GeometryType.Point, coordinates: getterLocation?.value?.coordinates || [0, 0] },
  briefing: '',
  newTips: '',
  headBackground: '',
  openTime: {
    start: '',
    end: '',
  },
  shopMode: 'COMMON',
  shopType: 'ORDINARY',
  follow: '',
  isFollow: false,
  minLimitPrice: 0,
  distance: 0,
  productNum: 0,
  sales: 0,
  score: '',
})
const shopEnable = computed(() => shopInfo.value.status)
// 控件信息
const controllerInfo = ref()
// 导航信息
const tabbarInfo = ref<NavBarType>({
  codeStyle: 1,
  defaultColor: '',
  selectColor: '',
  menuList: [],
  underfillColor: '',
})
const currentChooseIndex = ref<number>(0)
const currentChoosedId = computed(() => {
  // 初始化页面取默认第一个id
  if (!contentSwiperId.value) {
    if (tabbarInfo.value.menuList && tabbarInfo.value.menuList.length) {
      return tabbarInfo.value.menuList[0].link.id
    } else {
      return '0'
    }
  } else {
    return contentSwiperId.value
  }
})

const initGetShopInfo = async (shopId: Long) => {
  const { code, data } = await doGetShopInfo({
    shopId,
    location: getterLocation?.value?.coordinates.length ? getterLocation?.value : void 0,
    type: 'SHOP_HOME',
  })
  if (code !== 200 || !data) {
    return uni.showToast({
      icon: 'none',
      title: '获取商家信息失败',
    })
  }
  shopInfo.value = { id: shopId, ...data }
  // 此接口如果不返回 data 将导致页面不展示
  if (shopInfo.value.status === 'NORMAL') {
    recordVisitor()
    // o2o 店铺无需底部导航
    if (shopInfo.value.shopMode !== 'O2O') {
      initTabbar(shopId)
    }
  } else if (shopEnable.value === 'FORBIDDEN') {
    uni.showToast({
      title: '店铺不可用',
      icon: 'none',
      success: () => {
        const time = setTimeout(() => {
          uni.navigateBack()
          clearTimeout(time)
        }, 1500)
      },
    })
    return Promise.reject('店铺不可用')
  }
}
onLoad((res) => {
  uni.$emit('updateTitle')
  if (res?.shopId) {
    initGetShopInfo(res.shopId)
  }
})

watch(
  () => getterLocation.value,
  (val) => {
    if (val?.coordinates && shopInfo.value.id) {
      initGetShopInfo(shopInfo.value.id)
    }
  },
  {
    deep: true,
  },
)

onShow(() => {
  uni.$emit('initCarData')
})

/**
 * 底部导航切换回调
 */
const handleChangeTabCall = (callInfo: { index: number; link: LinkType }) => {
  contentSwiperId.value = callInfo.link.id
  currentChooseIndex.value = callInfo.index
}

uni.$on('handleChangeTabCall', handleChangeTabCall)

/**
 * 初始化底部导航
 */
async function initTabbar(shopId: Long) {
  let endpointType = ''
  // #ifdef MP-WEIXIN
  endpointType = 'WECHAT_MINI_APP'
  // #endif
  // #ifndef MP-WEIXIN
  endpointType = 'H5_APP'
  // #endif
  try {
    const { code, data } = await doGetEnablePageByType(shopId, endpointType, 'SHOP_BOTTOM_NAVIGATION_PAGE')
    if (code !== 200 || !data) {
      return useNavBack(1, { msg: '店铺还未装修完成,再逛逛吧' })
    }
    controllerInfo.value = JSON.parse(data.properties)
    tabbarInfo.value = JSON.parse(data.properties).filter((item: { value: string }) => {
      return item.value === 'navBar'
    })[0].formData
  } catch (error) {
    console.error(error)
    uni.showToast({
      icon: 'none',
      title: '店铺暂无配置',
    })
    const time = setTimeout(() => {
      useNavBack()
      clearTimeout(time)
    }, 1000)
  }
}

/**
 * 记录访问数量
 */
async function recordVisitor() {
  if (useUserStore().userInfo.token) {
    await doAddVistor(shopInfo.value.id)
  }
}
</script>

<template>
  <view>
    <!-- 顶部状态栏占位 -->
    <template v-if="shopEnable && shopInfo.shopMode === 'COMMON'">
      <view
        v-for="(item, index) in tabbarInfo.menuList"
        v-show="currentChoosedId === item.link.id && index === currentChooseIndex"
        :key="item.link.id"
        :item-id="item.link.id"
        :style="{ height: '100%' }"
      >
        <Home v-if="item.link.id === '1' && currentChoosedId === item.link.id" :shopInfo="shopInfo" />
        <Classification v-if="item.link.id === '2' && currentChoosedId === item.link.id" :shopInfo="shopInfo" />
      </view>
    </template>
    <NavTabbar
      v-if="shopEnable && shopInfo.shopMode === 'COMMON'"
      :codeStyle="tabbarInfo.codeStyle"
      :currentChooseId="currentChoosedId"
      :defaultColor="tabbarInfo.defaultColor"
      :menuList="tabbarInfo.menuList"
      :underfillColor="tabbarInfo.underfillColor"
      :selectColor="tabbarInfo.selectColor"
      @tabChange="handleChangeTabCall"
    />
    <O2oShop v-else-if="shopEnable && shopInfo.shopMode === 'O2O'" :shopInfo="shopInfo" />
    <view v-else style="height: 100dvh"></view>
    <Auth />
  </view>
</template>

<style lang="scss" scoped>
@include b(merchant) {
  width: 100%;
  height: 300rpx;
  padding: 66rpx 14rpx 0 28rpx;
  box-sizing: border-box;
  position: relative;
  background-repeat: no-repeat;
  z-index: 11;
  background-size: 100% 100%;
  &::after {
    content: ' ';
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    background: rgba(17, 17, 17, 0.2);
    z-index: -1;
  }
  @include e(fir) {
    @include flex(space-between);
  }
  @include e(fir-btn) {
    width: 126rpx;
    height: 50rpx;
    background: #f54319;
    border-radius: 32rpx;
    font-size: 28rpx;
    color: #ffffff;
    line-height: 50rpx;
    text-align: center;
    @include m(active) {
      background: #ccc;
    }
  }
  @include e(fir-info) {
    @include flex();
    @include m(nav) {
      transform: rotate(90deg);
    }
  }
  @include e(fir-info-content) {
    margin-left: 16rpx;
    @include flex();
    @include m(img) {
      width: 108rpx;
      height: 108rpx;
      border-radius: 10rpx;
      margin-right: 10rpx;
    }
    @include m(name) {
      font-size: 24rpx;
      color: #fff;
    }
    @include m(sale) {
      color: #6a6a6a;
      font-size: 16rpx;
      padding: 12rpx 22rpx;
      background-color: #fff;
      border-radius: 24rpx;
      margin-top: 10rpx;
      display: inline-block;
    }
  }
  @include e(sec) {
    width: 100%;
    height: 80rpx;
    @include flex(flex-start);
    background-color: rgba($color: #000000, $alpha: 0.2);
    position: absolute;
    bottom: 0;
    left: 0;
  }
  @include e(sec-search) {
    width: 160rpx;
    height: 50rpx;
    border-radius: 32rpx;
    line-height: 50rpx;
    text-align: center;
    background: rgba(255, 255, 255, 0.15);
    color: #fff;
    margin-left: 28rpx;
  }
}

@include b(tab) {
  @include flex();
  @include e(pane) {
    position: relative;
    padding: 0 20rpx;
    height: 80rpx;
    line-height: 80rpx;
    font-size: 28rpx;
    color: #fff;
    @include m(active) {
      &::after {
        content: '';
        width: 100%;
        height: 4rpx;
        background: #fff;
        border-radius: 2rpx;
        position: absolute;
        bottom: 4rpx;
        left: 0;
      }
    }
  }
}
</style>
