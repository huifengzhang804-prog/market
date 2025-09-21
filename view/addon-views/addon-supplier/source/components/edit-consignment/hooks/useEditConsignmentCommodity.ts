import { Ref, reactive, ref, onMounted, watch } from 'vue'
import {
  doGetConsignmentCommidityInfo,
  doGetConsignmentSetting,
  doPostUpdateConsignmentCommodityInfo,
  doGetCategory,
  doGetPlatformCategory,
  doGetLabelList,
} from '../../../apis'
import { CascaderProps, ElMessage, FormRules } from 'element-plus'
import { SkuInterface } from '../types'
import Decimal from 'decimal.js'
import useConvert from '@/composables/useConvert'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import { useRoute, useRouter } from 'vue-router'

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
  const formRef = ref<{ validate: (callback?: (isValid: boolean) => void) => void }>()
  const distributionSkus = ref<SkuInterface[]>([])
  const specGroups = ref<any[]>([])
  const distributionFormModel = reactive({
    name: '',
    shopCategory: [] as string[],
    saleDescribe: '',
    labelId: '',
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
  const LabelList = ref([])
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
  /**
   * 获取商品标签
   */
  async function getLabelList() {
    const { code, data } = await doGetLabelList(useShopInfoStore().shopInfo.shopType)
    if (code !== 200) {
      ElMessage.error('获取商品标签失败')
      return
    }
    LabelList.value = data
  }
  const initialCommdityData = async () => {
    const commodityId = $route.query.id as string
    const { data } = await doGetConsignmentCommidityInfo(commodityId)
    distributionFormModel.name = data?.name
    distributionFormModel.saleDescribe = data?.saleDescribe
    distributionFormModel.labelId = data?.labelId
    currentComodityInfo.value.platformCategoryId = [
      data?.extra?.platformCategory?.one,
      data?.extra?.platformCategory?.two,
      data?.extra?.platformCategory?.three,
    ] as string[]
    distributionFormModel.consignmentPriceSetting.type = data?.extra?.consignmentPriceSetting?.type
    distributionFormModel.consignmentPriceSetting.sale = divTenThousand(data?.extra?.consignmentPriceSetting?.sale).toNumber()
    distributionFormModel.consignmentPriceSetting.scribe = divTenThousand(data?.extra?.consignmentPriceSetting?.scribe).toNumber()
    distributionFormModel.shopCategory = [data?.extra?.shopCategory?.one, data?.extra?.shopCategory?.two, data?.extra?.shopCategory?.three]
    const skuInfo = data?.storageSpecSku?.[0]

    distributionSkus.value = skuInfo?.skus?.map((item: SkuInterface) => ({
      ...item,
      salePrice: divTenThousand(item.salePrice)?.toString(),
      price: divTenThousand(item.price)?.toString(),
    }))
    specGroups.value = skuInfo?.specGroups || []
    buildPrice(distributionFormModel, distributionSkus)
  }
  const initialSettingsData = async () => {
    const { data, code } = await doGetConsignmentSetting()
    if (code === 200) {
      if (data) {
        distributionFormModel.unifyPriceSetting.sale = divTenThousand(data?.sale).toString()
        distributionFormModel.unifyPriceSetting.scribe = divTenThousand(data?.scribe).toString()
        distributionFormModel.unifyPriceSetting.type = data?.type
      }
    }
  }
  watch(
    () => distributionFormModel.consignmentPriceSetting,
    () => buildPrice(distributionFormModel, distributionSkus),
    { deep: true },
  )
  const initialData = async () => {
    getPlatformCategory()
    getShopCategory()
    getLabelList()
    try {
      await initialSettingsData()
    } finally {
      initialCommdityData()
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
          id: $route.query?.id,
          name: distributionFormModel.name,
          saleDescribe: distributionFormModel.saleDescribe,
          labelId: distributionFormModel.labelId,
        }
        const consignmentPriceSetting: any = {}
        if (distributionFormModel.consignmentPriceSetting.type === 'UNIFY') {
          // 统一设置
          consignmentPriceSetting.type = distributionFormModel.unifyPriceSetting.type
          consignmentPriceSetting.sale = mulTenThousand(distributionFormModel.unifyPriceSetting.sale).toString()
          consignmentPriceSetting.scribe = mulTenThousand(distributionFormModel.unifyPriceSetting.scribe).toString()
        } else {
          consignmentPriceSetting.type = distributionFormModel.consignmentPriceSetting.type
          consignmentPriceSetting.sale = mulTenThousand(distributionFormModel.consignmentPriceSetting.sale).toString()
          consignmentPriceSetting.scribe = mulTenThousand(distributionFormModel.consignmentPriceSetting.scribe).toString()
        }
        data.consignmentPriceSetting = consignmentPriceSetting
        const { code, success, msg } = await doPostUpdateConsignmentCommodityInfo(data)
        if (code === 200 && success) {
          ElMessage.success({ message: msg || '修改商品信息成功' })
          cancelDistribution()
        } else {
          ElMessage.error({ message: msg })
        }
      }
    })
  }
  onMounted(() => initialData())
  return {
    formRef,
    distributionFormModel,
    distributionFormRules,
    platformCategoryList,
    currentComodityInfo,
    shopCascaderProps,
    shopCategoryList,
    LabelList,
    distributionSkus,
    divTenThousand,
    specGroups,
    cancelDistribution,
    handleConfirmDistribution,
  }
}

export default useDistribution

function buildPrice(config: any, skuRefs: Ref<SkuInterface[]>) {
  if (config?.consignmentPriceSetting?.type === 'UNIFY') {
    // 统一处理
    if (config?.unifyPriceSetting?.type === 'RATE') {
      skuRefs.value = skuRefs.value?.map((skuRef) => {
        const actualSalePrice = new Decimal(skuRef.salePrice)
          ?.mul(new Decimal(1).add(new Decimal(config?.unifyPriceSetting?.sale || 0).div(100)))
          .toString()
        return {
          ...skuRef,
          actualSalePrice,
          actualPrice: new Decimal(actualSalePrice)?.mul(new Decimal(1).add(new Decimal(config?.unifyPriceSetting?.scribe || 0).div(100))).toString(),
        }
      })
    } else {
      skuRefs.value = skuRefs.value?.map((skuRef) => {
        const actualSalePrice = new Decimal(skuRef.salePrice)?.add(new Decimal(config?.unifyPriceSetting?.sale || 0)).toString()
        return {
          ...skuRef,
          actualSalePrice,
          actualPrice: new Decimal(actualSalePrice)?.add(new Decimal(config?.unifyPriceSetting?.scribe || 0)).toString(),
        }
      })
    }
  } else if (config?.consignmentPriceSetting?.type === 'RATE') {
    skuRefs.value = skuRefs.value?.map((skuRef) => {
      const actualSalePrice = new Decimal(skuRef.salePrice)
        ?.mul(new Decimal(1).add(new Decimal(config?.consignmentPriceSetting?.sale || 0).div(100)))
        .toString()
      return {
        ...skuRef,
        actualSalePrice,
        actualPrice: new Decimal(actualSalePrice)
          ?.mul(new Decimal(1).add(new Decimal(config?.consignmentPriceSetting?.scribe || 0).div(100)))
          .toString(),
      }
    })
  } else {
    skuRefs.value = skuRefs.value?.map((skuRef) => {
      const actualSalePrice = new Decimal(skuRef.salePrice)?.add(new Decimal(config?.consignmentPriceSetting?.sale || 0)).toString()
      return {
        ...skuRef,
        actualSalePrice,
        actualPrice: new Decimal(actualSalePrice)?.add(new Decimal(config?.consignmentPriceSetting?.scribe || 0)).toString(),
      }
    })
  }
}
/**
 * 检查是分类否可用
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
