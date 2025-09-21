import { defineStore } from 'pinia'

//  组合组件渐模糊背景
export const composeDecorationStore = defineStore('backgroundStore', {
  state: () => ({
    autoplay: true, // 组合组件
    background: '',
    headbg: '',
    currentBg: '',
    tabBg: 'background: rgba(52, 52, 52, 0.1)',
    fixedBackground: false, // 滚动后顶部背景固定
    topAreaShow: false, // 顶部区域是否显示(只选中一个tab时不显示)
  }),
  actions: {
    setTopAreaShow(val: boolean) {
      this.topAreaShow = val
    },
    setFixedBackground(val: boolean) {
      this.fixedBackground = val
    },
    setBackground(val: string) {
      if (val) {
        // 有背景图做记录 后面页面触顶可以直接使用
        this.currentBg = val
      }
      this.background = !this.fixedBackground ? val : ''
    },
    setHeadbg(val: string) {
      this.headbg = val
    },
    setTabBg(val: string) {
      this.tabBg = val
    },
    setAutoplay(val = true) {
      this.autoplay = val
    },
    // 默认轮播组件背景
    defaultBg() {
      this.autoplay = false
      this.headbg = 'background:linear-gradient(rgb(249, 249, 249)'
      this.tabBg = 'linear-gradient(180deg, rgb(245, 67, 25), rgb(249, 249, 249) 87.786%)'
    },
    reset() {
      this.autoplay = true
      this.background = this.currentBg
    },
  },
})
