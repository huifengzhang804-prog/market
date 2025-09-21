import type { ComOperationType } from '@/pluginPackage/goods/commodityInfo/types'

export interface BtnOption {
  color: string
  backgroundColor: string
  text: string
  type: ComOperationType
}
const btnConfig = {
  SECONDS_KILL: [
    {
      type: 'BUY',
      color: '#fff',
      backgroundColor: '#F54319',
      text: '立即购买',
    },
  ],
  GROUP: [
    {
      type: 'JOINCART',
      color: '#fff',
      backgroundColor: '#595754',
      text: '加入购物车',
    },
    {
      type: 'BUY',
      color: '#fff',
      backgroundColor: '#F54319',
      text: '立即购买',
    },
  ],
  BARGAIN: [
    {
      type: 'BUY',
      color: '#fff',
      backgroundColor: '#595754',
      text: '立即购买',
    },
    {
      type: 'NAVIGATE',
      color: '#fff',
      backgroundColor: '#F54319',
      text: '找人帮砍',
    },
  ],
  DEFAULT: [
    {
      type: 'JOINCART',
      color: '#fff',
      backgroundColor: '#595754',
      text: '加入购物车',
    },
    {
      type: 'BUY',
      color: '#fff',
      backgroundColor: '#F54319',
      text: '立即购买',
    },
  ],
}
export function getBarConfig() {
  return {
    btnConfig,
  }
}
