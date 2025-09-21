<script setup lang="ts">
import { ref, reactive, toRefs } from 'vue'

const $emit = defineEmits(['change'])
const selectDate = reactive({
  years: [] as number[],
  year: new Date().getFullYear(),
  months: [] as number[],
  month: new Date().getMonth() + 1,
  days: [],
  indicatorStyle: `height: 50px;background:rgb(204 204 204 / 24%);color:red;`,
  visible: true,
  value: [9999, new Date().getMonth()],
})
const { years, year, months, month, indicatorStyle, visible, value } = toRefs(selectDate)
for (let i = 1990; i <= new Date().getFullYear(); i++) {
  selectDate.years.push(i)
}
for (let i = 1; i <= 12; i++) {
  selectDate.months.push(i)
}
// 动态类名
const yearActiveIndex = ref(years.value.length - 1)
const monthActiveIndex = ref(months.value.length - 1)

//  $emit('change', $event)

const handleDatePicker = (e: { detail: { value: number[] } }) => {
  const { value } = e.detail
  yearActiveIndex.value = value[0]
  monthActiveIndex.value = value[1]
  const year = years.value[value[0]]
  const month = months.value[value[1]]
  $emit('change', { year, month })
}
</script>

<template>
  <picker-view :indicator-style="indicatorStyle" :value="value" class="picker-view" indicator-class="indicator" @change="handleDatePicker">
    <picker-view-column>
      <view v-for="(item, index) in years" :key="index" class="picker-view__item" :class="{ yearActiveIndex: yearActiveIndex === index }">{{
        item
      }}</view>
    </picker-view-column>
    <picker-view-column>
      <view v-for="(item, index) in months" :key="index" class="picker-view__item" :class="{ monthActiveIndex: monthActiveIndex === index }">{{
        item
      }}</view>
    </picker-view-column>
  </picker-view>
</template>

<style scoped lang="scss">
@include b(picker-view) {
  width: 750rpx;
  height: 350rpx;
  margin-top: 20rpx;

  @include e(item) {
    height: 100rpx;
    align-items: center;
    justify-content: center;
    text-align: center;
    line-height: 100rpx;
  }
}
@include b(yearActiveIndex) {
  color: #000;
  font-weight: bold;
}
@include b(monthActiveIndex) {
  color: #000;
  font-weight: bold;
}
</style>
