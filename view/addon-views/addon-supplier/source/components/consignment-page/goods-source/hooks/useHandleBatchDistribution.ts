import { reactive, ref, computed } from 'vue'
import { doGetConsignmentSetting, doGetCategory } from '../../../../apis'
import { CascaderProps, ElMessage, FormRules } from 'element-plus'
import { WritableComputedRef } from 'vue'
import useConvert from '@/composables/useConvert'
import { ListInterface } from '../../types/list'

const { divTenThousand, mulTenThousand } = useConvert()
type ShopCategoryItem = Record<'id' | 'name' | 'parentId' | 'level', string>
interface ShopCategoryList extends ShopCategoryItem {
  disabled?: boolean
  children: ShopCategoryList[]
}
const shopCascaderProps: CascaderProps = {
  expandTrigger: 'hover',
  label: 'name',
  value: 'id',
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

const useHandleBatchDistribution = (computedBatchItems: WritableComputedRef<ListInterface[]>) => {
  const formRefs = ref<{ validate: (callback?: (isValid: boolean) => void) => void }>()
  const formModel = reactive({
    shopCategory: '',
    consignmentPriceSetting: {
      type: 'UNIFY',
      sale: 0,
      scribe: 0,
    },
    unifyPriceSetting: {
      type: '',
      sale: 0,
      scribe: 0,
    },
  })
  const formRules: FormRules = {
    shopCategory: { required: true, message: '请选择商家分类', type: 'array', trigger: 'change' },
    'consignmentPriceSetting.scribe': {
      validator: (_, value, callback: any) => {
        if (formModel.consignmentPriceSetting.type === 'RATE') {
          if (value > 100) {
            callback(new Error('划线价比率不能大于100%'))
          }
          callback()
        }
      },
      trigger: 'blur',
    },
    'consignmentPriceSetting.sale': {
      validator: (_, value, callback: any) => {
        if (formModel.consignmentPriceSetting.type === 'RATE') {
          if (value > 100) {
            callback(new Error('销售价比率不能大于100%'))
          }
          callback()
        }
      },
      trigger: 'blur',
    },
  }
  const computedSalePrice = computed(() => (row: any) => {
    const salePricesGroup = (row?.salePrices || []).map((item: any) => Number(item))
    const minPrice = Math.min(...salePricesGroup)
    const maxPrice = Math.max(...salePricesGroup)
    return minPrice === maxPrice ? maxPrice / 10000 : `${minPrice / 10000}~${maxPrice / 10000}`
  })
  const computedSuplier = computed(() => (storageSkus?: any[]) => {
    let totalNum = 0,
      isLimited = false
    storageSkus?.forEach((item) => {
      totalNum += Number(item.stock)
      if (item.stockType === 'LIMITED') isLimited = true
    })
    return isLimited ? totalNum : '无限库存'
  })
  const shopCategoryList = ref<ShopCategoryList[]>([])
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
  const initialSettingsData = async () => {
    const { data, code } = await doGetConsignmentSetting()
    if (code === 200) {
      if (data) {
        formModel.unifyPriceSetting.sale = divTenThousand(data?.sale).toNumber()
        formModel.unifyPriceSetting.scribe = divTenThousand(data?.scribe).toNumber()
        formModel.unifyPriceSetting.type = data?.type
      }
    }
  }
  const buildData = () => {
    return new Promise((resolve, reject) => {
      const formRef = formRefs.value
      if (formRef) {
        const resolveData: any = {
          shopCategory: { one: formModel.shopCategory?.[0], two: formModel.shopCategory?.[1], three: formModel.shopCategory?.[2] },
          shopProductKeys: computedBatchItems.value.map((item) => ({ shopId: item.shopId, productId: item.id })),
        }
        const consignmentPriceSetting: any = {}
        if (formModel.consignmentPriceSetting.type === 'UNIFY') {
          // 统一设置
          consignmentPriceSetting.type = formModel.unifyPriceSetting.type
          consignmentPriceSetting.sale = mulTenThousand(formModel.unifyPriceSetting.sale).toString()
          consignmentPriceSetting.scribe = mulTenThousand(formModel.unifyPriceSetting.scribe).toString()
        } else {
          consignmentPriceSetting.type = formModel.consignmentPriceSetting.type
          consignmentPriceSetting.sale = mulTenThousand(formModel.consignmentPriceSetting.sale).toString()
          consignmentPriceSetting.scribe = mulTenThousand(formModel.consignmentPriceSetting.scribe).toString()
        }
        resolveData.consignmentPriceSetting = consignmentPriceSetting
        resolve(resolveData)
      } else {
        reject('valid error')
      }
    })
  }
  initialSettingsData()
  getShopCategory()
  return {
    formRefs,
    formModel,
    formRules,
    shopCategoryList,
    shopCascaderProps,
    computedSalePrice,
    computedSuplier,
    buildData,
  }
}

export default useHandleBatchDistribution
