<script lang="ts" setup>
import QNav from '@/components/q-nav/q-nav.vue'
import TitleMsg from '@/pages/modules/consumer/components/title-msg.vue'
import RetrieveComUnit from '@/components/retrieve-com-unit/retrieve-com-unit.vue'
import { doGetGuessYouLike, doGetUserBalance } from '@/apis/consumer/footprint'
import { useStatusBar } from '@/hooks'
import { useSettingStore } from '@/store/modules/setting'
import type { PayType } from '@/apis/paymentDetail/model'
import { onLoad } from '@dcloudio/uni-app'
import { jumpGoods } from '@/utils/navigateToShopInfo'
import { computed, reactive, ref, watch } from 'vue'

const isConcern = ref(false)
const customStyle = reactive({
  width: '126rpx',
  height: '50rpx',
  color: '#ffffff',
  background: '#F54319',
  marginRight: 0,
})
const likeData = reactive<{ list: any[] }>({
  list: [],
})
const waterFallRef = ref()
const pageConfig = reactive({
  current: 1,
  size: 10,
  pages: 1,
  status: 'nomore',
})
const statusBarHeight = useStatusBar()
const statusTypeHeight = uni.upx2px(200)
const titleHeight = uni.upx2px(92)
const sysInfo = uni.getSystemInfoSync()
const payFrom = ref<PayType>('ORDER')

watch(
  () => isConcern.value,
  (val) => {
    customStyle.background = val ? 'red' : '#F54319'
  },
  { immediate: true },
)

// 计算scroll-view高度
const scrollHeight = computed(() => {
  return `${sysInfo.screenHeight - statusBarHeight.value - statusTypeHeight - titleHeight}px`
})
const $settingStore = useSettingStore()
const goHome = {
  width: '160rpx',
  height: '58rpx',
}
const goHomeMarginRight = {
  marginRight: '44rpx',
}

initGuessYouLike()
onLoad((res = {}) => {
  uni.$emit('updateTitle')
  if (res.payFrom) {
    payFrom.value = res.payFrom
  }
})

const handelNavToHome = () => {
  $settingStore.NAV_TO_INDEX('首页')
}
const handelNavToOrderList = () => {
  uni.redirectTo({
    url: '/pluginPackage/order/orderList/orderList',
  })
}
const handelNavToMember = () => {
  uni.redirectTo({
    url: '/pluginPackage/member/views/MemberCenter',
  })
}
const handleReachBottom = () => {
  initGuessYouLike(true)
}
const handleClickItem = (e: { productId: Long; shopId: Long }) => {
  jumpGoods(e.shopId, e.productId)
}

async function initGuessYouLike(isLoad = false) {
  if (!isLoad) {
    waterFallRef.value?.clear()
    // 刷新
    pageConfig.current = 1
    likeData.list = await getGuessYouLike()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    likeData.list = likeData.list.concat(await getGuessYouLike())
  }
}

async function getGuessYouLike() {
  const { code, data, msg } = await doGetGuessYouLike(pageConfig.current, pageConfig.size)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取猜你喜欢失败'}`, icon: 'none' })
  if (data.current >= data.pages) {
    pageConfig.status = 'nomore'
  } else {
    pageConfig.status = 'loadmore'
  }
  pageConfig.pages = data.pages
  return data.records
}

const handelNavToBalance = async () => {
  const { code, msg, data } = await doGetUserBalance()
  if (code !== 200)
    return uni.showToast({
      title: `${msg ? msg : '获取余额失败'}`,
      icon: 'none',
    })
  uni.redirectTo({
    url: `/basePackage/pages/storedValue/StoredValue?balance=${data}`,
  })
}
</script>

<template>
  <view>
    <!-- #ifdef APP-PLUS -->
    <q-nav bg-color="#fff" height="0" />
    <!-- #endif -->
    <!-- #ifdef MP-WEIXIN -->
    <q-nav bg-color="#fff" title="" />
    <!-- #endif -->
    <view class="paySccuss">
      <view class="paySccuss__prompt-box">
        <q-icon color="red" name="icon-chenggong" size="48rpx" style="margin-right: 24rpx" />
        <text class="paySccuss__prompt">支付成功</text>
      </view>
      <view class="paySccuss__btn-box">
        <u-button :custom-style="{ ...goHomeMarginRight, ...goHome }" shape="circle" size="medium" @click="handelNavToHome">返回首页 </u-button>
        <u-button v-if="payFrom === 'BALANCE'" :custom-style="goHome" shape="circle" size="medium" @click="handelNavToBalance">查看余额 </u-button>
        <u-button v-else-if="payFrom === 'ORDER'" :custom-style="goHome" shape="circle" size="medium" @click="handelNavToOrderList"
          >查看订单
        </u-button>
        <u-button v-else :custom-style="goHome" shape="circle" size="medium" @click="handelNavToMember"> 会员中心 </u-button>
      </view>
    </view>
    <TitleMsg content-value="精品推荐" right-value="您心尖尖上的宝贝" />
    <scroll-view :refresher-threshold="45" :style="{ height: scrollHeight }" scroll-y @scrolltolower="handleReachBottom">
      <u-waterfall v-if="likeData.list.length" ref="waterFallRef" v-model="likeData.list" idKey="productId">
        <template #left="{ leftList }">
          <view v-for="(item, index) in leftList" :key="index" class="water_item" @click="handleClickItem(item)">
            <u-lazy-load :image="item.productAlbumPics" :index="index" border-radius="16" threshold="-450"></u-lazy-load>
            <RetrieveComUnit :good-name="item.productName" :price="[item.lowestPrice]" :sale-volume="Number(item.salesVolume)" />
          </view>
        </template>
        <template #right="{ rightList }">
          <view v-for="(item, index) in rightList" :key="index" class="water_item" @click="handleClickItem(item)">
            <u-lazy-load :image="item.productAlbumPics" :index="index" border-radius="16" threshold="-450"></u-lazy-load>
            <RetrieveComUnit :good-name="item.productName" :price="[item.lowestPrice]" :sale-volume="Number(item.salesVolume)" />
          </view>
        </template>
      </u-waterfall>
      <u-loadmore v-if="likeData.list.length" :status="pageConfig.status" />
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
@include b(paySccuss) {
  background: #fff;
  text-align: center;
  padding: 40rpx 0;
  @include e(prompt-box) {
    font-size: 34rpx;
    @include flex;
  }
  @include e(btn-box) {
    margin-top: 34rpx !important;
  }
  @include e(prompt) {
    font-weight: bold;
    color: #000000;
  }
  @include e(go-home) {
    width: 160rpx;
    height: 58rpx;
    margin-top: 34rpx;
  }
}

@include b(like) {
  @include e(title) {
    font-size: 32rpx;
    font-weight: Bold;
    color: #000000;
    margin: 25rpx 28rpx;
    @include m(left) {
      &::after {
        content: '';
        margin-left: 15rpx;
        padding-left: 15rpx;
        border-left: 1rpx solid #ccc;
        height: 30rpx;
        display: inline-block;
        vertical-align: middle;
      }
    }
    @include m(right) {
      font-size: 24rpx;
      font-weight: Normal;
      color: #949494;
    }
  }
}
</style>
