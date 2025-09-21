/**
 * 储值管理
 * @param ladderMoney 充值金额
 * @param  presentedMoney 赠送金额
 * @param presentedGrowthValue 赠送成长值
 */
export interface SavingManageItem {
    id?: string
    ladderMoney: string
    presentedGrowthValue: string
    presentedMoney: string
}
