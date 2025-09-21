<script lang="ts" setup>
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import TitleMsg from '@/pages/modules/consumer/components/title-msg.vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import ConsumerTool from './components/consumer-tool.vue'
// #ifdef H5
import ScanPop from '@/components/scan-pop/scan-pop.vue'
// #endif
import RetrieveComUnit from '@/components/retrieve-com-unit/retrieve-com-unit.vue'
import { useUserStore } from '@/store/modules/user'
import { LIVE_ANCHOR_ROUTER_BLACK_LIST, LIVE_ROUTER_BLACK_LIST } from '@/config/live-icon-https'
import { useAppStore } from '@/store/modules/app'
import type { ApiUserData } from '@/store/modules/user/state'
import { doGetGuessYouLike, doGetMyCount, doGetuserPerson } from '@/apis/consumer/footprint'
import { doGetEnablePageByType } from '@/apis/decoration/platform'
import { doPostBinding } from '@/apis/distribute/index'
import type { UserCenterType } from './types'
import { useMsgCountStore } from '@/store/modules/message'
import { useSettingStore } from '@/store/modules/setting'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { useRebatePrices } from './hooks/useRebatePrices'
import { cropImg } from '@/utils'
import { cloneDeep } from 'lodash'
import useOrderDispatcherStore from '@/store/dispatcher/useOrderDispatcher'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'
import useRebateDispatcher from '@/store/dispatcher/useRebateDispatcher'
import Auth from '@/components/auth/auth.vue'
import { SHOW_LOGIN_MODAL, TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import { doGetUserData } from '@/apis/afs'

const $collectionDispatcherStore = useCollectionDispatcher()
const orderDispatcherStore = useOrderDispatcherStore()
const $rebateDispatcherStore = useRebateDispatcher()
type UserInfoType = Pick<ApiUserData, 'avatar' | 'gender' | 'nickname' | 'userId'>
type GuessYouLike = {
  cIndex: number
  column: number
  index: number
  lowestPrice: string
  o: number
  productAlbumPics: string
  productId: Long
  productName: string
  salesVolume: string
  shopId: Long
}

const InitUserPerson = {
  balance: '0',
  collectCount: '0',
  footprint: '0',
  integral: '0',
  rebate: '0',
}
const InitMyorderCount = {
  uncommented: '0',
  undelivered: '0',
  unhandledAfs: '0',
  unpaid: '0',
  unreceived: '0',
}
const { divTenThousand, salesVolumeToStr } = useConvert()

const pages = getCurrentPages()
const rebateDispatcher = () => initialRebateCount()
onMounted(() => {
  $rebateDispatcherStore.addSubscriber(rebateDispatcher)
})
onUnmounted(() => {
  $rebateDispatcherStore.removeSubscriber(rebateDispatcher)
})
const { SET_LOADING } = useSettingStore()
const $userStore = useUserStore()
const userInfo = computed<UserInfoType>(() => {
  return $userStore.getterToken
    ? $userStore.getterUserInfo?.info
    : ({
        avatar: '',
        gender: 'UNKNOWN',
        nickname: '',
        userId: '',
      } as any)
})
const decorationData = ref<UserCenterType>({
  customStyle: {
    backgroundImage: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220928/ccf24502a08642a8a9a4ed1aab622ebb.jpg',
    cardColor: '#CD7A27',
    textColor: '#F1C8C8',
    infoColor: '#2c2c2c',
    scanCodeColor: '',
    membershipCodeColor: '',
    activateNowColor: '',
    activateNowBtnColor: '',
  },
  getCartText: '',
  headStyle: 1,
  hideCartInlet: 0,
  scanCode: 0,
  membershipCode: 0,
  id: '',
  menuStyle: 1,
  menuList: [],
  menuScratchable: [],
  orderInfo: [
    {
      name: '待付款',
      url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211221/b160c674136f4d9985b5df6aab4b9d66.png',
      link: {
        id: '',
        name: '',
        url: '/pluginPackage/order/orderList/orderList?id=1',
        type: 0,
        append: '',
      },
      key: 'unpaid',
    },
    {
      name: '待发货',
      url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/api/file-read-12014.png',
      link: {
        id: '',
        name: '',
        url: '/pluginPackage/order/orderList/orderList?id=2',
        type: 0,
        append: '',
      },
      key: 'undelivered',
    },
    {
      name: '待收货',
      url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211221/3dd39e45222546d585f0fed7aa83dd82.png',
      link: {
        id: '',
        name: '',
        url: '/pluginPackage/order/orderList/orderList?id=3',
        type: 0,
        append: '',
      },
      key: 'unreceived',
    },
    {
      name: '售后',
      url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20211221/331a0e38aa2945cc9517ca9a820ad64f.png',
      link: {
        id: '',
        name: '',
        url: '/pluginPackage/order/afterSales/AfterSales',
        type: 0,
        append: '',
      },
      key: 'unhandledAfs',
    },
    {
      name: '评价中心',
      url: 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20220122/c636d13705014f6d87e8370e864976c6.png',
      link: {
        id: '',
        name: '',
        url: '/pluginPackage/order/appraise/Appraise',
        type: 0,
        append: '',
      },
      key: 'uncommented',
    },
  ],
})
const userPerson = reactive(cloneDeep(InitUserPerson))
const likeData = reactive<{ list: any[] }>({
  list: [],
})
const $useAppStore = useAppStore()
const { init: rebateInit, formatPrice: rebateFormatPrice } = useRebatePrices()

async function initRebateTransactions() {
  const data = await rebateInit()
  userPerson.rebate = data.rebateBalance
}
const waterFallRef = ref()
const pageConfig = reactive({ current: 1, size: 10, pages: 1, status: 'loadmore' })
const messageCount = ref('0')
const myOrderCount = ref<{
  unpaid: string
  undelivered: string
  unreceived: string
  unhandledAfs: string
  uncommented: string
}>(InitMyorderCount)
const showScan = ref(false)
const isPayMember = computed(() => {
  return decorationData.value.hideCartInlet
})
// 扫码是否展示
const canScan = computed(() => {
  return decorationData.value.scanCode
})
// 用户二维码是否展示
const displayQrCode = computed(() => {
  return decorationData.value.membershipCode
})
const isAnchor = computed(() => $useAppStore.IS_ANCHOR())

let menuButtonInfo = {
  top: 0,
}
// #ifdef MP-WEIXIN
menuButtonInfo = uni.getMenuButtonBoundingClientRect()
// #endif
// #ifdef APP-PLUS
uni.getSystemInfo({
  success: (res) => {
    if (res.statusBarHeight) menuButtonInfo.top = res.statusBarHeight
  },
})
// #endif

watch(
  () => userInfo.value.userId,
  (val) => {
    // 登录状态更改，刷新数据
    initDecoration()
    if (val) {
      initConsumerData()
    } else {
      resetData()
    }
    initGuessYouLike()
  },
)
initDecoration()
initConsumerData()
initGuessYouLike()
updateUserInfo()

// 更新用户信息
function updateUserInfo() {
  doGetUserData().then(({ data }) => {
    useUserStore().ADD_USER_INFO(data)
  })
}

async function initConsumerData() {
  getUserInfo()
  if (userInfo.value.userId) {
    initPigeonMessageMyCount()
    initCount()
  }
}

// 数据重置
function resetData() {
  messageCount.value = '0'
  myOrderCount.value = InitMyorderCount
  Object.keys(InitUserPerson).forEach((key) => {
    const InitUserPersonKey = key as keyof typeof InitUserPerson
    userPerson[InitUserPersonKey] = InitUserPerson[InitUserPersonKey]
  })
  likeData.list = []
  pageConfig.status = 'loadmore'
}

function initPigeonMessageMyCount() {
  const msgCountStore = useMsgCountStore()
  messageCount.value = msgCountStore.getCount
  msgCountStore.$subscribe((mutation, state) => {
    messageCount.value = state.count
  })
}

function initialRebateCount() {
  rebateInit()
    .then(({ rebateBalance }) => {
      userPerson.rebate = rebateFormatPrice(rebateBalance)
    })
    .catch((error) => {
      console.log(error)
    })
}

async function initialPersonCount() {
  try {
    const { code, data } = await doGetuserPerson()
    if (code === 200) {
      Object.keys(InitUserPerson).forEach((key) => {
        const InitUserPersonKey = key as keyof typeof InitUserPerson
        if (InitUserPersonKey !== 'rebate') {
          userPerson[InitUserPersonKey] = data?.[InitUserPersonKey] || InitUserPerson[InitUserPersonKey]
        }
      })
    }
  } catch (error) {
    console.log(error)
  }
}

/**
 * 获取用户
 */
async function getUserInfo() {
  initUserPerson()
  // #ifndef H5
  $userStore.GET_ROLE_MENU()
  // #endif
}

async function initUserPerson() {
  initialRebateCount()
  initialPersonCount()
}

async function initCount() {
  if (!useUserStore().userInfo.token) {
    return
  }
  const { code, data } = await doGetMyCount()
  if (code === 200) {
    myOrderCount.value = data
  }
  initRebateTransactions()
}

onShow(() => {
  initCount()
})
const initialPersonCountDispatcher = () => initialPersonCount()
onMounted(() => {
  $collectionDispatcherStore.addCommoditySubscriber(initialPersonCountDispatcher)
  $collectionDispatcherStore.addShopSubscriber(initialPersonCountDispatcher)
})
onUnmounted(() => {
  $collectionDispatcherStore.removeCommoditySubscriber(initialPersonCountDispatcher)
  $collectionDispatcherStore.removeShopSubscriber(initialPersonCountDispatcher)
})
let count = 0

async function initGuessYouLike(isLoad = false) {
  if (!isLoad) {
    waterFallRef.value?.clear()
    // 刷新
    pageConfig.current = 1
    likeData.list = []
  } else {
    // 下拉更新
    if (pageConfig.current >= pageConfig.pages) return
    pageConfig.current++
  }
  const requestData = (await getGuessYouLike())?.map((item: any) => {
    item.productAlbumPics = cropImg(item.productAlbumPics, 468, 468) // 图片压缩
    item.auniqueKey = count++
    return item
  })
  likeData.list = likeData.list.concat(requestData)
  SET_LOADING(false)
}

async function getGuessYouLike() {
  const { code, data, msg } = await doGetGuessYouLike(pageConfig.current, pageConfig.size)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取猜你喜欢失败'}`, icon: 'none' })
  if (pageConfig.current === data.pages) {
    pageConfig.status = 'nomore'
  }
  pageConfig.pages = data.pages
  return data.records
}

async function initDecoration() {
  let endpointType = ''
  // #ifdef MP-WEIXIN
  endpointType = 'WECHAT_MINI_APP'
  // #endif
  // #ifndef MP-WEIXIN
  endpointType = 'H5_APP'
  // #endif
  const { code, data } = await doGetEnablePageByType(endpointType, 'PERSONAL_CENTER_PAGE')
  if (code !== 200) return uni.showToast({ icon: 'none', title: '获取个人信息失败' })
  if (data && data.properties) {
    const formData = JSON.parse(data.properties)[0].formData
    console.log('formData', formData)
    formData.orderInfo = Object.values(formData.orderInfo)
    decorationData.value = formData
  }
}

/**
 * 导航去我的足迹
 */
const handleGoFootprint = () => {
  handleGoToLandingPageWithToken('/basePackage/pages/footprint/Index')
}
/**
 * 导航去我的收藏
 */
const handleGoMyCollect = () => {
  handleGoToLandingPageWithToken('/basePackage/pages/assessList/AssessList')
}
/**
 * 导航去用户设置
 */
const handleGosetting = () => {
  handleGoToLandingPageWithToken('/basePackage/pages/editUserInfo/EditUserInfo')
}

const handleGoToLandingPageWithToken = (landingPagePath: string) => {
  if ($userStore.getterToken) {
    uni.navigateTo({ url: landingPagePath })
  } else {
    uni.$emit(TOKEN_DEL_KEY)
  }
}

const handleToMembershipCode = () => {
  handleGoToLandingPageWithToken('/basePackage/pages/membershipCode/index')
}
/**
 * 导航去[售后、待付款、待发货、待提货、售后、评价中心]
 * @param {*} path
 */
const handleGoorderList = (path: string) => {
  if (!$userStore.getterToken) return uni.$emit(TOKEN_DEL_KEY)
  if (!path) return
  uni.navigateTo({ url: path })
}
/**
 * 导航去订单列表
 */
const handleGoOrder = () => {
  handleGoToLandingPageWithToken('/pluginPackage/order/orderList/orderList')
}
const handleScroll = () => {
  initGuessYouLike(true)
}
const handleGoStoredValue = () => {
  handleGoToLandingPageWithToken(`/basePackage/pages/storedValue/StoredValue?balance=${userPerson.balance}`)
}
const handleNavToGoodsDetail = (goods: GuessYouLike) => {
  const { shopId, productId } = goods
  jumpGoods(shopId, productId)
}
/**
 * 扫码
 */
const handleScan = () => {
  // if (!$userStore.getterToken) return
  // #ifdef H5
  showScan.value = true
  // #endif
  // #ifndef H5
  uni.scanCode({
    success: async function (res) {
      handleScanResult(res.result)
    },
  })
  // #endif
}
/**
 * 扫码解析回调
 */
const handleScanResult = async (result: string) => {
  showScan.value = false
  bindCode(result)
}
const handleNavToMember = () => {
  uni.navigateTo({
    url: '/pluginPackage/member/views/MemberCenter',
  })
}
const handleAuth = () => {
  uni.$emit(SHOW_LOGIN_MODAL, true)
}

async function bindCode(result: string) {
  try {
    const searchParams = result.split('?')[1]
    const searchParamsArray = searchParams.split('&')
    const strArr: string[] = []
    searchParamsArray.forEach((item) => {
      item.split('=').forEach((item) => strArr.push(item))
    })
    const index = strArr.findIndex((item) => item === 'distributorCode')
    if (index !== -1) {
      const discode = strArr[index + 1]
      const { code, msg } = await doPostBinding(discode)
      if (code && code === 200) {
        uni.showToast({
          icon: 'none',
          title: '绑定分销商成功',
        })
      } else {
        uni.showToast({
          icon: 'none',
          title: `${msg}`,
        })
      }
    }
  } catch (error) {
    uni.showToast({
      icon: 'none',
      title: '绑定分销商失败',
    })
  }
}

const handleNavToRebate = () => {
  handleGoToLandingPageWithToken('/pluginPackage/rebate/views/index')
}
defineExpose({
  initCount,
})
// tabbar 组件高度
const tabbarHeight = uni.upx2px(100)
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - tabbarHeight
})
</script>

<template>
  <scroll-view :scroll-y="true" :style="{ height: `${height}px` }" @scrolltolower="handleScroll">
    <view
      :style="{
        background: decorationData.headStyle === 1 ? 'rgb(254, 78, 99)' : `url('${decorationData.customStyle.backgroundImage}')`,
        backgroundRepeat: 'no-repeat',
        backgroundSize: 'cover',
      }"
      class="bg"
    >
      <!-- 头像部分 -->
      <view :style="{ marginTop: menuButtonInfo.top * 2 + 'rpx' }" class="bg__profile">
        <view class="bg__nickname">
          <u-avatar v-if="$userStore.getterToken" :src="userInfo.avatar" class="bg__box" size="140" @click="handleGosetting"></u-avatar>
          <u-avatar v-else :src="userInfo.avatar" class="bg__box" size="140" @click="handleAuth"></u-avatar>
          <view class="bg__text" :style="{ color: decorationData.headStyle === 1 ? '' : decorationData.customStyle.infoColor }">
            {{ userInfo.nickname }}</view
          >
        </view>
        <view class="bg__setting">
          <q-icon
            v-if="canScan"
            name="icon-saoma"
            size="48rpx"
            :color="decorationData.headStyle === 1 ? '#2c2c2c' : decorationData.customStyle.scanCodeColor"
            @click="handleScan"
          />
          <q-icon
            v-if="displayQrCode"
            name="icon-fenxiaoma"
            size="48rpx"
            :color="decorationData.headStyle === 1 ? '#2c2c2c' : decorationData.customStyle.membershipCodeColor"
            @click="handleToMembershipCode"
          />
        </view>
      </view>
      <!-- 宫格部分s -->
      <u-grid :border="false" :col="4" :style="{ color: decorationData.headStyle === 1 ? '#2c2c2c' : decorationData.customStyle.infoColor }">
        <u-grid-item bg-color="rgba(0,0,0,0)" @click="handleGoStoredValue">
          <text class="consumer__number">{{ salesVolumeToStr(divTenThousand(userPerson.balance)) }}</text>
          <view class="grid-text"> 储值</view>
        </u-grid-item>
        <u-grid-item bg-color="rgba(0,0,0,0)" @click="handleNavToRebate">
          <text>{{ salesVolumeToStr(divTenThousand(userPerson.rebate)) }}</text>
          <view class="grid-text">返利</view>
        </u-grid-item>
        <u-grid-item bg-color="rgba(0,0,0,0)" @click="handleGoMyCollect">
          <text>{{ salesVolumeToStr(userPerson.collectCount) }}</text>
          <view class="grid-text">收藏</view>
        </u-grid-item>
        <u-grid-item bg-color="rgba(0,0,0,0)" class="grid_line" @click="handleGoFootprint">
          <text>{{ salesVolumeToStr(userPerson.footprint) }}</text>
          <view class="grid-text">足迹</view>
        </u-grid-item>
      </u-grid>
      <!-- 会员开通 -->
      <view
        v-if="isPayMember"
        :style="{
          background: decorationData.headStyle === 1 ? 'rgb(69, 64, 60)' : decorationData.customStyle.cardColor,
        }"
        class="memberCard"
      >
        <view
          :style="{ color: decorationData.headStyle === 1 ? 'rgb(228, 203, 152)' : decorationData.customStyle.textColor }"
          class="memberCard__left"
          >尊享会员 | {{ decorationData.getCartText }}
        </view>
        <view
          :style="{
            background: decorationData.headStyle === 1 ? '#E4CB98' : decorationData.customStyle.activateNowBtnColor,
            color: decorationData.headStyle === 1 ? '#fff' : decorationData.customStyle.activateNowColor,
          }"
          class="memberCard__right"
          @click="handleNavToMember"
          >立即开通
        </view>
      </view>
    </view>
    <!-- 宫格部分e  -->
    <view class="orderNav">
      <view class="orderNav__title">
        <view class="orderNav__title--text">
          <text class="bg__order--textLeft" @click="handleGoOrder">我的订单</text>
          <view class="bg__order--textRight" @click="handleGoOrder">
            <view>全部订单</view>
            <q-icon color="#999" name="icon-chevron-right" size="36rpx" />
          </view>
        </view>
      </view>
      <view class="orderNav__grid">
        <u-grid :border="false" :col="decorationData.orderInfo.length">
          <u-grid-item v-for="item in decorationData.orderInfo" :key="item.name" bg-color="#fff" @click="handleGoorderList(item.link.url)">
            <view class="orderNav__grid--item">
              <u-image :height="60" :src="item.url" :width="60" />
              <u-badge :count="myOrderCount[item.key] || 0" class="badge" is-center></u-badge>
            </view>
            <view class="grid-text">{{ item.name }}</view>
          </u-grid-item>
        </u-grid>
      </view>
    </view>
    <!-- #ifdef H5 -->
    <!-- 过滤APP直播 -->
    <ConsumerTool
      :menu-list="decorationData.menuList.filter((item) => !LIVE_ROUTER_BLACK_LIST.includes(item.linkSelectItem.url))"
      :menu-scratchable="decorationData.menuScratchable.filter((item) => !LIVE_ROUTER_BLACK_LIST.includes(item.linkSelectItem.url))"
      :show-style="decorationData.menuStyle"
    />
    <!-- #endif -->
    <!-- #ifndef H5 -->
    <ConsumerTool
      :menu-list="
        isAnchor
          ? decorationData.menuList
          : decorationData.menuList.filter((item) => !LIVE_ANCHOR_ROUTER_BLACK_LIST.includes(item.linkSelectItem.url))
      "
      :menu-scratchable="
        isAnchor
          ? decorationData.menuScratchable
          : decorationData.menuScratchable.filter((item) => !LIVE_ANCHOR_ROUTER_BLACK_LIST.includes(item.linkSelectItem.url))
      "
      :show-style="decorationData.menuStyle"
    />
    <!-- #endif -->
    <TitleMsg content-value="猜你喜欢" right-value="爆品挑选为你倾献" />
    <view class="guessList">
      <u-waterfall v-if="likeData.list.length" ref="waterFallRef" v-model="likeData.list" idKey="productId">
        <template #left="{ leftList }">
          <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleNavToGoodsDetail(item)">
            <u-lazy-load :image="item.productAlbumPics" :index="index" border-radius="16" threshold="-450"></u-lazy-load>
            <RetrieveComUnit :good-name="item.productName" :price="[item.lowestPrice]" :sale-volume="Number(item.salesVolume)" />
          </view>
        </template>
        <template #right="{ rightList }">
          <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleNavToGoodsDetail(item)">
            <u-lazy-load :image="item.productAlbumPics" :index="index" border-radius="16" threshold="-450"></u-lazy-load>
            <RetrieveComUnit :good-name="item.productName" :price="[item.lowestPrice]" :sale-volume="Number(item.salesVolume)" />
          </view>
        </template>
      </u-waterfall>
      <view style="padding: 0.625rem 0 1.0625rem 0; font-size: 0.875rem; color: rgb(96, 98, 102); text-align: center">没有更多了</view>
    </view>
  </scroll-view>
  <!-- 扫码弹窗 -->
  <!-- #ifdef H5  -->
  <ScanPop v-model="showScan" @analysisResult="handleScanResult" />
  <!-- #endif -->
  <Auth v-if="pages[pages.length - 1].route === 'pages/modules/consumer/consumer'" />
</template>
<style lang="scss" scoped>
@include b(water) {
  padding: 0 20rpx;
}

@include b(bg) {
  position: relative;
  padding: 40rpx 25rpx 0 25rpx;
  @include e(profile) {
    @include flex(space-between);
    height: 144rpx;
  }
  @include e(nickname) {
    @include flex;
  }
  @include e(box) {
    height: 144rpx;
  }
  @include e(text) {
    color: #000000;
    height: 34rpx;
    margin-left: 10rpx;
    line-height: 34rpx;
    font-size: 28rpx;
    font-weight: Bold;
    white-space: nowrap;
  }
  @include e(setting) {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 40rpx;
    width: 160rpx;
    padding-right: 10rpx;
    @include m(badge) {
      position: absolute;
      top: -10rpx;
      right: -10rpx;
      width: 30rpx;
      height: 30rpx;
      border: 2rpx solid #ffffff;
      border-radius: 50%;
      font-size: 18rpx;
      color: #ffffff;
      line-height: 26rpx;
      background: #fe2b32;
      text-align: center;
    }
  }
  @include e(order) {
    height: 284rpx;
    background-color: #fff;
    border-radius: 2%;
    margin-bottom: 20rpx;
    @include m(text) {
      @include flex(space-between);
      padding: 0 40rpx;
      height: 116rpx;
      border-bottom: 2rpx solid #f3f3f3;
    }
    @include m(textLeft) {
      font-size: 28rpx;
      font-weight: Bold;
      color: #2c2c2c;
    }
    @include m(textRight) {
      display: flex;
      align-items: center;
      font-size: 24rpx;
      color: #999;
    }
  }
}

@include b(orderNav) {
  margin: 0 25rpx;
  overflow: hidden;
  border-radius: 10rpx;
  @include e(title) {
    background-color: #fff;
    @include m(text) {
      @include flex(space-between);
      padding: 30rpx 40rpx;
    }
  }
  @include e(grid) {
    @include m(item) {
      position: relative;
    }
  }
}

@include b(tool) {
  margin: 0 25rpx;
  background-color: #fff;
  border-radius: 2%;
  @include e(text) {
    @include flex(space-between);
    padding: 0 40rpx;
    font-size: 28rpx;
    color: #2c2c2c;
    height: 100rpx;
    font-weight: Bold;
  }
}

@include b(guessList) {
  padding: 0 28rpx 0rpx 28rpx;
  box-sizing: border-box;
}

@include b(memberCard) {
  padding: 0 30rpx;
  border-radius: 20rpx 20rpx 0 0;
  height: 90rpx;
  @include flex(space-between);
  font-size: 26rpx;
  @include e(right) {
    border-radius: 40rpx;
    width: 160rpx;
    height: 40rpx;
    line-height: 40rpx;
    text-align: center;
    background-color: rgb(228, 203, 152);
    color: rgb(69, 64, 60);
  }
}

.grid-text {
  font-size: 24rpx;
  margin-top: 10rpx;
}

.consumer__number::before {
  content: '￥';
  display: inline-block;
}
</style>
