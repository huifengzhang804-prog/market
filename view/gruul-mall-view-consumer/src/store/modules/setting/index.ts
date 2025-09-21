//TODO:身份过期后 通过弹窗登录 跳转至首页 返回界面可以返回登录 不合理 待优化
import { defineStore } from 'pinia'
import state from './state'
import $storage from '@/utils/storage'
import { constNavBar } from './state'
import type { NavBarType, NavBarMenuType, LinkType } from '@decoration/components/types'

export const defaultLinkMap: Record<string, string> = {
  分类: '/pages/modules/classification/classification',
  购物车: '/pages/modules/car/car',
  个人中心: '/pages/modules/consumer/consumer',
}

export const useSettingStore = defineStore('settingStore', {
  state: () => state,
  actions: {
    SET_CUR_SWIPER_ID(id: string) {
      this.currentSwiperId = id
      this.SET_TABBAR_STATUS(id, true)
    },
    NAV_TO_INDEX(linkName: string) {
      // 获取导航信息
      const gettterPlatformNavBar = this.gettterPlatformNavBar
      const navBarList = gettterPlatformNavBar?.menuList || constNavBar.menuList
      const currentSwiperIndex = navBarList.findIndex((item) => item.link.name === linkName)

      let currentChoosedId = '1'
      let currentChoosedName = '首页'
      // 未在底部导航注册组件 或缓存失效
      if (currentSwiperIndex === -1) {
        const targetUrl = defaultLinkMap[linkName]
        if (targetUrl) {
          uni.navigateTo({
            url: targetUrl,
          })
        }
      } else {
        uni.reLaunch({
          url: `/pages/platform/Index`,
        })
        this.tabbarStatus = {}
        // 跳转后改变swiper
        currentChoosedId = navBarList[currentSwiperIndex].link.id
        currentChoosedName = navBarList[currentSwiperIndex].link.name
        this.currentSwiperId = currentChoosedId
        this.ChoosedNavName = currentChoosedName
        this.UPDATE_REFRESH_STATE()
      }
    },
    SET_TAB_LIST(val: NavBarType) {
      this.navBar = val
      this.INIT_TABBAR_STATUS(val.menuList) // 初始化 tabbar 各菜单栏状态
      $storage.set('PLATFORM_NAV_BAR', val, 30)
    },
    SET_SHOP_ID(val: string) {
      this.shopId = val
    },
    INIT_TABBAR_STATUS(MenuList: NavBarMenuType[]) {
      this.SET_LOADING(true, this.currentSwiperId)
      this.SET_TABBAR_STATUS(this.currentSwiperId, true)
    },
    SET_TABBAR_STATUS(id: string, status: boolean) {
      this.tabbarStatus[id] = status
    },
    UPDATE_REFRESH_STATE() {
      this.refreshState = Math.round(Math.random() * 1000) // 更新数据状态
    },
    SET_LOADING(loading: boolean, id?: string) {
      if (id && this.tabbarStatus[id]) return
      this.loading = loading
    },
  },
  getters: {
    /**
     * 获取平台已注册底部导航信息
     * @param {*} state
     */
    gettterPlatformNavBar(state): NavBarType | null {
      if (!state.navBar) {
        const localData = $storage.get('PLATFORM_NAV_BAR')
        if (!localData) {
          return null
        } else {
          return localData
        }
      }
      return state.navBar
    },
  },
})
