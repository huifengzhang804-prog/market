import { createSSRApp } from 'vue'
import { qszrUI } from '@/components/qszr-core/packages/components'
import uView from './uni_modules/vk-uview-ui'
import { getPlugin } from '@/libs/sysConfig'
import { StompStarter } from '@/hooks/stomp/StompStarter'
import { getCdnUrl } from '@/utils'
import store from '@/store/global'
import App from './App.vue'
import { useUserStore } from './store/modules/user'
import { useAmapKeyStore } from './store/modules/amapKey'
// #ifdef H5
import VConsole from 'vconsole'
// #endif
// #ifdef APP-PLUS
import devTools from './devTools/index.js'
import devToolsConfig from './devTools/config.js'
// import mpDevBubble from './devTools/core/components/mpDevBubble.vue'
import devToolsVueMixin from './devTools/core/proxy/vueMixin.js'
// #endif
import { doGetAmapKey, getLocation, locToAddress } from './apis/amap'

// #ifdef H5
if (import.meta.env.MODE !== 'production') {
  new VConsole()
}
// #endif

const amapKeyStore = useAmapKeyStore(store)
doGetAmapKey()
  .then((res) => {
    console.log(res)
    if (res.data) {
      amapKeyStore.SET_ALL_KEY(res.data)
    }
  })
  .catch((err) => {
    console.log('获取远程高德配置失败,使用本地静态配置', err)
    amapKeyStore.SET_AMAP_KEY()
    amapKeyStore.SET_AMAP_SAFE_CODE_PROXY(import.meta.env.VITE_GD_MAP_SAFECODE)
    amapKeyStore.SET_AMAP_WEB_SERVICE_KEY()
  })
  .finally(async () => {
    const lb = await getLocation()
    locToAddress(lb)
  })

const $userStore = useUserStore(store)
/**
 * navigateTo拦截器，只针对微信小程序端生效
 */
const navigateToInterceptor = () => {
  // #ifdef MP-WEIXIN
  const originalNavigateTo = uni.navigateTo.bind(uni)
  uni.navigateTo = (options: UniNamespace.RedirectToOptions) => {
    const pages = getCurrentPages()
    if (pages.length > 7) {
      uni.redirectTo(options)
    } else {
      originalNavigateTo(options)
    }
  }
  // #endif
}
/**
 * navigateBack拦截器，只针对H5生效
 */
const navigateBackInterceptor = () => {
  // #ifdef H5
  uni.navigateBack = (options: UniNamespace.NavigateBackOptions) => {
    const backHistory = options?.delta || 1
    history.go(-backHistory)
  }
  // #endif
}
// global.WebSocket = window.WebSocket = WebSocketPolyfill
export function createApp() {
  // uni.$popup = new Popup()
  navigateToInterceptor()
  navigateBackInterceptor()
  const app = createSSRApp(App)
  // #ifdef APP-PLUS
  if (import.meta.env.MODE !== 'production') {
    //混入DevTools生命周期监听
    app.mixin(devToolsVueMixin)
    //挂载Devtools
    app.use(devTools, devToolsConfig)
    //注册小程序端专用的拖动浮标组件
    // app.component('mpDevBubble', mpDevBubble)
  }
  // #endif
  app.config.globalProperties.$getCdnUrl = getCdnUrl
  app.use(store).use(qszrUI).use(uView)
  getPlugin()
  StompStarter.start()
  return {
    app,
  }
}
