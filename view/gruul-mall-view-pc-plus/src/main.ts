import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { installs } from '@/libs/plugin'
import { getPlugin } from '@/libs/sysConfig'
import '@/assets/css/base.scss'
// 按需导入需引入 需导入API组件样式
import 'element-plus/es/components/message/style/css'
import 'element-plus/es/components/message-box/style/css'
import 'element-plus/es/components/date-picker/style/css'
import 'element-plus/es/components/time-picker/style/css'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import 'uno.css'
import { createPinia } from 'pinia'
import { useAmapKeyStore } from './store/modules/amapKey'
import { doGetAmapKey } from './apis/amap'
import { asyncGetGDRegionData } from './store/modules/GDRegionData'
import Global from '@/libs/global'
import { watchPluginPort } from '@/server'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
if (process.env.NODE_ENV === 'development') {
  watchPluginPort()
}
const amapKeyStore = useAmapKeyStore(pinia)
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
  .finally(() => {
    asyncGetGDRegionData()
  })

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app
  .use(router)
  .use(installs)
  .use(Global)
  .use(ElementPlus, {
    locale: zhCn,
  })
  .mount('#app')
getPlugin()
