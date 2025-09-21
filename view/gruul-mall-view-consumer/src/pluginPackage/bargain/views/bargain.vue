<script lang="ts" setup>
import { computed, reactive, ref, watch } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import QIcon from '@/components/q-icon/q-icon.vue'
import Auth from '@/components/auth/auth.vue'
import { EMPTY_GB } from '@/constant'
import StockModal from '@/pages/modules/car/components/stock-modal.vue'
import STORAGE from '@/utils/storage'
import { doGetGproductDelivery, doGetShopInfo } from '@/apis/good'
import { toConfirmOrderValid } from '@/apis/order'
import { useCountdown } from '@/hooks/useCountdown/useCountdown'
import { useUserStore } from '@/store/modules/user'
import { doGetBargainHelpPeopleList, doGetSponsorProductSku, doPostBargainHelpBargain } from '@/pluginPackage/bargain/apis'
import type { DistributionKeyType, TParamsGetGproductDelivery } from '@/apis/good/model'
import type { BARGAIN_SPONSOR_SKU_STATUS, BargainHelpPeopleItemRes, BargainSponsorProductSkuRes } from '@/pluginPackage/bargain/apis/model'
import { Decimal } from 'decimal.js-light'
import useGoodsInfoDispatcher from '@/store/dispatcher/useGoodsInfoDispatcher'
import { storeToRefs } from 'pinia'
import ShopStoreCheck from '@pluginPackage/shopStore/components/shopStoreCheck.vue'
import { SHOW_LOGIN_MODAL } from '@/utils/tokenConfig'

type BargainStatus = keyof typeof BARGAIN_SPONSOR_SKU_STATUS

const $useGoodsInfoDispatcher = useGoodsInfoDispatcher()
const msgPng = 'https://medusa-small-file-1253272780.cos.ap-shanghai.myqcloud.com/gruul/20230320/96bd7605ecdd47e493598550e57da174.png'
const bargainQuery = ref({
  activityId: '',
  productId: '',
  shopId: '',
  skuId: '',
  userId: '',
  bargainOrderId: '',
})
const bargainPopupConfig = reactive({
  showBargainPopup: false,
  msg: '',
  btnText: '我也要参与',
  click: toProduct,
})
const translate = {
  leftMax: 85,
  leftMin: 15,
}
const { divTenThousand } = useConvert()
const bargainInfo = ref<BargainSponsorProductSkuRes>({
  activityId: '',
  amountCut: '',
  endTime: '',
  floorPrice: '',
  productId: '',
  shopId: '',
  skuId: '',
  skuImage: '',
  skuName: '',
  productName: '',
  skuPrice: '',
  userHeadPortrait: '',
  userId: '',
  isSelfBargain: false,
  userNickName: '',
  bargainOrderId: '',
  bargainSponsorSkuStatus: 'IN_PROGRESS',
  stackable: {
    coupon: true,
    full: true,
    vip: true,
  },
})
// 提示语：1、帮砍成功：你一出手就帮 昵称 砍了 XX.XX 元2、已成功：您的好友砍价成功(已下单)3、已售罄：您晚来一步，活动商品已售罄4、已结束：您晚来一步，活动已结束5、已失败：可惜了，商品砍价失败
const countdownBlacklist = ['IN_PROGRESS', 'HELPED']
const skuPrice = computed(() => bargainInfo.value.skuPrice && divTenThousand(bargainInfo.value.skuPrice))
const floorPrice = computed(() => bargainInfo.value.floorPrice && divTenThousand(bargainInfo.value.floorPrice))
const amountCut = computed(() => bargainInfo.value.amountCut && divTenThousand(bargainInfo.value.amountCut))
// 好友帮砍金额
const friendsHelpCutTheAmount = ref('0')
const friendsHelpCutAmount = computed(() => friendsHelpCutTheAmount.value && divTenThousand(friendsHelpCutTheAmount.value))
// 进入页面是否是砍价发起人
const isOriginator = computed(() => {
  return useUserStore().userInfo.info.userId && bargainInfo.value?.userId && useUserStore().userInfo.info.userId === bargainInfo.value?.userId
})
const percent = computed(() => {
  if (bargainInfo.value?.activityId && skuPrice.value && floorPrice.value && amountCut.value) {
    const originalPrice = skuPrice.value.sub(floorPrice.value)
    return amountCut.value.div(divTenThousand(originalPrice).mul(100))
  } else {
    return new Decimal(0)
  }
})

const timeDown = ref({ days: '', hours: '', minutes: '', seconds: '' })
const isInProcess = computed(() => {
  return countdownBlacklist.includes(bargainInfo.value.bargainSponsorSkuStatus)
})
// 是否已经砍价完成
const isFloorPrice = computed(() => {
  if (skuPrice.value && floorPrice.value && amountCut.value) {
    return amountCut.value.greaterThanOrEqualTo(skuPrice.value.sub(floorPrice.value))
  } else {
    return false
  }
})
const pageConfig = reactive({
  size: 5,
  current: 1,
  pages: 1,
})
const helpPeopleList = ref<BargainHelpPeopleItemRes[]>([])
const showStockModal = ref(false)
const stockModalData = ref({})

//配送方式
const distributionMode = reactive({
  //是否展示配送方式选择器
  show: false,
  //当前商品支持的配送方式
  modes: [] as Array<DistributionKeyType>,
  //当前选择的配送方式
  selectedMode: 'EXPRESS' as DistributionKeyType,
  //运费模板 id
  freightTemplateId: '',
  //商品重量
  weight: 0,
})

let params: any
onLoad((res) => {
  params = res
  initBargain(res)
})

/**
 * @: 初始化砍价
 */
const initBargain = async (res: any) => {
  if (res?.extra) {
    try {
      bargainQuery.value = JSON.parse(decodeURIComponent(res.extra))
      await initSponsorProductSku()
      await initHelpPeopleList()
      const $useUserStore = useUserStore()
      const currentUserid = $useUserStore.userInfo.info.userId
      if (!currentUserid) {
        uni.$emit(SHOW_LOGIN_MODAL, true)
        return
      }
      if (bargainInfo.value.bargainSponsorSkuStatus === 'IN_PROGRESS') {
        const { userId, isSelfBargain } = bargainInfo.value || {}
        // 已经登陆的其他人 或 开启了自砍价 直接发起帮砍
        if (userId !== currentUserid || isSelfBargain) {
          await handleHelpBargain()
        }
      }
    } finally {
      $useGoodsInfoDispatcher.updateData()
    }
  }
}

const { userInfo } = storeToRefs(useUserStore())
watch(
  () => userInfo.value.info?.userId,
  (newVal) => {
    if (newVal) initBargain(params)
  },
)

const helpBargainClick = () => {
  if (bargainInfo.value?.productName) {
    const { activityId, productId, shopId, skuId, userId, bargainOrderId } = bargainInfo.value
    const params = {
      activityId,
      productId,
      shopId,
      skuId,
      userId,
      bargainOrderId,
    }
    shareBargainLinks(bargainInfo.value.productName, params)
  }
}

function shareBargainLinks(
  productName: string,
  params: {
    activityId?: Long
    productId?: Long
    shopId?: Long
    skuId?: Long
    userId?: Long
  },
) {
  const { nickname } = useUserStore().userInfo.info
  const baseUrl = import.meta.env.VITE_BASE_URL.replace(/api\//, '')
  const url = `您的好友：${nickname} ，喊你帮忙砍价！！！
        商品名称：${productName} ${baseUrl}h5/#/pluginPackage/bargain/views/bargain?extra=${encodeURIComponent(JSON.stringify(params))}`
  generateCode(url)
}

const generateCode = (data: string) => {
  if (data.length) {
    uni.setClipboardData({
      data,
      showToast: false,
      success: function () {
        uni.showToast({ title: '活动商品链接已复制，邀请好友一起砍价吧！！！', icon: 'none' })
      },
    })
  }
}

async function getShopBaseInfo() {
  if (bargainInfo.value?.shopId) {
    const { data, code } = await doGetShopInfo({ shopId: bargainInfo.value.shopId, type: 'PRODUCT_DETAIL' })
    if (code !== 200 || !data) return
    const { name, logo } = data
    return { shopId: bargainInfo.value.shopId, shopName: name, shopLogo: logo }
  }
}

async function initSponsorProductSku() {
  const { userId: sponsorId, activityId, shopId, productId, skuId, bargainOrderId } = bargainQuery.value
  const { code, data, msg } = await doGetSponsorProductSku({
    sponsorId,
    activityId,
    shopId,
    productId,
    skuId,
    bargainOrderId,
  })
  if (code !== 200) {
    uni.showToast({ title: `${msg || '获取砍价商品信息失败'}`, icon: 'none' })
    return
  }
  if (data) {
    bargainInfo.value = data
  }
  if (isInProcess.value) {
    const { timeSet } = useCountdown(bargainInfo.value.endTime || 0, { immediate: true }, () => {
      console.log('倒计时结束')
      // 刷新页面数据
      initBargain(params)
    })
    timeDown.value = timeSet
  }
  setPopupOption(bargainInfo.value.bargainSponsorSkuStatus)
}

const statusPopupCn: {
  [Key in BargainStatus]: { msg: string; btnText: string; showBargainPopup: boolean; click: () => void }
} = {
  END: {
    msg: '您晚来一步，活动已结束',
    btnText: '确定',
    showBargainPopup: true,
    click: handleClose,
  },
  IN_PROGRESS: {
    msg: '',
    btnText: '我也要参与',
    showBargainPopup: false,
    click: handleClose,
  },
  FAILED_TO_BARGAIN: {
    msg: '可惜了，商品砍价失败',
    btnText: '确定',
    showBargainPopup: true,
    click: handleClose,
  },
  SUCCESSFUL_BARGAIN: {
    msg: `您${isOriginator.value ? '' : '的好友'}砍价成功（已下单)`,
    btnText: '我也要参与',
    showBargainPopup: true,
    click: toProduct,
  },
  HELPED: {
    msg: '您已帮好友砍过啦',
    btnText: '我也要参与',
    showBargainPopup: true,
    click: toProduct,
  },
  SOLD_OUT: {
    msg: `您晚来一步，活动商品已抢光`,
    btnText: '确定',
    showBargainPopup: true,
    click: handleClose,
  },
}

function setPopupOption(key: BargainStatus) {
  const { msg, btnText, showBargainPopup } = statusPopupCn[key]
  bargainPopupConfig.msg = msg
  bargainPopupConfig.btnText = btnText
  bargainPopupConfig.showBargainPopup = showBargainPopup
}

function handleClose() {
  bargainPopupConfig.showBargainPopup = false
}

/**
 * 跳转至商品详情
 */
function toProduct() {
  const { productId, shopId } = bargainInfo.value
  uni.redirectTo({
    url: `/pluginPackage/goods/commodityInfo/InfoEntrance?shopId=${shopId}&goodId=${productId}`,
  })
}

/**
 * 帮好友砍价
 */
async function handleHelpBargain() {
  const { nickname: userNickName, avatar: userHeadPortrait } = useUserStore().userInfo.info
  const { productId, activityId, shopId, userId: sponsorId } = bargainInfo.value
  const { bargainOrderId } = bargainQuery.value
  const param = {
    sponsorId,
    shopId,
    productId,
    activityId,
    userHeadPortrait,
    userNickName,
  }
  const { code, data, msg } = await doPostBargainHelpBargain({ ...param, bargainOrderId })
  if (code !== 200) {
    await uni.showToast({ duration: 2500, title: `${msg || '帮砍失败'}`, icon: 'none' })
    return
  }
  if (data) {
    friendsHelpCutTheAmount.value = data
  }
  if (+friendsHelpCutTheAmount.value > 0) {
    bargainPopupConfig.msg = ''
    bargainPopupConfig.showBargainPopup = true
    setTimeout(() => {
      initSponsorProductSku()
    }, 1500)
  }
  await initHelpPeopleList()
}

/**
 * 请求帮砍列表
 */
async function initHelpPeopleList(isLoad = false) {
  // 无砍价信息不去请求列表接口
  if (!bargainInfo.value?.activityId) return
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    helpPeopleList.value = await getBargainHelpPeopleList()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    helpPeopleList.value = helpPeopleList.value.concat(await getBargainHelpPeopleList())
  }
}

async function getBargainHelpPeopleList() {
  if (!bargainInfo.value?.activityId) return []
  const { userId: sponsorId, activityId, productId, shopId } = bargainInfo.value
  const params = {
    sponsorId,
    shopId,
    productId,
    activityId,
    bargainOrderId: bargainQuery.value.bargainOrderId,
  }
  const { code, data, msg } = await doGetBargainHelpPeopleList({ ...params, ...pageConfig })
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取帮砍好友列表失败'}`, icon: 'none' })
    return []
  }
  if (!data) {
    return []
  }
  pageConfig.pages = data.pages
  return data.records
}

//选择 配送方式
const selectDistributionMode = async () => {
  let modes = distributionMode.modes
  if (!modes.length) {
    const { shopId, productId, skuId } = bargainInfo.value
    if (!shopId || !productId || !skuId) return
    const data = await getProductDelivery([{ shopId, productId, skuId }])
    const current = data[0]
    if (!current) {
      return
    }
    modes = current.distributionMode
    distributionMode.modes = modes
    distributionMode.selectedMode = modes[0]
    distributionMode.freightTemplateId = current.freightTemplateId
    distributionMode.weight = current.weight
  }
  if (modes.length === 1) {
    //如果只有一种配送方式 直接购买
    await buyNow()
    return
  }
  distributionMode.show = true
}
/**
 * 立即购买
 */
const buyNow = async () => {
  const shop = await getShopBaseInfo()
  if (!shop) return
  const {
    skuId,
    skuImage: image,
    skuPrice: price,
    floorPrice: salePrice,
    productId,
    skuName,
    activityId,
    stackable,
    productAttributes,
    productName,
  } = bargainInfo.value
  if (!productId || !skuId) return

  const params = {
    id: productId,
    skuId,
    image,
    price,
    salePrice,
    productId,
    productName,
    freightTemplateId: distributionMode.freightTemplateId,
    weight: distributionMode.weight,
    productFeaturesValue: productAttributes ?? [],
    distributionMode: distributionMode.modes,
    num: 1,
    specs: skuName ? skuName.split(',') : [],
  }
  let activityParam = {
    productId,
    type: 'BARGAIN',
    activityId,
    extra: {},
    stackable,
  }
  const param = [{ activityParam, products: [params], distributionMode: distributionMode.selectedMode, ...shop }]
  // 提交前做校验 doPostOrderValid
  const orderValidResp = await toConfirmOrderValid(param)
  if (!orderValidResp.success) {
    stockModalData.value = orderValidResp
    showStockModal.value = true
    return
  }
  STORAGE.set(`commodityInfo`, param)
  await uni.navigateTo({
    url: `/pluginPackage/order/confirmOrder/confirmOrder?source=PRODUCT`,
  })
}

/**
 * 获取物流模板 id 商品重量
 * @param {*} params
 */
async function getProductDelivery(params: TParamsGetGproductDelivery[]) {
  const { code, data, msg } = await doGetGproductDelivery(params)
  if (code !== 200 || !data) {
    await uni.showToast({ title: `${msg || '查询运费相关信息失败'}`, icon: 'none' })
    return
  }
  return data
}

const formatLeft = () => {
  const { value } = percent
  const { leftMin, leftMax } = translate
  if (value.toNumber() < leftMin) {
    return leftMin
  }
  if (value.toNumber() > leftMax) {
    return leftMax
  }
  return value.toNumber()
}
const formatTriangleLeft = () => {
  const { value } = percent
  const { leftMin, leftMax } = translate
  if (value.toNumber() < leftMin) {
    return 10
  }
  if (value.toNumber() > leftMax) {
    return 90
  }
  return 50
}

// 跳转商品详情
const toGoodsDetail = () => {
  const { productId, shopId } = bargainInfo.value
  if (!productId && !shopId) return
  let url = `/pluginPackage/goods/commodityInfo/InfoEntrance?goodId=${productId}&shopId=${shopId}`
  uni.navigateTo({
    url,
  })
}
</script>

<template>
  <view class="bargain_box" style="background: #fc9536">
    <view class="bargain_info">
      <!-- 用户 -->
      <view class="bargain_info__user">
        <view class="bargain_info__user_left">
          <u-image :src="bargainInfo.userHeadPortrait" height="80rpx" shape="circle" width="100%"></u-image>
        </view>

        <view class="bargain_info__user_right">
          <text class="bargain_info__user_right--name f12">{{ bargainInfo.userNickName }}</text>
          <text class="bargain_info__user_right--describe f14">我想要这个商品，快帮我砍价吧!</text>
        </view>
      </view>
      <!-- 用户 -->
      <!-- 倒计时 -->
      <view class="count_down_box">
        <view class="count_down_box__body">
          <text class="count_down_box__body--text">砍价倒计时</text>
          <text class="count_down_box__body--time">{{ timeDown.hours || '00' }}</text>
          :
          <text class="count_down_box__body--time">{{ timeDown.minutes || '00' }}</text>
          :
          <text class="count_down_box__body--time">{{ timeDown.seconds || '00' }}</text>
        </view>
      </view>
      <!-- 倒计时 -->
      <!-- 商品 -->
      <view class="goods" @click="toGoodsDetail">
        <view class="goods__left">
          <u-image :src="bargainInfo.skuImage" height="180" mode="aspectFit" width="180"></u-image>
        </view>
        <view class="goods__right">
          <view class="goods__right--name fontBold">{{ bargainInfo.productName }}</view>

          <view class="goods__right--spec f13"> {{ bargainInfo.skuName }}</view>

          <view class="goods__right_footer">
            <view class="goods__right_footer--floorPrice">
              底价
              <text class="f20">{{ floorPrice }}</text>
              元
            </view>
            <view class="goods__right_footer--price f12"> 原价：{{ skuPrice }}元</view>
          </view>
        </view>
      </view>
      <!-- 商品 -->
      <!-- 进度条 -->
      <view class="progress_bar">
        <view class="progress_bar__container">
          <u-line-progress
            :height="25"
            :percent="percent"
            :show-percent="false"
            :striped="true"
            :striped-active="true"
            active-color
            inactive-color="#FFCCDD"
            type="warning"
          />
          <!-- left 最大范围 85 -->
          <!-- left 最小范围 15 -->
          <view :style="{ left: `${formatLeft()}%`, transform: 'translateX(-50%)' }" class="progress_bar__msg f12">
            已砍{{ amountCut }}元
            <text :style="{ left: `${formatTriangleLeft()}%` }" class="progress_bar__msg--triangle"></text>
          </view>
        </view>
        <view class="progress_bar__price f12">
          <text>原价{{ skuPrice }}元</text>
          <text class="progress_bar__price--footerPrice">底价{{ floorPrice }}元</text>
        </view>
      </view>
      <!-- 进度条 -->
      <!-- 按钮 -->
      <!-- {{ isOriginator }}
{{ isFloorPrice }}
{{ bargainInfo.bargainSponsorSkuStatus }} -->
      <view
        v-if="isOriginator && isFloorPrice && bargainInfo.bargainSponsorSkuStatus === 'IN_PROGRESS'"
        class="btn_box f22"
        @click="selectDistributionMode"
      >
        <view class="btn_box__btn"> 立即购买</view>
      </view>
      <view v-if="!isFloorPrice" class="btn_box f22" @click="helpBargainClick">
        <view class="btn_box__btn"> 找人帮砍</view>
      </view>
      <!-- 按钮 -->
    </view>
    <!-- 帮砍列表 -->
    <view class="bargain_info bargain_list">
      <view class="bargain_list__title">
        <text class="f22">已砍价好友</text>
        <text class="bargain_list__title--round round_one left_one"></text>
        <text class="bargain_list__title--round round_tow left_tow"></text>
        <text class="bargain_list__title--round round_three left_three"></text>
        <text class="bargain_list__title--round round_three right_three"></text>
        <text class="bargain_list__title--round round_tow right_tow"></text>
        <text class="bargain_list__title--round round_one right_one"></text>
      </view>
      <!-- 列表项 -->
      <scroll-view scroll-y style="height: 410rpx" @scrolltolower="initHelpPeopleList(true)">
        <u-empty v-if="!helpPeopleList.length" :src="EMPTY_GB.EVALUATION_EMPTY" text="暂无帮砍记录"></u-empty>
        <template v-else>
          <view v-for="(item, index) in helpPeopleList" :key="index" class="bargain_list__item">
            <view class="bargain_list__item_left">
              <view class="bargain_list__item_left--image">
                <u-image :src="item.userHeadPortrait" height="100%" shape="circle" width="100%" />
              </view>
              <text class="bargain_list__item_left--text f16">{{ item.userNickName }}</text>
            </view>
            <view class="bargain_list__item_right f16"> 帮忙砍掉{{ item.helpCutAmount && divTenThousand(item.helpCutAmount) }}元 </view>
          </view>
        </template>
      </scroll-view>
      <!-- 列表项 -->
    </view>
    <!-- 帮砍列表 -->
    <!-- 弹窗 -->
    <u-popup v-model="bargainPopupConfig.showBargainPopup" :mask-custom-style="{ background: 'rgba(0, 0, 0, 0.3)' }" border-radius="30" mode="center">
      <view class="msg_box">
        <view v-if="isOriginator && isFloorPrice && !bargainPopupConfig.msg" class="msg_box__img">
          <image :src="msgPng" style="width: 100%; height: 100%"></image>
        </view>
        <view class="msg_box__bg">
          <view class="msg_box__body">
            <view v-if="bargainPopupConfig.msg" class="msg_box__body_dateil f18"> {{ bargainPopupConfig.msg }}</view>
            <view v-else class="msg_box__body_dateil">
              你一出手就帮
              <text>{{ bargainInfo.userNickName }}</text>
              砍了
              <text class="msg_box__body--price fontBold">
                {{ friendsHelpCutAmount }}
              </text>
              元
            </view>
            <view class="btn_box__btn" @click="bargainPopupConfig.click"> {{ bargainPopupConfig.btnText }}</view>
          </view>
        </view>
        <view class="msg_box__close_icon">
          <q-icon color="#fffffe" name="icon-icon-close1" size="35px" @click="handleClose" />
        </view>
      </view>
    </u-popup>
    <!-- 弹窗 -->
  </view>
  <!--  选择配送方式  -->
  <u-popup v-model="distributionMode.show" border-radius="14" closeable mode="center">
    <view style="padding: 20rpx">
      <ShopStoreCheck v-model:curDistributionMode="distributionMode.selectedMode" :list="distributionMode.modes" />
      <button class="spec__btn" @click="buyNow">确认</button>
    </view>
  </u-popup>
  <StockModal v-model="showStockModal" :stockModalData="stockModalData" />
  <Auth />
</template>

<style lang="scss" scoped>
$textC: #fd1616;
$bgC: #c9762a;
$btnBgC: #ffb444;
.f20 {
  font-size: 40rpx;
}

.f16 {
  font-size: 32rpx;
}

.f18 {
  font-size: 36rpx;
}

.spec__btn {
  margin-top: 40rpx;
  background: linear-gradient(95.47deg, #fa3534 0%, #ff794d 78.13%);
  color: #fff;
  font-size: 28rpx;
  text-align: center;
  line-height: 80rpx;
  height: 80rpx;
  border-radius: 60rpx;
}

@include b(msg_box) {
  position: relative;
  width: 85vw;
  background: #fc9536;
  .f22 {
    font-size: 44rpx;
  }
  @include e(img) {
    position: absolute;
    width: 500rpx;
    height: 250rpx;
    top: -200rpx;
    left: 50%;
    transform: translateX(-50%);
  }
  @include e(bg) {
    height: 350rpx;
    background: linear-gradient(0deg, #f6aa4d, #facd8b);
    padding: 10rpx 15rpx;
    border-radius: 10rpx;
  }
  @include e(body) {
    height: 100%;
    border-radius: 10rpx;
    background: #fbe9d1;
    @include flex;
    flex-direction: column;
    justify-content: space-evenly;
    @include m(price) {
      color: #fe273b;
    }
  }
  @include e(body_dateil) {
    padding-top: 50rpx;
    color: #8f6250;
    font-weight: 600;
  }
  @include e(close_icon) {
    position: absolute;
    bottom: -80rpx;
    left: 50%;
    transform: translateX(-50%);
  }
}

@include b(bargain_box) {
  padding: 20rpx 30rpx;
  @include e(top_link) {
    @include flex();
    justify-content: space-between;
    color: #fff;
    font-size: 14rpx;
    @include m(home) {
      background-color: $bgC;
      border-radius: 20rpx;
      padding: 5rpx 25rpx;
      padding-left: 10rpx;
    }
    @include m(home-text) {
      margin-left: 10rpx;
    }
    @include m(rules) {
      border-radius: 20rpx;
      padding: 5rpx 25rpx;
      background-color: $bgC;
    }
  }
}

@include b(bargain_info) {
  background: #fff;
  border-radius: 50rpx;
  padding: 20rpx;
  margin-top: 20rpx;
  @include e(user) {
    @include flex();
    justify-content: flex-start;
  }
  @include e(user_left) {
    height: 80rpx;
    width: 80rpx;
  }
  @include e(user_right) {
    height: 80rpx;
    @include flex();
    flex-direction: column;
    justify-content: space-between;
    align-items: flex-start;
    margin-left: 25rpx;
    @include m(name) {
      color: #f27239;
    }
    @include m(describe) {
      color: #434242;
    }
  }
}

@include b(count_down_box) {
  margin: 30rpx;
  @include flex;
  @include e(body) {
    line-height: 80rpx;
    text-align: center;
    width: 500rpx;
    height: 80rpx;
    background: #f7f7f7;
    border-radius: 50rpx;
    @include m(text) {
      margin-right: 15rpx;
    }
    @include m(time) {
      background: #1b1b1d;
      padding: 5rpx;
      color: #fff;
    }
  }
}

@include b(goods) {
  padding: 10rpx;
  @include flex();
  justify-content: flex-start;
  background: #f7f7f7;
  border-radius: 20rpx;
  @include e(left) {
    height: 180rpx;
    width: 180rpx;
    flex-shrink: 0;
  }
  @include e(right) {
    margin-left: 15rpx;
    height: 180rpx;
    @include flex();
    flex-direction: column;
    justify-content: space-around;
    align-items: flex-start;
    @include m(name) {
      color: #6d6d6d;
    }
    @include m(spec) {
      color: #999;
    }
  }
  @include e(right_footer) {
    @include flex();
    justify-content: flex-start;
    align-items: flex-end;
    @include m(floorPrice) {
      color: $textC;
      margin-right: 50rpx;
      & > text {
        margin-left: 15rpx;
      }
    }
    @include m(price) {
      color: #b6b6b5;
      margin-bottom: 2rpx;
    }
  }
}

@include b(progress_bar) {
  margin-top: 100rpx;
  padding-bottom: 20rpx;
  @include e(container) {
    position: relative;
  }
  @include e(msg) {
    background: #fa731e;
    text-align: center;
    width: 200rpx;
    color: #fff;
    position: absolute;
    top: -60rpx;
    height: 40rpx;
    line-height: 40rpx;
    border-radius: 10rpx;
    z-index: 11;
    @include m(triangle) {
      content: '';
      position: absolute;
      bottom: -20rpx;
      left: 50%;
      transform: translateX(-50%);
      display: inline-block;
      border-top: 15rpx solid #fa731e;
      border-right: 10rpx solid transparent;
      border-bottom: 10rpx solid transparent;
      border-left: 10rpx solid transparent;
    }
    /* &::before {
content: '';
position: absolute;
bottom: -20rpx;
left: 50%;
transform: translateX(-50%);
display: inline-block;
border-top: 15rpx solid #fa731e;
border-right: 10rpx solid transparent;
border-bottom: 10rpx solid transparent;
border-left: 10rpx solid transparent;
} */
  }
  @include e(price) {
    @include flex;
    justify-content: space-between;
    @include m(footerPrice) {
      color: $textC;
    }
  }
}

@include b(btn_box) {
  margin: 30rpx;
  @include flex();
  @include e(btn) {
    font-family: KaiTi;
    width: 85%;
    border-radius: 50rpx;
    height: 80rpx;
    background: linear-gradient(0deg, #ff8738, #ffcd5e);
    text-align: center;
    line-height: 80rpx;
    color: #ffff;
    font-weight: bold;
  }
}

@include b(bargain_list) {
  @include e(title) {
    position: relative;
    font-family: KaiTi;
    font-weight: bold;
    color: #ef601f;
    text-align: center;
    @include m(round) {
      background: #fff2ea;
      border-radius: 50%;
    }
  }
  @include e(item) {
    @include flex;
    justify-content: space-between;
    margin: 0 30rpx;
    padding: 20rpx 0;
    border-bottom: 1px solid #f3f2f3;
  }
  @include e(item_left) {
    @include flex;
    justify-content: flex-start;
    @include m(image) {
      width: 60rpx;
      height: 60rpx;
      border: 1px solid #ccc;
      border-radius: 50%;
      margin-right: 20rpx;
    }
    @include m(text) {
      color: #855a2a;
    }
  }
  @include e(item_right) {
    color: #f2753c;
  }
}

@include b(round_one) {
  position: absolute;
  width: 10rpx;
  height: 10rpx;
}

@include b(round_tow) {
  position: absolute;
  width: 15rpx;
  height: 15rpx;
}

@include b(round_three) {
  position: absolute;
  width: 25rpx;
  height: 25rpx;
}

@include b(left_one) {
  left: 130rpx;
  top: 50%;
  transform: translateY(-50%);
}

@include b(right_one) {
  right: 130rpx;
  top: 50%;
  transform: translateY(-50%);
}

@include b(left_tow) {
  left: 155rpx;
  top: 60%;
  transform: translateY(-50%);
}

@include b(right_tow) {
  right: 155rpx;
  top: 60%;
  transform: translateY(-50%);
}

@include b(left_three) {
  left: 180rpx;
  top: 50%;
  transform: translateY(-50%);
}

@include b(right_three) {
  right: 180rpx;
  top: 50%;
  transform: translateY(-50%);
}

:deep(.u-mode-center-box) {
  overflow: unset !important;
}

:deep(.u-drawer__scroll-view > .uni-scroll-view > .uni-scroll-view) {
  overflow: unset !important;
}
</style>
