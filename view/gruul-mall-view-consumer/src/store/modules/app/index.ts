import { defineStore } from 'pinia'
import state from './state'
import storage from '@/utils/storage'
import type { StateType } from './state'
import type { AppPluginName } from '@/apis/sys/model'

export const useAppStore = defineStore('appStore', {
  state: () => state,
  actions: {
    SET_PLUGIN_LIST(val: StateType['registerPlugin']) {
      this.registerPlugin = val
      storage.set('plugin', val)
    },
    // 检测插件是否存在
    GET_PLUGIN(pluginName: AppPluginName) {
      return this.getPluginList.includes(pluginName)
    },
    SET_ROLE_MENUS(val: StateType['roleMenus']) {
      this.roleMenus = val
      storage.set('roleMenus', val)
    },
    IS_ANCHOR() {
      // 是否是主播
      return this.roleMenus.includes('ANCHOR')
    },
  },
  getters: {
    getPluginList(state): StateType['registerPlugin'] {
      if (state.registerPlugin?.length) {
        return state.registerPlugin
      } else {
        return storage.get('plugin') || []
      }
    },
  },
})
