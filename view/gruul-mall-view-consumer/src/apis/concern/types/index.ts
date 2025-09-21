export const enum SkipWay {
  // 不跳过
  NO_SKIP = 'NO_SKIP',
  // 手动跳过
  MANUAL_SKIP_AFTER_SECONDS = 'MANUAL_SKIP_AFTER_SECONDS',
  // 自动跳过
  AUTO_SKIP_AFTER_SECONDS = 'AUTO_SKIP_AFTER_SECONDS',
}
// 开屏广告
export interface AdvertisementFormType {
  endPoint: string
  endTime: string
  imageList: ImageListType[]
  showFlag: true
  showFrequency: string
  skipSecond: number
  skipWay: keyof typeof SkipWay
  startTime: string
  times: number
}

export interface ImageListType {
  link: string
  showTime: number
  url: string
}

export interface HomeBulletFrameFormType {
  endPoint: string
  endTime: string
  imageInfo: ImageListType
  notExists: boolean
  showFlag: boolean
  startTime: string
  isPlay: boolean
}
