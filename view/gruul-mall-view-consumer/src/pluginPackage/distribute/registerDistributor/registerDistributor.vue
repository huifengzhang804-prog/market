<script setup lang="ts">
import { ref, type PropType, watch, reactive } from 'vue'
import QCountDown from '@/components/q-count-down/q-count-down.vue'
import { doPostDistributeForm } from '../apis'
import { REGEX_MOBILE } from '@/libs/validate'
import { useNavBack } from '@/hooks/useNavBack'
import type { DistributeConfigType } from '@/apis/plugin/distribute/model'
import SliderCaptcha from '@/components/slide-captcha/SliderCaptcha.vue'
import { doGetCaptchaSlider, doPostSmsCodeFen } from '@/apis/sign'
import { useStatusBar } from '@/hooks'

const showSliderCaptcha = ref(false)
let statusBarHeight = useStatusBar()

const $props = defineProps({
  config: {
    type: Object as PropType<DistributeConfigType>,
    default() {
      return {}
    },
  },
  skipMinApp: {
    type: Boolean,
    default: false,
  },
  oppIdMinApp: {
    type: String,
    default: '',
  },
})
const countDown = ref(60000)
const countDownRef = ref()
const showCode = ref(false)
const needFillForm = ref(true)
const distributeForm = reactive({
  name: '',
  mobile: '',
  code: '',
  isCheck: false,
})
const captchaList = ref()
const apiErrorCode = {
  34301: '分销配置不存在',
  34302: '当前用户已是分销员',
  34303: '当前用户已是分销商',
  34304: '分销码错误',
  34305: '申请请勿重复提交',
  34306: '手机号已被使用',
  34307: '验证码已过期',
  34308: '验证码错误',
  34309: '申请不存在',
  343010: '分销商信息不存在',
  343011: '不可绑定自己的分销码',
  100002: '验证码错误',
} as { [x: string]: string }

watch($props, (newVal) => {
  becomeDistributorWay(newVal.config.condition)
})

const handleCountFinish = () => {
  // countDown.value = 66000
  showCode.value = false
  countDownRef.value.reset()
}
// const handleGetSms = async () => {
//     if (!needFillForm.value) return
//     if (REGEX_MOBILE(distributeForm.mobile)) {
//         const { code, data } = await doGetDistributeSms(distributeForm.mobile)
//         if (!code || code !== 200) {
//             uni.showToast({
//                 icon: 'none',
//                 title: '获取短信失败',
//             })
//         } else {
//             distributeForm.code = data
//             showCode.value = true
//         }
//     } else {
//         uni.showToast({
//             icon: 'none',
//             title: '手机号填写有误',
//         })
//     }
// }

const handleGetSms = async () => {
  if (!REGEX_MOBILE(distributeForm.mobile)) {
    uni.showToast({
      title: '手机号有误',
      icon: 'none',
    })
    return
  }
  getCaptchaSlider()
}

const getCaptchaSlider = async () => {
  const { code, data } = await doGetCaptchaSlider(distributeForm.mobile, 'DISTRIBUTOR')
  if (code !== 200) {
    return
  }
  if (data?.captcha) {
    captchaList.value = data.captcha
    showSliderCaptcha.value = true
  } else if (data?.smsCode) {
    distributeForm.code = data.smsCode
  }
}

const success = async (e: { data: string }) => {
  showCode.value = true
  showSliderCaptcha.value = false
  distributeForm.code = e.data
}
const handleSubForm = async () => {
  console.log(distributeForm.isCheck)
  if (distributeForm.name === '' || distributeForm.code === '' || distributeForm.mobile === '') {
    uni.showToast({
      icon: 'none',
      title: '请填写完成申请信息',
    })
    return
  } else if (!REGEX_MOBILE(distributeForm.mobile)) {
    uni.showToast({
      icon: 'none',
      title: '手机号填写有误',
    })
    return
  } else if (!distributeForm.isCheck) {
    uni.showToast({
      icon: 'none',
      title: '请勾选协议',
    })
    return
  }
  const { code, data, msg } = await doPostDistributeForm(distributeForm)
  if (captureError(code)) {
    uni.showToast({
      icon: 'none',
      title: '分销商申请提交成功',
    })
    useNavBack()
  } else {
    uni.showToast({
      icon: 'none',
      title: `${msg}`,
    })
  }
}
function becomeDistributorWay(condition: DistributeConfigType['condition']) {
  if (condition.types.length === 2 || condition.types.includes('APPLY')) {
    needFillForm.value = true
  } else {
    needFillForm.value = false
  }
}
function captureError(code: number) {
  if (code === 200) return true
  if (apiErrorCode[code]) {
    uni.showToast({
      icon: 'none',
      title: `${apiErrorCode[code]}`,
    })
    return false
  }
}

const handleNavToProtocol = () => {
  uni.navigateTo({
    url: '/pluginPackage/distribute/protocol/Index',
  })
}
const fixedBool = ref(false)
const systemInfo = uni.getSystemInfoSync()
const contentHeight = ref(0)
//  #ifdef H5
contentHeight.value = systemInfo.screenHeight - 44
// #endif
// #ifndef H5
contentHeight.value = systemInfo.screenHeight - statusBarHeight.value
// #endif
</script>

<template>
  <view :style="{ height: contentHeight + 'px' }" class="container">
    <!-- 去商城微信小程序端申请  多方分账 -->
    <view v-if="$props.skipMinApp">
      <view style="font-size: 30rpx; color: #333; margin: 30rpx auto; width: 710rpx">
        <text>为保障分销佣金的正常分账，请您通过商城的微信小程序端（点击下方按钮直达）申请！</text>
      </view>
      <a class="btnDuo" :href="$props.oppIdMinApp">去商城微信小程序端申请</a>
    </view>
    <view v-else class="background">
      <view style="color: #333; font-size: 30rpx; font-weight: bold"
        ><span
          style="
            margin: 30rpx 17rpx 0 32rpx;
            display: inline-block;
            width: 8rpx;
            height: 40rpx;
            background-color: #fa3534;
            border-radius: 215rpx;
            transform: translateY(6rpx);
          "
        ></span
        >分销商申请</view
      >
      <view class="position">
        <!-- <view class="position__title">分销商申请</view> -->
        <view class="disform">
          <view class="disform__item" style="margin-top: 20rpx">
            <view class="disform__item--label">姓名</view>
            <input
              v-model="distributeForm.name"
              class="disform__item--input"
              placeholder="请输入您的姓名"
              maxlength="12"
              :disabled="!needFillForm"
              @focus="fixedBool = true"
              @blur="fixedBool = false"
            />
          </view>
          <view class="disform__item">
            <view class="disform__item--label">手机号</view>
            <input
              v-model="distributeForm.mobile"
              class="disform__item--input"
              placeholder="请输入您的手机号"
              maxlength="11"
              :disabled="!needFillForm"
              @focus="fixedBool = true"
              @blur="fixedBool = false"
            />
          </view>
          <view class="disform__item">
            <view class="disform__item--label">验证码</view>
            <view class="disform__item--code">
              <input
                v-model="distributeForm.code"
                class="disform__item--input"
                placeholder="请输入验证码"
                maxlength="8"
                :disabled="!needFillForm"
                @focus="fixedBool = true"
                @blur="fixedBool = false"
              />
              <q-count-down v-if="showCode" ref="countDownRef" :timestamp="countDown" format="mm:ss" @end="handleCountFinish" />
              <text v-else :class="distributeForm.mobile.length === 11 ? 'red1' : 'red'" @click="handleGetSms">获取验证码</text>
            </view>
          </view>
        </view>
        <view style="bottom: 0; left: 0; width: 100%" :style="fixedBool ? 'position: absolute' : 'position: fixed'">
          <view class="disform__protocol">
            <radio
              :checked="distributeForm.isCheck"
              class="disform__protocol--radio"
              color="#2A64F8"
              @click="distributeForm.isCheck = !distributeForm.isCheck"
            ></radio>

            <text class="disform__protocol--text" @click="handleNavToProtocol">《分销商申请协议》</text>
          </view>
          <!-- <view v-if="needFillForm" class="btn" @click="handleSubForm" -->
          <view
            :class="
              distributeForm.isCheck === false || distributeForm.name === '' || distributeForm.code === '' || distributeForm.mobile === ''
                ? 'btn'
                : 'btn1'
            "
            @click="handleSubForm"
            ><span style="width: 120rpx; text-align: justify; text-align-last: justify; display: inline-block">提交</span></view
          >
          <!-- <view v-else class="btn">消费{{ $props.config.condition.requiredAmount }}元自动成为分销商</view>
                <slider-captcha
                    v-model="showSliderCaptcha"
                    :get-form="() => distributeForm.mobile"
                    :do-submit="doPostSmsCodeFen"
                    :scale="2"
                    @success="success"
                /> -->
          <!-- <view v-if="true" class="btn">消费{{ $props.config.condition.requiredAmount }}元自动成为分销商</view> -->
        </view>
      </view>
    </view>
    <slider-captcha
      v-model="showSliderCaptcha"
      :get-form="() => distributeForm.mobile"
      :do-submit="doPostSmsCodeFen"
      :scale="2"
      :captcha-list="captchaList"
      @success="success"
    />
  </view>
</template>

<style lang="scss" scoped>
@include b(container) {
  position: relative;
}
@include b(background) {
  height: 100%;
  background-color: #fff;
}
@include b(disform) {
  // background: #fff;
  color: #000;
  font-size: 30rpx;
  @include e(item) {
    width: 100%;
    height: 112rpx;
    border-bottom: 1rpx solid rgb(231, 231, 231);
    @include flex(flex);
    padding: 32rpx 32rpx 32rpx 32rpx;
    font-size: 32rpx;
    @include m(label) {
      width: 180rpx;
      margin-right: 32rpx;
      &::before {
        content: '*';
        display: inline-block;
        color: #fc425a;
      }
      // margin-left: 40rpx;
    }
    @include m(input) {
      // width: 606rpx;
      height: 112rpx;
      border: none;
      // margin-left: 70rpx;
      font-size: 32rpx;
      flex: 1;
    }
    @include m(code) {
      flex: 1;
      @include flex(space-between);
    }
  }
  @include e(protocol) {
    // height: 120rpx;
    // margin-top: 579rpx;
    background: #fff;
    font-size: 24rpx;
    color: #ebedf0;
    @include flex(center);
    @include m(text) {
      color: #2a64f8;
      // margin-left: 6rpx;
      &::before {
        content: '我已阅读并了解';
        display: inline-block;
        color: #666;
        margin-right: 10rpx;
      }
    }
    @include m(radio) {
      margin-left: 30rpx;
      transform: scale(0.8);
    }
  }
}
.uni-radio-input {
  width: 30rpx;
  height: 30rpx;
}
@include b(btn) {
  width: 100%;
  height: 98rpx;
  line-height: 98rpx;
  text-align: center;
  font-size: 40rpx;
  color: #fff;
  margin: 91rpx auto 0 auto;
  border-top: 1px solid rgb(231, 231, 231);
  background-color: #f54319;
}
@include b(btn1) {
  width: 100%;
  height: 98rpx;
  line-height: 98rpx;
  text-align: center;
  font-size: 40rpx;
  color: #fff;
  margin: 91rpx auto 0 auto;
  border-top: 1px solid rgb(231, 231, 231);
  background-color: #f54319;
}
@include b(position) {
  background: #fff;
  width: 750rpx;
  height: 100%;
  border-radius: 16rpx;
  display: flex;
  overflow-y: scroll;
  flex-direction: column;
  align-items: center;
  @include e(title) {
    // text-align: center;
    color: rgba(255, 99, 43, 1);
    font-size: 40rpx;
    margin: 52rpx 0 80rpx;
    font-weight: bold;
  }
}
.red {
  width: 153rpx;
  height: 42rpx;
  text-align: center;
  color: #999999;
  font-size: 30rpx;
}
.red1 {
  width: 153rpx;
  height: 42rpx;
  text-align: center;
  color: #005cf4;
  font-size: 30rpx;
}
.line {
  text-decoration: underline;
}
@include b(btnDuo) {
  position: fixed;
  bottom: 0;
  left: 0;
  height: 98rpx;
  width: 100%;
  color: #fd5e37;
  border-top: 1px solid rgb(231, 231, 231);
  font-size: 40rpx;
  font-weight: bold;
  text-align: center;
  line-height: 98rpx;
  text-decoration: none;
}
</style>
