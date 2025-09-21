import { type Ref, ref } from 'vue'

export default function useScreenHeight(): Ref<number> {
  const screenHeight = ref(0)
  uni.getSystemInfo({
    success: (res) => {
      screenHeight.value = res.windowHeight
    },
  })
  return screenHeight
}
