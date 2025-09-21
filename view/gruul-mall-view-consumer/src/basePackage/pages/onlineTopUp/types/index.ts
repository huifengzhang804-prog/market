/**
 * 充值
 * @params {*} discountsState  	优惠状态 0无优惠 1有优惠
 * @params {*}  ruleJson 充值数据
 * @params {*} switching 储值管理开关 0关闭 1开启
 */
export interface ApiOnlineTopUp {
  discountsState: boolean
  id: string
  ruleJson: OnlineTopUpItem[]
  switching: boolean
}
export interface OnlineTopUpItem {
  id: string
  ladderMoney: string
  presentedGrowthValue: string
  presentedMoney: string
}
