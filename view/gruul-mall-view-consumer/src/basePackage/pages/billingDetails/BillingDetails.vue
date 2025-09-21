<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import ConditionsChecked from '@/basePackage/pages/billingDetails/components/conditions-checked.vue'
import DateSelect from '@/basePackage/pages/billingDetails/components/date-select.vue'
import TableDetail from '@/basePackage/pages/billingDetails/components/table-detail.vue'
import DataPicker from '@/basePackage/pages/billingDetails/components/data-picker.vue'
import { doGetPaymentHistory } from '@/apis/paymentDetail'
import QIcon from '@/components/q-icon/q-icon.vue'
import { useStatusBar, useSelector } from '@/hooks'
import type { BillingDetailsItem, DealAggregationType, UserPaymentHistoryStatisticsList } from '@/basePackage/pages/billingDetails/types'
import Auth from '@/components/auth/auth.vue'
type ChangeType = 'INCREASE' | 'REDUCE' | string

const popup = ref(false)
const basePopupArr = [
  {
    label: '全部',
    name: 'ALL',
  },
  {
    label: '用户消费',
    name: 'USER_CONSUME',
  },
  {
    label: '退款成功',
    name: 'REFUND_SUCCEED',
  },
  {
    label: '充值',
    name: 'CHARGING',
  },
] as const
const popupArr = ref<{ label: string; name: string }[]>([
  {
    label: '全部',
    name: 'ALL',
  },
  {
    label: '用户消费',
    name: 'USER_CONSUME',
  },
  {
    label: '退款成功',
    name: 'REFUND_SUCCEED',
  },
  {
    label: '充值',
    name: 'CHARGING',
  },
])
const popupSelectDate = ref(false)
const currentSelectDate = ref({ year: '', month: '' })
// 高度计算
const statusBarHeight = useStatusBar()
const conditionsHeight = useSelector('.conditions')
const dateSelectHeight = useSelector('.date-select')
const sysInfo = uni.getSystemInfoSync()
const pageConfig = reactive({
  size: 10,
  current: 1,
  pages: 0,
})
const searchParam = reactive<{ changeType: ChangeType; dealAggregationType: DealAggregationType; queryTime: string }>({
  changeType: '',
  dealAggregationType: 'ALL',
  queryTime: `${new Date().getFullYear()}-${new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth() + 1) : new Date().getMonth() + 1}`,
})
const billingDetailList = ref<BillingDetailsItem[]>([])
const paymentHistoryStatisticsList = ref<UserPaymentHistoryStatisticsList>([
  { changeType: 'INCREASE', statisticsMoney: '0' },
  { changeType: 'REDUCE', statisticsMoney: '0' },
])

initPaymentHistory()

const scrollViewHeight = computed(() => {
  if (!conditionsHeight.value || !dateSelectHeight.value) {
    return
  }
  return `${sysInfo.screenHeight - (conditionsHeight.value.height + dateSelectHeight.value.height + statusBarHeight.value)}px`
})
/**
 * 获取
 * @param {number} index
 */
async function initPaymentHistory(isLoad = false) {
  if (!isLoad) {
    // 刷新
    pageConfig.current = 1
    billingDetailList.value = await paymentHistoryFn()
  } else if (isLoad && pageConfig.current < pageConfig.pages) {
    // 更新
    pageConfig.current++
    billingDetailList.value = billingDetailList.value.concat(await paymentHistoryFn())
  }
}
async function paymentHistoryFn() {
  const { code, data, msg } = await doGetPaymentHistory(Object.assign(pageConfig, searchParam))
  if (code !== 200) {
    uni.showToast({ title: `${msg ? msg : '获取账单明细失败'}`, icon: 'none' })
    return []
  }
  const { records, pages, userPaymentHistoryStatisticsList } = data
  pageConfig.pages = pages
  paymentHistoryStatisticsList.value = userPaymentHistoryStatisticsList
  return records
}
const handleConditionsChecked = (e: { label: string; name: string }) => {
  if (e.label !== '筛选') {
    searchParam.dealAggregationType = 'ALL'
    searchParam.changeType = e.name
  }
  switch (e.label) {
    case '全部':
      searchParam.changeType = ''
      popupArr.value = basePopupArr.map((item) => item)
      initPaymentHistory()
      break
    case '收入':
      popupArr.value = basePopupArr.filter((item) => !['USER_CONSUME'].includes(item.name))
      initPaymentHistory()
      break
    case '支出':
      popupArr.value = basePopupArr.filter((item) => !['REFUND_SUCCEED', 'CHARGING'].includes(item.name))
      initPaymentHistory()
      break
    case '筛选':
      popup.value = true
      break
    default:
      break
  }
}
/**
 * 筛选
 */
const filterDealAggregation = () => {
  initPaymentHistory()
}
const handleFilterPopupItemClick = (val: DealAggregationType) => {
  searchParam.dealAggregationType = val
  popup.value = false
  filterDealAggregation()
}
const handleDetermine = () => {
  const { month, year } = currentSelectDate.value
  if (month) {
    searchParam.queryTime = Number(month) < 10 ? `${year}-0${month}` : `${year}-${month}`
  }
  console.log('searchParam.queryTime', searchParam.queryTime)
  initPaymentHistory()
  popupSelectDate.value = false
}
const scrolltolower = () => {
  initPaymentHistory(true)
}
</script>

<template>
  <conditions-checked class="conditions" @click="handleConditionsChecked" />
  <date-select
    class="date-select"
    :query-time="searchParam.queryTime"
    :payment-history-statistics-list="paymentHistoryStatisticsList"
    @click="popupSelectDate = true"
  />
  <scroll-view scroll-y :style="{ height: scrollViewHeight }" @scrolltolower="scrolltolower">
    <template v-if="billingDetailList.length">
      <table-detail v-for="item in billingDetailList" :key="item.id" :item="item" />
    </template>
    <u-empty v-else text="暂无账单" mode="list"></u-empty>
  </scroll-view>
  <!-- 日期选择 s -->
  <u-popup v-model="popupSelectDate" mode="bottom" border-radius="20" safe-area-inset-bottom>
    <view class="popupSelectDate-title">
      <view @click="popupSelectDate = false">取消</view>
      <view style="color: #f54319" @click="handleDetermine">确认</view>
    </view>
    <u-line color="#ccc" />
    <data-picker @change="currentSelectDate = $event" />
  </u-popup>
  <!-- 日期选择 e -->
  <u-popup v-model="popup" mode="bottom" border-radius="14" safe-area-inset-bottom>
    <view class="popup-title">
      <q-icon name="icon-icon-close1" size="40rpx" class="popup-close" @click="popup = false" /> <view>选择筛选项</view>
    </view>
    <u-line color="#ccc" />
    <view class="content">
      <scroll-view scroll-y="true" style="height: 250rpx">
        <view class="popup-filter">
          <view
            v-for="item in popupArr"
            :key="item.name"
            class="popup-filter--item"
            :class="{ actrveIndex: searchParam.dealAggregationType === item.name }"
            @click="handleFilterPopupItemClick(item.name as DealAggregationType)"
            >{{ item.label }}
          </view>
        </view>
      </scroll-view>
    </view>
  </u-popup>
  <Auth />
</template>

<style scoped lang="scss">
@include b(popup-title) {
  position: relative;
  text-align: center;
  font-weight: 700;
  padding: 20rpx 0 30rpx 0;
  color: #000;
  font-size: 30rpx;
}
@include b(popup-close) {
  position: absolute;
  left: 30rpx;
}

@include b(content) {
  padding: 24rpx;
  text-align: center;
}
@include b(popup-filter) {
  @include flex(flex-start);
  flex-wrap: wrap;
  @include m(item) {
    width: 30%;
    height: 116rpx;
    font-weight: bold;
    line-height: 116rpx;
    border-radius: 15rpx;
    background-color: rgba(250, 250, 250, 1);
    color: rgba(51, 51, 51, 1);
    font-size: 24rpx;
    text-align: center;
    border: 1px solid transparent;
    margin: 0 10rpx 20rpx 10rpx;
  }
}
@include b(actrveIndex) {
  border-color: red !important;
  color: red;
  background-color: #fdf6f6;
}
@include b(popupSelectDate-title) {
  @include flex;
  justify-content: space-between;
  padding: 20rpx 40rpx 30rpx 40rpx;
  color: #000;
  font-size: 30rpx;
}
</style>
