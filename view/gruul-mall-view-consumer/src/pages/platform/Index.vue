<!-- eslint-disable no-redeclare -->
<!-- eslint-disable no-import-assign -->
<script setup lang="ts">
import { storeToRefs } from 'pinia'
import { onLoad, onShareAppMessage } from '@dcloudio/uni-app'
import { computed, ref, provide, defineAsyncComponent } from 'vue'
import NavTabbar from '@/decoration/components/nav-tabbar/nav-tabbar.vue'
import Loading from '@/components/q-loading/q-loading.vue'
import Home from '@/pages/modules/home/home.vue'
// #ifndef H5
// @ts-ignore
import Classification from '@/pages/modules/classification/classification.vue'
// @ts-ignore
import Consumer from '@/pages/modules/consumer/consumer.vue'
// @ts-ignore
import Car from '@/pages/modules/car/car.vue'
import linkNavTo from '@/libs/linkNavTo'
// #endif
// #ifdef H5
const Classification = defineAsyncComponent(() => import('@/pages/modules/classification/classification.vue'))
const Consumer = defineAsyncComponent(() => import('@/pages/modules/consumer/consumer.vue'))
const Car = defineAsyncComponent(() => import('@/pages/modules/car/car.vue'))
const AmapChoose = defineAsyncComponent(() => import('@/basePackage/pages/addressManage/components/AmapChoose.vue'))
// #endif
import Auth from '@/components/auth/auth.vue'
import { useUserStore } from '@/store/modules/user'
import { useStatusBar } from '@/hooks'
import { useSettingStore } from '@/store/modules/setting'
import { constNavBar } from '@/store/modules/setting/state'
import { doGetEnablePageByType } from '@/apis/decoration/platform'
import type { NavBarType, LinkType } from '@decoration/components/types'

// #ifdef MP-WEIXIN
import PrivacySetting from './components/PrivacySetting.vue'
// #endif
import Advertisement from '@/components/advertisement/index.vue'
import HomeBulletFrame from '@/components/homeBulletFrame/index.vue'
import { useAdvStore } from '@/store/modules/adv'
import { useLocationStore } from '@/store/modules/location'
import { locToAddress } from '@/apis/amap'
import { nextTick } from 'vue'

const { getterLocation } = storeToRefs(useLocationStore())

const adv = useAdvStore()

const isPlay = storeToRefs(adv).getOpenAdvIsPlay

const $settingStore = useSettingStore()
const sonComponentChangeIndex = ref()
const shareCode = ref('')
const { tabbarStatus } = storeToRefs($settingStore) // tabbar 栏目状态值
const statusBarHeight = useStatusBar()
const safeHeight = useBottomSafe()
const tabbarNode = computed(() => {
  return safeHeight.value + uni.upx2px(100)
})

// 可视区域滑动高度计算
const scrollViewHeight = computed(() => {
  let height = ''
  // #ifdef APP-PLUS
  height = `${useScreenHeight().value - tabbarNode.value}px`
  // #endif
  // #ifndef APP-PLUS
  height = `${useScreenHeight().value - tabbarNode.value}px`
  // #endif
  console.log('可视区域滑动高度计算height', height)
  return height
})
// 装修控件
const tabbarInfo = ref<NavBarType>(constNavBar)
const currentChoosedTabId = computed(() => {
  return $settingStore.currentSwiperId
})

// 二级页面跳转首页tabbar调用接口
onLoad(async (params) => {
  uni.$emit('updateTitle')
  await initTabbar()
  if (params?.code) shareCode.value = params.code // 分享 code
})

const isRendering = (tabbarId: string, id: string) => {
  return tabbarStatus.value[tabbarId] && tabbarId === id // 是否渲染
}
const consumerRef = ref()
const CarRef = ref()
const handleChangeTabCall = async (e: { index: number; link: LinkType }) => {
  sonComponentChangeIndex.value = null
  useUserStore().GET_ROLE_MENU()
  $settingStore.SET_CUR_SWIPER_ID(e.link.id)
  $settingStore.SET_LOADING(true, e.link.id)
  uni.setNavigationBarTitle({
    title: e.link.name,
  })
  // 点击我的时 调用 count 计数接口
  if (e.link.id === '4') {
    consumerRef.value?.[0]?.initCount?.()
  }
  // 点击购物车时候 刷新购物车
  if (e.link.id === '3') {
    await nextTick()
    CarRef.value?.[0]?.showOn()
  }
}

const handleChangeSwiper = (e: { detail: { currentItemId: string; source: string } }) => {
  console.log(e)
  if (e.detail.source === 'touch') {
    const link = tabbarInfo.value.menuList.filter((item) => Number(item.link.id) === Number(e.detail.currentItemId))[0].link
    if (link.url === '/basePackage/pages/merchant/Index' || link.url === '/') {
      $settingStore.SET_CUR_SWIPER_ID(String(link.id))
      uni.$emit(`${link.name}`)
    } else {
      // #ifdef H5
      import('@/libs/linkNavTo').then(({ default: linkNavTo }) => {
        linkNavTo(link)
      })
      // #endif
      // #ifndef H5
      linkNavTo(link)
      // #endif
    }
  }
}
function changeCurrentSwiperIndex(e: number | null) {
  sonComponentChangeIndex.value = e
}
async function initTabbar() {
  // 协商缓存
  let endpointType = ''
  // #ifdef MP-WEIXIN
  endpointType = 'WECHAT_MINI_APP'
  // #endif
  // #ifndef MP-WEIXIN
  endpointType = 'H5_APP'
  // #endif
  const { code, data } = await doGetEnablePageByType(endpointType, 'BOTTOM_NAVIGATION_PAGE')
  if (code !== 200)
    return uni.showToast({
      icon: 'none',
      title: '获取导航控件失败',
    })
  if (data && data.properties) {
    const formData = JSON.parse(data.properties)[0].formData
    registerTabbar(formData)
    tabbarInfo.value = formData
    console.log('tabbarInfo.value', tabbarInfo.value)
  }

  const safeHeight = ref(0)

  uni.getSystemInfo({
    success: (res) => {
      if (res.safeAreaInsets) {
        safeHeight.value = res.safeAreaInsets.bottom
      }
    },
  })
}
/**
 * 注册首页tab信息
 */
function registerTabbar(navBar: NavBarType) {
  $settingStore.SET_TAB_LIST(navBar)
}
/**
 * 微信小程序分享
 */
// #ifdef MP-WEIXIN
onShareAppMessage(() => ({}))
// #endif
provide('changeCurrentSwiperIndex', changeCurrentSwiperIndex)

// #ifdef MP-WEIXIN
const showPrivacySetting = ref(false)
// #endif

// #ifdef H5
const showChooseMap = ref(false)
uni.$on('showChooseMap', (val: boolean) => {
  showChooseMap.value = val
})
const placeChoose = (e: any) => {
  locToAddress(e)
}
// #endif
</script>
<template>
  <view>
    <!-- 头部占位 -->
    <!-- #ifndef APP-PLUS -->
    <!-- APP待考证是否需要目前看不需要所以暂时注释 -->
    <!-- <q-nav height="0" /> -->
    <!-- #endif -->
    <Loading :loading="$settingStore.loading" />
    <swiper
      v-show="!$settingStore.loading"
      :current-item-id="currentChoosedTabId"
      easing-function="easeInOutCubic"
      class="swiper"
      :style="{ height: `${scrollViewHeight}`, overflowY: 'hidden' }"
      :disable-touch="true"
      :touchable="false"
      @change="handleChangeSwiper"
    >
      <swiper-item
        v-for="item in tabbarInfo.menuList"
        v-show="currentChoosedTabId === item.link.id"
        :key="item.link.id"
        :item-id="String(item.link.id)"
        class="swiper__item"
        @touchmove.stop
      >
        <!-- 用v-if为组件做重新渲染(需要每次点击'我的'时发送 count 的计数请求) -->
        <!-- <div v-if="currentChoosedTabId === item.link.id"> -->
        <Home v-if="isRendering(item.link.id, '1')" key="1" :code="shareCode" />
        <Classification v-if="isRendering(item.link.id, '2')" key="2" />
        <Car v-if="isRendering(item.link.id, '3')" key="3" ref="CarRef" />
        <Consumer v-if="isRendering(item.link.id, '4')" key="4" ref="consumerRef" />
        <!-- </div> -->
      </swiper-item>
    </swiper>
    <!-- <view :style="{ height: `${55 + safeHeight}px` }"></view> -->
    <!-- 底部导航 -->
    <NavTabbar
      :codeStyle="tabbarInfo.codeStyle"
      :defaultColor="tabbarInfo.defaultColor"
      :selectColor="tabbarInfo.selectColor"
      :menuList="tabbarInfo.menuList"
      :underfillColor="tabbarInfo.underfillColor"
      :current-choose-id="currentChoosedTabId"
      @tabChange="handleChangeTabCall"
    />
    <!-- 授权弹窗 -->
    <Auth />
    <!-- #ifdef MP-WEIXIN -->
    <PrivacySetting v-model:show="showPrivacySetting" />
    <!-- #endif -->
    <!-- 开屏广告 -->
    <Advertisement v-if="isPlay" />
    <!-- 首页弹窗 -->
    <HomeBulletFrame />
    <!-- #ifdef H5 -->
    <!-- 地图选点 -->
    <AmapChoose
      v-model:show="showChooseMap"
      :initLocation="[getterLocation?.coordinates?.[0] || [121.489551, 29.936806][0], getterLocation?.coordinates?.[1] || [121.489551, 29.936806][1]]"
      @placeChoose="placeChoose"
    />
    <!-- #endif -->
  </view>
</template>
<style lang="scss" scoped></style>
