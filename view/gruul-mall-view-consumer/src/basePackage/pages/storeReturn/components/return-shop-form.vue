<script setup lang="ts">
import { ref, onMounted, type PropType } from 'vue'
import QSection from '@/components/q-section/q-section.vue'
import { REGEX_MOBILE } from '@/libs/validate'
import QFooterDefault from '@/components/q-btns/q-footer-default.vue'
import { doGetReturnedByAfsNo } from '@/apis/afs'

const fromData = ref({
  clerk: '',
  mobile: '',
  remark: '',
})

const $props = defineProps({
  afsNo: { type: String, default: '' },
  orderNo: { type: String, required: true },
  shopId: { type: String as PropType<Long>, required: true },
  packageId: { type: String, required: true },
})
const formRef = ref()
const isSelect = ref(false)
const rules = ref({
  clerk: [
    {
      required: true,
      message: '请输入收货店员',
      trigger: ['change', 'blur'],
    },
  ],
  mobile: [
    {
      required: true,
      message: '请输入手机号',
      trigger: ['change', 'blur'],
    },
    { pattern: REGEX_MOBILE, message: `手机号不正确`, trigger: ['change', 'blur'] },
  ],
  expressCompanyName: [
    {
      required: true,
      message: '请选择物流公司',
      trigger: ['change', 'blur'],
    },
  ],
  remark: [{ max: 100, message: '退货说明不能超过100字', trigger: ['change', 'blur'] }],
})

onMounted(() => {
  formRef.value.setRules(rules.value)
})

/**
 * 提交表单
 */
const handleSubmit = async () => {
  const isValidate = await formRef.value.validate()
  if (!isValidate) return
  const { clerk, mobile, remark } = fromData.value
  const params = { deliverType: 'WITHOUT', goStoreRefund: { explain: remark, mobile, shopAssistantName: clerk } }
  const { code, data, msg } = await doGetReturnedByAfsNo($props.afsNo, 'GO_STORE_REFUND', params)
  if (code !== 200) return uni.showToast({ title: `${msg ? msg : '提交失败'}`, icon: 'none', duration: 2000 })
  uni.showToast({
    title: `提交成功`,
    icon: 'none',
    duration: 1000,
    success: () => {
      let time = setTimeout(() => {
        //导航去订单详情
        const url = `/pluginPackage/order/orderDetail/OrderDetail?orderNo=${$props.orderNo}&shopId=${$props.shopId}&packageId=${$props.packageId}`
        uni.navigateTo({ url })
        clearTimeout(time)
      }, 1000)
    },
  })
}
</script>

<template>
  <u-form ref="formRef" :model="fromData" :error-type="['toast']">
    <u-form-item prop="clerk" style="padding: 0">
      <q-section title="收货店员" :rightIcon="false">
        <u-input v-model="fromData.clerk" placeholder="请填写收货店员" type="" :border="false" style="margin-left: 30rpx; flex: 1" :maxlength="11" />
      </q-section>
    </u-form-item>
    <u-form-item prop="mobile" style="padding: 0">
      <q-section title="店员电话" :rightIcon="false">
        <u-input v-model="fromData.mobile" placeholder="请填写店员电话" type="" :border="false" style="margin-left: 30rpx; flex: 1" :maxlength="11" />
      </q-section>
    </u-form-item>
    <u-form-item style="padding: 0" prop="remark">
      <q-section title="退货说明" :rightIcon="false">
        <u-input v-model="fromData.remark" placeholder="选填" type="" :border="false" style="margin-left: 30rpx; flex: 1" />
      </q-section>
    </u-form-item>
  </u-form>
  <q-footer-default text="提交" @click="handleSubmit" />
</template>

<style scoped lang="scss">
.picker-style {
  position: absolute;
  left: 0;
  right: 0;
  height: 40px;
  background: #000;
}
</style>
