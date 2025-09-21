import api from '@/libs/request'
/**
 * 查看剩余积分
 * @param {keyof} ruleType SHARE = '分享' LOGIN = '登入' SING_IN = '签到'
 */
export const doGetUserIntegralSystemtotal = () => {
  return api.get(`gruul-mall-user/user/integral/system/total`)
}
