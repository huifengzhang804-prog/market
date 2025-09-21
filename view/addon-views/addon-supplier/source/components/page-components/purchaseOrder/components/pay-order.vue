<script lang="ts" setup>
import { ref, reactive, withDefaults, defineProps } from 'vue'
import Decimal from 'decimal.js'
import { FormInstance, FormItemRule } from 'element-plus'
import QUpload from '@/components/q-upload/q-upload.vue'
import { Arrayable } from 'element-plus/es/utils'
import { doGetShopBalance, doGetSupplierOrderPaymentMethod } from '../../../../apis'

const $props = withDefaults(defineProps<{ price: Decimal }>(), {
  price: () => new Decimal(0),
})
const payOrderFormModel = reactive({
  payType: '',
  proof: [],
})
const paymentMethods = ref<string[]>([])
const shopBalance = ref()
const rules: Partial<Record<string, Arrayable<FormItemRule>>> = {
  payType: [{ required: true, message: '请选择支付方式', trigger: 'blur' }],
  proof: [
    { required: true, message: '请选择支付方式', trigger: 'change' },
    {
      validator: (_, val, callback) => {
        if (payOrderFormModel.payType === 'OFFLINE') {
          if (!val) {
            callback(new Error('请上传凭证'))
          }
        }
        callback()
      },
      trigger: 'change',
    },
  ],
}
const formRef = ref<FormInstance | null>(null)
const getPayOrderFormModel = () => {
  return new Promise((resolve, reject) => {
    if (formRef.value) {
      formRef.value.validate((isValid) => {
        if (isValid) {
          resolve(payOrderFormModel)
        } else {
          reject('valid error')
        }
      })
    } else {
      reject('none instance')
    }
  })
}

const addNewMainSuccess = (response: string) => {
  if (typeof response === 'string') {
    ;(payOrderFormModel.proof as string[]).push(response)
  }
}

/**
 * 获取商店余额的异步函数
 */
const getShopBalance = async () => {
  const { data, code } = await doGetShopBalance()
  if (code === 200 && data) {
    shopBalance.value = data
  }
}

const isDisabledBalance = ref(false)
function initSupplierOrderPaymentMethod() {
  getShopBalance().then(async () => {
    const { code, data } = await doGetSupplierOrderPaymentMethod()
    if (code !== 200) return
    paymentMethods.value = data.paymentMethods
    console.log(paymentMethods)
    if (data.paymentMethods.includes('SHOP_BALANCE') && shopBalance.value?.undrawn / 10000 > Number($props.price)) {
      payOrderFormModel.payType = 'BALANCE'
      isDisabledBalance.value = false
    } else if (data.paymentMethods.includes('PAY_OFF_LINE')) {
      payOrderFormModel.payType = 'OFFLINE'
      isDisabledBalance.value = true
    } else {
      payOrderFormModel.payType = ''
      isDisabledBalance.value = true
    }
  })
}
initSupplierOrderPaymentMethod()
defineExpose({ getPayOrderFormModel })
</script>

<template>
  <el-form ref="formRef" :model="payOrderFormModel" :rules="rules" label-width="80px" label-position="right">
    <el-form-item label="应付款"
      ><span style="color: red"
        >￥<text style="font-size: 18px">{{ $props.price.toFixed(2) }}</text></span
      ></el-form-item
    >
    <el-form-item label="支付方式" prop="payType">
      <el-radio-group v-model="payOrderFormModel.payType">
        <el-radio v-if="paymentMethods.includes('SHOP_BALANCE')" value="BALANCE" :disabled="isDisabledBalance"
          >店铺余额(<span style="color: red; font-size: 16px">{{ String((shopBalance?.undrawn / 10000).toFixed(2)) }}</span
          >)
        </el-radio>
        <el-radio v-if="paymentMethods.includes('PAY_OFF_LINE')" value="OFFLINE">线下付款</el-radio>
      </el-radio-group>
    </el-form-item>
    <el-form-item v-if="payOrderFormModel.payType === 'OFFLINE'" label="付款凭证" prop="proof">
      <div>
        <div style="color: #999; font-size: 12px">【最多可上传3张】，至少 1 张，每张2MB以内，jpg、jpeg、png</div>
        <div style="display: flex; flex-wrap: wrap">
          <div v-for="(item, index) in payOrderFormModel.proof" :key="item">
            <q-upload
              v-model:src="payOrderFormModel.proof[index]"
              style="margin-right: 10px"
              :format="{ size: 2, width: 10000, height: 10000, types: ['image/png', 'image/jpg', 'image/jpeg'] }"
              :cropper="false"
              :height="100"
              :width="100"
            />
          </div>
          <q-upload
            v-show="payOrderFormModel.proof.length < 3"
            :format="{ size: 2, width: 10000, height: 10000, types: ['image/png', 'image/jpg', 'image/jpeg'] }"
            :height="100"
            :width="100"
            @change="addNewMainSuccess"
          />
        </div>
      </div>
    </el-form-item>
  </el-form>
</template>
<style scoped lang="scss">
.el-form-item {
  margin-bottom: 5px;
}
</style>
