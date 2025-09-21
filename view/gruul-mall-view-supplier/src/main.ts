import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import '@/assets/css/base.scss'
import '@/assets/css/font/iconfont.css'
import { initPlugin } from '@/libs/plugin'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import Global from '@/libs/global'
import { createPinia } from 'pinia'
import 'element-plus/dist/index.css'
import { useAmapKeyStore } from './store/modules/amapKey'
import { doGetAmapKey } from './apis/amap'
import { asyncGetGDRegionData } from './store/modules/GDRegionData'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
const amapKeyStore = useAmapKeyStore()
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

app.use(router)
    .use(initPlugin)
    .use(Global)
    .use(ElementPlus, {
        locale: zhCn,
    })
    .mount('#app')
