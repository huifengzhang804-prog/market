import axios, { AxiosResponse } from 'axios'
import router from '../router/index'
import { R } from './http.type'
import storage from '@/libs/storage'
import { useUserStore } from '@/store/modules/user'
// import { useAdminInfo } from '@/store/modules/admin'
import { CRUD_ERROR_CODE, TOKEN_OVERDUE } from './http.type'
import { useCardInfo } from '@/store/modules/cart'
import { ElMessage } from 'element-plus'
import { doSignByUser } from '@/apis/sign'
import { GrantType } from '@/apis/sign/index.type'
// import createUuid from '@/libs/uuid'
// import { signByUser } from '@/apis/sign'
axios.defaults.headers.post['Content-Type'] = 'application/json'
axios.defaults.headers.put['Content-Type'] = 'application/json'
const request = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
  timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT),
  withCredentials: false,
  headers: {
    'Device-Id': '1',
    'Client-Type': 'CONSUMER',
    'Shop-Id': '0',
    Platform: 'PC',
  },
})
// 是否正在刷新的标记
let isRefreshing = false
// 重试队列
let requests: any = []
//请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = new storage().getItem('CLIENT-userStore')
    if (!isRefreshing && token) {
      config.headers.Authorization = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)
//响应拦截器
request.interceptors.response.use(
  async (response: AxiosResponse<R>) => {
    const result = response.data
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
    if (code === 2) {
      useUserStore().loginTypeTrue()
    }
    if (TOKEN_OVERDUE.includes(code)) {
      useUserStore().delToken()
      useUserStore().delUserInfo()
      useCardInfo().DEL_CARD()
      // router
      //     .push({
      //         path: '/home',
      //         query: {},
      //     })
      //     .catch((fail) => {
      //         console.log('跳转失败', fail)
      //     })
    }
    if (code === 200) {
      return Promise.resolve(response)
    }
    if (code === 2 || code === 4) {
      // router.push({
      //   path: '/home',
      //   query: {},
      // })
      return Promise.reject(result)
    }
    // 错误捕获
    if (CRUD_ERROR_CODE.includes(code)) {
      ElMessage.error(response.data.msg)
    }
    return Promise.resolve(response)
  },
  (error) => {
    return Promise.reject(error)
  },
)

// function uuidHandle() {
//     const $storage = new storage()
//     let uuid = $storage.getItem('UUID')
//     if (!uuid) {
//         uuid = createUuid(8)
//         $storage.setItem('UUID', uuid, 60 * 60 * 24)
//     }
//     return uuid
// }

const refreshingFn = async () => {
  const userStore = useUserStore()
  const refreshToken = userStore.userInfo.refreshToken
  try {
    isRefreshing = true
    const { data } = await doSignByUser(GrantType.REFRESH_TOKEN, { value: refreshToken })
    const token = data?.value
    if (!token) {
      useUserStore().delToken()
      useUserStore().delUserInfo()
      useCardInfo().DEL_CARD()
      return
    }
    const expiresIn = data?.expiresIn as number
    userStore.addToken(token, expiresIn)
    if (data?.refreshToken) {
      userStore.addRefreshToken(data.refreshToken.value, data.refreshToken.expiresIn as number)
    }
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
