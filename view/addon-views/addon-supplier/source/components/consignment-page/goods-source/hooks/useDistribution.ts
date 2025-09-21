import {
  doGetConsignmentSetting,
  doGetSupplierCommodityDetails,
  doPostSingleDistribution,
  doGetCategory,
  doGetCommoditySku,
  doGetPlatformCategory,
} from '../../../../apis'
import { CascaderProps, ElMessage, FormRules } from 'element-plus'
import { SkuInterface } from '../../types/list'
import Decimal from 'decimal.js'
import { Ref, reactive, ref, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import useConvert from '@/composables/useConvert'

type ShopCategoryItem = Record<'id' | 'name' | 'parentId' | 'level', string>
interface ShopCategoryList extends ShopCategoryItem {
  disabled?: boolean
  children: ShopCategoryList[]
}
const { divTenThousand, mulTenThousand } = useConvert()
const basicFormInfo = {
  name: '',
  saleDescribe: '',
  platformCategoryId: [] as string[],
  categoryId: '',
  providerId: '',
  widePic: '',
  distributionMode: [] as string[],
  videoUrl: '',
  albumPics: '',
  productType: 'REAL_PRODUCT',
  specGroups: [],
  //平台类目 1 2 3级id
  platformCategory: {
    one: null,
    two: null,
    three: null,
  },
  //店铺分类 1 2 3级id
  shopCategory: {
    one: null,
    two: null,
    three: null,
  },
  skus: [
    {
      id: '',
      image: '',
      initSalesVolume: 0,
      limitNum: 0,
      limitType: 'UNLIMITED',
      price: 0,
      productId: '',
      initStock: 0,
      salePrice: 0,
      shopId: '',
      stockType: 'UNLIMITED',
      weight: 0,
      specs: [],
    },
  ],
  productParameters: [],
  productAttributes: [],
  serviceIds: [],
  detail: '',
  freightTemplateId: '0',
  status: 'SELL_ON',
  brandId: '',
}
const useDistribution = () => {
  const $route = useRoute()
  const $router = useRouter()
  const formRef = ref<{
    validate: (callback?: (isValid: boolean) => void) => void
  }>()
  const distributionSkus = ref<SkuInterface[]>([])
  const specGroups = ref<any[]>([])
  const distributionFormModel = reactive({
    name: '',
    shopCategory: '',
    saleDescribe: '',
    consignmentPriceSetting: {
      type: 'UNIFY',
      sale: 0,
      scribe: 0,
    },
    unifyPriceSetting: {
      type: '',
      sale: '',
      scribe: '',
    },
  })
  const distributionFormRules: FormRules = {
    name: { required: true, message: '请输入商品名称', trigger: 'blur' },
    shopCategory: { required: true, message: '请选择店铺类目', trigger: 'change', type: 'array' },
  }
  const shopCascaderProps: CascaderProps = {
    expandTrigger: 'hover',
    label: 'name',
    value: 'id',
  }
  const currentComodityInfo = ref(basicFormInfo)
  const platformCategoryList = ref<ShopCategoryList[]>([])
  const shopCategoryList = ref<ShopCategoryList[]>([])
  async function getPlatformCategory() {
    const { code, data, success } = await doGetPlatformCategory()
    if (code !== 200) {
      ElMessage.error('获取平台分类失败')
      return
    }
    platformCategoryList.value = checkCategoryEnable(
      1,
      data?.map((item: any) => ({ ...item, id: item.categoryId })),
    )
  }
  /**
   * 获取店铺分类
   */
  async function getShopCategory() {
    const { code, data } = await doGetCategory({
      current: 1,
      size: 500,
    })
    if (code !== 200) {
      ElMessage.error('获取店铺分类失败')
      return
    }
    shopCategoryList.value = checkCategoryEnable(1, data.records)
  }
  const initialCommdityData = async () => {
    const shopId = $route.query.shopId as string
    const commodityId = $route.query.id as string
    const { data } = await doGetSupplierCommodityDetails(commodityId, shopId)
    distributionFormModel.name = data?.name
    currentComodityInfo.value.platformCategoryId = [
      data?.extra?.platformCategory?.one,
      data?.extra?.platformCategory?.two,
      data?.extra?.platformCategory?.three,
    ] as string[]
  }
  const initialSettingsData = async () => {
    const { data, code } = await doGetConsignmentSetting()
    if (code === 200) {
      if (data) {
        distributionFormModel.unifyPriceSetting.sale = divTenThousand(data?.sale).toString()
        distributionFormModel.unifyPriceSetting.scribe = divTenThousand(data?.scribe).toString()
        distributionFormModel.unifyPriceSetting.type = data?.type
      } else {
        distributionFormModel.consignmentPriceSetting.type = 'RATE'
      }
    }
  }
  watch(
    () => distributionFormModel.consignmentPriceSetting,
    () => buildPrice(distributionFormModel, distributionSkus),
    { deep: true },
  )
  const initialCommoditySkuInfo = async () => {
    const shopId = $route.query.shopId as string
    const commodityId = $route.query.id as string
    const { data } = await doGetCommoditySku(shopId, commodityId)
    // row?.stockType === 'UNLIMITED' ? '无限库存' : row?.stock
    distributionSkus.value = data?.skus
      ?.map((item: SkuInterface) => ({
        ...item,
        salePrice: divTenThousand(item.salePrice)?.toString(),
        price: divTenThousand(item.price)?.toString(),
      }))
      ?.filter((item: SkuInterface) => !(item?.stockType !== 'UNLIMITED' && Number(item?.stock) === 0))
    specGroups.value = data?.specGroups || []
    buildPrice(distributionFormModel, distributionSkus)
  }
  const initialData = async () => {
    initialCommdityData()
    getPlatformCategory()
    getShopCategory()
    try {
      await initialSettingsData()
    } finally {
      initialCommoditySkuInfo()
    }
  }
  const cancelDistribution = () => {
    $router.back()
  }
  const handleConfirmDistribution = () => {
    formRef.value?.validate(async (isValid) => {
      if (isValid) {
        const data: any = {
          shopCategory: {
            one: distributionFormModel?.shopCategory?.[0],
            two: distributionFormModel?.shopCategory?.[1],
            three: distributionFormModel?.shopCategory?.[2],
          },
          shopProductKey: {
            shopId: $route.query?.shopId,
            productId: $route.query?.id,
          },
          name: distributionFormModel.name,
          saleDescribe: distributionFormModel.saleDescribe,
        }
        const consignmentPriceSetting: any = {}
        if (distributionFormModel.consignmentPriceSetting.type === 'UNIFY') {
          // 统一设置
          consignmentPriceSetting.type = distributionFormModel.unifyPriceSetting.type
          consignmentPriceSetting.sale = mulTenThousand(distributionFormModel.unifyPriceSetting.sale || 0).toString()
          consignmentPriceSetting.scribe = mulTenThousand(distributionFormModel.unifyPriceSetting.scribe || 0).toString()
        } else {
          consignmentPriceSetting.type = distributionFormModel.consignmentPriceSetting.type
          consignmentPriceSetting.sale = mulTenThousand(distributionFormModel.consignmentPriceSetting.sale || 0).toString()
          consignmentPriceSetting.scribe = mulTenThousand(distributionFormModel.consignmentPriceSetting.scribe || 0).toString()
        }
        data.consignmentPriceSetting = consignmentPriceSetting
        const { code, success, msg } = await doPostSingleDistribution(data)
        if (code === 200 && success) {
          ElMessage.success({ message: msg || '铺货完成' })
          cancelDistribution()
        } else {
          ElMessage.error({ message: msg })
        }
      }
    })
    // cancelDistribution()
  }
  initialData()
  return {
    formRef,
    distributionFormModel,
    distributionFormRules,
    platformCategoryList,
    currentComodityInfo,
    shopCascaderProps,
    shopCategoryList,
    distributionSkus,
    divTenThousand,
    specGroups,
    cancelDistribution,
    handleConfirmDistribution,
  }
}

export default useDistribution

// 按比例设价(乘法)
const handleRatePrice = (config: any, skuRefs: Ref<SkuInterface[]>) => {
  const type = config?.consignmentPriceSetting?.type === 'UNIFY' ? 'unifyPriceSetting' : 'consignmentPriceSetting'
  return skuRefs.value?.map((skuRef) => {
    // 销售价
    const actualSalePrice = new Decimal(skuRef.salePrice)?.mul(new Decimal(1).add(new Decimal(config[type]?.sale || 0).div(100))).toFixed(2)
    // 划线价
    const actualPrice = new Decimal(actualSalePrice)?.mul(new Decimal(1).add(new Decimal(config[type]?.scribe || 0).div(100)))
    return {
      ...skuRef,
      actualSalePrice,
      actualPrice,
    }
  })
}
// 固定金额设价(加法)
const handleRegularPrice = (config: any, skuRefs: Ref<SkuInterface[]>) => {
  const type = config?.consignmentPriceSetting?.type === 'UNIFY' ? 'unifyPriceSetting' : 'consignmentPriceSetting'
  return skuRefs.value?.map((skuRef) => {
    // 销售价
    const actualSalePrice = new Decimal(skuRef.salePrice)?.add(new Decimal(config[type]?.sale || 0)).toFixed(2)
    // 划线价
    const actualPrice = new Decimal(actualSalePrice)?.add(new Decimal(config[type]?.scribe || 0)).toFixed(2)
    return {
      ...skuRef,
      actualSalePrice,
      actualPrice,
    }
  })
}
// 统一设价
const handleUnifyPrice = (config: any, skuRefs: Ref<SkuInterface[]>) => {
  const setType = config?.unifyPriceSetting?.type
  return handleConfigPrice[setType](config, skuRefs)
}
// 策略模式 分开计算价格 由buildPrice()统一调度
const handleConfigPrice = {
  UNIFY: handleUnifyPrice,
  RATE: handleRatePrice,
  REGULAR: handleRegularPrice,
  undefined: () => [],
}

function buildPrice(config: any, skuRefs: Ref<SkuInterface[]>) {
  const setType = config?.consignmentPriceSetting?.type
  skuRefs.value = handleConfigPrice[setType](config, skuRefs)
}
/**
 * 检查是分类否可用
 * @param currentLevel
 * @param records
 */
function checkCategoryEnable(currentLevel: number, records: any[]) {
  const isLastLevel = currentLevel === 3
  for (let index = 0; index < records.length; ) {
    const record = records[index]
    if (isLastLevel) {
      record.disabled = false
      index++
      continue
    }
    const children = (record.children || record.secondCategoryVos || record.categoryThirdlyVos) as any[]
    delete record.secondCategoryVos
    delete record.categoryThirdlyVos
    const disable = !children || children.length === 0
    record.disabled = disable
    if (disable) {
      records.splice(index, 1)
      continue
    }
    checkCategoryEnable(currentLevel + 1, children)
    if (children.length === 0) {
      records.splice(index, 1)
      continue
    }
    record.children = children
    index++
  }

  return records
}
