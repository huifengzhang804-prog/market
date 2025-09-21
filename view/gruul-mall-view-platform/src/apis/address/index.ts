import { useAmapKeyStore } from '@/store/modules/amapKey'
import { type AddressListItem, type Coordinate, type Geometry, GeometryOptionalCoordinates, GeometryType } from '../afs/type'

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
            `https://restapi.amap.com/v5/place/around?key=${useAmapKeyStore().getAmapWebServiceKey}&location=${data.location}&radius=${
                data.radius
            }&types=${data.types}`,
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
export const doGetAddressByLocation = (location: Coordinate) => {
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
                    resolve(resp)
                } else {
                    reject(result)
                }
                resolve(result)
            })
            .catch((err) => {
                console.log('获取位置信息失败', err)
                reject(err)
            })
    })
}
