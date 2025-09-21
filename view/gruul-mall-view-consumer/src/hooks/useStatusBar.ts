import { ref } from 'vue'
export function useStatusBar() {
  let statusBarHeight = ref(0)
  uni.getSystemInfo({
    success: function (res) {
      if (res.statusBarHeight) {
        // #ifdef APP-PLUS
        if (res.platform === 'android') {
          statusBarHeight.value = res.statusBarHeight + 50
        } else {
          statusBarHeight.value = res.statusBarHeight + 45
        }
        // #endif
        // #ifdef MP-WEIXIN
        let custom = uni.getMenuButtonBoundingClientRect()
        statusBarHeight.value = custom.bottom + custom.top - res.statusBarHeight
        // #endif
      }
    },
  })
  return statusBarHeight
}
