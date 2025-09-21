export const formRule = {
  companyName: [
    {
      required: true,
      message: '请输入商户名称',
      trigger: ['change', 'blur'],
    },
    {
      max: 20,
      message: '商户名称20个字以内',
    },
  ],
  name: [
    {
      required: true,
      message: '请输入店铺名称',
      trigger: ['change', 'blur'],
    },
  ],
  logo: [
    {
      required: true,
      message: '请上传店铺logo',
      trigger: ['change', 'blur'],
    },
  ],
  contractNumber: [
    {
      required: true,
      message: '请输入联系方式',
      trigger: ['change', 'blur'],
    },
    {
      pattern: /^1[3456789]\d{9}$/,
      message: '请输入正确的手机号',
    },
  ],
  address: [
    {
      required: true,
      message: '请输入详情地址',
      trigger: ['change', 'blur'],
    },
  ],
  briefing: [
    {
      required: true,
      message: '请输入店铺介绍',
      trigger: ['change', 'blur'],
    },
    {
      min: 5,
      max: 100,
      message: '长度在5-100个字符之间',
    },
  ],
  license: [
    {
      required: true,
      message: '请上传营业执照',
      trigger: ['change', 'blur'],
    },
  ],
  legalPersonIdFront: [
    {
      required: true,
      message: '请上传证件照正面',
      trigger: ['change', 'blur'],
    },
  ],
  legalPersonIdBack: [
    {
      required: true,
      message: '证件照背面',
      trigger: ['change', 'blur'],
    },
  ],
  payee: [
    {
      required: true,
      message: '请输入收款人',
      trigger: ['change', 'blur'],
    },
  ],
  bankName: [
    {
      required: true,
      message: '请输入银行名',
      trigger: ['change', 'blur'],
    },
  ],
  bankAccount: [
    {
      required: true,
      message: '请输入账号',
      trigger: ['change', 'blur'],
    },
    // {
    //     pattern: /^([1-9]{1})(\d{14}|\d{18})$/,
    //     message: '请输入正确的银行卡号',
    // },
  ],
  openAccountBank: [
    {
      required: true,
      message: '请输入开户行',
      trigger: ['change', 'blur'],
    },
  ],
  area: [
    {
      required: true,
      message: '请选择所在地区',
      trigger: ['change'],
      validator: (rule: any, value: string[], callback: any) => {
        return Boolean(value.length)
      },
    },
  ],
  fakeAddress: [
    {
      required: true,
      message: '请填写详细地址',
      trigger: ['change'],
    },
    {
      message: '详细地址不能包含~号',
      trigger: ['change'],
      validator: (rule: any, value: string, callback: any) => {
        return !value.includes('~')
      },
    },
  ],
}
