<script setup lang="ts">
import { ref, onMounted, type PropType } from 'vue'
import QSection from '@/components/q-section/q-section.vue'
import { REGEX_MOBILE } from '@/libs/validate'
import logisticsCompany from '@/assets/json/data.json'
import QFooterDefault from '@/components/q-btns/q-footer-default.vue'
import { doGetReturnedByAfsNo } from '@/apis/afs'

const fromData = ref({
  expressNo: '',
  mobile: '',
  remark: '',
  deliverType: 'EXPRESS',
  expressCompanyName: '',
  expressCompanyCode: '',
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
  expressNo: [
    {
      required: true,
      message: '请输入物流单号',
      trigger: ['change', 'blur'],
    },
    {
      pattern: /^[0-9a-zA-Z]*$/g,
      // 正则检验前先将值转为字符串
      transform(value: any) {
        return String(value)
      },
      message: '物流单号只能包含字母或数字',
    },
    { max: 20, message: '请输入正确的物流单号', trigger: ['change', 'blur'] },
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
  // GO_STORE_REFUND
  const isValidate = await formRef.value.validate()
  if (!isValidate) return
  const { expressCompanyName, expressCompanyCode, expressNo, mobile, remark, deliverType } = fromData.value
  const params = { mobile, remark, deliverType, expressCompany: { expressCompanyName, expressCompanyCode, expressNo } }
  const { code, data, msg } = await doGetReturnedByAfsNo($props.afsNo, 'EXPRESS_REFUND', { expressRefund: params })
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
const indicatorStyle = () => {}
const bindChange = (e: { detail: { value: number } }) => {
  const { companyName, companyCode } = logisticsCompany[e.detail.value]
  fromData.value.expressCompanyName = companyName
  fromData.value.expressCompanyCode = companyCode
}
</script>

<template>
  <u-form ref="formRef" :model="fromData" :error-type="['toast']">
    <u-form-item prop="expressCompanyName" style="padding: 0">
      <picker
        :range="logisticsCompany"
        range-key="companyName"
        :indicator-style="indicatorStyle"
        class="picker-view"
        mode="selector"
        @change="bindChange"
      >
        <q-section
          title="物流公司"
          :sub-title="fromData.expressCompanyName ? fromData.expressCompanyName : '请选择物流公司'"
          @click="isSelect = true"
        >
        </q-section>
      </picker>
    </u-form-item>
    <u-form-item prop="expressNo" style="padding: 0">
      <q-section title="物流单号" :rightIcon="false">
        <u-input v-model="fromData.expressNo" type="" placeholder="请填写物流单号" :border="false" style="margin-left: 30rpx; flex: 1" />
      </q-section>
    </u-form-item>
    <u-form-item prop="mobile" style="padding: 0">
      <q-section title="联系电话" :rightIcon="false">
        <u-input v-model="fromData.mobile" placeholder="请填写手机号码" type="" :border="false" style="margin-left: 30rpx; flex: 1" :maxlength="11" />
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
