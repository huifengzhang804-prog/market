import { ref, type Ref, reactive, provide, computed, getCurrentInstance } from 'vue'
import { doGetGoodsDetailsCouponPage } from '@/pluginPackage/coupon/apis'
import { useAppStore } from '@/store/modules/app'
import type { GetCouponListParams, CouponDispatcherType } from '@/apis/plugin/coupon/model'
import type { CartApiCouponVO, ApiCouponVO } from '@/apis/plugin/coupon/model'
import type { StorageSku } from '@/apis/good/model'

export default function hooksOfCoupon(choosedSku: Ref<StorageSku>) {
  const instance = getCurrentInstance()
  const goodsDetailCouponList = ref<CartApiCouponVO[]>([])
  const pageConfig = reactive({ size: 5, current: 1, pages: 1 })
  // 是否存在优惠券插件
  const isExist = computed(() => !!useAppStore().GET_PLUGIN('addon-coupon'))
  async function doRequest() {
    console.log('----刷新·········1·')
    let routeShopId = ''
    // #ifdef H5
    routeShopId = (instance?.attrs?.shopId as string) || ''
    // #endif
    // #ifndef H5

    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    // @ts-ignore
    routeShopId = currentPage.options?.shopId || ''
    // #endif
    if (!choosedSku.value) return
    const { productId, salePrice, shopId } = choosedSku.value
    const options = {
      shopId: routeShopId || shopId,
      amount: salePrice,
      productId,
    }
    await initGoodsDetailCouponList(false, options).catch(() => {})
    // 请求失败 / 成功 都会走这里
    // 商品详情通过优惠券数量决定是否计算优惠券优惠
    return goodsDetailCouponList.value[0]
  }

  //TODO:这里的 options 怎样入参合适 直接取到 choosedSku 还是作为一个单独的模块去给商品使用
  async function initGoodsDetailCouponList(isLoad = false, options: GetCouponListParams) {
    console.log('----刷新··········')

    if (!isLoad) {
      // 刷新
      pageConfig.current = 1
      goodsDetailCouponList.value = await getCouponList(options)
    } else if (isLoad && pageConfig.current < pageConfig.pages) {
      // 更新
      pageConfig.current++
      goodsDetailCouponList.value = [...goodsDetailCouponList.value, ...(await getCouponList(options))]
    }
  }
  /**
   * 获取优惠券列表
   * @param {GetCouponListParams} options
   * @param {number} pages
   */
  async function getCouponList(options: GetCouponListParams) {
    const { code, data } = await doGetGoodsDetailsCouponPage({ ...options, current: pageConfig.current, size: pageConfig.size })
    if (code !== 200) {
      return []
    }

    console.log('data`````````````````````', data)
    pageConfig.pages = data.pages
    return data.records.map((item: ApiCouponVO) => ({
      ...item,
      watermark: false,
    }))
  }

  const exportSlice: CouponDispatcherType = {
    doRequest,
    initGoodsDetailCouponList,
    goodsDetailCouponList,
    isExist,
  }
  provide('couponProvide', exportSlice)
  return exportSlice
}
