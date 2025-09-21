import { useStatusBar } from '@/hooks'
import { ref, computed } from 'vue'
import { composeDecorationStore } from '@/store/modules/composedecoration'

/**
 * 回到顶部hooks
 * @returns {
 * scrollTop顶部距离
 * handleGoTop 回到顶部
 * scroll 滚动方法
 * }
 */
export const useGotop = () => {
  const tabbarNode = uni.upx2px(90)
  const statusBarHeight = useStatusBar()
  const safeHeight = useBottomSafe()
  const systermInfo = uni.getSystemInfoSync()
  // 可视区域滑动高度计算
  const scrollViewHeight = computed(() => {
    let height = 0
    // #ifdef H5
    height = systermInfo.windowHeight - (tabbarNode + statusBarHeight.value + safeHeight.value)
    // #endif
    // #ifndef H5
    height = systermInfo.screenHeight - tabbarNode - safeHeight.value
    // #endif
    return height
  })
  const { setBackground, reset, defaultBg, setAutoplay, setHeadbg, setTabBg, setFixedBackground } = composeDecorationStore()
  const show = ref(false)
  const topItem = ref('')
  const handleGoTop = (e: any) => {
    if (e.type === 'scroll') return
    topItem.value = 'top'
  }
  const scroll = (e: any) => {
    const { scrollTop } = e.detail
    if (topItem.value === 'top') topItem.value = ''
    setBackground('')
    // 滑动顶部背景效果
    if (scrollTop === 0 || scrollTop <= 10) {
      setFixedBackground(false)
      reset()
    } else if (scrollTop > 0) {
      setFixedBackground(true)
    } else {
      setAutoplay()
      setHeadbg('background:none')
      setTabBg('')
      setFixedBackground(false)
    }

    if (scrollTop >= scrollViewHeight.value && show.value) return
    if (scrollTop <= scrollViewHeight.value && !show.value) return
    show.value = scrollTop > scrollViewHeight.value
  }
  return {
    show,
    handleGoTop,
    scroll,
    topItem,
  }
}
