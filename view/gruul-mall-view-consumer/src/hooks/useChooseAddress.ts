import type { AddressItemType } from '@/apis/address/model'
import type { Address } from '@/apis/address/type'

// 将Address重命名成TConfirmAddress导出
export type TConfirmAddress = Address

// 确认订单类型
type TCallGetConfirmAddress = (e: Address) => void
// 确认订单函数
let callGetConfirmAddress: TCallGetConfirmAddress | null
//删除地址回调
let callGetDelAddress: (() => void) | null
// 订单按钮函数
let callGetOrderBtnAddress: TCallGetConfirmAddress | null

// 地区函数
let callGetAreasAddress: TCallGetConfirmAddress | null

// 积分函数
let callGetInteralAreasAddress: TCallGetConfirmAddress | null

// 积分列表
let callGetInteralListAddresss: TCallGetConfirmAddress | null
export const useChosseAddress = () => {
  // 确认订单地址处理
  const confirmOredrAddress = () => {
    const setConfirmAddress = (e: Address) => {
      callGetConfirmAddress?.(e)
    }
    const getConfirmAddress = (fn: TCallGetConfirmAddress) => {
      callGetConfirmAddress = fn
    }
    const offConfirmAddress = () => (callGetConfirmAddress = null)
    const setEdlAddress = () => {
      callGetDelAddress?.()
    }
    const getEdlAddress = (fn: () => void) => {
      callGetDelAddress = fn
    }
    const offEdlAddress = () => (callGetDelAddress = null)
    return {
      setConfirmAddress,
      getConfirmAddress,
      offConfirmAddress,
      setEdlAddress,
      getEdlAddress,
      offEdlAddress,
    }
  }

  // 订单按钮地址处理
  const orderBtnAddress = () => {
    const setOrderBtnAddress = (e: Address) => {
      callGetOrderBtnAddress?.(e)
    }
    const getOrderBtnAddress = (fn: TCallGetConfirmAddress) => {
      callGetOrderBtnAddress = fn
    }
    const offOrderBtnAddress = () => (callGetOrderBtnAddress = null)
    return {
      offOrderBtnAddress,
      getOrderBtnAddress,
      setOrderBtnAddress,
    }
  }

  // 地区地址处理
  const areasAddress = () => {
    const setAreasAddress = (e: Address) => {
      callGetAreasAddress?.(e)
    }
    const getAreasAddress = (fn: TCallGetConfirmAddress) => {
      callGetAreasAddress = fn
    }
    const offAreasAddress = () => (callGetAreasAddress = null)
    return {
      setAreasAddress,
      getAreasAddress,
      offAreasAddress,
    }
  }

  // 积分订单处理
  const interalAreasAddress = () => {
    const setInteralAreasAddress = (e: Address) => {
      callGetInteralAreasAddress?.(e)
    }
    const getInteralAreasAddress = (fn: TCallGetConfirmAddress) => {
      callGetInteralAreasAddress = fn
    }
    const offInteralAreasAddress = () => (callGetInteralAreasAddress = null)
    return {
      setInteralAreasAddress,
      getInteralAreasAddress,
      offInteralAreasAddress,
    }
  }

  // 积分列表处理
  const interalListAddress = () => {
    const setInteralListAddress = (e: Address) => {
      callGetInteralListAddresss?.(e)
    }
    const getnteralListAddress = (fn: TCallGetConfirmAddress) => {
      callGetInteralListAddresss = fn
    }
    const offInteralListAddress = () => (callGetInteralListAddresss = null)
    return {
      setInteralListAddress,
      getnteralListAddress,
      offInteralListAddress,
    }
  }
  return {
    confirmOredrAddress,
    orderBtnAddress,
    areasAddress,
    interalAreasAddress,
    interalListAddress,
  }
}
