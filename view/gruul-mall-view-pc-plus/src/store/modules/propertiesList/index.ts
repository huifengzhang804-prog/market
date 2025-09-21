import { defineStore } from 'pinia'
import Storage from '@/libs/storage'

const storage = new Storage()
export const usePropertiesListStore = defineStore('propertiesListStore', {
  state: (): { propertiesList: PropertiesList } => {
    return {
      propertiesList: storage.getItem('propertiesList') || {},
    }
  },
  actions: {
    SET_PROPERTIESLIST(payload: PropertiesList) {
      this.propertiesList = payload
      storage.setItem('propertiesList', this.propertiesList, 60 * 60 * 24)
    },
    DEL_PROPERTIESLIST() {
      this.propertiesList = {}
      storage.removeItem('propertiesList')
    },
  },
  getters: {
    getterPropertiesList: (state) => {
      return state.propertiesList
    },
  },
})

export interface PropertiesList {
  bottomComponents?: Obj
  components?: Obj[]
  otherData?: Obj
  topComponents?: Obj[]
}

type Obj = Record<string, any>
