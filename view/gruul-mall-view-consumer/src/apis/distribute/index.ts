import api from '@/libs/request'

/**
 * 用户扫码之后成为分销员 推荐人为当前code所属分销商
 */
export const doPostBinding = (code: string) => {
  return api.post(`addon-distribute/distribute/distributor/code/${code}`)
}
