import { OrderFormSubmitType } from '@/views/settlement/types'
import { get, post, put, del } from '../http'
import { AddressListItem, Coordinate, GeometryType } from './types'
import { useAmapKeyStore } from '@/store/modules/amapKey'
import { type GeometryOptionalCoordinates, useLocationStore } from '@/store/modules/location'

/**
 * @description: 查询地址列表
 */
export const doGetAddressList = (): Promise<any> => {
  return get({ url: `gruul-mall-user/user/address`, params: { size: 9999 } })
}

/**
 * @description: 根据id查询地址
 * @param {string} addressId
 */
export const doGetAddress = (addressId: string) => {
  return get({ url: `gruul-mall-user/user/address/${addressId}` })
}
/**
 * @description: 下单页查询默认地址
 */
export const doGetDefaultAddress = (): Promise<any> => {
  return get({ url: 'gruul-mall-user/user/address/default' })
}
/**
 * @description: 新增收货地址
 * @param {any} data
 */
export const doNewAddress = (data: any) => {
  return post({ url: 'gruul-mall-user/user/address', data })
}
/**
 * @description: 编辑收货地址
 * @param {string} addressId
 */
export const doEditAddress = (addressId: string, data: any) => {
  return put({ url: `gruul-mall-user/user/address/${addressId}`, data })
}
/**
 * @description: 删除收货地址
 * @param {string} addressId
 */
export const doDelAddress = (addressId: string) => {
  return del({ url: `gruul-mall-user/user/address/${addressId}` })
}
/**
 * @description: 设置默认地址
 * @param {string} addressId
 * @returns {*}
 */
export const doSetAddressDefault = (addressId: string, isDefault: boolean) => {
  return put({ url: `gruul-mall-user/user/address/${addressId}/${isDefault}` })
}
/**
 * @description: 查询订单同城配送信息
 */
export const doPostIntraCity = (data: any) => {
  return post({ url: `addon-ic/intraCityDistribution/config/calculate/cost`, data })
}
/**
 * 生成价格预算
 * @param {any} data
 */
export const doPostBudget = (data: OrderFormSubmitType): Promise<any> => {
  return post({ url: 'gruul-mall-order/order/budget', data })
}

/**
 * 高德 根据经纬度获取周边POI
 */
export const doGetPoiNearby = (data: { location: string; radius: number; types: string }) => {
  return new Promise<{
    status: string
    pois: AddressListItem[]
    infocode: string
  }>((resolve, reject) => {
    fetch(
      `https://restapi.amap.com/v5/place/around?key=${useAmapKeyStore().getAmapWebServiceKey}&location=${data.location}&radius=${data.radius}&types=${
        data.types
      }`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      },
    )
      .then((res) => res.json())
      .then((res) => {
        resolve(res)
      })
      .catch((err) => reject(err))
  })
}

/**
 * 高德 根据关键词以及经纬度获取周边地址列表
 */
export const doGetAddressListByKeywordsAndLocation = (data: { keywords: string; location: string }) => {
  return new Promise<{
    status: string
    tips: AddressListItem[]
    infocode: string
  }>((resolve, reject) => {
    fetch(
      `https://restapi.amap.com/v3/assistant/inputtips?key=${useAmapKeyStore().getAmapWebServiceKey}&keywords=${data.keywords}&location=${
        data.location
      }`,
      {
        method: 'GET',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      },
    )
      .then((res) => res.json())
      .then((res) => resolve(res))
      .catch((err) => reject(err))
  })
}

/**
 * 高德 逆地理编码 需要web服务高德key
 */
export const doGetAddressByLocation = (location: Coordinate, saveStore = true) => {
  return new Promise<GeometryOptionalCoordinates>((resolve, reject) => {
    fetch(`https://restapi.amap.com/v3/geocode/regeo?key=${useAmapKeyStore().getAmapWebServiceKey}&location=${location.toString()}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
    })
      .then((res) => res.json())
      .then((result) => {
        if (result.status === '1' && result.infocode === '10000') {
          let tempObj = result.regeocode
          const area = [] as unknown as [string, string, string?]
          if (tempObj.addressComponent.province.length) {
            area.push(tempObj.addressComponent.province)
          }
          if (tempObj.addressComponent.city.length) {
            area.push(tempObj.addressComponent.city)
          }
          if (tempObj.addressComponent.district.length) {
            area.push(tempObj.addressComponent.district)
          }
          if (area.length < 2) {
            area.push('')
          }
          const resp = {
            location: {
              type: GeometryType.Point,
              coordinates: location,
            },
            address: tempObj.formatted_address
              .replace(tempObj.addressComponent.province, '')
              .replace(tempObj.addressComponent.city, '')
              .replace(tempObj.addressComponent.district, ''),
            area,
          } as GeometryOptionalCoordinates
          if (saveStore) {
            useLocationStore().SET_ALL(resp)
          }
          resolve(resp)
        } else {
          if (saveStore) {
            useLocationStore().SET_ADDRESS('获取地址失败，点击手动设置地址')
          }
          reject(result)
        }
        resolve(result)
      })
      .catch((err) => {
        console.log('获取位置信息失败', err)
        if (saveStore) {
          useLocationStore().SET_ADDRESS('获取地址失败，点击手动设置地址')
        }
        reject(err)
      })
  })
}
