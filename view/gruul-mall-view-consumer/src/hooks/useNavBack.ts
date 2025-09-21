import { useSettingStore } from '@/store/modules/setting'
export function useNavBack(delta = 1, model?: { msg?: string }) {
  const pages = getCurrentPages()
  if (pages[pages.length - 2]) {
    if (model?.msg) {
      uni.showToast({
        title: model.msg,
        icon: 'none',
      })
      setTimeout(() => {
        uni.navigateBack({
          delta,
        })
      }, 1000)
    } else {
      uni.navigateBack({
        delta,
      })
    }
  } else {
    useSettingStore().NAV_TO_INDEX('首页')
  }
}
