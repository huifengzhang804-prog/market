import api from '@/libs/request'

/**
 * 获取快递公司列表,json文件存到服务器
 * 取消'@/assets/json/data.json'的引用
 * 减少小程序包体积
 */
export const getCourierServicesCompanyList = () => {
  return api.get(`data.json`)
}
