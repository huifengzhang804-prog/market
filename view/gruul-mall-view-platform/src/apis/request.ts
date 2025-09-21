import axios, { AxiosResponse } from 'axios'
import router from '../router/index'
import { R } from './http.type'
import storage from '@/utils/Storage'
import { useAdminInfo } from '@/store/modules/admin'
import { CRUD_ERROR_CODE, TOKEN_OVERDUE } from './http.type'
import { ElMessage } from 'element-plus'
import createUuid from '@/utils/uuid'
import { doPostLogout, signByUser } from '@/apis/sign'
// 取消请求
import { AxiosCanceler } from '@/utils/http/axiosCancel'
import { GrantType } from '@apis/sign/index.type'
import { storeToRefs } from 'pinia'

const isDev = import.meta.env.MODE === 'dev'
/**
 * TODO: axios升级为 1.0.0 版本后，需要使用新的取消请求方法 signal
 * const controller = new AbortController();
 * const signal = controller.signal
 * axios.get(url, { signal })
 * controller.abort()
 */
const axiosCance = new AxiosCanceler()
// const LastModified = ref('')
// const $useLastModified = useLastModified()
axios.defaults.headers.post['Content-Type'] = 'application/json'
axios.defaults.headers.put['Content-Type'] = 'application/json'
const request = axios.create({
    // 开发环境统一使用 vite server 代理请求 防止跨域, 这样可以在开发环境模拟 https
    baseURL: isDev ? '/proxy/' : import.meta.env.VITE_BASE_URL,
    timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT),
    withCredentials: false,
    headers: {
        'Device-Id': uuidHandle(),
        'Client-Type': 'PLATFORM_CONSOLE',
        'Shop-Id': '0',
        Platform: 'PC',
    },
})
// 是否正在刷新的标记
let isRefreshing = false
// 重试队列
let requests: any = []

const notNeedTokenList = [
    'gruul-mall-uaa/uaa/auth/captcha/slider/new',
    'gruul-mall-uaa/uaa/auth/captcha/sms',
    '/gruul-mall-uaa/login?grant_type=sms_code',
    '/gruul-mall-uaa/login?grant_type=password',
    'gruul-mall-addon-platform/platform/config/query-config-by-module?modules=PUBLIC_SETTING, PLATFORM_SETTING, SHOP_SETTING, SUPPLIER_SETTING',
]
//请求拦截器
request.interceptors.request.use(
    (config) => {
        const token = storeToRefs(useAdminInfo()).getterToken.value
        // @ts-ignore
        if (!notNeedTokenList.includes(config.url!)) {
            if (!isRefreshing && token) {
                config.headers.Authorization = token
            }
        }
        axiosCance.addPending(config)
        return config
    },
    (error) => {
        return Promise.reject(error)
    },
)

//响应拦截器
request.interceptors.response.use(
    async <T>(response: AxiosResponse<R<T>>) => {
        const result = response.data
        if (result.data?.total) {
            result.data.total = Number(result.data.total)
            result.data.size = Number(result.data.size)
            result.data.pages = Number(result.data.pages)
        }
        if (result.data?.current) {
            result.data.current = Number(result.data.current)
        }
        if (response.status !== 200 || !result) {
            return Promise.reject({
                msg: '服务器异常',
            })
        }
        const code = result.code
        // 2 需要登陆 3 token不可用 4 token已过期 reFreshToken 换 token
        if (code === 4) {
            if (!isRefreshing) {
                isRefreshing = true
                refreshingFn()
            }
            // 返回未执行 resolve 的 promise
            return new Promise((resole) => {
                requests.push((token: string) => {
                    response.headers.Authorization = token
                    resole(request(response.config))
                })
            })
        }
        if (TOKEN_OVERDUE.includes(code)) {
            axiosCance.removeAllPending()
            redirectWithLogin()
            return Promise.reject(result)
        }

        if ([200, 304].includes(code)) {
            return Promise.resolve(response)
        }
        // 错误捕获
        if (CRUD_ERROR_CODE.includes(code)) {
            ElMessage.error(response.data.msg)
            return Promise.reject(result)
        }
        axiosCance.removePending(response.config)
        return Promise.resolve(response)
    },
    (error) => {
        return Promise.reject(error)
    },
)

export function uuidHandle() {
    const $storage = new storage()
    let uuid = $storage.getItem('UUID')
    if (!uuid) {
        uuid = createUuid(8)
        $storage.setItem('UUID', uuid, 60 * 60 * 24)
    }
    return uuid
}

const redirectWithLogin = () => {
    useAdminInfo().REMOVE_ADMIN_INFO()
    let currentFullPath = router.currentRoute.value.fullPath
    if (currentFullPath === '/hello') {
        currentFullPath = ''
    }
    const query: any = {}
    if (currentFullPath) {
        query.redirect = currentFullPath
    }
    doPostLogout().finally(() => {
        router
            .push({
                path: '/login',
                query,
            })
            .catch((fail) => {
                console.log('跳转失败', fail)
            })
    })
}

const refreshingFn = async () => {
    const refresh_token = useAdminInfo().adminInfo.refreshToken.value
    try {
        const { data, code } = await signByUser(GrantType.REFRESH_TOKEN, { value: refresh_token })
        //刷新 token 不可用 直接重新登录
        if (code !== 200) {
            axiosCance.removeAllPending()
            ElMessage.error('认证已失效，请重新登录。')
            redirectWithLogin()
            return
        }
        const token = useAdminInfo().SET_ADMIN_INFO(data)
        requests.forEach((cd: (t: string) => any) => cd(token))
        requests = []
        isRefreshing = false
    } catch (error) {
        console.log('error', error)
    } finally {
        isRefreshing = false
    }
}
export default request
