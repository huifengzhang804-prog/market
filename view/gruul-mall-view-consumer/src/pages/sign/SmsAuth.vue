<script setup lang="ts">
import { ref } from 'vue'
import { useUserStore } from '@/store/modules/user'
// import { useAppStore } from '@/store/modules/app'
import { useSettingStore } from '@/store/modules/setting'
import { REGEX_MOBILE } from '@/libs/validate'
import SliderCaptcha from '@/components/slide-captcha/SliderCaptcha.vue'
import QCountDown from '@/components/q-count-down/q-count-down.vue'
import { doGetCaptchaSlider, doPostSmsCode, doSignByUser } from '@/apis/sign'
import { doGetIntegralBehaviorSave } from '@/apis/plugin/integral'
// import { doPostBinding } from '@/apis/distribute/index'
import { onLoad } from '@dcloudio/uni-app'
import { GrantType } from '@/apis/sign/type'
import { SHOW_LOGIN_MODAL } from '@/utils/tokenConfig'
import HeadNavH5 from '@/pluginPackage/goods/commodityInfo/components/head-nav/head-nav-h5.vue'

const { doRequest } = useMember()

const tel = ref('')
const code = ref()
const hasCountDown = ref(false)
const countDown = ref(66000)
const countDownRef = ref()
const $userStore = useUserStore()
const $settingStore = useSettingStore()
const showSliderCaptcha = ref(false)
const captchaList = ref()

let route = '/'
onLoad((res) => {
  if (res) {
    route = res.route ? res.route : route
    console.log('decodeURIComponent(route)', decodeURIComponent(route))
  }
})

const changeNav = () => {
  uni.navigateBack()
}

const getCodeHandle = async () => {
  if (!REGEX_MOBILE(tel.value)) {
    uni.showToast({
      title: '手机号有误',
      icon: 'none',
    })
  } else {
    getCaptchaSlider()
  }
}

const getCaptchaSlider = async () => {
  const res = await doGetCaptchaSlider(tel.value, 'LOGIN')
  if (res.code !== 200) {
    return
  }
  if (res.data?.captcha) {
    captchaList.value = res.data.captcha
    showSliderCaptcha.value = true
  } else if (res.data?.smsCode) {
    code.value = res.data.smsCode
  }
}

const countFinishHandle = () => {
  hasCountDown.value = false
  countDownRef.value.reset()
}
const signHandle = async () => {
  if (!code.value) {
    uni.showToast({
      icon: 'none',
      title: '请填写验证码',
    })
    return
  }
  // #ifdef APP-PLUS
  if (!isCheckedPolicy.value) {
    uni.showToast({
      icon: 'none',
      title: '请勾选隐私协议',
    })
    return
  }
  // #endif

  try {
    const {
      data,
      code: resCode,
      // data: { value: access_token, refreshToken },
    } = await doSignByUser(GrantType.SMS_CODE, {
      mobile: tel.value,
      code: code.value,
    })
    if (resCode !== 200 || !data) {
      return uni.showToast({
        title: '登录失败',
        icon: 'none',
      })
    }
    const access_token = data.value
    const refreshToken = data.refreshToken
    $userStore.ADD_TOKEN({ access_token, refresh_token: refreshToken.value })
    // 关闭授权弹窗
    uni.$emit(SHOW_LOGIN_MODAL, false)
    doGetIntegralBehaviorSave('LOGIN').then((res) => {
      if (res.code !== 200) {
        return
      }
      if (Number(res.data) > 0) {
        uni.showToast({ title: `登录获得${res.data}积分`, icon: 'none' })
      }
    })
    await doRequest()
    if ($settingStore.refreshState) {
      uni.navigateBack()
    } else {
      uni.redirectTo({ url: decodeURIComponent(route) })
    }
  } catch (e) {
    console.log(e)
    uni.showToast({
      title: '登录失败',
      icon: 'none',
    })
  }
}

const success = (e: { data: string }) => {
  hasCountDown.value = true
  countDownRef.value.start()
  showSliderCaptcha.value = false
  code.value = e.data
}
// #ifdef APP-PLUS
const safeBottomHeight = useBottomSafe()
const isCheckedPolicy = ref(false)
const handleNavToPrivateAgreement = () => {
  uni.navigateTo({
    url: '/basePackage/pages/privateAgreement/PrivateAgreement',
  })
}
// #endif

const inputRef = ref()
</script>

<template>
  <view class="sign">
    <!-- 返回按钮 -->
    <HeadNavH5 id="nav" :isShowAssess="false" @changeNav="changeNav" />
    <view class="sign__title">手机号注册登录</view>
    <view class="sign__form">
      <view class="sign__form-item">
        <u-icon name="phone-fill" size="40" color="#000"></u-icon>
        <u-input v-model="tel" type="number" font-size="15" placeholder="请输入手机号" maxlength="11" :border="false"></u-input>
      </view>
      <view class="sign__form-item">
        <view class="sign__form-item--code">
          <u-icon name="lock-fill" size="40"></u-icon>
          <u-input ref="inputRef" v-model="code" type="number" font-size="15" placeholder="请输入验证码" maxlength="6" :border="false"></u-input>
        </view>
        <view v-show="!hasCountDown" class="sign__form-item--btn" @click="getCodeHandle">获取</view>
        <q-count-down v-show="hasCountDown" ref="countDownRef" :timestamp="countDown" format="mm:ss" @end="countFinishHandle" />
      </view>
    </view>
    <view class="sign__form--submit" @click="signHandle">登录</view>
    <slider-captcha
      v-model="showSliderCaptcha"
      :get-form="() => tel"
      :do-submit="doPostSmsCode"
      :scale="2"
      :captcha-list="captchaList"
      @success="success"
      @focus="() => inputRef.onFocus()"
    />
  </view>
  <!-- #ifdef APP-PLUS -->
  <!-- 隐私政策 -->
  <view class="agreement" :style="{ height: safeBottomHeight + 30 + 'px' }">
    <u-checkbox v-model="isCheckedPolicy" shape="circle" active-color="#d82f34" size="26" style="width: 18px"></u-checkbox>
    <text class="agreement__checked">已阅读并同意</text>
    <text class="agreement__policy" @click="handleNavToPrivateAgreement">《隐私权政策》</text>
  </view>
  <!-- #endif  -->
</template>
<style lang="scss" scoped>
@include b(sign) {
  @include e(title) {
    font-size: 48rpx;
    margin: 220rpx 0 70rpx 80rpx;
    font-weight: bold;
  }
  @include e(form) {
    margin: 0 50rpx;
    @include m(submit) {
      width: 460rpx;
      height: 94rpx;
      background: #d82f34;
      box-shadow: 0px 8rpx 15rpx 3rpx rgba(216, 47, 52, 0.3);
      border-radius: 46rpx;
      color: #fff;
      font-size: 32rpx;
      text-align: center;
      line-height: 94rpx;
      margin: 132rpx auto 0 auto;
      font-weight: bold;
    }
  }
  @include e(form-item) {
    @include flex(space-between);
    height: 126rpx;
    border-bottom: 2rpx solid #e4e4e4;
    @include m(code) {
      @include flex();
    }
    @include m(btn) {
      font-size: 30rpx;
      color: #d82f34;
      margin-right: 26rpx;
    }
  }
}
/* #ifdef APP-PLUS  */
@include b(agreement) {
  position: fixed;
  bottom: 0;
  font-size: 22rpx;
  width: 100%;
  @include flex();
  @include e(checked) {
    color: #ccc;
  }
}
/* #endif */
</style>
