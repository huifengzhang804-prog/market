<template>
  <view style="background-color: #fff">
    <view class="header">
      <view @click="datePickerVisible = true">
        <text class="header__time">{{ currentDate }}</text>
        <q-icon name="icon-xiadanjiantou" size="18rpx"></q-icon>
      </view>
      <view style="margin-right: 15rpx">
        合计：<text style="color: #fa3534">{{ earnIntegral }}分</text>
      </view>
    </view>
    <view v-for="item in integralList" :key="item.id" class="integralItem">
      <view>+{{ item.variationIntegral }}</view>
      <view>签到</view>
      <view style="color: #999999; width: 290rpx; text-align: left">{{ item.createTime }}</view>
    </view>
    <u-picker
      v-model="datePickerVisible"
      mode="time"
      :start-year="startDate"
      :end-year="endDate"
      :safe-area-inset-bottom="true"
      :default-time="currentDate"
      :params="params"
      @confirm="bindDateChange"
    />
  </view>
</template>

<script setup lang="ts">
import { computed, ref, reactive } from 'vue'
import { doGetUserIntegralDetailInfo } from '@pluginPackage/integral/api'
import DateUtil from '@/utils/date'

const datePickerVisible = ref(false)

const date = new Date()
const params = {
  year: true,
  month: true,
  day: false,
  hour: false,
  minute: false,
  second: false,
  // 选择时间的时间戳
  timestamp: false,
}

const getDate = (type?: 'start' | 'end', dateParam = date) => {
  let year = dateParam.getFullYear()
  let month: number | string = dateParam.getMonth() + 1

  if (type === 'start') {
    return `${year - 3}`
  } else if (type === 'end') {
    return `${year}`
  }
  month = month > 9 ? month : '0' + month
  return `${year}-${month}`
}

const currentDate = ref(getDate())

const startDate = computed(() => getDate('start'))

const endDate = computed(() => getDate('end'))

const bindDateChange = ({ year, month }: { month: string; year: string }) => {
  currentDate.value = `${year}-${month}`
  getUserIntegralDetailInfo()
}

const pageConfig = reactive({
  size: 31,
  current: 1,
  pages: 1,
  beginTime: `${currentDate.value}-01`,
  endTime: '',
  gainIntegralType: 'DAY_SIGN_IN',
})

const earnIntegral = ref('0')

enum ChangeType {
  INCREASE = '增加',
  REDUCE = '减少',
}

enum GainIntegralType {
  /**
   * 每日登入
   */
  DAY_LOGIN = '每日登入',
  /**
   * 积分商品兑换
   */
  INTEGRAL_PRODUCT_EXCHANGE = '积分商品兑换',

  /**
   * 每日分享
   */
  DAY_SHARE = '每日分享',

  /**
   * 积分清空
   */
  INTEGRAL_CLEAR = '积分清空',

  /**
   * 每日签到
   */
  DAY_SIGN_IN = '每日签到',

  /**
   * 系统充值
   */
  SYSTEM_RECHARGE = '系统充值',

  /**
   * 系统扣除
   */
  SYSTEM_DEDUCT = '系统扣除',

  /**
   * 订单消费
   */
  ORDER_CONSUMPTION = '订单消费',

  /**
   * 订单取消
   */
  ORDER_CANCEL = '订单取消',
}

interface IntegralDetailInfo {
  changeType: keyof typeof ChangeType
  clear: boolean
  createTime: string
  deleted: false
  gainIntegralType: keyof typeof GainIntegralType
  id: string
  particulars: string
  updateTime: string
  userId: string
  variationIntegral: string
}
// 积分列表
const integralList = ref<IntegralDetailInfo[]>([])
// 获取积分详情
async function getUserIntegralDetailInfo() {
  const year = currentDate.value.split('-')[0]
  // let month = `${+currentDate.value.split('-')[1] + 1}`
  // month = +month > 9 ? month : 0 + month
  // pageConfig.endTime = `${year}-${month}-01`
  console.log(currentDate.value, '当前时间')

  const dateUtilB = new DateUtil(currentDate.value)
  console.log(dateUtilB, '工具')
  console.log(dateUtilB.getY, '工具getY')
  const beginTime = `${dateUtilB.getY()}-${dateUtilB.getM()}-${dateUtilB.getD()}`
  console.log(beginTime, 'beginTime')
  pageConfig.beginTime = beginTime
  const prevDateTime = new Date(
    new Date(new Date(currentDate.value).setMonth(new Date(currentDate.value).getMonth() + 1)).setDate(
      new Date(new Date(currentDate.value).setMonth(new Date(currentDate.value).getMonth() + 1)).getDate() - 1,
    ),
  )
  const dateUtil = new DateUtil(prevDateTime)
  const endTime = `${dateUtil.getY()}-${dateUtil.getM()}-${dateUtil.getD()}`
  pageConfig.endTime = endTime
  try {
    const { code, data, msg } = await doGetUserIntegralDetailInfo(pageConfig)
    if (code !== 200 || !data) {
      return uni.showToast({ title: `${msg ? msg : '获取积分明细列表失败'}`, icon: 'none' })
    }
    earnIntegral.value = data.statistics.earnIntegral
    integralList.value = data.records
  } catch (error) {
    console.log('出错了', error)
    uni.showToast({ title: '获取积分明细列表失败', icon: 'none' })
  }
}

getUserIntegralDetailInfo()
</script>

<style scoped lang="scss">
@include b(header) {
  height: 96rpx;
  display: flex;
  align-items: center;
  padding: 0 16rpx;
  justify-content: space-between;
  @include e(time) {
    color: #fa3534;
    font-size: 28rpx;
    padding-right: 23rpx;
  }
}
.integralItem {
  color: #333333;
  display: flex;
  justify-content: space-around;
  padding: 18rpx 0;
  font-size: 28rpx;
  border-top: 1rpx solid #f2f2f2;
}
</style>
