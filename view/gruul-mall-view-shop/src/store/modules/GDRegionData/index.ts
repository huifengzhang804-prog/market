import { defineStore } from 'pinia'
import Storage from '@/utils/Storage'
import { useAmapKeyStore } from '../amapKey'

const localStorageExample = new Storage()
export const useGDRegionDataStore = defineStore('GDRegionDataStore', {
    state: (): { GD_RegionData: RegionData[] } => {
        return {
            GD_RegionData: localStorageExample.getItem('GD_RegionData') || [],
        }
    },
    actions: {
        SET_GD_RegionData(payload: RegionData[]) {
            this.GD_RegionData = payload
            localStorageExample.setItem('GD_RegionData', this.GD_RegionData, 60 * 60 * 24)
        },
        DEL_GD_RegionData() {
            this.GD_RegionData = []
            localStorageExample.removeItem('GD_RegionData')
        },
    },
    getters: {
        getterGDRegionData: (state) => {
            return state.GD_RegionData
        },
    },
})

interface RegionData {
    adcode: string
    center: string
    citycode: string
    districts: RegionData[]
    name: string
    level: string
    children: RegionData[]
    label: string
    value: string
}
export const asyncGetGDRegionData = async () => {
    const localData = localStorageExample.getItem('GD_RegionData')
    if (localData) {
        return
    }
    const res = await fetch('https://restapi.amap.com/v3/config/district', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },

        body: `key=${useAmapKeyStore().getAmapWebServiceKey}&keywords=&subdistrict=3&extensions=base`,
    }).then((res) => res.json())
    if (res.status === '1' && res.info === 'OK' && res.infocode === '10000') {
        const data: RegionData[] = res.districts[0].districts
        data.forEach((item) => {
            if (item.citycode.length > 0 && item.districts.length === 1) {
                item.districts = item.districts[0].districts
            } else if (item.districts.length === 0) {
                item.districts.push({
                    adcode: '',
                    districts: [],
                    name: '',
                    center: '',
                    children: [],
                    citycode: '',
                    level: '',
                    label: '',
                    value: '',
                })
            }
        })
        const handle = (regionData: RegionData[]) => {
            regionData.forEach((item) => {
                item.children = JSON.parse(JSON.stringify(item.districts))
                item.label = item.name
                item.value = item.adcode
                // 对重庆市进行特殊处理(重庆郊县与重庆城区子项合并展示)
                if (item.value === '500000' && item.label === '重庆市') {
                    const tempChildren: RegionData[] = []
                    item.children.forEach((i) => {
                        tempChildren.unshift(...i.districts)
                    })
                    item.children = tempChildren
                }
                if (item.children.length > 0) {
                    handle(item.children)
                }
                Reflect.deleteProperty(item, 'center')
                Reflect.deleteProperty(item, 'citycode')
                Reflect.deleteProperty(item, 'districts')
                Reflect.deleteProperty(item, 'level')
                Reflect.deleteProperty(item, 'name')
                Reflect.deleteProperty(item, 'adcode')
            })
        }
        handle(data)
        useGDRegionDataStore().SET_GD_RegionData(data)
    }
}
