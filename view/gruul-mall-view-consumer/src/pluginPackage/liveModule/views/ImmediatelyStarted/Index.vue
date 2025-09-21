<script setup lang="ts">
import { ref } from 'vue'
import { onReady, onUnload } from '@dcloudio/uni-app'
import { getheader } from '@/libs/request/returnHeader'
import QFooterDefault from '@/components/q-btns/q-footer-default.vue'
import { doPostCreateLiveRoom } from '@/pluginPackage/liveModule/apis/CreateLive'
import { toLive } from '@/pluginPackage/liveModule/views/components/liveList'
import type { CreateLiveRoomParameter } from '@/pluginPackage/liveModule/apis/CreateLive/model'

const form = ref<CreateLiveRoomParameter>({
  title: '',
  liveSynopsis: '',
  pic: '',
})
const formRef = ref()
const uploadRef = ref()
//获取上传地址
// @ts-ignore
const action: string = import.meta.env.VITE_BASE_URL + import.meta.env.VITE_UPLOAD_URI
const show = ref(false)
const rules = {
  title: [
    {
      required: true,
      message: '请输入直播主题',
      // 可以单个或者同时写两个触发验证方式
      trigger: ['change', 'blur'],
    },
  ],
}

onReady(() => {
  formRef.value.setRules(rules)
})

async function submit() {
  const res = await validate()
  if (res) {
    const { code, data, msg } = await doPostCreateLiveRoom(form.value)
    if (code !== 200) {
      uni.showToast({ title: `${msg || '创建直播间失败'}`, icon: 'none' })
      return
    }
    if (data) {
      toLive(data)
    }
  }
}
async function validate() {
  const res = await formRef.value.validate()
  if (!res) return false
  const uploadList: string[] = uploadRef.value.lists.filter((item: any) => item.progress === 100).map((item: any) => item.response.data)
  form.value.pic = uploadList.length ? uploadList[0] : ''
  if (!form.value.pic) {
    uni.showToast({ title: '请上传直播背景图', icon: 'none' })
    return false
  }
  return true
}
</script>

<template>
  <view style="margin: 20rpx 0 0 0; background: #fff">
    <u-form ref="formRef" :model="form" :label-style="{}" :label-width="200" label-align="right" :error-type="['toast']">
      <u-form-item :required="true" label="直播主题" prop="title">
        <u-input v-model="form.title" placeholder="请输入直播主题" />
      </u-form-item>
      <u-form-item label="直播简介" prop="liveSynopsis"><u-input v-model="form.liveSynopsis" placeholder="快来直播间看看吧！" /></u-form-item>
      <u-form-item label="直播背景图" required :border-bottom="false">
        <template #right>
          <view class="live_bg">建议尺寸750*1334或9:16</view>
        </template>
      </u-form-item>
      <u-form-item :border-bottom="false">
        <view style="padding: 0 40rpx">
          <u-upload ref="uploadRef" :action="action" :header="getheader()" width="150rpx" height="150rpx" max-count="1"></u-upload>
        </view>
      </u-form-item>
    </u-form>
  </view>
  <q-footer-default text="立即开播" bg-color="#FA3534" @click="submit"></q-footer-default>
</template>

<style scoped lang="scss">
@include b(live_bg) {
  width: calc(100vw - 200rpx);
  box-sizing: border-box;
}
:deep(.u-form-item--left__content--required) {
  // 可能是组件库的问题
  left: 20rpx !important;
}
</style>
