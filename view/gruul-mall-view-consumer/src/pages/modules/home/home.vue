<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import SwiperHeader from './components/swiper-header.vue'
import { doGetEnablePageByType } from '@/apis/decoration/platform'
import { useAppStore } from '@/store/modules/app'
import { useUserStore } from '@/store/modules/user'
import { useSettingStore } from '@/store/modules/setting'
import { doPostBinding } from '@/apis/distribute/index'
import type { lodingType, dataItem } from './types'
import type { PageInfo } from '@/apis/decoration/platform/model'

const endpointType = ref('H5_APP')

interface SwiperRef {
  chooseIndex: number
  setSwiperLoaded: (index: number) => void
}
const localList = ref<PageInfo>([])
const { doRequest } = useMember()
const { SET_LOADING } = useSettingStore()
const swiperRef = ref<SwiperRef | null>(null)
const $props = defineProps({
  code: {
    type: String,
    default: '',
  },
})
const initFn = (name: keyof typeof SwiperType = '推荐') => {
  doRequest()
  initDecoration(name)
}
onMounted(() => {
  getDoubleTypeDecorationData()
  initShare()
})
enum SwiperType {
  '推荐' = '推荐',
  '同城' = '同城',
}
const swiperTypeRequestMap = {
  推荐: 'RECOMMENDED_MALL_HOME_PAGE',
  同城: 'SAME_CITY_MALL_HOME_PAGE',
}
const renderData = ref<dataItem[]>([
  {
    list: [],
    status: '加载中',
    name: '推荐',
    renderName: '',
    active: false,
  },
  {
    list: [],
    status: '加载中',
    name: '同城',
    renderName: '',
    active: false,
  },
])

/**
 * 同时获取推荐以及同城的数据
 */
const getDoubleTypeDecorationData = async () => {
  let list: any[] = []
  let status: lodingType = '加载中'
  let activeIndex = -1
  // #ifdef MP-WEIXIN
  endpointType.value = 'WECHAT_MINI_APP'
  // #endif
  // #ifndef MP-WEIXIN
  endpointType.value = 'H5_APP'
  // #endif
  const promiseList = [
    doGetEnablePageByType(endpointType.value, swiperTypeRequestMap['推荐']),
    doGetEnablePageByType(endpointType.value, swiperTypeRequestMap['同城']),
  ]
  try {
    const res = await Promise.all(promiseList)
    activeIndex = res.filter((item) => item.code === 200).findIndex((item) => item?.data?.id)
    if (activeIndex === -1) {
      uni.showToast({ title: '获取装修数据失败', icon: 'none' })
      return
    }
    res
      .filter((item) => !!item.data)
      .forEach((item, index) => {
        list = item?.data?.properties ? JSON.parse(item.data.properties) || [] : []
        status = item?.data?.id && list.length ? '成功' : '未配置'
        renderData.value[index] = { ...renderData.value[index], list, status, active: activeIndex === index, renderName: item.data.name }
      })
  } catch (error) {
    uni.showToast({ title: '获取装修数据失败', icon: 'none' })
  } finally {
    SET_LOADING(false)
    const successData = renderData.value.filter((item) => item.status === '成功')
    if (successData.length) {
      swiperRef.value?.setSwiperLoaded(0)
    }
  }
}

/**
 * 初始化首页推荐装修数据
 */
async function initDecoration(name: keyof typeof SwiperType) {
  let list: any[] = []
  let status: lodingType = '加载中'
  const index = renderData.value.findIndex((item) => item.name === name)
  if (index === -1 || renderData.value[index]?.active) return
  if (renderData.value[index]?.status === '成功') {
    SET_LOADING(false)
    swiperRef.value?.setSwiperLoaded(index)
    renderData.value[index].active = true
    return
  }
  try {
    // #ifdef MP-WEIXIN
    endpointType.value = 'WECHAT_MINI_APP'
    // #endif
    // #ifndef MP-WEIXIN
    endpointType.value = 'H5_APP'
    // #endif
    const { data } = await doGetEnablePageByType(endpointType.value, swiperTypeRequestMap[name])
    list = JSON.parse(data?.properties) || []
    status = data?.id && list.length ? '成功' : '未配置'
  } catch (error) {
    status = '失败'
  } finally {
    SET_LOADING(false)
    swiperRef.value?.setSwiperLoaded(index)
  }
  if (status === '成功') {
    renderData.value[index] = { ...renderData.value[index], list, status, active: true }
  }
}

async function initShare() {
  if (useAppStore().GET_PLUGIN('addon-distribute') && $props.code) {
    let code = decodeURIComponent($props.code)
    const { code: state, msg } = await doPostBinding(code)
    if (state !== 200) {
      useUserStore().SET_DIS_CODE(code)
      return uni.showToast({ title: `${msg ? msg : '绑定分销商失败'}`, icon: 'none' })
    }
    uni.showToast({ title: `绑定分销商成功`, icon: 'none' })
    useUserStore().DEL_DIS_CODE()
  }
}
</script>
<template>
  <SwiperHeader ref="swiperRef" :o2oShow="!!localList.length" :renderData="renderData" @initFn="initFn" />
</template>
