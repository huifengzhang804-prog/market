import { doAddAssess } from '@/apis/consumer/footprint'
import useCollectionDispatcher from '@/store/dispatcher/useCollectionDispatcher'
import { useUserStore } from '@/store/modules/user'
import { TOKEN_DEL_KEY } from '@/utils/tokenConfig'
import type { comDispatcherType } from '@/pluginPackage/goods/commodityInfo/types'
import { inject, ref } from 'vue'

const useGoodCollection = () => {
  const isCollection = ref<boolean | null | undefined>(false) //收藏标识
  const $collectionDispatcherStore = useCollectionDispatcher()
  const comProvideInject = inject('comProvide') as comDispatcherType
  const handleAssess = async () => {
    if (useUserStore().userInfo.token) {
      const message = isCollection.value ? '取消' : '加入'
      const {
        shopId,
        productId,
        albumPics,
        name: productName,
        price: { underlined },
        supplierId,
      } = comProvideInject.goodInfo.value
      let productPrice = underlined
      let productPic = albumPics[0]
      const { code, msg } = !isCollection.value
        ? await doAddAssess({
            userCollectDTOList: [{ shopId, productId, productPic, productName, productPrice, supplierId }],
            whetherCollect: true,
          })
        : await doAddAssess({ userCollectDTOList: [{ shopId, productId }], whetherCollect: false })
      if (code !== 200) return uni.showToast({ title: `${msg ? msg : `${message}收藏失败`}`, icon: 'none' })
      uni.showToast({ title: `${message}收藏成功`, icon: 'none' })
      const currentCollectionStatus = !isCollection.value
      isCollection.value = currentCollectionStatus
      comProvideInject.goodInfo.value.whetherCollect = currentCollectionStatus
      $collectionDispatcherStore.updateCommodityData()
      return
    }
    uni.$emit(TOKEN_DEL_KEY)
  }
  return {
    isCollection,
    handleAssess,
  }
}

export default useGoodCollection
