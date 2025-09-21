<script lang="ts" setup>
import { ref } from 'vue'
import QNav from '@/components/q-nav/q-nav.vue'
import RegisterDistributor from './registerDistributor/registerDistributor.vue'
import DistributorCenter from './distributorCenter/distributorCenter.vue'
import { useNavBack } from '@/hooks/useNavBack'
import { doGetDistributeConfig, doGetDistributeCode, doGetDistributionEnable } from './apis'
import type { DistributeConfigType } from '@/apis/plugin/distribute/model'
import { useUserStore } from '@/store/modules/user'
import Auth from '@/components/auth/auth.vue'
import { onLoad } from '@dcloudio/uni-app'

onLoad(() => {
  uni.$emit('updateTitle')
})
const $userStore = useUserStore()

const isDistributor = ref(false)
const isLoad = ref(false)
const distributeConfig = ref<DistributeConfigType>()
const apiErrorCode = {
  34301: '分销活动还未开启',
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
} as { [x: string]: string }

initDisCode()

// 定义是否显示跳转到小程序页面按钮
const skipMinApp = ref(false)
const oppIdMinApp = ref('')

async function initDisCode() {
  const { code, data } = await doGetDistributeCode()
  if (!code && code !== 200) {
    uni.showToast({
      icon: 'none',
      title: '获取分销码失败',
      duration: 2000,
    })
    const time = setTimeout(() => {
      useNavBack()
      clearTimeout(time)
    }, 1000)
    return
  }
  if (code === 200 && data) $userStore.PUT_DISTRIBUTOR_CODE(data)
  const res = await doGetDistributionEnable()
  if (captureError(code) && res.code === 200 && res.data.serviceEnable === false) {
    // 未获取到分销码 调取分销配置接口
    if (!data) {
      const { code: configCode, data: configData } = await doGetDistributeConfig()
      console.log('doGetDistributeConfig1', configCode, configData)
      isDistributor.value = false
      if (!configCode) {
        uni.showToast({
          icon: 'none',
          title: '获取分销配置失败',
        })
        return
      }
      if (captureError(configCode)) {
        distributeConfig.value = configData
      }
    } else {
      console.log('doGetDistributeCode', code, data)
      isDistributor.value = true
    }
    skipMinApp.value = false
  } else if (captureError(code) && res.code === 200 && res.data.serviceEnable === true && res.data.openid) {
    // 未获取到分销码 调取分销配置接口
    if (!data) {
      const { code: configCode, data: configData } = await doGetDistributeConfig()
      console.log('doGetDistributeConfig2', configCode, configData)
      isDistributor.value = false
      if (!configCode) {
        uni.showToast({
          icon: 'none',
          title: '获取分销配置失败',
        })
        return
      }
      if (captureError(configCode)) {
        distributeConfig.value = configData
      }
    } else {
      console.log('doGetDistributeCode2', code, data)
      isDistributor.value = true
    }
    skipMinApp.value = false
  } else if (res.data.openid) {
    console.log('res.data.openid')
    isDistributor.value = false
    skipMinApp.value = true
    oppIdMinApp.value = res.data.openid
  }
  isLoad.value = true
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
</script>

<template>
  <!-- :title="isDistributor ? '分销中心' : '申请分销商'" -->
  <q-nav :bg-color="isDistributor ? '#3D3C41' : '#fff'" :color="isDistributor ? '#F1F2F6' : '#000'" title="分销中心" />
  <DistributorCenter v-if="isDistributor && isLoad" />
  <RegisterDistributor v-if="!isDistributor && isLoad" :config="distributeConfig" :opp-id-min-app="oppIdMinApp" :skip-min-app="skipMinApp" />
  <Auth />
</template>
<style lang="scss" scoped></style>
