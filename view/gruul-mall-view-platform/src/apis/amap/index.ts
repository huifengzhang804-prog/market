import { get, post, put, del, patch } from '../http'
import { type AmapKeys, useAmapKeyStore } from '@/store/modules/amapKey'
import { R } from '../http.type'
import { Geometry, GeometryType } from '../afs/type'

const amapKeyDefaultResolve = {
    code: 200,
    data: {
        amapKey: import.meta.env.VITE_GD_MAP_KEY,
        amapWebServiceKey: import.meta.env.VITE_GD_WEB_MAP_KEY,
    },
    msg: '',
    success: true,
}
/**
 * 接口获取 高德 的各项 key
 */
export const doGetAmapKey = () => {
    return new Promise<R<AmapKeys>>((resolve) => {
        post<AmapKeys>({
            url: 'gruul-mall-carrier-pigeon/geo/conf',
        })
            .then((res) => {
                if (res.code === 200 && res.data) {
                    resolve(res)
                } else {
                    resolve(amapKeyDefaultResolve)
                }
            })
            .catch(() => {
                resolve(amapKeyDefaultResolve)
            })
    })
}

/**
 * 更新 高德 的各项 key
 */
export const doEditAmapKey = (data: AmapKeys) => {
    return post<AmapKeys>({
        url: 'gruul-mall-carrier-pigeon/geo/edit',
        data,
    })
}

/**
 * 高德地图 IP 定位
 * 文档地址: https://lbs.amap.com/api/webservice/guide/api/ipconfig
 */
export const ipToAddress = (ip?: string) => {
    return new Promise<Geometry>((resolve, reject) => {
        fetch(`https://restapi.amap.com/v3/ip?key=${useAmapKeyStore().getAmapWebServiceKey}${ip ? `&ip=${ip}` : ''}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        })
            .then((res) => res.json())
            .then((result) => {
                if (result.status === '1' && result.infocode === '10000') {
                    // 左下坐标
                    const locationLeftBottom: string[] = result.rectangle?.split(';')?.[0]?.split(',') || []
                    // 右上坐标
                    const locationRightTop: string[] = result.rectangle?.split(';')?.[1]?.split(',') || []
                    let locationCenter = [] as unknown as [number, number]
                    if (locationLeftBottom.length && locationRightTop.length) {
                        // 保留八位小数
                        locationCenter = [
                            Number(((Number(locationLeftBottom[0]) + Number(locationRightTop[0])) / 2).toFixed(8)),
                            Number(((Number(locationLeftBottom[1]) + Number(locationRightTop[1])) / 2).toFixed(8)),
                        ]
                    }
                    console.log('高德ip获取位置信息成功,城市中心坐标:', locationCenter)
                    resolve({
                        type: GeometryType.Point,
                        // 坐标只能定位到城市,所以选取城市中心点
                        coordinates: locationCenter,
                    })
                } else {
                    console.log('高德ip获取位置信息失败', result)
                    reject(result)
                }
            })
            .catch((err) => reject(err))
    })
}

/**
 * 获取当前定位
 */
export const getLocation = () => {
    return new Promise<{ longitude: number; latitude: number; address?: string }>((resolve, reject) => {
        if (window?.navigator?.geolocation) {
            window.navigator.geolocation.getCurrentPosition(
                (position) => {
                    console.log('浏览器高精度定位成功:', position.coords)
                    resolve({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude,
                    })
                },
                (error) => {
                    console.log('浏览器获取定位失败,尝试使用ip定位', error)
                    // 尝试使用高德地图ip定位
                    ipToAddress()
                        .then((res) => {
                            resolve({
                                latitude: res.coordinates[1],
                                longitude: res.coordinates[0],
                            })
                        })
                        .catch((err) => {
                            console.log('ip定位失败,彻底定位失败', err)
                            reject(err)
                        })
                },
                {
                    enableHighAccuracy: true, //是否要求高精度的地理位置信息
                    timeout: 3001, //对地理位置信息的获取操作做超时限制，如果再该事件内未获取到地理位置信息，将返回错误
                },
            )
        } else {
            // 尝试使用高德地图ip定位
            ipToAddress()
                .then((res) => {
                    resolve({
                        latitude: res.coordinates[1],
                        longitude: res.coordinates[0],
                    })
                })
                .catch((err) => {
                    console.log('ip定位失败,彻底定位失败', err)
                    reject(err)
                })
        }
    })
}
