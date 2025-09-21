<script lang="ts" setup>
import { onHide, onLoad, onShareAppMessage, onShareTimeline } from '@dcloudio/uni-app'

// ====组件引入 start
// 底层轮播图 + 商品详情 组件
import CommodityLine from '@/pluginPackage/goods/commodityInfo/components/commodity-line.vue'
// 购物车等底部栏组件
import CommodityBar from '@/pluginPackage/goods/commodityInfo/components/commodity-bar.vue'
// 商品异常组件
import StockModal from '@/pages/modules/car/components/stock-modal.vue'
// 顶层轮播组件
import SkuCover from '@/pluginPackage/goods/commodityInfo/module-components/sku-cover.vue'
// 商品购买规格弹窗组件
import SkuPopup from '@/pluginPackage/goods/commodityInfo/module-components/sku-popup.vue'
// #ifdef H5
// H5头部组件
import HeadNavH5 from '@/pluginPackage/goods/commodityInfo/components/head-nav/head-nav-h5.vue'
import { getSystemInfo, navDate, reset, selectorQuery } from '@/pluginPackage/goods/commodityInfo/components/head-nav/activeHeightScroll'
// #endif
// #ifndef H5
// 非H5头部组件
import HeadNavOther from '@/pluginPackage/goods/commodityInfo/components/head-nav/head-nav-other.vue'
// #endif
// 自动登录组件
import Auth from '@/components/auth/auth.vue'
// ====组件引入 end

// 分享得积分接口
import { doGetIntegralBehaviorSave } from '@/apis/plugin/integral'
// 我的消息查询接口
import { doGetPigeonMessageMyCount } from '@/apis/consumerSever'

// 类型定义引入
import type { ComOperationType, StorageSku } from '@/pluginPackage/goods/commodityInfo/types'
import type { Shop } from '@/apis/shops/type'
import type { BtnOption } from '@/pluginPackage/goods/commodityInfo/hooks/getBarConfig'

// 底部购买按钮配置
import { getBarConfig } from '@/pluginPackage/goods/commodityInfo/hooks/getBarConfig'

// #ifdef MP-WEIXIN
import { SHARE_HERF } from '@/components/canvas-share/components/useGetQrcode'
// #endif
import { useSettingStore } from '@/store/modules/setting'

// 商品信息 hook
import { useGoodsInfo } from '@/pluginPackage/goods/commodityInfo/hooks/useGoodsInfo'

import storage from '@/utils/storage'
import { computed, onBeforeUnmount, onMounted, provide, reactive, ref, watch } from 'vue'

/**
 * 获取路由信息
 */
const pageQuery = {
  goodId: '',
  shopId: '',
}
onLoad((query) => {
  uni.$emit('updateTitle')
  pageQuery.goodId = query?.goodId.split('?')[0] ? query?.goodId.split('?')[0] : query?.goodId || ''
  pageQuery.shopId = query?.shopId.split(',')[0] ? query?.shopId.split(',')[0] : query?.shopId || ''
  if (query?.shopingCart === 'true') {
    setTimeout(() => {
      handleCommodityBarClick({
        type: 'JOINCART',
        color: '#fff',
        backgroundColor: '#595754',
        text: '加入购物车',
      })
    }, 600)
  }
})

/**
 * 购物车已选几件
 */
const count = ref(1)

/**
 * 商品信息 hooks 解构
 */
const {
  BARGAIN: { handlerBargainSponsor },
  TEAM: { groupInfo, groupIndex },
  SPIKE: { isJoinSecKill, secKillGoodsInfo },
  getParam,
  updateIsCar,
  updateSku,
  setOperation,
  skuGroup,
  goodInfo,
  currentChoosedSku,
  currentGoodsExtraData,
  swiperList,
  productId,
  addParam,
  refreshPage,
  refreshPageFlag,
} = useGoodsInfo()

/**
 * 向子组件响应式抛出商品 useGoodsInfo() 数据及方法
 * 与 useGoodsInfo() 中的 provide('comProvide') 功能相似
 * 猜测可能是因为 setOperation 需要实时向子组件传递从而专门抛出
 * 开发时需注意数据源
 */
provide('comProvideGoodsUse', {
  TEAM: { groupInfo, groupIndex },
  getParam,
  setOperation,
  productId,
  addParam,
})

/**
 * 立即购买按钮模式切换
 * @param {*} computed
 */
const btnOptions = computed(() => {
  const { btnConfig } = getBarConfig()
  let btnOptionsType: keyof typeof btnConfig = 'DEFAULT'
  if (goodInfo.value.activityOpen && goodInfo.value.skuActivity) {
    if (goodInfo.value?.activity?.type === 'SPIKE') {
      btnOptionsType = 'SECONDS_KILL'
    } else if (goodInfo.value?.activity?.type === 'BARGAIN') {
      btnOptionsType = 'BARGAIN'
    } else if (goodInfo.value?.activity?.type === 'TEAM') {
      btnOptionsType = 'GROUP'
    }
  } else {
    btnOptionsType = 'DEFAULT'
  }
  return btnConfig[btnOptionsType]
})

/**
 * 店铺信息
 */
const commodityLineShopInfo = ref<Shop>({
  shopId: '',
  shopLogo: '',
  shopName: '',
  newTips: '',
  status: '',
  shopType: 'ORDINARY' as 'ORDINARY' | 'SELF_OWNED' | 'PREFERRED',
})

/**
 * 页面离开关闭弹窗
 */
onHide(() => {
  setOperation.control = false
  setOperation.type = 'SWITCH'
})

/**
 * 当前sku改变时 切换至相应的轮播图
 */
watch(
  () => currentChoosedSku.value?.image,
  (val) => {
    if (skuGroup.value.skus.length > 1) {
      swiperList.swiperList = [val, ...swiperList.list]
    } else {
      swiperList.swiperList = [...swiperList.list, ...swiperList.swiperList]
    }
  },
)

/**
 * 向子组件更新 setOperation 以及 规格弹窗状态 的方法
 */
const actionFlags = reactive({
  isGroup: false,
  flag: true,
})

/**
 * 打开规格弹窗(立即购买|拼团|规格选择)
 * @param key setOperation键
 * @param value setOperation值
 * @param flag 是否是拼团
 */
function updateSetOperation(key: any, value: any, flag = false) {
  // @ts-ignore
  setOperation[key] = value
  actionFlags.isGroup = flag
}

provide('updateSetOperation', updateSetOperation)

/**
 * 判断是否售罄 & 底部购买等按钮 disabled
 */
const isSellOut = computed(() => {
  const skus = skuGroup.value.skus
  //未加载时不判断
  if (!skus || !skus.length) {
    return false
  }
  if (currentChoosedSku.value.stockType.includes('UNLIMITED')) {
    return false
  }
  return Number(currentChoosedSku.value.stock) <= 0
})

/**
 * 判断是否有物流服务
 */
const isNoShipService = computed(() => {
  return !goodInfo.value?.distributionMode?.length
})

/**
 * 底层轮播图 改变 sku & 顶层轮播图
 */
const handleUpdateSku = (e: StorageSku, skuIndex: number, request = true) => {
  if (request) {
    updateSku(e)
  }
  if (skuIndex === -1) {
    swiperList.currenuIdx = 0
  } else {
    swiperList.currenuIdx = swiperList.mainList.length + skuIndex
  }
}

/**
 * 点击购物车按钮
 */
const handleChooseCar = () => {
  updateIsCar(true)
  actionFlags.flag = true
  setOperation.control = true
}

/**
 * 跳转店铺首页
 */
const handleNavToShop = () => {
  uni.navigateTo({
    url: `/basePackage/pages/merchant/Index?shopId=${pageQuery.shopId}`,
  })
}

/**
 * 跳转客服聊天
 */
const handleNavToConsumerSever = async () => {
  if (!goodInfo.value) return
  // 存入缓存供客服弹窗
  const { productId, name, price, pic } = goodInfo.value
  const params = {
    id: productId,
    name,
    price,
    pic,
  }
  storage.set('cSProduct', params)
  const { shopId, shopLogo, shopName } = commodityLineShopInfo.value
  if (!shopId) return uni.showToast({ title: '暂未获取到商家信息', icon: 'none' })
  // 看看有没有登录先
  await doGetPigeonMessageMyCount()
  uni.navigateTo({
    url: `/basePackage/pages/customerService/CustomerService?shopId=${shopId}&shopLogo=${shopLogo}&shopName=${shopName}`,
  })
}

/**
 * 跳转购物车
 */
const $settingStore = useSettingStore()
const handleNavToGoodsCar = () => {
  $settingStore.NAV_TO_INDEX('购物车')
}

/**
 * 点击立即购买(找人帮砍)
 */
const handleChooseBuy = (type: ComOperationType) => {
  setOperation.type = type
  updateIsCar(false)
  actionFlags.flag = true
  actionFlags.isGroup = false
  setOperation.control = true
}

/**
 * 底部栏按钮点击事件
 */
const handleCommodityBarClick = async (e: BtnOption) => {
  setOperation.loading = true
  setOperation.source = 'COMMON'
  switch (e.text) {
    case '加入购物车':
      handleChooseCar()
      setOperation.loading = false
      break
    case '立即购买':
      handleChooseBuy('BUY')
      setOperation.loading = false
      if (goodInfo.value?.activity?.type !== 'SPIKE') {
        setOperation.immediately = true
      }
      break
    case '找人帮砍':
      if (goodInfo.value.extra) {
        const attr = judgeBargain()
        if (!attr && productAttributesRef.value.length) {
          setOperation.loading = false
          return handleChooseBuy('BARGAIN')
        }
      }
      await handlerBargainSponsor(goodInfo.value.name, productAttributesRef.value)
      setOperation.loading = false
      break
    default:
      setOperation.loading = false
      break
  }
}
const judgeBargain = () => {
  if (goodInfo.value.extra) {
    if (!currentGoodsExtraData.value.currentSpecs?.length) {
      productAttributesRef.value =
        goodInfo.value.extra.productAttributes?.map((item) => ({
          ...item,
          featureValues: [],
        })) || []
    } else {
      productAttributesRef.value = currentGoodsExtraData.value.currentSpecs
    }

    const attr = productAttributesRef.value.some((item: any) => {
      if (!item.isRequired) return true
      if (item.isRequired && !item.featureValues.length) {
        uni.showToast({ title: `属性${item.featureName}为必选属性`, icon: 'none' })
        return false
      }
      return true
    })
    return attr
  }
  return false
}
const productAttributesRef = ref<any[]>([])
const copyBargainLink = () => {
  judgeBargain()
  handlerBargainSponsor(goodInfo.value.name, productAttributesRef.value)
}

/**
 * 计算底部安全高度
 */
const safeHeight = useBottomSafe()
const barBottomHeight = uni.upx2px(100)
const computedBarBottomHeight = computed(() => {
  return safeHeight.value + barBottomHeight + 'px'
})

/**
 * 微信小程序分享
 */
// #ifdef MP-WEIXIN
const weixinShare = (titleFn: () => void) => {
  if (goodInfo.value.shopId && goodInfo.value.productId) {
    doGetIntegralBehaviorSave('SHARE').then((res) => {
      if (res.code === 200 && Number(res.data) > 0) {
        uni.showToast({ title: `分享获得${res.data}积分`, icon: 'none' })
      }
    })
    return titleFn
  } else {
    uni.showToast({
      title: '分享失败',
      icon: 'none',
    })
    return {}
  }
}
onShareAppMessage(() =>
  weixinShare(() => {
    const path = SHARE_HERF(goodInfo.value.shopId, goodInfo.value.productId)
    return {
      from: ['button', 'menu'],
      title: '推荐一个好物给你，请查收！',
      path,
      imageUrl: currentChoosedSku.value.image,
    }
  }),
)
onShareTimeline(() =>
  weixinShare(() => {
    return {
      title: '推荐一个好物给你，请查收！',
      imageUrl: currentChoosedSku.value.image,
    }
  }),
)
// #endif

/**
 * 商品异常处理提醒
 */
const showStockModal = ref(false)
const stockModalData = ref({})
const changeshowStockModal = (res: { success: boolean; data: { title: string; content?: string } }) => {
  showStockModal.value = !res.success
  stockModalData.value = res
}

/**
 * 非H5退出分享
 */
// #ifndef H5
const commodityNavRef = ref()
// #endif
const handleShowMenu = () => {
  // #ifndef H5
  commodityNavRef.value.handleShowMenu()
  // #endif
}

const screenHeight = useScreenHeight()

/**
 * 顶部导航栏渐变动画
 */
const scroll = (e: Obj) => {
  // #ifdef H5
  const query = navDate.nameS.map((item) => selectorQuery(item.id)).flat()
  const index = query.findIndex((item) => item.top >= 0 && item.top <= 100)
  if (index !== -1) {
    navDate.trigger = index
  }
  navDate.styleOpacity = e.detail.scrollTop / 500 > 1 ? 1 : e.detail.scrollTop / 500
  // #endif
}
const scrollIntoView = ref('')
// #ifdef H5
onMounted(() => {
  getSystemInfo()
})
const changeNav = () => {
  scrollIntoView.value = navDate.nameS[navDate.trigger].id.replace('#', '')
}
onBeforeUnmount(() => {
  reset()
})
// #endif
</script>

<template>
  <view v-if="refreshPageFlag">
    <!-- #ifdef H5 -->
    <HeadNavH5 id="nav" :isShowAssess="false" @changeNav="changeNav" />
    <!-- #endif -->
    <!-- #ifndef H5 -->
    <HeadNavOther ref="commodityNavRef" />
    <!-- #endif -->
    <scroll-view :style="{ height: `${screenHeight}px` }" scroll-y scroll-with-animation :scroll-into-view="scrollIntoView" @scroll="scroll">
      <view>
        <!-- 顶层轮播图 -->
        <SkuCover :isSellOut="isSellOut" @showMenu="handleShowMenu" />

        <!-- 底层轮播图 + 商品详情 -->
        <CommodityLine
          :count="count"
          :currentChoosedSku="currentChoosedSku"
          :currentParams="currentGoodsExtraData.currentParams"
          :info="goodInfo"
          :isJoinSecKill="isJoinSecKill"
          :secKillGoodsInfo="secKillGoodsInfo"
          :sku="skuGroup.skus"
          @chooseBuy="handleChooseBuy('SWITCH')"
          @choosedSkuChange="handleUpdateSku"
          @seckillEnd="refreshPage"
          @updateShopInfo="(e) => (commodityLineShopInfo = e)"
        />

        <!-- 购物车等底部栏 -->
        <CommodityBar
          :btnOptions="btnOptions"
          :disabled="isSellOut"
          :noShipService="isNoShipService"
          :loading="setOperation.loading"
          @btnClick="handleCommodityBarClick"
          @chooseShop="handleNavToShop"
          @consumerSever="handleNavToConsumerSever"
          @goodsCar="handleNavToGoodsCar"
        />

        <!-- 底部安全高度 -->
        <view :style="{ height: computedBarBottomHeight }"></view>

        <!-- 规格弹窗 -->
        <SkuPopup
          v-model:currentParams="currentGoodsExtraData.currentParams"
          v-model:currentSpecs="currentGoodsExtraData.currentSpecs"
          v-model:isFreshSpec="actionFlags.flag"
          v-model:isGroup="actionFlags.isGroup"
          :shopInfo="commodityLineShopInfo"
          @changeshowStockModal="changeshowStockModal"
          @copyBargainLink="copyBargainLink"
        />

        <StockModal v-model="showStockModal" :stockModalData="stockModalData" />
      </view>
    </scroll-view>
    <Auth />
  </view>
</template>

<style lang="scss" scoped>
@include b(guahao_index) {
  width: 100%;
  height: 100%;
  overflow: hidden;
}

@include b(spec) {
  box-sizing: border-box;
  padding: 50rpx 36rpx;
  @include e(info) {
    width: 100%;
    display: flex;
    box-sizing: border-box;
    margin-bottom: 40rpx;
  }
  @include e(product) {
    flex: 1;
    margin-left: 10rpx;
    @include m(price) {
      font-size: 38rpx;
      font-weight: bold;
      color: #fa5555;
      &::before {
        content: '￥';
        display: inline-block;
        font-size: 30rpx;
      }
    }
    @include m(space) {
      font-size: 24rpx;
      color: #000000;
      @include utils-ellipsis(3);
    }
  }
  @include e(product-line) {
    font-size: 24rpx;
    font-weight: 400;
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
    margin-top: 20rpx;
    background: linear-gradient(95.47deg, #fa3534 0%, #ff794d 78.13%);
    color: #fff;
    font-size: 28rpx;
    text-align: center;
    line-height: 80rpx;
    height: 80rpx;
    border-radius: 60rpx;
  }
  @include e(number) {
    margin-top: 40rpx;
    @include flex(space-between);
  }
}

:deep(.u-swiper-indicator) {
  padding: 0 20rpx !important;
  bottom: 20rpx !important;
}

:deep(.u-swiper-indicator) {
  justify-content: flex-end !important;
}

:deep(.u-indicator-item-number) {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64rpx;
  width: 64rpx;
  border-radius: 50%;
  padding: 0;
}
</style>
