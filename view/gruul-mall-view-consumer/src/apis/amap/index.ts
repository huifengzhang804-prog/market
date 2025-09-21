import api from '@/libs/request'
import { type GeometryOptionalCoordinates, useLocationStore } from '@/store/modules/location'
import { type Geometry, GeometryType } from '@/apis/address/type'
import { type AmapKeys, useAmapKeyStore } from '@/store/modules/amapKey'
import { type R } from '@/utils/types'

/**
 * 接口获取 高德 的各项 key
 */
export const doGetAmapKey = () => {
  return new Promise<R<AmapKeys>>((resolve, reject) => {
    api
      .post<AmapKeys>('gruul-mall-carrier-pigeon/geo/conf')
      .then((res) => {
        if (res.code === 200 && res.data) {
          resolve(res)
        } else {
          reject({ res, msg: '服务器获取高德key失败,请前往后台配置' })
        }
      })
      .catch((error) => {
        reject(error)
      })
  })
}

interface LocationInfo {
  longitude: Long
  latitude: Long
  address?: string
  name?: string
  area?: [string, string, string?]
}

/**
 * 逆地址解析 可选择存储 store
 * @param {Array} data.location location[0]:经度,[-180,180],负数表示西经。 location[1]:纬度,[-90,90],负数表示南纬
 * 文档地址: https://lbs.amap.com/api/webservice/guide/api/georegeo
 */
export const locToAddress = (data: LocationInfo, saveStore = true) => {
  return new Promise<GeometryOptionalCoordinates>((resolve, reject) => {
    if (data?.area?.length) {
      return resolve({
        location: {
          type: GeometryType.Point,
          coordinates: [Number(data.longitude), Number(data.latitude)],
        },
        address:
          data.address
            ?.replace(data.area[0], '')
            ?.replace(data.area[1], '')
            ?.replace(data.area?.[2] || '', '') || '',
        area: data.area,
        name: data.name || '',
      })
    }
    uni.request({
      // 逆地理编码 需要web服务高德key
      url: `https://restapi.amap.com/v3/geocode/regeo?key=${useAmapKeyStore().getAmapWebServiceKey}&location=${data.longitude},${data.latitude}`,
      success: (res: any) => {
        const result = res.data
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
          const originalAddress = data.address
            ?.replace(tempObj.addressComponent.province, '')
            ?.replace(tempObj.addressComponent.city, '')
            ?.replace(tempObj.addressComponent.district, '')
          const resp = {
            location: {
              type: GeometryType.Point,
              coordinates: [data.longitude, data.latitude],
            },
            address:
              originalAddress ||
              tempObj.formatted_address
                .replace(tempObj.addressComponent.province, '')
                .replace(tempObj.addressComponent.city, '')
                .replace(tempObj.addressComponent.district, ''),
            area: data.area || area,
            name: data.name,
          } as GeometryOptionalCoordinates
          if (saveStore) {
            useLocationStore().SET_ALL(resp)
          }
          resolve(resp)
        } else {
          if (saveStore) {
            useLocationStore().SET_ADDRESS('获取地址失败，点击手动设置地址')
          }
          reject(res)
        }
      },
      fail: (err) => {
        console.log('获取位置信息失败', err)
        if (saveStore) {
          useLocationStore().SET_ADDRESS('获取地址失败，点击手动设置地址')
        }
        reject(err)
      },
    })
  })
}

/**
 * 高德地图 IP 定位
 * 文档地址: https://lbs.amap.com/api/webservice/guide/api/ipconfig
 */
const ipToAddress = (ip?: string) => {
  return new Promise<Geometry>((resolve, reject) => {
    uni.request({
      url: `https://restapi.amap.com/v3/ip?key=${useAmapKeyStore().getAmapWebServiceKey}${ip ? `&ip=${ip}` : ''}`,
      success: (res: any) => {
        const result = res.data
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
          console.log('高德ip获取位置信息失败', res)
          reject(res)
        }
      },
      fail: (err) => {
        console.log('高德ip获取位置信息请求失败', err)
        useLocationStore().SET_ADDRESS('获取地址失败，点击手动设置地址')
        reject(err)
      },
    })
  })
}

/**
 * 坐标系类型
 */
enum CoordinateSystemType {
  gps = 'gps', // GPS(WGS84) 坐标系
  mapbar = 'mapbar', // Mapbar 坐标系
  baidu = 'baidu', // 百度坐标系(BD09) 坐标系
  autonavi = 'autonavi', // 高德坐标系(AMAP) 坐标系
}
/**
 * 坐标转换
 * 非高德坐标（GPS 坐标、mapbar 坐标、baidu 坐标）转换成高德坐标
 * 文档地址: https://lbs.amap.com/api/webservice/guide/api/convert
 * @param {Array} coords 经纬度坐标[经度,纬度], 经纬度小数点后不得超过6位。
 * @param {string} type 坐标的类型,可选值：gps;mapbar;baidu;autonavi(不进行转换)
 */
const locConvert = (coords: [number, number], type: CoordinateSystemType = CoordinateSystemType.gps) => {
  return new Promise<Geometry>((resolve) => {
    uni.request({
      url: `https://restapi.amap.com/v3/assistant/coordinate/convert?key=${
        useAmapKeyStore().getAmapWebServiceKey
      }&locations=${coords}&coordsys=${type}`,
      success: (res: any) => {
        const result = res.data
        if (result.status === '1' && result.infocode === '10000') {
          const location = result.locations.split(',')
          console.log('坐标转换成功:', location)
          resolve({
            type: GeometryType.Point,
            coordinates: [Number(location[0]), Number(location[1])],
          })
        } else {
          console.log('坐标转换失败,继续使用原坐标', res)
          resolve({
            type: GeometryType.Point,
            coordinates: coords,
          })
        }
      },
      fail: (err) => {
        console.log('坐标转换失败,继续使用原坐标', err)
        resolve({
          type: GeometryType.Point,
          coordinates: coords,
        })
      },
    })
  })
}

/**
 * 获取当前定位
 */
export const getLocation = () => {
  return new Promise<{ longitude: number; latitude: number; address?: string }>((resolve, reject) => {
    if (window?.navigator?.geolocation) {
      window.navigator.geolocation.getCurrentPosition(
        async (position) => {
          console.log('浏览器高精度定位成功:', position.coords)
          try {
            console.log('开始坐标转换:', [position.coords.longitude, position.coords.latitude])
            const { coordinates } = await locConvert([position.coords.longitude, position.coords.latitude])
            resolve({
              latitude: coordinates[1],
              longitude: coordinates[0],
            })
          } catch (error) {
            console.log('坐标转换失败,使用浏览器定位', error)
            resolve({
              latitude: position.coords.latitude,
              longitude: position.coords.longitude,
            })
          }
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
      // 先尝试原有的获取位置信息方式
      uni.getLocation({
        type: 'gcj02',
        isHighAccuracy: true,
        highAccuracyExpireTime: 3001,
        success: (lb) => {
          console.log('uni.getLocation()获取定位成功', lb)
          resolve({
            latitude: lb.latitude,
            longitude: lb.longitude,
          })
        },
        fail: (error) => {
          console.log('uni.getLocation()获取定位失败,尝试使用ip定位', error)
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
      })
    }
  })
}
