import { GeometryType, type Geometry } from '@/apis/address/types'
import { defineStore } from 'pinia'

// Geometry 中的 coordinates 变为可选的
export type GeometryOptionalCoordinates = {
  location?: Geometry
  address: string
  area: [string, string, string?]
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
    SET_ALL(data: GeometryOptionalCoordinates) {
      this.SET_LOCATION(data.location)
      this.SET_ADDRESS(data.address)
      this.SET_AREA(data.area)
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
  },
})
