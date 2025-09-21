enum SECONDS_KILL_STATUS {
  NOT_STARTED = 'NOT_STARTED',
  PROCESSING = 'PROCESSING',
  OVER = 'OVER',
  ILLEGAL_SELL_OFF = 'ILLEGAL_SELL_OFF',
}
export type SecondsKillJointType = keyof typeof SECONDS_KILL_STATUS

/**
 * @description:适用优惠
 */
enum APPLY_TYPES {
  'APPLY_USER_PRICE' = '会员价',
  'APPLY_COUPON' = '优惠劵',
  'APPLY_FULL_REDUCED' = '满减',
}
