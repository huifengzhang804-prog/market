import { useUserStore } from '@/store/modules/user'
import { TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import type { comDispatcherType } from '../../types'
import { doGetIntegralBehaviorSave } from '@/apis/plugin/integral'

import type { ShareData } from '@/components/canvas-share/components/useGetQrcode'
import { inject, nextTick, ref } from 'vue'

const useGoodShare = () => {
  const sharePopUp = ref(false)
  const comProvideInject = inject('comProvide') as comDispatcherType
  const posterRef = ref()
  const shareData = ref<ShareData>({
    shopId: '',
    goodId: '',
    productName: '',
    image: '',
    salePrice: '',
  })
  const handleShare = async () => {
    if (!useUserStore().userInfo.token) {
      return uni.$emit(TOKEN_DEL_KEY)
    }
    sharePopUp.value = true
    const {
      currentChoosedSku: {
        value: { salePrice },
      },
      goodInfo,
    } = comProvideInject
    const { name: productName, shopId, productId: goodId, pic: image } = goodInfo.value
    shareData.value = { image, salePrice, productName, shopId, goodId }
    nextTick(() => posterRef.value?.openShare())
  }
  const handleCanvasClose = (callback: () => void) => {
    sharePopUp.value = false
    doGetIntegralBehaviorSave('SHARE').then((res) => {
      if (res.code !== 200) {
        return
      }
      if (Number(res.data) > 0) {
        setTimeout(() => {
          uni.showToast({ title: `分享获得${res.data}积分`, icon: 'none' })
        }, 1000)
      }
    })
    callback()
  }
  return {
    sharePopUp,
    posterRef,
    shareData,
    handleShare,
    handleCanvasClose,
  }
}

export default useGoodShare
