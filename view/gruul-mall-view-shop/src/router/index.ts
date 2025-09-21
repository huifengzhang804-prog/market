/**
 * vue-router index 路由跳转
 * @author 张治保
 */
import { createRouter, createWebHashHistory, RouteRecordRaw } from 'vue-router'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import {
    goods,
    order,
    overview,
    system,
    mall,
    notice,
    freight,
    decoration,
    decorationo2o,
    decorationPc,
    business,
    finance,
    marketingApp,
    member,
    inventory,
} from './MenusRoute'

const routes: Array<RouteRecordRaw> = [
    {
        path: '',
        name: 'Main',
        redirect: '/',
    },
    {
        path: '/',
        name: 'Main',
        redirect: '/overview',
    },
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
    overview,
    goods,
    marketingApp,
    order,
    system,
    mall,
    notice,
    freight,
    decoration,
    business,
    finance,
    member,
    inventory,
    decoration,
    decorationo2o,
    decorationPc,
    {
        path: '/:pathMatch(.*)',
        redirect: '/',
        meta: {
            isShow: 1,
        },
    },
]

const router = createRouter({
    history: createWebHashHistory(),
    routes: routes,
})
router.beforeEach((to) => {
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
