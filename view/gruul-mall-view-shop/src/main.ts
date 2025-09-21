import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import '@/assets/css/base.scss'
import '@/assets/css/font/iconfont.css'
import { initPlugin } from '@/libs/plugin'
import { Search, Swipe, SwipeItem, Icon, Image as VanImage } from 'vant'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'vant/lib/index.css'
import 'default-passive-events' // 解决控制台警告
import Global from '@/libs/global'
import { createPinia } from 'pinia'
import 'element-plus/dist/index.css'
import { useAmapKeyStore } from './store/modules/amapKey'
import { doGetAmapKey } from './apis/amap'
import { asyncGetGDRegionData } from './store/modules/GDRegionData'
import { watchPluginPort } from '@/server'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
if (import.meta.env.MODE === 'dev') {
    watchPluginPort()
}
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
    .use(Search)
    .use(Swipe)
    .use(SwipeItem)
    .use(VanImage)
    .use(Icon)
    .mount('#app')
