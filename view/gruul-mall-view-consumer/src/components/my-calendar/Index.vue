<script setup lang="ts">
import { ref, reactive, unref, watch } from 'vue'
import QIcon from '@/components/q-icon/q-icon.vue'
import { useSelectorQuery } from '@/hooks/useSelectorQuery'
import { doGetGoodFootprint } from '@/apis/consumer/footprint'

// $props.year 是否开启年切换
const $props = defineProps({
  year: {
    type: Boolean,
    default: false,
  },
})
const $emit = defineEmits(['changeDate', 'changeShrinkage', 'changeMonth'])
let week = ref<string[]>(['日', '一', '二', '三', '四', '五', '六']) // 一个星期
const monthList = ref<number[]>([31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31]) // 一年的月份对应其天数
const weekDay = ref(0)
const lastWeekDay = ref(1)
// 组件高度
const canlendarPanelH = ref(0)
const correntDate = reactive({
  year: 0,
  month: 0,
  day: 0,
})
// 日历收缩时的数据shrinkageArr
const shrinkageArr = ref<number[]>([])
// 日历是否收缩
const isShrinkage = ref(true)
// 是否显示 上个月的日期
const isLastMonth = ref(true)
// 是否显示下个月的日期
const isLastWeekDay = ref(true)
// 头部是否显示
const isHeader = ref(true)
// 获得本月的1号是周一
const footprintArr = ref<string[]>([''])
const recordIndex = ref()

getCurrentDate()
initDate()
initFootprint()
watch(
  () => isShrinkage.value,
  (val) => {
    useSelectorQuery(
      '.canlendarPanel',
      (res) => {
        if (res?.height) {
          canlendarPanelH.value = res.height
          return
        }
        canlendarPanelH.value = val ? 280 : 80
      },
      { cache: false },
    )
  },
  {
    immediate: true,
  },
)

/**
 * 初始化足迹标记
 */
async function initFootprint() {
  const { code, data } = await doGetGoodFootprint(correntDate.month)
  if (code === 200) {
    footprintArr.value = data
  }
}
function initDate() {
  if ((correntDate.year % 4 === 0 && correntDate.year % 100 !== 0) || correntDate.year % 400 === 0) {
    monthList.value[1] = 29
  } else {
    monthList.value[1] = 28
  }
  // 获得指定年月的1号是星期几
  const firstDay = getWeekDay(correntDate.year, correntDate.month, 1)
  // 在1号前面填充多少个上个月的日期
  weekDay.value = firstDay === 7 ? 0 : firstDay
  // 获得最后一天是星期几，往后填充多少个
  const remineDay = getWeekDay(correntDate.year, correntDate.month, monthList.value[correntDate.month - 1])
  lastWeekDay.value = remineDay === 7 ? 6 : 6 - remineDay
  recordIndex.value = correntDate.month === new Date().getMonth() + 1 ? correntDate.day - 1 : -1
}
/**
 * 获取今天的日期
 */
function getCurrentDate() {
  const date = new Date()
  correntDate.year = date.getFullYear()
  correntDate.month = date.getMonth() + 1
  correntDate.day = date.getDate()
}
/**
 * 今年今月不许往前跳
 */
function banLastMonth() {
  const date = new Date()
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  return year === correntDate.year && month === correntDate.month
}
/**
 * 今年今月不许往前跳（最多跳两个月）
 */
function banTwoMonths() {
  const date = new Date()
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  return year === correntDate.year && month - 1 === correntDate.month
}
/**
 * 获取今天是周几
 * @param {*} year
 * @param {*} month
 * @param {*} day
 */
function getWeekDay(year: number, month: number, day: number) {
  return new Date(`${year}/${month}/${day}`).getDay()
}
/**
 * 上一个月有多少天
 */
const preMonthSize = () => (correntDate.month - 1 === 0 ? 31 : monthList.value[correntDate.month - 2])
/**
 * 本月后填充几个下月的日期
 * @param {*} type
 */
const changeDate = (type: string) => {
  switch (type) {
    case 'preYear':
      correntDate.year -= 1
      break
    case 'preMonth':
      // 当前月份为1月， 上一个月分为12月，年份减1
      if (correntDate.month === 1) {
        correntDate.month = 12
        correntDate.year -= 1
      } else {
        correntDate.month -= 1
      }
      initFootprint()
      break
    case 'nextYear':
      correntDate.year += 1
      break
    case 'nextMonth':
      // 当前月份为12月， 下个月分为1月，年份加1
      if (correntDate.month === 12) {
        correntDate.month = 1
        correntDate.year += 1
      } else {
        correntDate.month += 1
      }
      initFootprint()
      break
    default:
      break
  }
  initDate()
  const date = { year: correntDate.year, month: correntDate.month, day: correntDate.day }
  $emit('changeMonth', date)
}
/**
 * 日期发生变化
 * @param {*} correntDay
 * @param {*} index
 */
const handleChangeDate = (correntDay: number, index: number) => {
  const newCorrentDate = `${correntDate.year}-${correntDate.month < 10 ? '0' + correntDate.month : correntDate.month}-${
    correntDay < 10 ? '0' + correntDay : correntDay
  }`

  if (footprintArr.value.includes(newCorrentDate)) {
    recordIndex.value = index
    const date = { year: correntDate.year, month: correntDate.month, day: correntDay }
    $emit('changeDate', date)
  } else {
    uni.showToast({
      title: '这一天没有足迹哦',
      icon: 'none',
    })
  }
}
/**
 * 日历收起
 */
const handleShrinkage = () => {
  isShrinkage.value = !isShrinkage.value
  if (!isShrinkage.value) {
    if (new Date().getMonth() + 1 !== correntDate.month) {
      getCurrentDate()
      initDate()
      initFootprint()
    }
    shrinkageArr.value = []
    isHeader.value = false
    // 当前选中那天currentDay
    const currentDay = recordIndex.value + 1
    //今天距离周日还有几天firstDay
    const firstDay = getWeekDay(correntDate.year, correntDate.month, currentDay)
    const inTheFuture = Math.abs(firstDay - 6) // 距离周六还有几天
    // 周六是哪一天=当前选中那天currentDay+距离周六还有几天
    const saturday = currentDay + inTheFuture
    isLastMonth.value = saturday >= 7 ? false : true
    isLastWeekDay.value = saturday > monthList.value[correntDate.month - 1] ? true : false
    for (let index = 0; index < 7; index++) {
      if (saturday - index > 0 && saturday - index <= monthList.value[correntDate.month - 1]) {
        shrinkageArr.value.push(saturday - index)
      }
    }
    shrinkageArr.value.reverse()
  } else {
    isShrinkage.value = true
    isLastMonth.value = true
    isLastWeekDay.value = true
    isHeader.value = true
  }
  $emit('changeShrinkage', unref(isShrinkage))
}
// 默认收起
handleShrinkage()
defineExpose({ canlendarPanelH, initFootprint })
</script>
<template>
  <view class="canlendarPanel">
    <view v-if="isHeader" class="canlendarPanel__header">
      <view class="canlendarPanel__pre">
        <view v-if="$props.year" @click="changeDate('preYear')">
          <view class="canlendarPanel__triangle-right" />
        </view>
        <view v-if="!banTwoMonths()" @click="changeDate('preMonth')">
          <view class="canlendarPanel__triangle-right" />
        </view>
        <view v-else>
          <view class="canlendarPanel__triangle-right canlendarPanel__triangle-right--disabled" />
        </view>
      </view>
      <text class="canlendarPanel__currenDate">{{ `${correntDate.year}年${correntDate.month}月` }}</text>
      <view class="canlendarPanel__pre">
        <view v-if="!banLastMonth()" @click.stop="changeDate('nextMonth')">
          <view class="canlendarPanel__triangle-left" />
        </view>
        <view v-else>
          <view class="canlendarPanel__triangle-left canlendarPanel__triangle-left--disabled" />
        </view>
        <view v-if="$props.year" @click="changeDate('nextYear')">
          <view class="canlendarPanel__triangle-left" />
        </view>
      </view>
    </view>
    <view class="canlendarPanel__canlendar-main">
      <view class="canlendarPanel__main-header">
        <view v-for="(item, index) in week" :key="index" style="color: #838383"> 周{{ item }} </view>
      </view>
      <view class="canlendarPanel__main">
        <template v-if="isLastMonth">
          <view v-for="(lastMonth, index) in weekDay" :key="index" class="canlendarPanel__main--date canlendarPanel__main--lastMonth">
            {{ preMonthSize() - weekDay + lastMonth }}
          </view>
        </template>
        <template v-if="isShrinkage">
          <view
            v-for="(correntDay, index) in monthList[correntDate.month - 1]"
            :key="index"
            class="canlendarPanel__main--date"
            :class="{
              canlendarPanel__currentDay: recordIndex === index,
              canlendarPanel__active: footprintArr.includes(
                `${correntDate.year}-${correntDate.month < 10 ? '0' + correntDate.month : correntDate.month}-${
                  correntDay < 10 ? '0' + correntDay : correntDay
                }`,
              ),
              canlendarPanel__currentDayRound:
                correntDay === correntDate.day && correntDate.month === new Date().getMonth() + 1 && correntDate.year === new Date().getFullYear(),
            }"
            @click="handleChangeDate(correntDay, index)"
          >
            <text
              style="z-index: 5"
              class="canlendarPanel__main--date-text"
              :class="{
                'canlendarPanel__main--correntDay':
                  correntDay === correntDate.day && correntDate.month === new Date().getMonth() + 1 && correntDate.year === new Date().getFullYear(),
              }"
              >{{ correntDay }}</text
            >
          </view>
        </template>
        <template v-else>
          <view
            v-for="correntDay in shrinkageArr"
            :key="correntDay"
            class="canlendarPanel__main--date"
            :class="{
              canlendarPanel__currentDay: recordIndex === correntDay - 1,
              canlendarPanel__currentDayRound:
                correntDay === correntDate.day && correntDate.month === new Date().getMonth() + 1 && correntDate.year === new Date().getFullYear(),
              canlendarPanel__active: footprintArr.includes(
                `${correntDate.year}-${correntDate.month < 10 ? '0' + correntDate.month : correntDate.month}-${
                  correntDay < 10 ? '0' + correntDay : correntDay
                }`,
              ),
            }"
            @click="handleChangeDate(correntDay, correntDay - 1)"
          >
            <text style="z-index: 5" class="canlendarPanel__main--date-text">
              {{ correntDay }}
            </text>
          </view>
        </template>
        <template v-if="isLastWeekDay">
          <view v-for="(nextMonth, index) in lastWeekDay" :key="index" class="canlendarPanel__main--date canlendarPanel__main--nextMonth">{{
            nextMonth
          }}</view>
        </template>
      </view>
      <view class="canlendarPanel__icon" :class="{ 'canlendarPanel__icon--rotate': isShrinkage }" @click="handleShrinkage">
        <q-icon size="44rpx" color="" name="icon-xiajiantou" />
      </view>
    </view>
  </view>
</template>

<style scoped lang="scss">
@include b(canlendarPanel) {
  border-top: 1rpx solid #f5f5f5;
  border-bottom: 1rpx solid #f5f5f5;
  flex-direction: column;
  @include e(icon) {
    @include flex;
    width: 52rpx;
    height: 52rpx;
    background: #fff;
    position: absolute;
    bottom: -20rpx;
    right: 80rpx;
    border-radius: 50%;
    z-index: 3;
    box-shadow: 0rpx 6rpx 4rpx 0rpx rgba(0, 0, 0, 0.06);
    @include m(rotate) {
      transform: rotate(180deg);
    }
  }
  @include e(header) {
    height: 104rpx;
    display: flex;
    justify-content: center;
    align-items: center;
  }
  @include e(currenDate) {
    margin: 0 20rpx;
    font-size: 24rpx;
  }
  @include e(triangle-right) {
    width: 0;
    height: 0;
    border-bottom: 12rpx solid transparent;
    border-top: 12rpx solid transparent;
    border-left: 12rpx solid transparent;
    border-right: 12rpx solid #000;
    @include m(disabled) {
      border-right-color: #d5d5d5;
    }
  }
  @include e(triangle-left) {
    width: 0;
    height: 0;
    border-bottom: 12rpx solid transparent;
    border-top: 12rpx solid transparent;
    border-left: 12rpx solid #000;
    border-right: 12rpx solid transparent;
    @include m(disabled) {
      border-left-color: #d5d5d5;
    }
  }
  @include e(main-date) {
    width: calc(100vw / 7);
    text-align: center;
    margin: 20rpx 0;
    height: 38rpx;
    line-height: 38rpx;
    position: relative;
  }
  @include e(pre) {
    display: flex;
    align-items: center;
  }
  @include e(canlendar-main) {
    flex: 1;
    display: flex;
    flex-direction: column;
    flex-wrap: wrap;
    position: relative;
  }
  @include e(main-header) {
    display: flex;
    line-height: 30px;
    justify-content: space-around;
  }
  @include e(main) {
    display: flex;
    flex-wrap: wrap;
    font-size: 14px;
    @include m(date) {
      width: calc(100vw / 7);
      text-align: center;
      margin: 20rpx 0;
      height: 38rpx;
      line-height: 38rpx;
      position: relative;
    }
    @include m(date-text) {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      z-index: 5;
    }
    @include m(nextMonth) {
      color: #b2b2b2;
    }
    @include m(lastMonth) {
      color: #b2b2b2;
    }
    @include m(correntDay) {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
    }
  }
  @include e(currentDayRound) {
    &:before {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 40rpx;
      height: 40rpx;
      border: #f54319 solid 1rpx;
      border-radius: 50%;
    }
  }
  @include e(currentDay) {
    color: #fff;
    &:before {
      content: '';
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 40rpx;
      height: 40rpx;
      background: #f54319;
      border-radius: 50%;
    }
  }
  @include e(active) {
    &::after {
      content: '';
      position: absolute;
      bottom: -15rpx;
      left: 50%;
      transform: translateX(-50%);
      width: 8rpx;
      height: 8rpx;
      background: #f54319;
      border-radius: 50%;
    }
  }
}
</style>
