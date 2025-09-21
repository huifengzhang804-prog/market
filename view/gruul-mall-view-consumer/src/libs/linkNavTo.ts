import { useSettingStore } from '@/store/modules/setting'
import { LIVE_ROUTER_BLACK_LIST } from '@/config/live-icon-https'
import { useUserStore } from '@/store/modules/user'
import type { LinkType } from '@decoration/components/types'
import { TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import { doDistribution, doShopInfo } from '@/apis/shops'
import { REGEX } from '@/constant'

export default function linkNavTo(link: LinkType): Promise<boolean> {
  if (typeof link.type !== 'number') return Promise.resolve(false)
  switch (link.type) {
    case 0:
      return functionNavMethod(link)
    case 1:
      return commodityNavMethod(link)
    case 2:
      return activityNavMethod(link)
    case 3:
      return cusPageNavMethod(link)
    case 6:
      return cusLinkNavMethod(link)
    case 7:
      return miniAppNavMethod(link)
    default:
      return defaultNavMethod(link)
  }
}

const applyUrlMap: Record<string, { url: string; type: string }> = {
  供应商申请: {
    url: '/basePackage/pages/applyFournisseurs/toExamine',
    type: 'SUPPLIER',
  },
  商户申请: {
    url: '/basePackage/pages/applyMerchant/toExamine',
    type: 'COMMON',
  },
}

/**
 * 功能页跳转处理方法
 */
function functionNavMethod(link: LinkType): Promise<boolean> {
  // 平台首页跳转
  if (link.url === '/') {
    useSettingStore().NAV_TO_INDEX(link.name)
    return Promise.resolve(true)
  } else if (link.url === '/basePackage/pages/merchant/Index') {
    // 店铺首页跳转
    return new Promise((resolve, reject) => {
      uni.navigateTo({
        url: `/basePackage/pages/merchant/Index?shopId=${link.shopId}&tabId=${link.id}`,
        fail: () => {
          reject(false)
        },
        success: () => {
          resolve(true)
        },
      })
    })
  } else {
    const $userStore = useUserStore()
    if (LIVE_ROUTER_BLACK_LIST.includes(link.url) && !$userStore.getterToken) {
      uni.$emit(TOKEN_DEL_KEY)
      return Promise.resolve(false)
    }

    if (Object.keys(applyUrlMap).includes(link.name)) {
      const current = applyUrlMap[link.name]
      return doShopInfo(current.type).then(({ code, data }) => {
        if (data && code) {
          uni.navigateTo({ url: current.url })
          return true
        } else if (code === 200 && !data) {
          uni.navigateTo({ url: link.url })
          return true
        }
        return false
      })
    } else {
      return new Promise((resolve, reject) => {
        uni.navigateTo({
          url: link.url,
          fail: () => {
            uni.showToast({
              icon: 'none',
              title: '该路径不存在',
            })
            reject(false)
          },
          success: () => {
            resolve(true)
          },
        })
      })
    }
  }
}
/**
 * 商品详情跳转
 */
function commodityNavMethod(link: LinkType): Promise<boolean> {
  return new Promise((resolve, reject) => {
    uni.navigateTo({
      url: `${link.url}?goodId=${link.id}&shopId=${link.shopId}`,
      fail: () => {
        reject(false)
      },
      success: () => {
        resolve(true)
      },
    })
  })
}
/**
 * 活动营销跳转
 */
function activityNavMethod(link: LinkType): Promise<boolean> {
  return doDistribution().then(({ code, data }) => {
    if (data && data.status !== 'NOT_APPLIED' && code && link.name === '分销中心') {
      if (data.visited) {
        return new Promise((resolve, reject) => {
          uni.navigateTo({
            url: link.url,
            fail: () => {
              uni.showToast({
                icon: 'none',
                title: '该路径不存在',
              })
              reject(false)
            },
            success: () => {
              resolve(true)
            },
          })
        })
      } else {
        return new Promise((resolve, reject) => {
          uni.navigateTo({
            url: '/pluginPackage/distribute/toExamine',
            fail: () => {
              reject(false)
            },
            success: () => {
              resolve(true)
            },
          })
        })
      }
    } else {
      return new Promise((resolve, reject) => {
        uni.navigateTo({
          url: link.url,
          fail: () => {
            uni.showToast({
              icon: 'none',
              title: '该路径不存在',
            })
            reject(false)
          },
          success: () => {
            resolve(true)
          },
        })
      })
    }
  })
}
/**
 * 自定义页面
 */
function cusPageNavMethod(link: LinkType): Promise<boolean> {
  return new Promise((resolve, reject) => {
    if (!link.shopId) {
      // 平台自定义页面
      uni.navigateTo({
        url: `/basePackage/pages/customPage/CustomPagePlatform?id=${link.append}&shopId=${link.shopId || 0}&pageName=${link.name}`,
        fail: () => {
          reject(false)
        },
        success: () => {
          resolve(true)
        },
      })
    } else {
      uni.navigateTo({
        url: `/basePackage/pages/customPage/CustomPageShop?id=${link.append}&shopId=${link.shopId}&pageName=${link.name}`,
        fail: () => {
          reject(false)
        },
        success: () => {
          resolve(true)
        },
      })
    }
  })
}
/**
 * 自定义链接跳转(外部链接)
 */
function cusLinkNavMethod(link: LinkType): Promise<boolean> {
  return new Promise((resolve, reject) => {
    // #ifndef H5
    uni.navigateTo({
      url: `/basePackage/pages/webView/WebView?url=${link.url}`,
      fail: () => {
        reject(false)
      },
      success: () => {
        resolve(true)
      },
    })
    // #endif
    // #ifdef H5
    if (REGEX.HTTP_URL.test(link.url)) {
      window.location.href = link.url
      resolve(true)
    } else {
      reject(false)
    }
    // #endif
  })
}
/**
 * 跳转小程序方法
 */
function miniAppNavMethod(link: LinkType): Promise<boolean> {
  return new Promise((resolve, reject) => {
    try {
      uni.navigateToMiniProgram({
        appId: link.url,
        envVersion: 'release',
        success: () => {
          resolve(true)
        },
        fail: () => {
          reject(false)
        },
      })
    } catch {
      uni.showToast({ title: 'h5暂不支持跳转小程序', icon: 'none' })
      reject(false)
    }
  })
}
/**
 * 默认跳转
 * @param link
 */
function defaultNavMethod(link: LinkType): Promise<boolean> {
  return new Promise((resolve, reject) => {
    uni.navigateTo({
      url: link.url,
      fail: () => {
        uni.showToast({
          icon: 'none',
          title: '该路径不存在',
        })
        reject(false)
      },
      success: () => {
        resolve(true)
      },
    })
  })
}
