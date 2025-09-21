<script setup lang="ts">
import { useUserStore } from '@/store/modules/user'
import { useSubscribeStore } from '@/store/modules/subscribe'
import { doGetIntegralBehaviorSave } from '@/apis/plugin/integral'
import { doSignByUser } from '@/apis/sign'
import { getTemplateIds } from '@/apis/message'
import { useSettingStore } from '@/store/modules/setting'
import { doPostBinding } from '@/apis/distribute/index'
import { useAppStore } from '@/store/modules/app'
import { GrantType } from '@/apis/sign/type'
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { SHOW_LOGIN_MODAL } from '@/utils/tokenConfig'

const popType = ref(false)
const $userStore = useUserStore()
const $SubscribeStore = useSubscribeStore()

const $emit = defineEmits(['cancel'])

onLoad(() => {
  uni.$on(SHOW_LOGIN_MODAL, (show) => {
    // 小程序获取当前页面路径
    const currentPath = getCurrentPages()[getCurrentPages().length - 1].route
    if (show && currentPath === 'pages/platform/Index') {
      handleNavToSign()
    }
    popType.value = show
  })
})

// iphoneX以上机型底部安全距离
const safeHeight = ref(0)
const $settingStore = useSettingStore()

uni.getSystemInfo({
  success: (res) => {
    if (res.safeAreaInsets) {
      safeHeight.value = res.safeAreaInsets.bottom
    }
  },
})

const cancel = () => {
  popType.value = false
  uni.$emit(SHOW_LOGIN_MODAL, false)
  $emit('cancel')
}
const handleNavToSign = () => {
  // #ifndef MP-WEIXIN

  let routes = getCurrentPages() as any
  var page = routes[routes.length - 1].$page
  const { fullPath } = page
  uni.navigateTo({
    url: `/pages/sign/SmsAuth?route=${encodeURIComponent(fullPath)}`,
  })
  $settingStore.UPDATE_REFRESH_STATE() // 更新路由状态
  // #endif
}
// #ifdef MP-WEIXIN
const getPhoneNumber = (e: any) => {
  const detail = e.detail
  if (detail.errMsg !== 'getPhoneNumber:ok') {
    //用户决绝授权
    console.log('用户拒绝')
    //拒绝授权后弹出一些提示
    return
  }
  uni.login({
    async success(res) {
      const loginCode = res.code
      const mobileCode = detail.code
      try {
        const { data, code: status } = await doSignByUser(GrantType.WECHAT_CODE, {
          loginCode,
          mobileCode,
        })

        if (status === 200) {
          const { value: access_token, refreshToken } = data!
          console.log(access_token, refreshToken)
          doGetIntegralBehaviorSave('LOGIN').then((res) => {
            if (res.code !== 200) {
              return false
            }
            if (Number(res.data) > 0) {
              uni.showToast({ title: `登录获得${res.data}积分`, icon: 'none' })
            }
          })
          uni.$emit(SHOW_LOGIN_MODAL, false)
          $userStore.ADD_TOKEN({ access_token, refresh_token: refreshToken.value })
          // $settingStore.NAV_TO_INDEX('首页')
          if (useAppStore().GET_PLUGIN('addon-distribute') && $userStore.discode) {
            const { code: disstate, msg: err } = await doPostBinding($userStore.discode)
            if (disstate !== 200) {
              return uni.showToast({ title: `${err ? err : '绑定分销商失败'}`, icon: 'none' })
            }
            uni.showToast({ title: `绑定分销商成功`, icon: 'none' })
            $userStore.DEL_DIS_CODE()
          }
          const { data: templateIds, code, msg } = await getTemplateIds()
          if (code !== 200 || !templateIds)
            return uni.showToast({
              title: `${msg || '获取订阅模板失败'}`,
              icon: 'none',
            })
          $SubscribeStore.SET_SUBSCRIBE_LIST(templateIds)
          return
        }
        uni.showToast({
          title: '登录失败',
          icon: 'none',
        })
      } catch (e) {
        console.log(e)
        uni.showToast({
          title: '登录失败',
          icon: 'none',
        })
      }
    },
  })
}
// #endif
</script>

<template>
  <!-- #ifndef MP-WEIXIN -->
  <u-modal
    v-model="popType"
    :show-cancel-button="true"
    :title-style="{ color: 'rgba(16, 16, 16, 1)', fontSize: '36rpx' }"
    confirm-color="#FA3534"
    confirm-text="手机登录"
    :confirm-style="{ borderLeft: '1px solid #f3f3f3' }"
    @cancel="cancel"
    @confirm="handleNavToSign"
  >
    <view class="login-box"> 登录后使用完整功能 </view>
  </u-modal>
  <!-- #endif -->
  <!-- #ifdef MP-WEIXIN -->
  <u-modal
    v-model="popType"
    :show-cancel-button="true"
    :title-style="{ color: 'rgba(16, 16, 16, 1)', fontSize: '36rpx' }"
    confirm-color="#FA3534"
    :show-confirm-button="false"
    :confirm-style="{ borderLeft: '1px solid #f3f3f3' }"
    @cancel="cancel"
    @confirm="handleNavToSign"
  >
    <view class="login-box"> 登录后使用完整功能 </view>
    <template #confirm-button>
      <button class="button" open-type="getPhoneNumber" withCredentials="true" @getphonenumber="getPhoneNumber">快捷登录</button>
    </template>
  </u-modal>
  <!-- #endif -->
</template>

<style lang="scss" scoped>
@include b(button) {
  border-radius: 0;
  height: 100rpx;
  line-height: 100rpx;
  font-size: 32rpx;
  color: #fa3534;
  background-color: #fff;
  &::after {
    border: 0;
  }
}
@include b(login-box) {
  height: 120rpx;
  text-align: center;
  line-height: 120rpx;
}
</style>
