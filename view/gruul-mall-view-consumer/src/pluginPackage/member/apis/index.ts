import api from '@/libs/request'
/**
 * 获取会员中心信息
 * @param params
 */
export const doGetMemberCardInfo = () => {
  return api.get(`gruul-mall-user/user/member/card/info`)
}
/**
 * 付费会员级别列表----移动端
 * @param params
 */
export const doAddonMemberPaidMemberRank = (params?: any) => {
  return api.get(`addon-member/paid/member/rank`, params)
}
/**
 * 获取会员所需成长值
 * @param {number} rankCode
 */
export const doGetMemberGrow = (rankCode: number) => {
  return api.get(`gruul-mall-user/user/free/member/next?rankCode=${rankCode}`)
}
