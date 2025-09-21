/**
 * axios api网络请求 设置拦截
 * @author 张治保
 */
import axios, { AxiosResponse } from 'axios'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { AxiosCanceler } from '@/utils/http/axiosCancel'
import router from '../router/index'
import { doPostLogout, signByUser } from '@/apis/sign'
import { CRUD_ERROR_CODE, TOKEN_OVERDUE } from './http.type'
import { ElMessage } from 'element-plus'
import { R } from './http.type'
import { GrantType } from '@apis/sign/index.type'
import Storage from '@/utils/Storage'

const isDev = import.meta.env.MODE === 'dev'
const storage = new Storage()

axios.defaults.headers.post['Content-Type'] = 'application/json'
axios.defaults.headers.put['Content-Type'] = 'application/json'
const request = axios.create({
    baseURL: isDev ? '/proxy/' : import.meta.env.VITE_BASE_URL,
    timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT),
    withCredentials: false,
    headers: {
        'Client-Type': import.meta.env.VITE_CLIENT_TYPE,
        'Device-Id': 1,
    },
})

// 取消请求的方法
const axiosCance = new AxiosCanceler()

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
        const { token, id } = storage.getItem('shopStore') || {}
        if (!notNeedTokenList.includes(config.url!)) {
            if (!isRefreshing && token) {
                config.headers.Authorization = token
            }
            if (id) {
                config.headers['Shop-Id'] = id
            }
        }
        axiosCance.addPending(config)
        return Promise.resolve(config)
    },
    (error) => {
        return Promise.reject(error)
    },
)
let isRefreshing = false
let requests: any[] = []
//响应拦截器
request.interceptors.response.use(
    async (response: AxiosResponse<R<any>>) => {
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

        if (code === 4) {
            if (!isRefreshing) {
                isRefreshing = true
                refreshingFn()
            }
            return new Promise((reslove) => {
                requests.push((token: string) => {
                    response.headers.Authorization = token
                    reslove(request(response.config))
                })
            })
        }
        if (TOKEN_OVERDUE.includes(code)) {
            axiosCance.removeAllPending()
            redirectWithLogin()
            return Promise.reject(result)
        } else if (CRUD_ERROR_CODE.includes(code)) {
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

const redirectWithLogin = () => {
    useShopInfoStore().DEL_SHOP_INFO()
    const { query, fullPath } = router.currentRoute.value
    let redirect = query?.redirect
    doPostLogout().finally(() => {
        router
            .push({
                path: '/sign',
                query: { redirect: redirect ? redirect : fullPath.indexOf('/sign') !== -1 ? '/' : fullPath },
            })
            .catch((fail) => {})
    })
}

const refreshingFn = async () => {
    const refresh_token = useShopInfoStore().refresh_token
    try {
        const { data, code } = await signByUser(GrantType.REFRESH_TOKEN, { value: refresh_token })
        //刷新 token 不可用 直接重新登录
        if (code !== 200) {
            axiosCance.removeAllPending()
            ElMessage.error('认证已失效，请重新登录。')
            redirectWithLogin()
            return
        }
        const newToken = useShopInfoStore().SET_SHOP_TOKEN({
            refresh_token: data.refreshToken.value,
            access_token: data.value,
        })
        requests.forEach((cd: (t: string) => any) => cd(newToken))
        requests = []
        isRefreshing = false
    } catch (error) {
        console.log('error', error)
    } finally {
        isRefreshing = false
    }
}
export default request
