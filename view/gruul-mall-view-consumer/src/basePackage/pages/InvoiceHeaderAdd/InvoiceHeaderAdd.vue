<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { onReady, onLoad } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/modules/user'
import { doPostInvoiceHeader, doGetInvoiceHeaderDetail } from '@/apis/invoice'

const $userStore = useUserStore()
const formRef1 = ref()
const formRef2 = ref()
const submitForm = ref({
  id: '',
  ownerType: 'USER',
  ownerId: $userStore.userInfo.info.userId,
  invoiceHeaderType: 'ENTERPRISE',
  header: '',
  taxIdentNo: '',
  openingBank: '',
  bankAccountNo: '',
  enterprisePhone: '',
  enterpriseAddress: '',
  email: '',
})
const formRule1 = {
  header: [
    {
      required: true,
      message: '请输入抬头',
      trigger: ['change', 'blur'],
    },
  ],
  taxIdentNo: [
    {
      required: true,
      message: '请输入税号',
      trigger: ['change', 'blur'],
    },
  ],
  email: [
    {
      required: true,
      message: '请输入邮箱',
      trigger: ['change', 'blur'],
    },
    {
      message: '请输入正确的邮箱',
      trigger: ['change', 'blur'],
      validator: (rule: any, value: string, callback: any) => {
        // @ts-ignore
        return uni.$u.test.email(value)
      },
    },
  ],
  enterprisePhone: [
    {
      message: '请输入正确的手机号',
      trigger: ['change', 'blur'],
      validator: (rule: any, value: string, callback: any) => {
        if (!value) {
          return true
        }
        // @ts-ignore
        return uni.$u.test.mobile(value)
      },
    },
  ],
}
const formRule2 = {
  header: [
    {
      required: true,
      message: '请输入抬头',
      trigger: ['change', 'blur'],
    },
  ],
  email: [
    {
      required: true,
      message: '请输入邮箱',
      trigger: ['change', 'blur'],
    },
    {
      message: '请输入正确的邮箱',
      trigger: ['change', 'blur'],
      validator: (rule: any, value: string, callback: any) => {
        // @ts-ignore
        return uni.$u.test.email(value)
      },
    },
  ],
}

onReady(() => {
  formRef1.value.setRules(formRule1)
  formRef2.value.setRules(formRule2)
})
const key = ref(0) //radio标识勿删
onLoad(async (e) => {
  if (e?.id) {
    const { code, msg, data } = await doGetInvoiceHeaderDetail({ invoiceHeaderId: e.id, invoiceHeaderOwnerType: 'USER' })
    if (code !== 200) {
      uni.showToast({
        icon: 'none',
        title: `${msg ? msg : '抬头详情获取失败'}`,
      })
      setTimeout(() => {
        return uni.navigateBack()
      }, 1000)
    }
    submitForm.value = { ...submitForm.value, ...data }
    key.value = Date.now()
  }
})

const changeRadio = () => {
  submitForm.value = {
    ...submitForm.value,
    taxIdentNo: '',
    openingBank: '',
    bankAccountNo: '',
    enterprisePhone: '',
    enterpriseAddress: '',
  }
}
const handleSubmit = () => {
  if (submitForm.value.invoiceHeaderType === 'ENTERPRISE') {
    formRef1.value.validate((valid: any) => {
      if (valid) {
        newInvoiceHeader()
      }
    })
  } else {
    formRef2.value.validate((valid: any) => {
      if (valid) {
        newInvoiceHeader()
      }
    })
  }
}
async function newInvoiceHeader() {
  const { code, data, msg } = await doPostInvoiceHeader(submitForm.value)
  if (code !== 200) {
    uni.showToast({
      icon: 'none',
      title: `${msg ? msg : '保存失败'}`,
    })
    return
  }
  uni.showToast({
    icon: 'none',
    title: '保存成功',
  })
  uni.navigateTo({
    url: `/basePackage/pages/InvoiceHeader/InvoiceHeader`,
  })
}
</script>

<template>
  <view class="headType">
    <u-radio-group :key="key" v-model="submitForm.invoiceHeaderType" width="350rpx" active-color="#F54319" @change="changeRadio">
      <u-radio name="ENTERPRISE"> 企业 </u-radio>
      <u-radio name="PERSONAL"> 个人或事业单位 </u-radio>
    </u-radio-group>
  </view>
  <view class="form">
    <u-form
      v-show="submitForm.invoiceHeaderType === 'ENTERPRISE'"
      ref="formRef1"
      :model="submitForm"
      label-width="200rpx"
      label-align="center"
      :error-type="['toast']"
    >
      <u-form-item label="发票抬头" prop="header" :required="true">
        <u-input v-model="submitForm.header" placeholder="请输入抬头" />
      </u-form-item>
      <u-form-item label="税号" prop="taxIdentNo" :required="true">
        <u-input v-model="submitForm.taxIdentNo" placeholder="请输入税号" />
      </u-form-item>
      <u-form-item label="邮箱" prop="email" :required="true">
        <u-input v-model="submitForm.email" placeholder="邮箱地址用于接收电子发票" />
      </u-form-item>
      <u-form-item label="开户行" prop="openingBank">
        <u-input v-model="submitForm.openingBank" placeholder="请输入开户行" />
      </u-form-item>
      <u-form-item label="银行账号" prop="bankAccountNo">
        <u-input v-model="submitForm.bankAccountNo" placeholder="请输入银行账号" />
      </u-form-item>
      <u-form-item label="企业电话" prop="enterprisePhone">
        <u-input v-model="submitForm.enterprisePhone" placeholder="请输入企业电话" />
      </u-form-item>
      <u-form-item label="企业地址" prop="enterpriseAddress">
        <u-input v-model="submitForm.enterpriseAddress" placeholder="请输入企业地址" />
      </u-form-item>
    </u-form>
    <u-form
      v-show="submitForm.invoiceHeaderType === 'PERSONAL'"
      ref="formRef2"
      :model="submitForm"
      label-width="200rpx"
      label-align="center"
      :error-type="['toast']"
    >
      <u-form-item label="发票抬头" prop="header" :required="true">
        <u-input v-model="submitForm.header" placeholder="请输入抬头" />
      </u-form-item>
      <u-form-item label="邮箱" prop="email" :required="true">
        <u-input v-model="submitForm.email" placeholder="邮箱地址用于接收电子发票" />
      </u-form-item>
    </u-form>
  </view>
  <!-- <view class="bottomDefault">
        <view>设置为默认</view>
        <view><q-icon name="icon-duigou-copy" color="#F54319;" size="27rpx" /></view>
    </view> -->
  <view class="fix" @click="handleSubmit"> 保存 </view>
</template>

<style lang="scss" scoped>
@include b(headType) {
  background: rgb(255, 255, 255);
  width: 750rpx;
  height: 112rpx;
  line-height: 112rpx;
  padding: 0 25rpx;
  margin: 20rpx 0;
}
@include b(form) {
  background: #fff;

  :deep(.u-form-item--left__content--required) {
    left: 16rpx;
  }
}
// @include b(bottomDefault) {
//     background: rgb(255, 255, 255);
//     width: 750rpx;
//     height: 112rpx;
//     display: flex;
//     align-items: center;
//     justify-content: space-between;
//     padding: 32rpx 48rpx 32rpx 32rpx;
//     margin-top: 20rpx;
// }
@include b(fix) {
  width: 100%;
  height: 98rpx;
  position: fixed;
  bottom: 0;
  left: 0;
  background: #f54319;
  font-size: 32rpx;
  line-height: 98rpx;
  text-align: center;
  color: #fff;
}
</style>
