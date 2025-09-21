<script lang="ts" setup>
import { reactive } from 'vue'
import { doGetSupplierBasicSet, doPutSupplierBasicSet } from '@/apis/setting'
import { ElMessage, FormItemRule } from 'element-plus'

const timeout = reactive({
  supplierD: 0,
  supplierH: 0,
  supplierM: 0,
})

// 订单支付超时时间表单校验
const validRules: FormItemRule[] = [
  {
    validator: (_, _v, callback) => {
      const seconds = timeout.supplierD * oneDaySecond + timeout.supplierH * oneHourSecond + timeout.supplierM * 60
      if (seconds < 15 * 60 || seconds > 30 * 24 * 60 * 60) {
        callback(new Error('可以设置的区间为15分钟至30天'))
        return
      }
      callback()
    },
    trigger: 'blur',
  },
]
let supplierPayTimeout = '0'
const oneHourSecond = 60 * 60
const oneDaySecond = 24 * oneHourSecond
const secondToPayTimeout = (seconds: string) => {
  const nums = parseInt(seconds)
  let dd = Math.floor(nums / oneDaySecond)
  let hh = Math.floor((nums % oneDaySecond) / oneHourSecond)
  let mm = Math.floor((nums % oneHourSecond) / 60)
  return [dd, hh, mm]
}
const handlesupplierTime = () => {
  doGetSupplierBasicSet().then(({ data: res }) => {
    supplierPayTimeout = res.payTimeout
    const supplierTimeSplit = secondToPayTimeout(res.payTimeout)
    timeout.supplierD = supplierTimeSplit[0]
    timeout.supplierH = supplierTimeSplit[1]
    timeout.supplierM = supplierTimeSplit[2]
  })
}
handlesupplierTime()

const handOrderTime = async () => {
  const data = {
    payTimeout: timeout.supplierD * oneDaySecond + timeout.supplierH * oneHourSecond + timeout.supplierM * 60 + '',
  }
  if (supplierPayTimeout === data.payTimeout) return
  try {
    const { code, msg } = await doPutSupplierBasicSet(data)
    if (code !== 200) {
      return ElMessage.error(msg || '更改失败')
    }
    handlesupplierTime()
  } catch (error) {
    ElMessage.error('更改失败')
  }
}
defineExpose({ handOrderTime })
</script>

<template>
  <div class="msg">
    <el-form-item :rules="validRules" label="未支付的采购订单" label-width="180px" prop="supplierPayTimeout" style="flex: 1">
      <el-input-number v-model="timeout.supplierD" :controls="false" :min="0" style="width: 60px" />
      <text class="ml12 mr12">天</text>
      <el-input-number v-model="timeout.supplierH" :controls="false" :min="0" style="width: 60px" />
      <text class="ml12 mr12">小时</text>
      <el-input-number v-model="timeout.supplierM" :controls="false" :min="0" style="width: 60px" />
      <span class="ml12">分钟后，自动关闭</span>
    </el-form-item>
    <span class="ml42 msg__text">最长可设置30天，&nbsp;最短可设置15分钟</span>
  </div>
</template>

<style lang="scss" scoped>
@include b(msg) {
  @include flex;
  justify-content: flex-start;
  @include e(text) {
    height: 32px;
    width: 300px;
    font-size: 12px;
    color: #999;
  }
}

@include b(ml12) {
  margin-left: 12px;
}

@include b(mr12) {
  margin-right: 12px;
}
</style>
