import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { Search, Swipe, SwipeItem, Icon, Image as VanImage } from 'vant'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'cropperjs/dist/cropper.css'
import '@/assets/css/base.scss'
import '@/assets/css/font/iconfont.css'
import 'vant/lib/index.css'
// 全局导入 element css
import 'element-plus/dist/index.css'
import { initPlugin } from '@/libs/plugin'
import { createPinia } from 'pinia'
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

app.use(Search)
    .use(Swipe)
    .use(SwipeItem)
    .use(ElementPlus, {
        locale: zhCn,
    })
    .use(VanImage)
    .use(Icon)
    .use(router)
    .use(initPlugin)
    .mount('#app')
