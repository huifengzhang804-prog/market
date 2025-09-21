import { defineStore } from 'pinia'
import state from './state'
import type { StateType } from './state'
import { AppPluginName } from '@/views/settlement/plugins/types'
import Storage from '@/libs/storage'

const storage = new Storage()

export const useAppStore = defineStore('appStore', {
  state: () => state,
  actions: {
    SET_PLUGIN_LIST(val: StateType['registerPlugin']) {
      this.registerPlugin = val
      storage.setItem('plugin', val)
    },
    // 检测插件是否存在
    GET_PLUGIN(pluginName: AppPluginName) {
      return this.getPluginList.includes(pluginName)
    },
    SET_ROLE_MENUS(val: StateType['roleMenus']) {
      this.roleMenus = val
      storage.setItem('roleMenus', val)
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
        const plugin = storage.getItem('plugin')
        if (plugin) {
          return JSON.parse(plugin)
        }
        return []
      }
    },
  },
})
