import api from '@/libs/request'
import { type Address, GeometryType } from '@/apis/address/type'
import type { Pagination } from '@/utils/types'

/**
 * 查询地址列表
 */
export const doGetAddressList = <T extends keyof typeof GeometryType = GeometryType.Point>() => {
  return api.get<Pagination<Address<T>[]>>('gruul-mall-user/user/address', {
    size: 100,
  })
}
/**
 * 根据id查询地址
 * @param {string} addressId
 */
export const doGetAddress = <T extends keyof typeof GeometryType = GeometryType.Point>(addressId: string) => {
  return api.get<Address<T>>(`gruul-mall-user/user/address/${addressId}`)
}
/**
 * 下单页查询默认地址
 */
export const doGetDefaultAddress = <T extends keyof typeof GeometryType = GeometryType.Point>() => {
  return api.get<Address<T>>('gruul-mall-user/user/address/default')
}
/**
 * 新增收货地址
 */
export const doNewAddress = (data: Address) => {
  return api.post('gruul-mall-user/user/address', data)
}
/**
 * 编辑收货地址
 * @param {string} addressId
 */
export const doEditAddress = (addressId: Long, data: Address) => {
  return api.put(`gruul-mall-user/user/address/${addressId}`, data)
}
/**
 * 删除收货地址
 * @param {string} addressId
 */
export const doDelAddress = (addressId: Long) => {
  return api.delete(`gruul-mall-user/user/address/${addressId}`)
}
/**
 * 设置默认地址
 * @param {string} addressId
 */
export const doSetAddressDefault = (addressId: string, isDefault: boolean) => {
  return api.put(`gruul-mall-user/user/address/${addressId}/${isDefault}`)
}
