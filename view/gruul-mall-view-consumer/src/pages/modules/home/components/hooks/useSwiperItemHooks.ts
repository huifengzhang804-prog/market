import { ref, nextTick } from 'vue'
import PayattentionVue from '../payattention/payattention.vue'

const useSwiperItemHooks = () => {
  const payattentionRef = ref<InstanceType<typeof PayattentionVue>[] | null>(null)
  const refreshSwiper = (currentIndex: number, swiperItemList: any[]) => {
    if (swiperItemList[currentIndex].name === '关注') {
      nextTick(() => {
        console.log(payattentionRef.value)
        payattentionRef.value?.[0]?.initialSwiperData()
      })
    }
  }
  return {
    payattentionRef,
    refreshSwiper,
  }
}

export default useSwiperItemHooks
