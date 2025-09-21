export const secondsKillStatus = {
  NOT_STARTED: '即将开抢',
  PROCESSING: '抢购中',
  OVER: '已结束',
  ILLEGAL_SELL_OFF: '违规下架',
}
/**
 * @description: 秒杀价格背景图
 */
export const PRICE_BACKGROUND_IMG = 'https://obs.xiaoqa.cn/gruul/20221126/441a63322cca4a02bc1e4d4f5d4b54b9.png'
/**
 * @description: 秒杀商品
 */
export const strategyPatternHandler = {
  NOT_STARTED: {
    color: '#333333',
    text: '即将开始',
    soldOut: false,
  },
  PROCESSING: {
    color: '#e31436',
    text: '立即抢购',
    soldOut: false,
  },
  OVER: {
    color: '#999999',
    text: '已结束',
    soldOut: false,
  },
  BUY_NOW: {
    color: '#e31436',
    text: '立即抢购',
    soldOut: false,
  },
  OUT_OF_STOCK: { color: '#999999', text: '已抢光', soldOut: true },
}
/**
 * @description: 倒计时
 */
export const countdownStrategyPatternHandler = {
  NOT_STARTED: {
    text: '距开始',
  },
  PROCESSING: {
    text: '距结束',
  },
  OVER: {
    text: '距结束',
    soldOut: false,
  },
  ILLEGAL_SELL_OFF: {
    text: '距结束',
  },
}
