import type { CouponJointType, CouponDTO } from './index'

/**
 * 动态设置表单校验
 */
export const setRules = (type: CouponJointType, rules: Record<keyof CouponDTO, any[]>, ruleForm: any) => {
  if (type === 'PRICE_REDUCE') {
    setPriceReduce(type, rules)
    return
  }
  if (type === 'PRICE_DISCOUNT') {
    setPriceDiscount(type, rules)
    return
  }
  if (type === 'REQUIRED_PRICE_REDUCE') {
    setPriceDiscount(type, rules)
    setRequiredPriceReduce(type, rules, ruleForm)
    return
  }
  if (type === 'REQUIRED_PRICE_DISCOUNT') {
    const requiredAmount = [
      { required: true, message: '请输入金额', trigger: 'blur' },
      {
        validator: (rule: any, value: string, callback: (str?: Error) => void) => {
          if (Number(value) <= 0) {
            callback(new Error('金额必须大于0'))
          } else {
            callback()
          }
        },
        trigger: 'blur',
      },
    ]
    const discount = [
      { required: true, message: '请输入优惠券规则', trigger: 'blur' },
      {
        validator: (rule: any, value: string, callback: (str?: Error) => void) => {
          if (Number(value) <= 0 && Number(value) < 100) {
            callback(new Error('0.1~99之间'))
          } else {
            callback()
          }
        },
        trigger: 'blur',
      },
    ]
    rules.requiredAmount = requiredAmount
    rules.discount = discount
  }
}
const setPriceReduce = (type: CouponJointType, rules: Record<keyof CouponDTO, any[]>) => {
  if (rules.discount.length) {
    rules.discount[0].required = false
  }
  if (rules.requiredAmount.length) {
    rules.discount[0].required = false
  }
  const amount = [
    { required: true, message: '请输入优惠券规则', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: (str?: Error) => void) => {
        if (Number(value) <= 0) {
          callback(new Error('金额必须大于0'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ]
  rules.amount = amount
}
const setPriceDiscount = (type: CouponJointType, rules: Record<keyof CouponDTO, any[]>) => {
  if (rules.requiredAmount.length) {
    rules.discount[0].required = false
  }
  if (rules.amount.length) {
    rules.amount[0].required = false
  }
  const discount = [
    { required: true, message: '请输入优惠券规则', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: (str?: Error) => void) => {
        if (Number(value) <= 0 && Number(value) < 100) {
          callback(new Error('0.1~99之间'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ]
  rules.discount = discount
}
const setRequiredPriceReduce = (type: CouponJointType, rules: Record<keyof CouponDTO, any[]>, ruleForm: any) => {
  if (rules.discount.length) {
    rules.discount[0].required = false
  }
  const requiredAmount = [
    { required: true, message: '请输入金额', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: (str?: Error) => void) => {
        if (Number(value) <= 0) {
          callback(new Error('金额必须大于0'))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ]
  const amount = [
    ...requiredAmount,
    {
      validator: (rule: any, value: string, callback: (str?: Error) => void) => {
        if (Number(ruleForm.requiredAmount) - Number(value) <= 0) {
          callback(new Error(`满减金额必须小于${Number(ruleForm.requiredAmount)}`))
        } else {
          callback()
        }
      },
      trigger: 'blur',
    },
  ]
  rules.amount = amount
  rules.requiredAmount = requiredAmount
}
