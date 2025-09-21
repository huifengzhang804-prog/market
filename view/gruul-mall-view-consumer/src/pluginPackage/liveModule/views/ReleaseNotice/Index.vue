<script setup lang="ts">
import { ref, computed, onMounted, onUpdated } from 'vue'
import { onLoad, onReady, onUnload } from '@dcloudio/uni-app'
import { getheader } from '@/libs/request/returnHeader'
import QFooterDefault from '@/components/q-btns/q-footer-default.vue'
import type { CreateLiveRoomParameter } from '@/pluginPackage/liveModule/apis/CreateLive/model'
import { doPostCreateLiveRoom } from '@/pluginPackage/liveModule/apis/CreateLive'
// #ifdef APP-PLUS
import PickerTime from '@/asyncPackages/picker-time/picker-time.vue'
// #endif
import DateUtil from '@/utils/date'

const form = ref<CreateLiveRoomParameter>({
  title: '',
  liveSynopsis: '',
  pic: '',
  beginTime: '',
})
const formRef = ref()
const pickerRef = ref()
const uploadRef = ref()
//获取上传地址
// @ts-ignore
const action: string = import.meta.env.VITE_BASE_URL + import.meta.env.VITE_UPLOAD_URI
const currentYear = computed(() => new Date().getFullYear())
const currentTime = computed(() => new DateUtil().getYMDHMSs(new Date()))
const defaultTime_ = ref('')
const show = ref(false)
const beginMsg = ref('请选择预约时间')
const rules = {
  title: [
    {
      required: true,
      message: '请输入直播主题',
      // 可以单个或者同时写两个触发验证方式
      trigger: ['change', 'blur'],
    },
  ],
  liveSynopsis: [
    {
      required: false,
      message: '请输入直播简介',
      // 可以单个或者同时写两个触发验证方式
      trigger: ['change', 'blur'],
    },
  ],
  switchVal: [
    {
      min: 5,
      message: '简介不能少于5个字',
      trigger: 'change',
    },
  ],
}

onReady(() => {
  formRef.value.setRules(rules)
  defaultTime_.value = currentTime.value
})
onMounted(() => {
  defaultTime_.value = currentTime.value
})

async function submit() {
  const res = await validate()
  if (res) {
    const { code, data, msg } = await doPostCreateLiveRoom(form.value)
    if (code !== 200) {
      uni.showToast({ title: `${msg || '创建直播间失败'}`, icon: 'none' })
      return
    }
    uni.navigateBack({ delta: 1 })
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
  if (!form.value.beginTime) {
    uni.showToast({ title: '请选择预约时间', icon: 'none' })
    return false
  }
  return true
}
const handleConfirm = (e: { month: string; day: string; hour: string; minute: string }) => {
  const { month, day, hour, minute } = e
  const YYMMDD_HHmmss = `${currentYear.value}-${month}-${day} ${hour}:${minute}:00`
  form.value.beginTime = YYMMDD_HHmmss
  beginMsg.value = `${month}月${day}日 ${hour}:${minute}`
  defaultTime_.value = YYMMDD_HHmmss
}
const handleShowPicker = () => {
  show.value = true
}
</script>

<template>
  <view style="margin: 20rpx 0 0 0; background: #fff">
    <u-form ref="formRef" :model="form" label-width="200" label-align="right">
      <u-form-item label="直播主题" prop="title" required><u-input v-model="form.title" placeholder="请输入直播主题" /></u-form-item>
      <u-form-item label="直播简介" prop="liveSynopsis"><u-input v-model="form.liveSynopsis" placeholder="快来直播间看看吧！" /></u-form-item>
      <u-form-item label="预约时间" prop="introduction" required>
        <template #right>
          <view class="live_bg live_msg" @click="handleShowPicker">
            <text :class="{ 'time-color': beginMsg !== '请选择预约时间' }">{{ beginMsg }}</text>
            <u-icon name="arrow-right"></u-icon>
          </view>
        </template>
      </u-form-item>
      <u-form-item label="直播背景图" prop="introduction" required :border-bottom="false" :label-style="{ paddingBottom: 0 }">
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
  <!-- #ifdef MP-WEIXIN -->
  <u-picker
    v-model="show"
    mode="time"
    :params="{
      year: false,
      month: true,
      day: true,
      hour: true,
      minute: true,
      second: false,
      timestamp: true, // 1.3.7版本提供
    }"
    :start-year="currentYear"
    :end-year="currentYear"
    :default-time="defaultTime_"
    @confirm="handleConfirm"
  ></u-picker>
  <!-- #endif -->
  <!-- #ifdef APP-PLUS -->
  <PickerTime
    ref="pickerRef"
    v-model="show"
    :params="{
      year: false,
      month: true,
      day: true,
      hour: true,
      minute: true,
      second: false,
      timestamp: true, // 1.3.7版本提供
    }"
    :start-year="currentYear"
    :end-year="currentYear"
    :default-time="defaultTime_"
    mode="time"
    @confirm="handleConfirm"
  ></PickerTime>
  <!-- #endif -->
  <q-footer-default text="发布预告" bg-color="#FA3534" @click="submit"></q-footer-default>
</template>

<style scoped lang="scss">
@include b(live_bg) {
  width: calc(100vw - 200rpx);
}
@include b(live_msg) {
  @include flex;
  justify-content: space-between;
  padding: 0 40rpx 0 0;
}
:deep(.u-form-item--left__content--required) {
  // 可能是组件库的问题
  left: 20rpx !important;
}
.time-color {
  color: #3333;
}
</style>
