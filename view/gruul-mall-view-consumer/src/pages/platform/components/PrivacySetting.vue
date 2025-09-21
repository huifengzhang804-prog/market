<template>
  <u-popup v-model="showPopup" :mask-close-able="false" mode="center" width="91%" border-radius="10" @close="handleClosePrivacySetting">
    <view class="setting">
      <view class="setting__title">用户隐私保护指引</view>
      <view class="setting__description">
        <text>感谢您选择使用我们的小程序，我们非常重视您的个人信息安全和隐私保护。使用我们的产品前，请您仔细阅读</text>
        <text class="setting__description--agreement" @click="handleOpenPrivacy">{{ privacyContractName }}</text>
        <text>，如您同意此隐私保护指引请点击同意按钮开始使用此小程序,我们将尽全力保护您的个人信息及合法权益，感谢您的信任!</text>
      </view>
      <view class="setting__btns">
        <button open-type="agreePrivacyAuthorization" @agreeprivacyauthorization="handleAgreePrivacyAuthorization">同意</button>
      </view>
    </view>
  </u-popup>
</template>

<script lang="ts" setup>
import { computed, ref } from 'vue'

const $props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
})
const $emit = defineEmits(['update:show'])
const privacyContractName = ref('')
const showPopup = computed({
  get() {
    return $props.show
  },
  set(newVal) {
    $emit('update:show', newVal)
  },
})
const initialAuth = () => {
  // @ts-ignore
  if (uni.getPrivacySetting) {
    // @ts-ignore
    uni.getPrivacySetting({
      success: (res: any) => {
        console.log(res, 'getPrivacySetting')
        if (res.needAuthorization) {
          privacyContractName.value = res.privacyContractName
          showPopup.value = true
        }
      },
    })
  }
}
initialAuth()
const handleClosePrivacySetting = () => {}
const handleAgreePrivacyAuthorization = (e: any) => {
  if (e.detail.errMsg === 'agreePrivacyAuthorization:ok') {
    showPopup.value = false
  } else {
    uni.showToast({ title: `${e.detail.errMsg}`, icon: 'none' })
  }
}
const handleOpenPrivacy = () => {
  // @ts-ignore
  uni.openPrivacyContract({
    success() {
      console.log('success')
    },
    fail(error: any) {
      console.log('error', error)
    },
  })
}
</script>

<style lang="scss" setup>
@include b(setting) {
  padding: 30rpx;
  @include e(title) {
    font-size: 36rpx;
    font-weight: 600;
    text-align: center;
    padding: 30rpx;
  }
  @include e(description) {
    font-size: 28rpx;
    color: #666;
    line-height: 1.5;
    @include m(agreement) {
      color: #009b15;
    }
  }
  @include e(btns) {
    margin-top: 30rpx;
  }
}
</style>
