<script setup lang="ts">
import { ref, computed, nextTick, reactive, watch } from 'vue'
import { useConversionPrice } from '@/utils/useConversionPrice'
import { Decimal } from 'decimal.js'
import Storage from '@/libs/storage'
import { ElMessage, ElMessageBox } from 'element-plus'
import { doDeleteShopCarData, doUpdateShopCarGood, doEmptyShopCarData, doPostOrderValid } from '@/apis/shopCar'
import { doAddAssess } from '@/apis/consumer'
import { useCardInfo } from '@/store/modules/cart'
import type { GoodListType, StoragePackage } from './types'
import { cloneDeep } from 'lodash-es'
import QIcon from '@/components/q-icon/q-icon.vue'
import { handleParams } from '@/utils/goodsUtils'
import { usePropertiesListStore } from '@/store/modules/propertiesList'
import { storeToRefs } from 'pinia'
import { doGetMessagesChatRoom } from '@/apis/consumerSever'
import { useUserStore } from '@/store/modules/user'
import { useRouterNewWindow } from '@/utils/useRouter'
import { useRouter } from 'vue-router'
import { doGetProduct } from '@/apis/goods'
import { ApiGoodType, ProductExtraDTO, ProductSpecsSkusVO, StorageSku } from '../detail/types'
import { doGetCouponList } from '@/apis/GroupPurchase'
import { CouponListType } from '@/apis/GroupPurchase/types'
import PcCouponGoodDetails from '@/q-plugin/coupon/PcCouponGoodDetails.vue'

const storage = new Storage()
const { getterPropertiesList } = storeToRefs(usePropertiesListStore())
const $router = useRouter()
const validList = ref<GoodListType[]>([])
const inValidList = ref<GoodListType[]>([])
const $cardInfo = useCardInfo()
const couponList = ref<CouponListType[]>([])
const { openNewWindow } = useRouterNewWindow()
// 全选标识
const isCheckAllTag = ref(false)
const resultLength = ref(0)
// 最终价格与数量计算
const resultPrice = computed(() => {
  if (validList.value.length) {
    const products = validList.value.map((item) => [...item.products]).flat(1)
    const priceArr = products.map((item) => {
      if (item.isChecked) {
        return new Decimal(item.finalPrice).mul(item.num)
      } else {
        return 0
      }
    })
    // eslint-disable-next-line vue/no-side-effects-in-computed-properties
    resultLength.value = priceArr.filter(Boolean).length
    return priceArr.reduce((pre, cur) => {
      return new Decimal(pre).add(new Decimal(cur))
    })
  } else {
    return 0
  }
})

initList()

async function initList() {
  await $cardInfo.INIT_CARD()
  validList.value = $cardInfo.validList.map((item: any) => {
    item.products = item.products.map((ite: any) => {
      ite.specs = [...ite.specs, ...handleParams(ite.productAttributes)]
      return ite
    })
    return item
  })
  inValidList.value = $cardInfo.inValidList
}
/**
 * @description: 商品数量变化
 */
const countChange = async (shopId: string, goodId: string, oldSkuId: string, num: number, productAttributes?: any) => {
  const { code, success, data } = await doUpdateShopCarGood(oldSkuId, {
    productId: goodId,
    skuId: oldSkuId,
    num,
    shopId,
    productAttributes,
  })
}

/**
 * @description: 单选回调
 * @param {string} shopId
 */
const goodCheckHandle = (shopId: string) => {
  const currentArr = validList.value.filter((item) => item.shopId === shopId)
  const products = currentArr[0].products
  nextTick(() => {
    if (products.every((item) => item.isChecked)) {
      currentArr[0].isAllChecked = true
    } else {
      currentArr[0].isAllChecked = false
    }
  })
  isCheckAllHandle()
}
/**
 * @description: 全选
 * @returns {*}
 */
const handleCheckAll = () => {
  validList.value = validList.value.map((item) => {
    item.isAllChecked = isCheckAllTag.value
    item.products = item.products.map((ite) => {
      ite.isChecked = isCheckAllTag.value
      return ite
    })
    return item
  })
}

/**
 * @description: 领券列表
 */
const noCoupon = ref(true)
const handleShowCouponList = (item: any) => {
  couponList.value = []
  nextTick(async () => {
    const { code, data, msg } = await doGetCouponList({
      isPlatform: false,
      shopId: item.shopId,
      status: 'UNCLAIMED',
    })
    if (code === 200 && data!.records.length) {
      noCoupon.value = true
      couponList.value = data!.records
    } else {
      noCoupon.value = false
    }
  })
}

/**
 * @description: 店铺全选回调
 * @param {string} shopId
 */
const goodCheckAllHandle = (shopId: string) => {
  const currentArr = validList.value.filter((item) => item.shopId === shopId)
  const allStatus = currentArr[0].isAllChecked
  currentArr[0].products = currentArr[0].products.map((item) => {
    item.isChecked = allStatus
    return item
  })
  isCheckAllHandle()
}

/**
 * @description: 客服
 */
const gotoCustomerService = async (shopId: string) => {
  await doGetMessagesChatRoom(shopId, useUserStore().getterUserInfo.userId)
  openNewWindow('/personalcenter/set/customerservice', { shopId: shopId })
}

/**
 * @description: 全选判断
 */
function isCheckAllHandle() {
  nextTick(() => {
    const unCheckArr = validList.value.filter((item) => {
      return item.isAllChecked
    })
    if (unCheckArr.length === validList.value.length) {
      isCheckAllTag.value = true
    } else {
      isCheckAllTag.value = false
    }
  })
}
/**
 * @description: 批量或单个删除购物车
 */
const handleDelete = (shopId?: string, skuId?: string, uniqueIds?: any) => {
  const list = validList.value
  // debugger
  const submitArr =
    shopId && skuId && uniqueIds
      ? [
          {
            shopId,
            skuIds: [skuId],
            uniqueIds: [uniqueIds],
          },
        ]
      : assemblyData(list)
  if (!submitArr.length) {
    ElMessage.error('请选择商品')
  } else {
    ElMessageBox.confirm(!shopId && !skuId ? '确认要删除所有选中的商品吗？' : '确认要删除该商品吗?', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      const { code, success } = await doDeleteShopCarData(submitArr)
      if (code === 200 && success) {
        ElMessage.success('删除成功')
        $cardInfo.INIT_CARD().then(() => {
          initList()
        })
      }
    })
  }
}
/**
 * @description: 整理提交可用或不可用数据
 * @param {GoodListType[]} list
 */
function assemblyData(list: GoodListType[]) {
  const tempArr = []
  for (let i = 0; i < list.length; i++) {
    const tempObj = {
      shopId: '',
      skuIds: [] as string[],
      uniqueIds: [] as string[],
    }
    if (list[i].products.length > 0) {
      tempObj.shopId = list[i].shopId
      for (let j = 0; j < list[i].products.length; j++) {
        // 对应可用商品判断是否checked 对应不可用商品检测enable
        if (list[i].products[j].isChecked || !list[i].enable) {
          tempObj.skuIds.push(list[i].products[j].id)
          tempObj.uniqueIds.push(list[i].products[j]?.uniqueId)
        }
      }
      if (tempObj.skuIds.length > 0) {
        tempArr.push(tempObj)
      }
    }
  }
  return tempArr
}
/**
 * @description: 移入收藏
 * @returns {*}
 */
const handleMovedFavorites = (shopId: string, item: any) => {
  const { productId, image: productPic, productName, finalPrice: productPrice, uniqueId, id } = item

  const submitAssessArrs = {
    userCollectDTOList: [{ shopId, productId, productPic, productName, productPrice }],
    whetherCollect: true,
  }
  ElMessageBox.confirm('确定移入到收藏夹吗', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, msg } = await doAddAssess(submitAssessArrs)
    if (code !== 200) return ElMessage.error('移入收藏失败')
    const { code: DCode, success } = await doDeleteShopCarData([{ shopId, skuIds: [id], uniqueIds: [uniqueId] }])
    if (DCode === 200 && success) ElMessage.success('收藏成功')
    initList()
  })
}
/**
 * @description: 清空购物车失效商品
 */
const hanleEmpty = () => {
  ElMessageBox.confirm('确定清空失效商品吗？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const { code, success } = await doEmptyShopCarData()
    if (code === 200 && success) {
      ElMessage.success('清空成功')
      $cardInfo.INIT_CARD().then(() => {
        initList()
      })
    }
  })
}

// 修改 DeliveryProduct 接口，使其更符合实际使用场景
interface DeliveryProduct {
  shopId: string
  image: string
  productImage?: string
  num: number
  products?: StorageProducts[]
  shopName?: string
  id?: string
  skuId?: string
  productId?: string
  distributionMode?: 'EXPRESS' | 'INTRA_CITY_DISTRIBUTION' | 'SHOP_STORE' | 'VIRTUAL' | 'WITHOUT'
}

// 扩展 StorageProducts 接口
interface ExtendedStorageProducts extends Omit<StorageProducts, 'distributionMode'> {
  distributionMode?: 'EXPRESS' | 'INTRA_CITY_DISTRIBUTION' | 'SHOP_STORE' | 'VIRTUAL' | 'WITHOUT'
}

// 修改 StorageProducts 接口
interface StorageProducts {
  distributionMode: string[]
  id: string
  image: string
  price: number
  num: number
  salePrice: number
  productId: string
  skuId: string
  productName: string
  specs: string[]
  freightTemplateId: string
  weight: number
  productFeaturesValue: Record<string, any>
}

// 修改 deliveryOptionsList 的类型定义
const deliveryOptionsList = reactive<{
  EXPRESS: DeliveryProduct[]
  INTRA_CITY_DISTRIBUTION: DeliveryProduct[]
  SHOP_STORE: DeliveryProduct[]
  VIRTUAL: DeliveryProduct[]
}>({
  EXPRESS: [],
  INTRA_CITY_DISTRIBUTION: [],
  SHOP_STORE: [],
  VIRTUAL: [],
})

const deliveryType: Record<string, string> = {
  EXPRESS: '快递配送',
  INTRA_CITY_DISTRIBUTION: '同城配送',
  SHOP_STORE: '到店自提',
  VIRTUAL: '无需配送',
}

function formatCartGoodsStorePoint(params: StoragePackage[], key: TypeMode): DeliveryProduct[] {
  const currentGoodsGroups = cloneDeep(params).map((shop: any) => {
    shop.products = shop.products.filter((item: any) => item.distributionMode && item.distributionMode.includes(key))
    shop.distributionMode = key
    return shop
  })
  return currentGoodsGroups.filter((item: any) => item.distributionMode === key).filter((item) => item.products.length > 0)
}

type TypeMode = keyof typeof deliveryOptionsList

// 超出限购次数处理
async function restrictedPurchase(types: boolean, typeMode: TypeMode) {
  const orderValid: any = {
    orderType: 'COMMON',
    activityId: '',
    products: [],
    distributionMode: typeMode || 'EXPRESS',
  }
  const list = !types ? deliveryOptionsList.EXPRESS : deliveryOptionsList[typeMode]
  if (typeMode !== 'EXPRESS') {
    list.forEach((item: DeliveryProduct) => {
      if (item.products && item.products.length > 0) {
        item.products.forEach((ite: StorageProducts) => {
          const { skuId, id: productId, num } = ite
          const shopProduct = { key: { shopId: item.shopId, productId, skuId }, num }
          orderValid.products.push(shopProduct)
        })
      }
    })
  } else {
    list.forEach((item: DeliveryProduct) => {
      const { skuId, id: productId, num } = item
      const shopProduct = { key: { shopId: item.shopId, productId, skuId }, num }
      orderValid.products.push(shopProduct)
    })
  }
  const { code } = await doPostOrderValid(orderValid)
  return code
}

const handleSettlement = async (types: boolean, typeMode: TypeMode = 'EXPRESS', index?: number, sameCity?: string) => {
  if (sameCity === '同城配送') return
  if ($cardInfo.count <= 0) return
  let routerPush = true
  if (!resultLength.value) {
    ElMessage.error('请选择商品')
  } else {
    if (!types) {
      Object.keys(deliveryOptionsList).forEach((item: string) => {
        deliveryOptionsList[item as TypeMode] = []
      })
      for (let i = 0; i < sortData(validList.value).length; i++) {
        if (types) break
        const item = sortData(validList.value)[i]
        const products = item.products || []
        for (let j = 0; j < products.length; j++) {
          const ite = products[j] as ExtendedStorageProducts
          if (ite.distributionMode && ite.distributionMode.indexOf('EXPRESS') !== -1)
            deliveryOptionsList.EXPRESS.push({ ...ite, shopId: item.shopId })
          routerPush = false
        }
      }
      deliveryOptionsList.INTRA_CITY_DISTRIBUTION = formatCartGoodsStorePoint(sortData(validList.value), 'INTRA_CITY_DISTRIBUTION')
      deliveryOptionsList.SHOP_STORE = formatCartGoodsStorePoint(sortData(validList.value), 'SHOP_STORE')
      deliveryOptionsList.VIRTUAL = formatCartGoodsStorePoint(sortData(validList.value), 'VIRTUAL')

      /**
       * @: 判断是否只有一种配送方式
       */
      let isOnlyDelivery = false
      let key: keyof typeof deliveryOptionsList
      for (key in deliveryOptionsList) {
        const item = deliveryOptionsList[key]

        if (item.length && isOnlyDelivery) {
          return (deliveryOptionsDialog.value = true)
        }
        if (item.length) {
          typeMode = key
          isOnlyDelivery = true
          types = true
        }
      }
    }

    if (types) {
      routerPush = true
      let validLists = formatCartGoodsStorePoint(sortData(validList.value, typeMode), typeMode)
      if (typeMode !== 'EXPRESS' && typeof index === 'number' && index! > -1) {
        let validListVal = []
        validListVal.push(validLists[index])
        storage.setItem('commodityInfo', validListVal, 60 * 60 * 2)
      } else storage.setItem('commodityInfo', validLists, 60 * 60 * 2)
    }
    if (!routerPush) return
    if ((await restrictedPurchase(types, typeMode)) === 30039) {
      ElMessage.error('商品库存不足或商品超限购')
      setTimeout(() => {
        $router.go(0)
      }, 700)
      return
    }
    $router.push({
      path: '/settlement',
      query: { source: 'CART' },
    })
  }
}

/**
 * @description: 整合商品数据存储缓存供提交订单使用
 * @returns {*}
 */
const sortData = (
  data: GoodListType[],
  typeMode?: 'EXPRESS' | 'INTRA_CITY_DISTRIBUTION' | 'SHOP_STORE' | 'VIRTUAL' | 'WITHOUT',
): StoragePackage[] => {
  const tempArr: StoragePackage[] = []
  for (let i = 0; i < data.length; i++) {
    const { shopId, shopLogo, shopName, shopType } = data[i]
    let outObject: StoragePackage = {
      distributionMode: typeMode,
      shopId,
      shopName,
      shopLogo,
      shopType,
      products: [],
    }
    for (let j = 0, innerProducts = data[i].products; j < innerProducts.length; j++) {
      if (innerProducts[j].isChecked) {
        const { id, image, num, price, finalPrice, productId, productName, specs, freightTemplateId, distributionMode, productImage } =
          innerProducts[j]
        let productFeaturesValue: Record<string, any> = {}
        innerProducts[j]?.productAttributes?.forEach(
          (item: { id: string | number; featureValues: { featureValueId: string }[] }) =>
            (productFeaturesValue[item.id] = item.featureValues.map((ite: { featureValueId: string }) => ite.featureValueId)) || [],
        )
        const storageProduct: ExtendedStorageProducts = {
          distributionMode,
          id: productId,
          image: image || productImage,
          price,
          num,
          salePrice: +finalPrice,
          productId,
          skuId: id,
          productName,
          specs,
          freightTemplateId,
          weight: 0,
          productFeaturesValue,
        }
        outObject.products.push(storageProduct)
      }
    }
    if (outObject.products.length) {
      tempArr.push(outObject)
    }
  }
  return tempArr
}

const gotoDetail = (productId: string, shopId: string) => {
  $router.push({
    path: '/detail',
    query: { productId, shopId },
  })
}
const getlimitNum = (limitType: string, limitNum: number) => {
  if (limitType !== 'UNLIMITED') {
    return limitNum
  } else {
    return 99999
  }
}

// 选择配送方式商品弹窗
const deliveryOptionsDialog = ref(false)
// 关闭
const handleClose = () => {
  deliveryOptionsDialog.value = false
}
watch(
  () => deliveryOptionsDialog.value,
  () => {
    // @ts-ignore
    if (!deliveryOptionsDialog.value) Object.keys(deliveryOptionsList).forEach((item) => (deliveryOptionsList[item] = []))
  },
)
const scrollVal = ref(false)
const scrollFn = () => (scrollVal.value = true)

/**
 * 修改规格弹窗相关状态
 */
const GoodSpecRef = ref()
const specsDialogVisible = ref(false)
const currentProduct = ref()
// 商品详情
const goodInfo = ref<ApiGoodType>()
// 规格组
const skuGroup = ref<ProductSpecsSkusVO>({
  skus: [],
  specGroups: [],
})
// 当前商品属性
const currentSpecs = ref<ProductExtraDTO>()
const myTeamKey = ref(0)
// 当前商品规格
const currentChoosedSku = ref<StorageSku>()
// 当前商品规格
const choosedSpec = ref<StorageSku>()
// 当前商品数量
const num = ref(1)

// 商品不存在或已下架直接跳页面
async function initGoodsInfo(goodId: string, shopId: string) {
  const { data, code, msg } = await doGetProduct({ productId: goodId, shopId: shopId })
  if (code !== 200) {
    ElMessage.error(msg || '商品详情获取失败')
    return
  }
  goodInfo.value = data
  skuGroup.value = data?.specsSkus
  currentSpecs.value = data.extra
  myTeamKey.value = Date.now()
  const currentSkuArr = data.specsSkus.skus
  const currentSku = data.specsSkus.skus.find((item: StorageSku) => +item.id === +choosedSpec.value!.id)
  const item = currentSkuArr?.find((item: StorageSku) => item.stockType === 'UNLIMITED' || parseInt(item.stock) > 0)
  currentChoosedSku.value = currentSku ?? item
}

/**
 * @description: 处理选择规格
 * @returns {*}
 */
const handleChooseSpec = (e: StorageSku[]) => {
  if (e[0]) choosedSpec.value = e[0]
}
const itemShopId = ref()
// 打开规格弹窗
const openSpecs = (row: any, shopId: string) => {
  specsDialogVisible.value = true
  choosedSpec.value = row
  currentProduct.value = row
  itemShopId.value = shopId
  num.value = row.num

  initGoodsInfo(row.productId, shopId)
}

// 关闭规格弹窗
const handleSpecsClose = () => {
  specsDialogVisible.value = false
}

// 计算限购数量
const ruleMax = computed(() => {
  if (choosedSpec.value?.limitType !== 'UNLIMITED' && choosedSpec.value?.stockType === 'UNLIMITED') {
    return Number(choosedSpec.value?.limitNum)
  } else if (choosedSpec.value?.stockType !== 'UNLIMITED' && choosedSpec.value?.limitType === 'UNLIMITED') {
    return Number(choosedSpec.value?.stock)
  } else if (
    choosedSpec.value?.stockType !== 'UNLIMITED' &&
    choosedSpec.value?.limitType !== 'UNLIMITED' &&
    Number(choosedSpec.value?.limitNum) <= Number(choosedSpec.value?.stock)
  ) {
    return Number(choosedSpec.value?.limitNum)
  } else if (
    choosedSpec.value?.stockType !== 'UNLIMITED' &&
    choosedSpec.value?.limitType !== 'UNLIMITED' &&
    Number(choosedSpec.value?.limitNum) > Number(choosedSpec.value?.stock)
  ) {
    return Number(choosedSpec.value?.stock)
  } else {
    return 999
  }
})

// 确认规格选择
const confirmSpecs = async () => {
  // TODO: 处理确认逻辑
  handleSpecsClose()
  const { code, success, data } = await doUpdateShopCarGood(currentProduct.value!.uniqueId, {
    productId: choosedSpec.value!.productId,
    skuId: choosedSpec.value!.id,
    num: num.value,
    shopId: itemShopId.value,
    productAttributes: GoodSpecRef.value!.productAttributes || null,
  })
  initList()
}

const gotoShop = (shopId: string) => {
  $router.push({
    path: '/shop',
    query: { shopId },
  })
}
</script>

<template>
  <div></div>
  <div class="shopCarBox">
    <div class="shopCarBox__title">
      <div style="box-sizing: content-box; font-size: 16px; font-weight: 700; line-height: 48px">全部商品&nbsp;({{ $cardInfo.count }})</div>
    </div>
    <div class="shopCarBox__header">
      <el-checkbox v-model="isCheckAllTag" label="全选" size="large" @change="handleCheckAll" />
      <div style="margin-left: 56px">商品</div>
      <div style="margin-left: 454px">单价</div>
      <div style="margin-left: 105px">数量</div>
      <div style="margin-left: 118px">金额</div>
      <div style="margin-left: 110px">操作</div>
    </div>

    <div v-if="validList.length <= 0 && inValidList.length <= 0" class="OutOfStock">
      <img src="@/assets/images/icon/carNo.png" />
      <span>暂无商品~</span>
    </div>
    <div v-else>
      <div style="margin-bottom: 72px">
        <div v-for="(item, key) in validList" :key="item.shopId">
          <!-- 店铺 -->
          <div flex items-center justify-between>
            <div class="shopCarBox__shopBox" :style="{ marginTop: key === 0 ? '0px' : '20px' }">
              <el-checkbox v-model="item.isAllChecked" :disabled="!item.enable" size="large" @change="goodCheckAllHandle(item.shopId)" />
              <span><QIcon name="icon-dianpu5" size="16px" color="#999" /></span>
              <template v-if="item.shopType">
                <text v-if="item.shopType === 'SELF_OWNED'" c-c-f54319 c-fs-12 c-lh-16 c-bg-fb232f class="shop_tag">自营</text>
                <text v-if="item.shopType === 'PREFERRED'" c-c-00bb2c c-fs-12 c-lh-16 c-bg-8239f6 class="shop_tag">优选</text>
              </template>
              <div cursor-pointer @click="gotoShop(item.shopId)">{{ item.shopName }}</div>
              <!-- 客服 -->
              <div v-if="getterPropertiesList?.otherData?.service" @click.stop="gotoCustomerService(item.shopId)">
                <QIcon
                  cursor-pointer
                  name="icon-lianxikefu"
                  color="rgb(245, 67, 25)"
                  style="margin-left: 8px; position: relative; top: -1px; font-weight: normal"
                />
              </div>
            </div>
            <el-popover class="box-item" trigger="click" placement="bottom-start" :width="480" @before-enter="handleShowCouponList(item)">
              <template #reference><div c-fs-14 style="color: #ff0f0f; cursor: pointer; margin-top: 20px">领券</div> </template>
              <div style="overflow-y: auto">
                <PcCouponGoodDetails v-if="noCoupon" :coupon-list="couponList" :cartCoupon="false" />
                <div v-else e-c-9 text-center>暂无优惠券</div>
              </div>
            </el-popover>
          </div>
          <!-- 商品 -->
          <div class="shopCarBox__goodBoxs">
            <div
              v-for="(ite, index) in item.products"
              :key="ite.productId"
              class="shopCarBox__goodBox"
              :style="{ borderBottom: index === item.products.length - 1 ? 'none' : '1px dashed rgba(153, 153, 153, 0.4)' }"
            >
              <div class="shopCarBox__goodBox--checkedBox">
                <el-checkbox
                  v-model="ite.isChecked"
                  class="shopCarBox__goodBox--checked"
                  :disabled="!item.enable"
                  size="large"
                  @change="goodCheckHandle(item.shopId)"
                />
                <div class="shopCarBox__goodBox--imgBox">
                  <el-image v-if="ite.image" :src="ite.image" @click="gotoDetail(ite.productId, item.shopId)" />
                  <el-image v-else :src="ite.productImage" @click="gotoDetail(ite.productId, item.shopId)" />
                </div>
              </div>
              <div class="shopCarFrist">
                <div class="shopCarFrist__specification">
                  <div @click="gotoDetail(ite.productId, item.shopId)">
                    {{ ite.productName }}
                  </div>
                  <!-- 打开规格选项 -->
                  <p v-if="ite.specs.length > 0" @click="openSpecs(ite, item.shopId)">
                    <span class="shopCarFrist__specification--specs"
                      >{{ [...ite.specs].join('，') }} <QIcon name="icon-xiala" size="10px" color="#999" style="position: absolute; right: 5px"
                    /></span>
                  </p>
                </div>
                <div class="shopCarFrist__price">
                  <text c-fs-12>￥</text>
                  <text>{{ useConversionPrice(ite.finalPrice).toFixed(2).split('.')[0] }}</text>
                  <text>.</text>
                  <text c-fs-12>{{ useConversionPrice(ite.finalPrice).toFixed(2).split('.')[1] }}</text>
                </div>
                <div class="shopCarFrist__number">
                  <el-input-number
                    v-model="ite.num"
                    :min="1"
                    :max="getlimitNum(ite.skuStock.limitType, ite.skuStock.limitNum)"
                    size="small"
                    @change="countChange(item.shopId, ite.productId, ite.id, ite.num)"
                  />
                  <div v-if="ite.skuStock.limitType !== 'UNLIMITED'" class="shopCarFrist__number--limitNum">限购 {{ ite.skuStock.limitNum }} 件</div>
                </div>
                <div class="shopCarFrist__price shopCarFrist__totalPrice">
                  <text c-fs-12>￥</text>
                  <text c-fs-16>{{ (Number(useConversionPrice(ite.finalPrice)) * ite.num).toFixed(2).split('.')[0] }}</text>
                  <text>.</text>
                  <text c-fs-12>{{ (Number(useConversionPrice(ite.finalPrice)) * ite.num).toFixed(2).split('.')[1] }}</text>
                </div>
                <div class="shopCarFrist__operate">
                  <div>
                    <div @click="handleMovedFavorites(item.shopId, ite)">移入收藏</div>
                    <div @click="handleDelete(item.shopId, ite.id, ite?.uniqueId)">删除</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-for="item in inValidList" :key="item.shopId">
          <!-- 店铺 -->
          <div class="shopCarBox__shopBox" style="margin-top: 8px">
            <div>无效商品</div>
            <div class="shopCarBox__shopBox--del" @click="hanleEmpty">清除无效商品</div>
          </div>
          <!-- 商品 -->
          <div class="shopCarBox__goodBoxs">
            <div v-for="ite in item.products" :key="ite.productId" class="shopCarBox__goodBox">
              <!-- :class="ite.isChecked ? 'c-bg-FFFBF0' : ''" -->
              <div class="shopCarBox__goodBox--checkedBox shopCarBox__goodBox--loseEfficacyImgBox">
                <div class="shopCarBox__goodBox--imgBox">
                  <img :src="ite.image" />
                </div>
                <span class="shopCarBox__goodBox--imgBox"><img src="@/assets/images/Delisted.png" style="opacity: 0.6" /></span>
              </div>
              <div class="shopCarFrist">
                <div class="shopCarFrist__specification shopCarFrist__loseEfficacy">
                  <div>
                    {{ ite.productName }}
                  </div>
                  <span>已下架</span>
                </div>
                <div class="shopCarFrist__price" />
                <div class="shopCarFrist__number" />
                <div class="shopCarFrist__price shopCarFrist__totalPrice" />
                <div class="shopCarFrist__operate" />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-affix v-if="validList.length > 0" :position="!scrollVal ? 'top' : 'bottom'" @scroll="scrollFn">
      <div class="affix">
        <!-- 左 -->
        <div class="affix__left">
          <el-checkbox
            v-model="isCheckAllTag"
            class="affix__left--checkbox"
            :label="`全选${+resultLength ? '(' + resultLength + ')' : ''}`"
            size="large"
            @change="handleCheckAll"
          />
          <div v-if="+resultLength > 0" @click="handleDelete()">删除</div>
        </div>
        <!-- 右 -->
        <div class="affix__right">
          <div v-if="+resultPrice > 0" class="affix__right--resultPrice">
            <div c-c-e31436 fw-700 c-fs-16>
              ￥<text c-fs-30>{{ useConversionPrice(resultPrice).toFixed(2).split('.')[0] }}</text
              ><text>.</text><text c-fs-16>{{ useConversionPrice(resultPrice).toFixed(2).split('.')[1] }}</text>
            </div>
          </div>
          <div
            :class="$cardInfo.count > 0 ? 'affix__right--settlement' : 'affix__right--settlement affix__right--settlementNull'"
            @click="handleSettlement(false)"
          >
            去结算
          </div>
        </div>
      </div>
    </el-affix>

    <el-dialog v-model="deliveryOptionsDialog" title="配送方式选择" :width="540" top="5%" :before-close="handleClose" style="position: relative">
      <div class="shopInfoBox">
        <div v-for="(item, index) in deliveryOptionsList" :key="index" class="shopInfo">
          <div v-if="index === 'EXPRESS'">
            <div v-if="item.length > 0" class="shopInfo__box">
              <div class="shopInfo__title">
                <div>{{ deliveryType[index] }}</div>
                <p>
                  共 <span>{{ item.length }}</span> 件
                </p>
              </div>
              <div class="shopInfo__title shopInfo__info">
                <div class="shopInfo__info--imgList">
                  <div v-for="(i, j) in item" :key="j" class="shopInfo__info--img">
                    <img v-if="i.image" :src="i.image" alt="" />
                    <img v-else :src="i.productImage" alt="" />
                    <span v-if="i.num > 0" class="shopInfo__info--num">
                      <span>x{{ i.num }}</span>
                    </span>
                  </div>
                </div>
                <el-button class="shopInfo__info--button" @click="handleSettlement(true, index)">去结算</el-button>
              </div>
            </div>
          </div>
          <div v-else>
            <div v-if="item.length > 0" class="shopInfo__box">
              <div class="shopInfo__title" style="margin-bottom: 12px">
                <div>{{ deliveryType[index] }}</div>
              </div>
              <div v-for="(shop, shopIndex) in item" :key="shopIndex">
                <div class="shopInfo__title--storeName">
                  <!-- 店铺名 -->
                  <div>
                    <span><QIcon name="icon-dianpu5" size="12px" color="#F54319" /></span>
                    <div>{{ shop?.shopName }}</div>
                  </div>
                  <p>
                    共 <span>{{ shop?.products?.length }}</span> 件
                  </p>
                </div>
                <div class="shopInfo__title shopInfo__info">
                  <div class="shopInfo__info--imgList">
                    <div v-for="(i, j) in shop?.products" :key="j" class="shopInfo__info--img">
                      <img :src="i.image" alt="" />
                      <span v-if="i.num > 0" class="shopInfo__info--num">
                        <span>x{{ i.num }}</span>
                      </span>
                    </div>
                  </div>
                  <div
                    class="shopInfo__info--button"
                    :class="{ 'shopInfo__info--disabled': deliveryType[index] === '同城配送' }"
                    @click="handleSettlement(true, index, shopIndex, deliveryType[index])"
                  >
                    去结算
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>

    <!-- 商品规格弹窗 -->
    <el-dialog v-model="specsDialogVisible" class="specs-dialog" :width="750">
      <div v-if="choosedSpec" class="specs-dialog">
        <!-- 商品基本信息 -->
        <div class="specs-dialog__header">
          <div class="specs-dialog__image">
            <el-image v-if="choosedSpec?.image" :src="choosedSpec?.image" fit="cover" />
            <el-image v-else-if="currentProduct?.image" :src="currentProduct?.image" fit="cover" />
            <el-image v-else :src="currentProduct?.productImage" fit="cover" />
          </div>
          <div>
            <div class="specs-dialog__info">
              <div class="specs-dialog__price">
                ￥<span style="font-size: 30px">{{ useConversionPrice(choosedSpec?.salePrice).toFixed(2) }}</span>
              </div>
              <div class="specs-dialog__stock">已售: {{ choosedSpec?.stock || 0 }}件</div>
            </div>
            <!-- 规格选项列表 -->
            <div c-h-300 overflow-y-auto>
              <GoodSpec
                v-if="goodInfo && (skuGroup.specGroups.length > 0 || currentSpecs?.productAttributes)"
                ref="GoodSpecRef"
                :key="myTeamKey"
                :params="currentSpecs"
                :current-specs="currentProduct.productAttributes"
                :sku-group="skuGroup"
                :current-choose="currentChoosedSku!.specs"
                :char-spec="false"
                style="max-width: 450px"
                @chooseSpec="handleChooseSpec"
              />
            </div>
          </div>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleSpecsClose">取消</el-button>
          <el-button type="primary" :disabled="ruleMax <= 0" @click="confirmSpecs">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<style lang="scss" scoped>
@include b(shopInfoBox) {
  max-height: 720px;
  overflow: auto;
}
@include b(shopInfo) {
  border-radius: 4px;
  background-color: #f5f5f5;
  @include e(box) {
    padding: 12px 8px 0px 16px;
    margin-top: 16px;
    :first-of-type {
      margin-top: 0;
    }
  }
  @include e(title) {
    display: flex;
    justify-content: space-between;
    color: #333;
    p {
      font-size: 12px;
      color: #999;
      span {
        color: #ff0000;
      }
    }
    @include m(storeName) {
      display: flex;
      justify-content: space-between;
      color: #333;
      font-size: 12px;
      padding-left: 2px;
      div {
        display: flex;
        div {
          line-height: 18px;
        }
        span {
          display: inline-block;
          padding-top: 2px;
          margin-right: 10px;
        }
      }
      p {
        font-size: 12px;
        color: #999;
        span {
          color: #ff0000;
        }
      }
    }
  }
  @include e(info) {
    margin-top: 12px;
    align-items: center;
    @include m(imgList) {
      display: flex;
      justify-content: start;
      width: 408px;
      height: 93px;
      margin-bottom: 16px;
      overflow-x: auto;
    }
    @include m(num) {
      position: absolute;
      bottom: -3px;
      right: 0px;
      span {
        width: 18px;
        position: absolute;
        bottom: 6px;
        right: 0px;
      }
    }
    @include m(img) {
      flex-shrink: 0;
      width: 90px;
      height: 90px;
      margin-right: 12px;
      position: relative;
      border-radius: 4px;
      overflow: hidden;
      img {
        width: 90px;
        height: 90px;
      }
    }
    @include m(button) {
      flex-shrink: 0;
      width: 64px;
      height: 26px;
      line-height: 26px;
      text-align: center;
      border-radius: 2px;
      background: #f54319;
      font-size: 12px;
      color: #fff;
      margin-left: 8px;
      cursor: pointer;
    }
    @include m(disabled) {
      background: #ccc;
      cursor: not-allowed;
    }
  }
}
@include b(shopCarBox) {
  width: 1200px;
  text-align: left;
  background-color: #fff;
  margin: 16px auto 24px;
  padding: 0 24px 0 24px;
  @include e(title) {
    display: flex;
    align-items: center;
    font-size: 16px;
    font-weight: 600;
    line-height: 23px;
    color: #333;
    span {
      color: #ff0000;
      span {
        display: inline-block;
        transform: translateY(1px);
      }
    }
  }
  @include e(header) {
    display: flex;
    height: 42px;
    font-size: 14px;
    line-height: 42px;
    color: #666;
    background-color: #f5f5f5;
    padding: 0 96px 0 16px;
    font-weight: bold;
    margin-top: 0px;
  }
  @include e(shopBox) {
    display: flex;
    align-items: center;
    height: 45px;
    line-height: 45px;
    padding-left: 16px;
    font-size: 16px;
    font-weight: bold;
    position: relative;
    span {
      margin: 0 5px 0 19px;
    }
    p {
      margin-left: 8px;
      cursor: pointer;
    }
    @include m(del) {
      position: absolute;
      right: 0;
      cursor: pointer;
      font-size: 12px;
      color: #999;
    }
  }
  @include e(goodBoxs) {
    border-top: 1px solid rgb(235, 234, 230);
  }
  @include e(goodBox) {
    display: flex;
    padding: 8px 0 8px 15px;
    border-bottom: 1px dashed rgba(153, 153, 153, 0.4);
    border-top: 0px;
    @include m(checkedBox) {
      display: flex;
      align-items: center;
    }
    @include m(checked) {
      transform: translateY(-3px);
      // margin-right: 16px;
    }
    @include m(imgBox) {
      width: 100px;
      height: 100px;
      border-radius: 4px;
      overflow: hidden;
      cursor: pointer;
      flex-shrink: 0;
      margin-left: 16px;
      img {
        width: 100%;
        height: 100%;
      }
    }
    @include m(loseEfficacyImgBox) {
      position: relative;
      margin-left: 8px;
      display: flex;
      span {
        width: 100px;
        height: 100px;
        display: block;
        position: absolute;
        display: flex;
        justify-content: center;
        align-items: center;
      }
    }
    @include b(shopCarFrist) {
      display: flex;
      color: #333;
      @include e(specification) {
        width: 420px;
        padding-left: 8px;
        cursor: pointer;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        div {
          font-size: 14px;
          line-height: 25px;
          height: 50px;
          color: #333;
          overflow: hidden;
        }
        @include m(specs) {
          position: relative;
          display: inline-block;
          overflow: hidden;
          font-size: 14px;
          line-height: 20px;
          width: auto;
          text-overflow: ellipsis;
          white-space: nowrap;
          max-width: 400px;
          color: #666;
          padding: 0 24px 0 8px;
          border: 1px solid #f3f3f3;
          border-radius: 6px;
          line-height: 28px;
        }
      }
      @include e(loseEfficacy) {
        cursor: auto;
        div {
          color: #999;
        }
        span {
          color: #333;
        }
      }
      @include e(price) {
        color: #ccc;
        width: 97px;
        text-align: center;
        font-size: 14px;
        margin-left: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
      }
      @include e(number) {
        margin-left: 37px;
        display: flex;
        align-items: center;
        justify-content: center;
        @include m(limitNum) {
          text-align: center;
          font-size: 12px;
          color: #ff0000;
          margin-top: 4px;
        }
      }
      @include e(totalPrice) {
        margin-left: 58px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #333;
      }
      @include e(operate) {
        margin-left: 63px;
        font-size: 14px;
        display: flex;
        align-items: center;
        margin-top: 16px;
        div {
          cursor: pointer;
          color: #999;
        }
        div:first-child {
          margin-bottom: 14px;
        }
      }
    }
  }
}
@include b(affix) {
  height: 48px;
  width: 100%;
  line-height: 48px;
  display: flex;
  justify-content: space-between;
  background-color: #fff;
  box-shadow: 0px 0px 2px 2px rgba(0, 0, 0, 0.08);
  @include e(left) {
    display: flex;
    font-size: 14px;
    padding-left: 16px;
    color: #999;
    div {
      cursor: pointer;
      margin-left: 40px;
    }
    @include m(checkbox) {
      height: 48px;
      line-height: 48px;
    }
  }
  @include e(right) {
    display: flex;
    font-size: 14px;
    color: #333;

    @include m(resultPrice) {
      margin-right: 37px;
    }
    @include m(settlement) {
      width: 96px;
      height: 48px;
      font-size: 20px;
      font-weight: 500;
      text-align: center;
      color: #fff;
      background-color: #ff0000;
      cursor: pointer;
    }
    @include m(settlementNull) {
      background-color: #999;
      cursor: default;
    }
  }
}
@include b(copyWriter) {
  position: absolute;
  top: 25px;
  left: 144px;
  color: #999;
  font-size: 12px;
  zoom: 0.9;
}
@include b(OutOfStock) {
  position: relative;
  display: flex;
  justify-content: center;
  img {
    margin-top: 94px;
    width: 488px;
    height: 488px;
  }
  span {
    position: absolute;
    top: 410px;
  }
}

@include b(specs-dialog) {
  height: 380px;
  max-height: 530px;
  @include e(header) {
    display: flex;
    margin-bottom: 20px;
  }

  @include e(image) {
    width: 220px;
    height: 220px;
    border-radius: 4px;
    overflow: hidden;
    margin-right: 16px;

    :deep(.el-image) {
      width: 100%;
      height: 100%;
    }
  }

  @include e(info) {
    flex: 1;
    display: flex;
    height: 60px;
    align-items: center;
    border-bottom: 1px solid #f2f2f2;
  }

  @include e(price) {
    font-size: 16px;
    color: #ff5000;
    font-weight: bold;
    margin-bottom: 8px;
    margin-right: 12px;
  }

  @include e(stock) {
    font-size: 14px;
    color: rgb(185, 185, 185);
    margin-bottom: 8px;
  }

  @include e(footer) {
    border-top: 1px solid #eeeeee;
    padding-top: 20px;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
.shop_tag {
  display: inline-block;
  font-size: 10px;
  zoom: 0.9;
  border-radius: 3px;
  overflow: hidden;
  padding: 1px 4px;
  color: #fff;
  margin-right: 5px;
}
:deep(.el-tag) {
  &.el-tag--primary {
    background-color: #e6f0ff;
    border-color: #0066ff;
    color: #0066ff;
  }
}

:deep().el-dialog__body {
  padding: 0 16px 16px 16px;
}
:deep().el-checkbox__label {
  color: #666 !important;
  font-weight: bold;
  margin-left: 2px;
}
:deep().el-input-number--small {
  width: 90px;
  border: 1px solid rgba(153, 153, 153, 0.5);
  overflow: hidden;
}
:deep().el-input-number--small .el-input-number__decrease,
.el-input-number--small .el-input-number__increase {
  height: 24px;
  height: 24px;
  border-radius: 0;
  background-color: #f5f5f5;
  transform: translate(-1px, -1px);
}
:deep().el-input-number__increase {
  height: 24px;
  border-radius: 0;
  background-color: #f5f5f5;
  transform: translate(1px, -1px);
}
:deep().el-checkbox__input.is-checked .el-checkbox__inner {
  width: 14px;
  height: 14px;
  background-color: #0066ff !important;
  border: 1px solid #0066ff !important;
}
:deep().el-checkbox__inner:hover {
  border-color: #0066ff !important;
}
:deep().el-checkbox__input.is-checked + .el-checkbox__label {
  color: #0066ff !important;
}
:deep().el-dialog__header {
  padding: 16px;
  margin: 0;
  border-bottom: 1px solid rgb(222, 222, 222);
  span {
    font-size: 16px;
  }
}
:deep().el-dialog__headerbtn .el-dialog__close {
  font-size: 22px;
}
:deep().specs-dialog .el-dialog__header {
  display: none;
}
:deep().couponList .el-popconfirm__action {
  display: none;
}
</style>
