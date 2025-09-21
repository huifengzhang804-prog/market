/**
 * 用户所使用得会员卡类型
 */
const memberTypeCn = {
  FREE_MEMBER: '免费会员',
  PAID_MEMBER: '付费会员',
}

/**
 * 有效的时间类型
 */
export const efficientTimeTypeCn = {
  ONE_MONTH: '一个月',
  THREE_MONTH: '三个月',
  TWELVE_MONTH: '12个月',
  THREE_YEAR: '三年',
  FIVE_YEAR: '五年',
} as const
/**
 * 有效的时间类型
 */
export const efficientTimeNumber = {
  ONE_MONTH: 30,
  THREE_MONTH: 90,
  TWELVE_MONTH: 365,
  THREE_YEAR: 365 * 3,
  FIVE_YEAR: 365 * 5,
} as const
