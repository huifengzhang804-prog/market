<script setup lang="ts">
import { ref, computed, withDefaults } from 'vue'
import DatePicker from '@/pluginPackage/rebate/views/components/date-picker.vue'
// import { changeTypeCn } from '@/basePackage/pages/billingDetails/types'
// import type { UserPaymentHistoryStatisticsList, userPaymentHistoryStatisticsItem } from '@/basePackage/pages/billingDetails/types'
type IProps = {
  queryTime: string
  rebateDetailStatistic: {
    income: string
    expenditure: string
    incomeText?: string
    expenditureText?: string
  }
  activeColor: string
}

const { divTenThousand } = useConvert()
const currentSelectDate = ref({ year: '', month: '' })
const popupSelectDate = ref(false)
const $props = withDefaults(defineProps<IProps>(), {
  queryTime: `${new Date().getFullYear()}-${new Date().getMonth() + 1 < 10 ? '0' + (new Date().getMonth() + 1) : new Date().getMonth() + 1}`,
  rebateDetailStatistic: () => ({
    income: '0',
    expenditure: '0',
    incomeText: '收入',
    expenditureText: '支出',
  }),
  activeColor: '',
})
const $emit = defineEmits(['click', 'change-select', 'update:queryTime'])

const initQueryTime = computed(() => {
  if (!$props.queryTime) return
  return $props.queryTime
})

const handleDetermine = () => {
  const { month, year } = currentSelectDate.value
  if (month) {
    const date = Number(month) < 10 ? `${year}-0${month}` : `${year}-${month}`
    $emit('update:queryTime', date)
    $emit('change-select', date)
  }
  popupSelectDate.value = false
}
const handleClick = () => {
  $emit('click')
  popupSelectDate.value = true
}
</script>

<template>
  <view class="date-select">
    <view class="date-select__month" @click="handleClick">
      <text :style="{ color: activeColor }">{{ initQueryTime }} </text>
      <u-icon name="arrow-down-fill" :size="20" style="margin-left: 15rpx"></u-icon>
    </view>
    <view class="date-select__detail">
      <text>{{ $props.rebateDetailStatistic.expenditureText }}</text>
      <text :style="{ color: activeColor }">￥{{ divTenThousand($props.rebateDetailStatistic?.expenditure || '0').toFixed(2) }}</text>
    </view>
    <view class="date-select__detail">
      <text>{{ $props.rebateDetailStatistic.incomeText }}</text>
      <text :style="{ color: activeColor }">￥{{ divTenThousand($props.rebateDetailStatistic?.income || '0').toFixed(2) }}</text>
    </view>
  </view>
  <!-- 日期选择 s -->
  <u-popup v-model="popupSelectDate" mode="bottom" border-radius="20" safe-area-inset-bottom>
    <view class="popupSelectDate-title">
      <view @click="popupSelectDate = false"> <text>取消</text></view>
      <view style="color: #f54319" @click="handleDetermine">确认</view>
    </view>
    <u-line color="#ccc" />
    <date-picker @change="currentSelectDate = $event" />
  </u-popup>
  <!-- 日期选择 e -->
</template>

<style scoped lang="scss">
@include b(date-select) {
  background: #fff;
  @include flex;
  justify-content: space-between;
  padding: 0rpx 46rpx 0rpx 13rpx;
  height: 95rpx;
  @include e(detail) {
    @include flex;
    color: rgba(153, 153, 153, 1);
    font-size: 24rpx;
    & > text:nth-child(1) {
      margin-right: 5rpx;
    }
  }
  @include e(month) {
    @include flex;
    color: rgba(16, 16, 16, 1);
    font-size: 26rpx;
  }
}
@include b(popupSelectDate-title) {
  @include flex;
  justify-content: space-between;
  padding: 20rpx 40rpx 30rpx 40rpx;
  color: #000;
  font-size: 30rpx;
}
</style>
