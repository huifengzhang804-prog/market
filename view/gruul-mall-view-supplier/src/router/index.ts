/**
 * vue-router index 路由跳转
 * @author 张治保
 */
import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import Main from '../components/layout/Main.vue'
import { goods, order, overview, mall, finance, inventory, business } from './MenusRoute'
import { useMenuList } from '@/store/modules/menu'

const routes: Array<RouteRecordRaw> = [
    {
        name: 'sign',
        path: '/sign',
        meta: {
            title: '登录',
            isShow: 1,
        },
        component: () => import('@views/sign/Index.vue'),
    },
    {
        name: 'changePass',
        path: '/changePass',
        meta: {
            title: '找回密码',
        },
        component: () => import('@views/changePass/Index.vue'),
    },
    {
        path: '/',
        name: 'Main',
        component: Main,
        children: [overview, goods, order, inventory, finance, mall],
    },
    {
        path: '/business',
        component: Main,
        children: [business],
    },
    {
        path: '/:pathMatch(.*)*',
        component: Main,
        meta: {
            title: '......',
        },
        children: [
            {
                path: '',
                name: 'hello',
                component: () => import('@views/^_^.vue'),
                meta: {
                    title: '......',
                },
            },
        ],
    },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes: routes,
})
router.beforeEach((to) => {
    const menu = useMenuList().getterMenu
    if (to.path === '/' && menu.length > 0) {
        if (menu.some((item: any) => item.path === '/overview')) {
            router.push('/overview').catch(() => {})
        } else {
            router.push(menu[0].children[0].path).catch(() => {})
        }
        return false
    }
    const { token } = useShopInfoStore().getterShopInfo
    const whiteList = ['/sign', '/changePass']
    if (!token && !whiteList.includes(to.path)) {
        router
            .push({
                path: '/sign',
            })
            .catch((fail) => {
                console.log('跳转失败', fail)
            })
        return false
    }
    return true
})

export default router
