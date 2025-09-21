import type { Joint_Live_List_Status } from '@/pluginPackage/liveModule/apis/CreateLive/model'

type StatusConfig = Record<Joint_Live_List_Status, { bg: string; text: string }>

const statusConfig: StatusConfig = {
  PROCESSING: {
    bg: '#00CF78',
    text: '直播中',
  },
  NOT_STARTED: {
    text: '预告',
    bg: '#FF794D',
  },
  OVER: {
    text: '已结束',
    bg: ' #999999',
  },
  ILLEGAL_SELL_OFF: {
    text: '违禁',
    bg: '#E50C00',
  },
}

export { statusConfig, type StatusConfig }
