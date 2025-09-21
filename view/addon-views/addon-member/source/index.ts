export interface ApiPaidMemberItem {
  id: string
  paidMemberName: string
  paidRuleJson: ApiPaidMemberRule[]
}
export interface ApiPaidMemberRule {
  price: number
  effectiveDurationType: EFFECTIVEDURATIONTYPE
}

export enum EFFECTIVEDURATIONTYPE {
  ONE_MONTH,
  THREE_MONTH,
  TWELVE_MONTH,
  THREE_YEAR,
  FIVE_YEAR,
}
/**
 * 会员权益状态
 * @param GOODS_DISCOUNT 商品抵扣
 * @param INTEGRAL_MULTIPLE 积分加倍
 * @param LOGISTICS_DISCOUNT 物流优惠
 * @param PRIORITY_SHIPMENTS 优先发货
 * @param QUICKNESS_AFS 极速售后
 * @param EXCLUSIVE_SERVICE 专属客服
 * @param USER_DEFINED 自定义
 */
export enum MEMBERBENEFITSTATUS {
  GOODS_DISCOUNT,
  INTEGRAL_MULTIPLE,
  LOGISTICS_DISCOUNT,
  PRIORITY_SHIPMENTS,
  QUICKNESS_AFS,
  EXCLUSIVE_SERVICE,
  USER_DEFINED,
}
