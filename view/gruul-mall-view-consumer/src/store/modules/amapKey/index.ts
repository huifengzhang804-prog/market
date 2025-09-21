import { defineStore } from 'pinia'

export interface AmapKeys {
  amapKey: string
  amapWebServiceKey: string
  amapsecretKey?: string
}

export const useAmapKeyStore = defineStore('useAmapKeyStore', {
  state: (): AmapKeys => {
    return {
      amapKey: '', // 高德 JSAPI key
      amapWebServiceKey: '', // 高德 Web服务 key
      amapsecretKey: '', // 高德 Web服务 安全密钥
    }
  },
  actions: {
    // 设置全部 key
    SET_ALL_KEY({ amapKey, amapWebServiceKey, amapsecretKey }: AmapKeys) {
      this.SET_AMAP_KEY(amapKey)
      this.SET_AMAP_SAFE_CODE_PROXY(amapsecretKey)
      this.SET_AMAP_WEB_SERVICE_KEY(amapWebServiceKey)
    },
    // 设置高德 JSAPI key
    SET_AMAP_KEY(key?: string) {
      this.amapKey = key || import.meta.env.VITE_GD_MAP_KEY
    },
    // 设置高德 JSAPI 安全密钥的 nginx 转发地址 amapsecretKey 存在则使用本地 securityJsCode 否则使用代理
    SET_AMAP_SAFE_CODE_PROXY(code?: string) {
      // #ifdef H5
      if (!code) {
        // @ts-ignore-next-line
        window._AMapSecurityConfig = {
          serviceHost: `${import.meta.env.VITE_WEB_URL}${import.meta.env.VITE_WEB_URL.endsWith('/') ? '' : '/'}_AMapService/`,
        }
      } else {
        // @ts-ignore-next-line
        window._AMapSecurityConfig = {
          securityJsCode: code,
        }
      }
      // #endif
    },

    // 设置高德 Web服务 key
    SET_AMAP_WEB_SERVICE_KEY(key?: string) {
      this.amapWebServiceKey = key || import.meta.env.VITE_GD_WEB_MAP_KEY
    },
  },
  getters: {
    getAmapKey(state) {
      return state.amapKey
    },
    getAmapWebServiceKey(state) {
      return state.amapWebServiceKey
    },
  },
})
