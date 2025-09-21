import Bxios from './bxios'
import { TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import storage from '@/utils/storage'
import { SET_SHOP_ID_KEY } from '@/utils/setShopId'
import { GrantType } from '@/apis/sign/type'
import { doSignByUser } from '@/apis/sign'
import { TOKEN_OVERDUE } from './bxios.type'
import { doPostLogout } from '@/apis/sign'
import { useUserStore } from '@/store/modules/user'

type Method = 'get' | 'post' | 'put' | 'delete'

// 老token
const userInfo = storage.get('userStore') as any
const isDev = import.meta.env.MODE === 'development'
let isH5 = false
// #ifdef H5
isH5 = true
// #endif
const bxios = new Bxios({
  baseUrl: isDev && isH5 ? '/api/' : import.meta.env.VITE_BASE_URL,
  header: {
    'Device-Id': uni.getSystemInfoSync().deviceId,
    'Client-Type': 'CONSUMER',
    'Shop-Id': 0,
    'Accept-Language': import.meta.env.VITE_ACCEPT_LANGUAGE,
    // #ifdef H5
    Platform: 'H5',
    // #endif
    // #ifdef MP-WEIXIN
    //@ts-ignore
    Platform: 'WECHAT_MINI_APP',
    // #endif
    // #ifdef APP-PLUS
    //@ts-ignore
    Platform: 'ANDROID',
    // #endif
  },
})
let requests: any[] = []
const whiteList = ['gruul-mall-uaa/login?grant_type=refresh_token']
const notNeedTokenList = [
  'gruul-mall-uaa/uaa/auth/captcha/slider/new ',
  'gruul-mall-uaa/uaa/auth/captcha/sms',
  'gruul-mall-uaa/login?grant_type=sms_code',
]

bxios.interceptors.request.use(
  (config) => {
    // 取消上一次请求,防止重复请求
    config.cancelLast = config.header.cancelLast
    delete config.header.cancelLast

    uni.$emit(SET_SHOP_ID_KEY, config)
    // 如果正在刷新中
    if (storage.get('isRefreshing') && !whiteList.includes(config.url)) {
      return new Promise((resolve) => {
        requests.push((token: string) => {
          config.header.Authorization = token
          resolve(config)
        })
      })
    }

    const userInfo = storage.get('userStore')
    const { token } = userInfo || { token: null }
    if (token) {
      if (!notNeedTokenList.includes(config.url)) {
        config.header.Authorization = token
      }
    } else {
      delete config.header.Authorization
    }
    return Promise.resolve(config)
  },
  (err) => {
    return Promise.reject(err)
  },
)
bxios.interceptors.response.use(
  async ({ data, config }) => {
    const res = data
    const { code, msg } = res
    if (TOKEN_OVERDUE.includes(code)) {
      uni.$emit(TOKEN_DEL_KEY)
      return Promise.reject(res)
    }
    if (code === 4) {
      // 无感刷新
      if (!storage.get('isRefreshing')) {
        storage.set('isRefreshing', true, 30)
        const refresh_token = storage.get('userStore')?.refresh_token
        try {
          const { data } = await doSignByUser(GrantType.REFRESH_TOKEN, { value: refresh_token })
          const newToken = data?.value as string
          if (!newToken) {
            storage.set('isRefreshing', false, 30)
            uni.$emit(TOKEN_DEL_KEY)
            doPostLogout()
            return Promise.reject('token刷新失败')
          }
          storage.set(
            'userStore',
            {
              ...userInfo,
              token: data?.value,
              refresh_token: data?.refreshToken.value,
            },
            30,
          )
          useUserStore().ADD_TOKEN({
            access_token: data?.value || '',
            refresh_token: data?.refreshToken.value || '',
          })

          requests.forEach((cb: (t: string) => any) => {
            cb(newToken)
          })
          requests = []
          storage.set('isRefreshing', false, 30)
          config.header.Authorization = newToken
          // config.method转小写
          const method = config.method.toLowerCase() as Method
          return Promise.resolve(bxios[method](config.url, config.data, config))
        } catch (error) {
          storage.set('isRefreshing', false, 30)
          console.log('error', error)
          return Promise.reject(error)
        }
      }
      return new Promise((resolve) => {
        requests.push((token: string) => {
          config.header.Authorization = token
          // config.method转小写
          const method = config.method.toLowerCase() as Method
          resolve(bxios[method](config.url, config.data, config))
        })
      })
    }

    return Promise.resolve(res)
  },
  (err) => {
    if (err?.errMsg === 'request:fail abort') {
      return Promise.reject('请求取消')
    }
    uni.showToast({
      icon: 'none',
      title: `${err || '服务器内部错误'}`,
    })
    return Promise.reject(err)
  },
)

export default bxios
