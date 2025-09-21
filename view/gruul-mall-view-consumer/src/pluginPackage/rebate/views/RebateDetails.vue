<script lang="ts" setup>
import { computed, onMounted, onUnmounted, reactive, ref } from 'vue'
import DateSelect from '@/pluginPackage/rebate/views/components/date-select.vue'
import PopupTitle from '@/components/popup/popup-title.vue'
import { doGetRebateDetailsList, doGetRebateEnabled } from '../apis'
import DateUtil from '@/utils/date'
import { type RebateOrderDetails, RebateTypeColorEnum, RebateTypeEnum } from './types/index'
import { onLoad } from '@dcloudio/uni-app'
import { useRebatePrices } from '../hooks'
import Auth from '@/components/auth/auth.vue'
import useRebateDispatcher from '@/store/dispatcher/useRebateDispatcher'

const $rebateDispatcherStore = useRebateDispatcher()
const { init: rebateInit, formatPrice: rebateFormatPrice } = useRebatePrices()
// const $query = ref<any>({})
const rebateBalanceRef = ref('')

const searchParam = reactive({
  changeType: '',
  dealAggregationType: 'ALL',
  queryTime: `${new Date().getFullYear()}-${new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth() + 1) : new Date().getMonth() + 1}`,
})
const paginationOptions = reactive({
  current: 1,
  totalPage: 0,
})
const rebateDetailStatistic = reactive({
  income: '',
  expenditure: '',
})
const windowHeight = ref(0)
const isShowPopup = ref(false)
const scrollHeight = computed(() => {
  return windowHeight.value - uni.upx2px(272) - uni.upx2px(95)
})
const rebateDetailsData = ref<RebateOrderDetails[]>([])
const { formatPrice } = useRebatePrices()

uni.getSystemInfo({
  success: (res) => {
    const statusBarHeight = res.statusBarHeight || 0
    //  屏幕高度 - 手机状态栏的高度 - 状态栏高度 - NavigationBar高度(  44 px )
    windowHeight.value = res.screenHeight - statusBarHeight - 44
  },
})

const loadMore = () => {
  paginationOptions.current++
  initialData()
}

const handleNavToWithdrawRequestPage = async () => {
  let enabled = false
  try {
    const { code, data } = await doGetRebateEnabled()
    enabled = code === 200 && data!
  } catch (e) {
    //do nothing
  }
  if (enabled) {
    uni.navigateTo({ url: `/pluginPackage/rebate/views/WithdrawRequest` })
    return
  }
  uni.showToast({ title: '消费返利已停用', icon: 'none' })
}
const updateQueryTime = (val: string) => {
  searchParam.queryTime = val
  paginationOptions.current = 1
  initialData()
}
const initialData = async () => {
  // console.log(searchParam.queryTime)
  const queryTimeStack = searchParam.queryTime?.split('-').map((item) => parseInt(item))
  const startTime = new DateUtil().getYMDHMSs(new Date(queryTimeStack?.[0], queryTimeStack?.[1] - 1, 1))
  const endTime = new DateUtil().getYMDHMSs(new Date(new Date(queryTimeStack?.[0], queryTimeStack?.[1], 1).getTime() - 1000))
  // console.log(startTime, endTime)
  const { data, code } = await doGetRebateDetailsList({ startTime, endTime, current: paginationOptions.current })
  if (code === 200 && data) {
    rebateDetailStatistic.expenditure = data?.rebateDetailStatistic?.expenditure
    rebateDetailStatistic.income = data?.rebateDetailStatistic?.income
    if (paginationOptions.current === 1) {
      rebateDetailsData.value = data?.records || []
    } else {
      rebateDetailsData.value = [...rebateDetailsData.value, ...(data?.records || [])]
    }
    paginationOptions.totalPage = Number(data?.total)
  }
}

const getRebateBalance = () => {
  rebateInit().then(({ rebateBalance }) => {
    rebateBalanceRef.value = rebateFormatPrice(rebateBalance)
  })
}
const initData = () => {
  getRebateBalance()
  initialData()
}
onLoad((query) => {
  initData()
})
const rebateDispatcher = () => initData()
onMounted(() => $rebateDispatcherStore.addSubscriber(rebateDispatcher))
onUnmounted(() => $rebateDispatcherStore.removeSubscriber(rebateDispatcher))
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - uni.upx2px(272) - uni.upx2px(95)
})
</script>

<template>
  <view class="card">
    <view class="card__main">
      <view class="card__main-title">
        <text>余额</text>
        <u-icon :size="30" class="card__main-title--icon" color="#999999" name="question-circle" @click="isShowPopup = true"></u-icon>
        <text style="color: #ccc">单笔提现最高￥500元</text>
      </view>
      <view class="card__main-bottom">
        <text class="card__main-bottom--price">{{ rebateBalanceRef || '0.00' }}</text>
        <view class="card__main-bottom--btn" @click="handleNavToWithdrawRequestPage">提现</view>
      </view>
    </view>
  </view>
  <date-select
    :query-time="searchParam.queryTime"
    :rebate-detail-statistic="{ ...rebateDetailStatistic, incomeText: '收入', expenditureText: '支出' }"
    active-color="#fa3534"
    @update:query-time="updateQueryTime"
  />
  <scroll-view :style="{ height: `${height}px` }" scroll-y @scrolltolower="loadMore">
    <view v-for="rebateDetailData in rebateDetailsData" :key="rebateDetailData.id" class="scroll-item">
      <view class="scroll-item__top">
        <view style="padding-right: 50rpx">
          <text :style="'color: ' + rebateDetailData.changeType === 'INCREASE' ? '#fa3534' : '#333'"
            >{{ rebateDetailData.changeType === 'INCREASE' ? '+' : '-' }}{{ formatPrice(rebateDetailData.amount) }}
          </text>
        </view>
        <view style="display: flex; justify-content: center; align-items: center">
          <text :style="'background-color: ' + RebateTypeColorEnum[rebateDetailData.rebateType]" class="scroll-item__top--tag"
            >{{ RebateTypeEnum[rebateDetailData.rebateType] }}
          </text>
        </view>
      </view>
      <view class="scroll-item__bottom">
        <text class="scroll-item__bottom--info">{{ rebateDetailData.rebateName }}</text>
        <text class="scroll-item__bottom--info">{{ rebateDetailData.createTime }}</text>
      </view>
    </view>
  </scroll-view>
  <!-- 说明弹窗 -->
  <u-popup v-model="isShowPopup" border-radius="20" mode="bottom">
    <popup-title title="返利明细说明" @close="isShowPopup = false" />
    <view class="popup-msg"> 1、收：即收入是指用户购买商品获得的返利金额，订单状态为【已完成】才能进行返利结算</view>
    <view class="popup-msg"> 2、支：即支出是指用户购买商品时使用【返利支付】的金额</view>
    <view class="popup-msg"> 3、退：是指用户使用返利支付购买的商品，退款时退还的金额</view>
    <view class="popup-msg"> 4、提：是指提现成功的返利金额</view>
  </u-popup>
  <!-- 说明弹窗 -->
  <Auth />
</template>

<style lang="scss" scoped>
@include b(card) {
  padding: 20rpx;
  height: 272rpx;
  @include e(main) {
    @include flex;
    align-items: flex-start;
    justify-content: space-between;
    flex-direction: column;
    padding: 40rpx;
    height: 100%;
    background: #fff;
    /* 卡片投影 */
    box-shadow: 0px 0px 15rpx rgba(0, 0, 0, 0.25);
    border-radius: 20rpx;
  }
  @include e(main-title) {
    /* 余额 */
    position: relative;
    color: #666666;
    font-size: 25rpx;
    font-weight: 400;
    @include m(icon) {
      // position: absolute;
      // right: -40rpx;
      margin: 0 15rpx 0 10rpx;
    }
  }
  @include e(main-bottom) {
    width: 100%;
    @include flex;
    justify-content: space-between;
    @include m(price) {
      color: #333333;
      font-family: Arial;
      font-size: 56rpx;
      font-weight: 400;
      &::before {
        content: '￥';
        font-size: 20rpx;
      }
    }
    @include m(btn) {
      width: 130rpx;
      height: 64rpx;
      line-height: 64rpx;
      text-align: center;
      /* 启山科技官网 */
      background: #ff794d;
      border-radius: 10rpx;
      color: #fff;
      &:active {
        opacity: 0.7;
      }
    }
  }
}

@include b(scroll-item) {
  /* Frame 201 */
  height: 134rpx;
  background: #fff;
  @include flex;
  flex-direction: column;
  padding: 0 20rpx;
  margin-top: 1rpx;
  @include e(top) {
    width: 100%;
    @include flex;
    justify-content: space-between;
    margin-bottom: 10rpx;
    @include m(tag) {
      /* Tag 标签 */
      display: inline-block;
      color: #fff;
      width: 80rpx;
      height: 40rpx;
      text-align: center;
      font-size: 20rpx;
      line-height: 40rpx;
      border-radius: 6rpx;
      margin-right: 10rpx;
    }
    @include m(shop-name) {
      display: inline-block;
      width: 380rpx;
    }
  }
  @include e(bottom) {
    width: 100%;
    @include flex;
    justify-content: space-between;
    @include m(info) {
      font-size: 24rpx;
      color: #999999;
    }
  }
}

@include b(popup-msg) {
  color: #666;
  font-size: 26rpx;
  padding: 0 20rpx 30rpx 20rpx;
}
</style>
