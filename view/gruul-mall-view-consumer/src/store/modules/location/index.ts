import { GeometryType, type Geometry } from '@/apis/address/type'
import { defineStore } from 'pinia'

// Geometry 中的 coordinates 变为可选的
export type GeometryOptionalCoordinates = {
  location?: Geometry
  address: string
  area: [string, string, string?]
  name?: string
}

export const useLocationStore = defineStore('useLocationStore', {
  state: (): GeometryOptionalCoordinates => {
    return {
      location: {
        type: '' as GeometryType.Point,
        coordinates: [] as unknown as [number, number],
      },
      address: '获取地址失败，点击手动设置地址',
      area: [] as unknown as [string, string, string?], // 省市区
      name: '',
    }
  },
  actions: {
    // 设置定位信息
    SET_LOCATION(location?: Geometry) {
      if (location) {
        this.location = { ...this.location, ...location }
      }
    },
    SET_ADDRESS(address: string) {
      this.address = address
    },
    SET_AREA(area: [string, string, string?]) {
      this.area = area
    },
    SET_NAME(name: string) {
      this.name = name
    },
    SET_ALL(data: GeometryOptionalCoordinates) {
      this.SET_LOCATION(data.location)
      this.SET_ADDRESS(data.address)
      this.SET_AREA(data.area)
      this.SET_NAME(data?.name || '')
    },
  },
  getters: {
    getterLocation(state) {
      return state.location
    },
    getterAddress(state) {
      return state.address
    },
    getterArea(state) {
      return state.area
    },
    getterName(state) {
      return state.name
    },
  },
})
