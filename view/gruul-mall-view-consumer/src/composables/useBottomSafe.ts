import { type Ref, ref } from 'vue'

export default function useBottomSafe(): Ref<number> {
  const bottomSafeHeight = ref(0)
  uni.getSystemInfo({
    success: (res) => {
      if (res.safeAreaInsets) {
        // #ifdef APP-PLUS
        bottomSafeHeight.value = res.safeAreaInsets.bottom - 1
        // #endif
        // #ifndef APP-PLUS
        bottomSafeHeight.value = res.safeAreaInsets.bottom
        // #endif
      }
    },
  })
  return bottomSafeHeight
}
