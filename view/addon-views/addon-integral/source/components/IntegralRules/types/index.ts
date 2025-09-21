export { IntegralRulesParameter, IntegralGainRule, RulesParameter, GAINRULETYPE }
/**
 * @param useRule: string 积分使用规则
 * @param indate: number 积分有效期
 * @param ruleInfo: string 积分值信息
 * @param integralGainRule: array 积分值信息
 */
interface IntegralRulesParameter {
  useRule: string
  indate: number
  ruleInfo: string
  integralGainRule: IntegralGainRule[]
}
/**
 * @param rulesParameter: object 获取积分规则参数
 * @param gainRuleType: enum 	获取积分规则类型
 * @param open: boolean 是否开启
 */
interface IntegralGainRule {
  rulesParameter: RulesParameter
  gainRuleType: keyof typeof GAINRULETYPE
  open: boolean
}
/**
 * @param basicsValue: number 基础值
 * @param extendValue:  HashMap<连续登入天数,对应积分值>
 */
interface RulesParameter {
  basicsValue?: number
  extendValue?: {
    [x: number]: number
  }
  consumeJson?: [{ consumeGrowthValueType: string; isSelected: boolean; orderQuantityAndAmount: string; presentedGrowthValue: string }]
}
enum GAINRULETYPE {
  SHARE = '分享',
  LOGIN = '登入',
  SING_IN = '签到',
  CONSUME = '消费获得',
}
