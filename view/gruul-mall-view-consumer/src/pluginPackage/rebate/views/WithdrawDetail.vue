<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import PopupTitle from '@/components/popup/popup-title.vue'
import DateSelect from '@/pluginPackage/rebate/views/components/date-select.vue'
import { doGetWithdrawList } from '@/pluginPackage/distribute/apis'
import type { ApiCommissionType } from '@/apis/plugin/distribute/model'
import DateUtil from '@/utils/date'
import Auth from '@/components/auth/auth.vue'

const { divTenThousand } = useConvert()
const isShowPopup = ref(false)
const paginationOptions = reactive({
  current: 1,
  totalPage: 0,
})
const statistic = reactive({
  applying: '',
  success: '',
  total: '',
})
const statusHandler = {
  待审核: {
    icon: 'error-circle-fill',
    color: '#005CF4',
    textColor: '#333333',
  },
  提现失败: {
    icon: 'error-circle-fill',
    color: '#E60C00',
    textColor: '#333333',
  },
  提现成功: {
    icon: 'checkmark-circle-fill',
    color: '#00CF78',
    textColor: '#FA3534',
  },
}
const searchParam = reactive({
  changeType: '',
  dealAggregationType: 'ALL',
  queryTime: `${new Date().getFullYear()}-${new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth() + 1) : new Date().getMonth() + 1}`,
})

const paymentHistoryStatisticsList = ref<any[]>([
  { changeType: 'INCREASE', statisticsMoney: '0' },
  { changeType: 'REDUCE', statisticsMoney: '0' },
])
const withdrawList = ref<any[]>([])
const scrolltolower = async () => {
  uni.showLoading({ title: '数据加载中...' })
  try {
    if (paginationOptions.current < paginationOptions.totalPage) {
      paginationOptions.current++
      await getWithdrawDetailList()
    }
  } finally {
    uni.hideLoading()
  }
}
const getWithdrawDetailList = async () => {
  const queryTimeStack = searchParam.queryTime?.split('-').map((item) => parseInt(item))
  const startTime = new DateUtil().getYMDHMSs(new Date(queryTimeStack?.[0], queryTimeStack?.[1] - 1, 1))
  const endTime = new DateUtil().getYMDHMSs(new Date(new Date(queryTimeStack?.[0], queryTimeStack?.[1], 1).getTime() - 1000))
  const { code, data, msg } = await doGetWithdrawList({
    current: paginationOptions.current,
    size: 10,
    startTime,
    endTime,
    withdrawSourceType: 'CONSUMPTION_REBATE',
    ownerType: 'REBATE',
  })
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '获取佣金明细失败'}`, icon: 'none' })
  Object.keys(statistic).forEach((key) => {
    statistic[key as keyof typeof statistic] = data?.statistic?.[key] || ''
  })
  if (paginationOptions.current === 1) {
    withdrawList.value = data.records
  } else {
    withdrawList.value = [...withdrawList.value, ...data.records]
  }
  paginationOptions.current = data?.current
  paginationOptions.totalPage = data?.total
}
getWithdrawDetailList()
function convertStatus(value: ApiCommissionType['status']) {
  const statusType = {
    APPLYING: '待审核',
    PROCESSING: '提现中',
    SUCCESS: '已到账',
    CLOSED: '提现失败',
    FORBIDDEN: '已拒绝',
  }
  return statusType[value]
}
function convertSource(value: string) {
  return value === 'BANK_CARD' ? '银行卡' : value === 'WECHAT' ? '微信钱包' : '支付宝钱包'
}
const updateQueryTime = (val: string) => {
  searchParam.queryTime = val
  paginationOptions.current = 1
  getWithdrawDetailList()
}
const height = computed(() => {
  return useScreenHeight().value - useBottomSafe().value - uni.upx2px(167) - uni.upx2px(95) - uni.upx2px(20)
})
</script>

<template>
  <!-- 顶部提现金额 -->
  <view class="head">
    <view class="head__main">
      <text class="head__main--price">{{ divTenThousand(statistic.total) }}</text>
      <view>
        <text class="head__main--msg">累计提现(元)</text>
        <u-icon name="question-circle" size="30" style="position: absolute; margin-left: 5rpx" @click="isShowPopup = true"></u-icon>
      </view>
    </view>
  </view>
  <!-- 顶部提现金额 -->
  <date-select
    :query-time="searchParam.queryTime"
    :rebate-detail-statistic="{ income: statistic.applying, expenditure: statistic.success, incomeText: '待审核', expenditureText: '已到账' }"
    :payment-history-statistics-list="paymentHistoryStatisticsList"
    active-color="#FA3534"
    @update:query-time="updateQueryTime"
  >
  </date-select>
  <scroll-view :scroll-y="true" :style="{ height: `${height}px` }" @scrolltolower="scrolltolower">
    <view v-for="(item, value) in withdrawList" :key="value" class="detail-item">
      <text class="detail-item__price">{{ divTenThousand(item.drawType.amount).toFixed(2) }}</text>
      <view class="detail-item__status">
        <view class="detail-item__status-icon">
          <u-icon v-if="item.status === 'APPLYING'" name="clock-fill" color="#3EAEFF" size="40"></u-icon>
          <u-icon v-else-if="item.status === 'SUCCESS'" name="checkmark-circle-fill" color="#3ABB07" size="40"></u-icon
          ><u-icon v-else-if="item.status === 'PROCESSING'" name="clock-fill" color="#FD9224" size="40"></u-icon
          ><QIcon v-else-if="item.status === 'CLOSED'" name="close-circle-fill" color="#999999" size="40" />
          <u-icon v-else name="error-circle-fill" color="#F5615F" size="40"></u-icon>
          {{ convertStatus(item.status) }}
        </view>
        <text class="detail-item__status--date">{{ item.createTime }}</text>
      </view>
      <view class="detail-item__type">{{ convertSource(item.drawType.type) }}</view>
    </view>
  </scroll-view>
  <!-- 说明弹窗 -->
  <u-popup v-model="isShowPopup" mode="bottom" border-radius="20">
    <popup-title title="提现明细说明" @close="isShowPopup = false" />
    <view style="padding: 0 20rpx 60rpx 20rpx"> 消费获得的返利，可用于购买商品或直接提现。返利提现需经平台审核，审核通过后将钱打到我的账号中。 </view>
  </u-popup>
  <!-- 说明弹窗 -->
  <Auth />
</template>

<style scoped lang="scss">
@include b(head) {
  height: 167rpx;
  background: #fd5e37;
  margin-bottom: 20rpx;
  @include flex;
  @include e(main) {
    color: #fff;
    @include flex;
    flex-direction: column;
    @include m(price) {
      font-size: 45rpx;
      font-weight: 700;
      margin-bottom: 10rpx;
    }
    @include m(msg) {
      font-family: Arial;
      font-size: 25rpx;
    }
  }
}

@include b(detail-item) {
  height: 112rpx;
  padding: 0 40rpx 0 20rpx;
  background: #fff;
  margin-top: 1rpx;
  @include flex;
  justify-content: space-between;
  @include e(price) {
    font-size: 30rpx;
    font-weight: 700;
  }
  @include e(status) {
    font-size: 20rpx;
    @include flex;
    flex-direction: column;
    @include m(date) {
      margin-top: 10rpx;
      font-size: 20rpx;
      transform: scale(0.9);
    }
  }
  @include e(status-icon) {
    @include flex;
  }
  @include e(type) {
    font-size: 26rpx;
    color: #333333;
    width: 130rpx;
    text-align: center;
  }
}
</style>
