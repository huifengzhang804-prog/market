import { get, post, del } from '@/apis/http'

/**
 * 获取付费会员列表
 */
export const doGetPaidMemberList = (): Promise<any> => {
  return get({
    url: 'addon-member/paid/member/list ',
  })
}
/**
 * 获取会员权益配置
 */
export const doGetAvailableMemberConfig = () => {
  return get({
    url: 'gruul-mall-user/user/member/rights/usable',
  })
}

/**
 * 新增付费会员
 */

export const doPostPaidMember = (data: any) => {
  return post({
    url: 'addon-member/paid/member/save',
    data,
  })
}
export const doPutPaidMember = (data: any) => {
  return post({
    url: 'addon-member/paid/member/update',
    data,
  })
}
/**
 * 获取免费会员配置
 */
export const doGetPaidMember = (id: string) => {
  return get({
    url: `addon-member/paid/member/info?id=${id}`,
  })
}
/**
 * 删除付费会员
 */

export const doDelPaidMember = (id: string) => {
  return del({
    url: `addon-member/paid/member/${id}`,
  })
}
/**
 * 付费会员设置标签
 * @param data 传输参数
 */
export const doPostPaidMemberSetLabel = (data: any) => {
  return post({ url: 'addon-member/paid/member/saveLabel', data })
}
