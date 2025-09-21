<script lang="ts">
import { useUserStore } from '@/store/modules/user'
import { useAdvStore } from '@/store/modules/adv'
import { SHOW_LOGIN_MODAL, TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import { SET_SHOP_ID_KEY } from '@/utils/setShopId'
import { useSettingStore } from '@/store/modules/setting'
import { doPostBinding } from '@/apis/distribute/index'
import { doGetOpenScreenAdvertisement } from '@/apis/concern'
import { doGetNotification } from '@/apis/concern'
import { queryConfigByModule } from '@/apis/order'
import { useAmapKeyStore } from './store/modules/amapKey'
import store from './store/global'

uni.$on(TOKEN_DEL_KEY, () => {
  const $store = useUserStore()
  $store.DEL_TOKEN()
  // 拉起授权弹窗
  uni.$emit(SHOW_LOGIN_MODAL, true)
  // 退出登录
  // doPostLogout()
})
uni.$on(SET_SHOP_ID_KEY, (conf: any) => {
  useSettingStore().SET_SHOP_ID(conf.header['Shop-Id'])
})

const amapKeyStore = useAmapKeyStore(store)
export default {
  setup() {
    return {}
  },
  // #ifdef APP-PLUS
  onError(err: any) {
    if (import.meta.env.MODE !== 'production') {
      try {
        // 挂载devTools全局报错拦截
        // @ts-ignore
        uni.$dev?.errorReport(err, 'at App.vue onError', 'oe')
      } catch (error) {
        console.log('挂载devTools全局报错拦截失败', error)
      }
    }
  },
  // #endif
  async onLaunch(ctx: any) {
    // #ifdef APP-PLUS
    if (import.meta.env.MODE !== 'production') {
      try {
        // 挂载APP启动日志提交
        // @ts-ignore
        uni.$dev.logReport('appOnLaunch>' + JSON.stringify(ctx))
      } catch (error) {
        console.log('挂载APP启动日志提交失败', error)
      }
    }
    // #endif
    // #ifdef H5
    getConfigByModule()
    // #endif
    const endpointType = uni.getSystemInfoSync().uniPlatform === 'mp-weixin' ? 'WECHAT_MINI_APP' : 'H5_APP'
    const { isPlay } = await initOpenScreenAdvertisement(endpointType)
    initNotification(endpointType, isPlay)
  },
  onShow({ path, query }: any) {
    console.log(path, query)
    //分销商绑定监听
    distributorBinding(query)
    //尝试获取分销商分销码
    distributorCodeFinder(query, path)
    //监听路由变化
    uni.addInterceptor('navigateTo', { success: routeChangeSuccess })
    uni.addInterceptor('redirectTo', { success: routeChangeSuccess })
    // @ts-ignore
    this.globalData.userInfo = useUserStore().getterUserInfo.info
    // @ts-ignore
    this.globalData.userInfo.token = useUserStore().getterUserInfo.token
    // @ts-ignore
    this.globalData.userInfo.refresh_token = useUserStore().getterUserInfo.refresh_token
  },
  globalData: {
    groupID: '',
    identity: 'HOST' as 'HOST' | 'USER', // HOST 主播 | USER 用户
    userInfo: {
      avatar: '',
      gender: '',
      nickname: '',
      userId: '',
      birthday: '',
      token: '',
      refresh_token: '',
    },
  },
}
// #ifdef H5
const getConfigByModule = async () => {
  const { code, data } = await queryConfigByModule('OTHERS_SETTING, PUBLIC_SETTING')
  if (code === 200 && data) {
    document.getElementById('website_icon')?.setAttribute('href', data.WEB_SIT_ICON)
    uni.$on('updateTitle', async () => {
      uni.setNavigationBarTitle({
        title: data.H5_MALL_NAME, // 设置为你想要显示的标题文本
      })
    })
  }
}

// #endif
function routeChangeSuccess() {
  distributorCodeFinder(currentPage()?.options || {})
}
/**
 * 开屏广告
 */
const initOpenScreenAdvertisement = async (endpointType: string) => {
  const { code, data } = await doGetOpenScreenAdvertisement(endpointType)
  if (code === 200 && data) {
    useAdvStore().SET_OPEN_ADV({ ...data, isPlay: true })
  } else {
    useAdvStore().SET_OPEN_ADV_FLAG(false)
  }
  return { isPlay: useAdvStore().getOpenAdvIsPlay }
}
/**
 * 首页弹窗
 */
const initNotification = async (endpointType: string, isOpenPlay: boolean) => {
  const { code, data } = await doGetNotification(endpointType)
  if (code === 200 && data) {
    if (isOpenPlay) {
      useAdvStore().SET_POP_ADV({ ...data, isPlay: false })
    } else {
      // 开屏广告没播放完，首页弹窗不播放
      useAdvStore().SET_POP_ADV({ ...data, isPlay: true })
    }
  } else {
    useAdvStore().SET_POP_ADV_FLAG(false)
  }
}

/**
 * 获取当前页面的信息
 */
function currentPage() {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  // #ifdef MP-WEIXIN
  // @ts-ignore
  return currentPage
  // #endif
  // #ifndef MP-WEIXIN
  // @ts-ignore
  // eslint-disable-next-line no-unreachable
  return currentPage?.$page
  // #endif
}
//function 读取 分销码参数
function distributorCodeFinder(query: any, path?: any) {
  let distributorCode = null
  if (query.q) {
    try {
      // distributorCode = new URLSearchParams(decodeURIComponent(query.q)).get('distributorCode')
      distributorCode = getQueryString(decodeURIComponent(query.q), 'distributorCode')
    } catch (e) {
      distributorCode = null
    }
  } else {
    distributorCode = query.distributorCode
  }
  const whiteList = ['pluginPackage/distribute/promotionCode/promotionCode']
  if (distributorCode && path && !whiteList.includes(path)) useUserStore().SET_DIS_CODE(distributorCode)
}
//分销商绑定
//监听discode变化执行绑定操作
function distributorBinding(query: any) {
  useUserStore().$subscribe(
    (mutation, state) => {
      if (state.discode && state.userInfo.token) {
        doPostBinding(state.discode).then((res) => {
          if (res.code === 200) {
            console.log(res.msg || '绑定分销成功')
          } else {
            console.log(res.msg || '绑定分销失败')
          }
        })
        useUserStore().DEL_DIS_CODE()
      }
    },
    { immediate: true },
  )
}

const getQueryString = (url: string, name: string) => {
  var reg = new RegExp('(^|&|/?)' + name + '=([^&|/?]*)(&|/?|$)', 'i')
  var r = url.substr(1).match(reg)
  if (r !== null) {
    return r[2]
  }
  return null
}
</script>

<style lang="scss">
@import './uni_modules/vk-uview-ui/index.scss';
page {
  background-color: #f5f5f5;
}
</style>
