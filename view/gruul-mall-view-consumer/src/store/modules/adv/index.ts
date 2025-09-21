import { type HomeBulletFrameFormType, SkipWay } from '@/apis/concern/types'
import { defineStore } from 'pinia'

interface Adv {
  isPlay: boolean
  imgs: string[]
  countDown: number
}
// 开屏广告
interface AdvertisementFormType {
  endPoint: string
  endTime: string
  imageList: ImageListType[]
  showFlag: boolean
  showFrequency: string
  skipSecond: number
  skipWay: keyof typeof SkipWay
  startTime: string
  times: number
  isPlay: boolean
}
interface ImageListType {
  link: string
  showTime: number
  url: string
}

export const useAdvStore = defineStore('useAdvStore', {
  state: () => ({
    // 开屏广告
    openAdv: {
      // 是否已经播放过开屏广告
      isPlay: false,
      // 应用类型
      endPoint: '',
      // 结束时间
      endTime: '',
      // 广告图片
      imageList: [] as ImageListType[],
      // 是否开启
      showFlag: true,
      showFrequency: '',
      // 展示时长
      skipSecond: 0,
      // 跳过方式
      skipWay: 'NO_SKIP',
      // 开始时间
      startTime: '',
      // 展示次数
      times: 0,
    } as AdvertisementFormType,
    // 弹窗广告
    popAdv: {
      // 是否已经播放过弹窗广告
      isPlay: false,
      endPoint: '',
      endTime: '',
      imageInfo: {
        link: '',
        showTime: 0,
        url: '',
      },
      notExists: false,
      showFlag: false,
      startTime: '',
    } as HomeBulletFrameFormType,
  }),
  actions: {
    // 设置开屏广告
    SET_OPEN_ADV(openAdv: AdvertisementFormType) {
      this.openAdv = openAdv
    },
    // 设置开屏广告
    SET_OPEN_ADV_FLAG(val: boolean) {
      this.openAdv.isPlay = val
    },
    // 设置弹窗广告
    SET_POP_ADV(popAdv: HomeBulletFrameFormType) {
      this.popAdv = popAdv
    },
    SET_POP_ADV_FLAG(val: boolean) {
      this.popAdv.isPlay = val
    },
  },
  getters: {
    getOpenAdvIsPlay(state): boolean {
      return state.openAdv.isPlay
    },
    getOpenAdv(state): AdvertisementFormType {
      return state.openAdv
    },
    getPopAdvIsPlay(state): boolean {
      return state.popAdv.isPlay
    },
  },
})
