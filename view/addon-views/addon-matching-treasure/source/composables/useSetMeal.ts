import { ref, reactive, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { doGetSetMealBasicInfo, doGetSetMealDetailPc } from '../apis'

// 类型定义
export interface CheckBoxItem {
  checkBox: boolean
  skuId: string
  price: number
  original: number
  salePrices: number
  name: string
  type: string
  specs: string
}

export interface SetMealNum {
  setMealId: string
  setMealName: string
}

export interface SetMealProductDetails {
  productId: string
  productName: string
  productPic: string
  productAttributes: string
  setMealProductSkuDetails: any[]
}

export interface TResponseGetSetMealDetail {
  distributionMode: string
  setMealId: string
  setMealName: string
  setMealDescription: string
  setMealType: string
  stackable: boolean
  startTime: string
  endTime: string
  setMealProductDetails: SetMealProductDetails[]
  setMealMainPicture: string
}

/**
 * 套餐数据管理与获取
 */
export default function useSetMeal(shopId: string, productId: string) {
  // 套餐数据状态
  const isLoading = ref(false)
  const setMealNum = ref<SetMealNum[]>([])
  const setMealIds = ref<string[]>([])
  const setMealData = ref<TResponseGetSetMealDetail[]>([])
  const setMealType = ref('')
  const distributionModeVal = ref('EXPRESS')
  const setMealSkus = ref<SetMealProductDetails[]>([])
  const mealId = ref('')
  const mainProduct = ref('')

  // 选中的商品状态
  const finalStatement = ref<any[]>([])

  // 选择套餐的附商品
  const checkBoxList = reactive<CheckBoxItem[][]>([createDefaultCheckBoxArray()])

  // 创建一个默认的CheckBox数组
  function createDefaultCheckBoxArray(): CheckBoxItem[] {
    return Array(5)
      .fill(null)
      .map(() => ({
        checkBox: false,
        skuId: '',
        price: 0,
        original: 0,
        salePrices: 0,
        name: '',
        type: '',
        specs: '',
      }))
  }

  /**
   * 获取套餐基本信息
   */
  const fetchSetMealBasicInfo = async () => {
    try {
      isLoading.value = true
      const { code, data } = await doGetSetMealBasicInfo(shopId, productId)

      if (code === 200 && data) {
        mealId.value = data?.[0]?.setMealId
        setMealNum.value = data

        // 收集所有套餐ID
        setMealIds.value = data.map((meal: SetMealNum) => meal.setMealId).filter(Boolean)

        // 确保checkBoxList长度与套餐数量匹配
        if (setMealNum.value.length > checkBoxList.length) {
          const diff = setMealNum.value.length - checkBoxList.length
          for (let i = 0; i < diff; i++) {
            checkBoxList.push(createDefaultCheckBoxArray())
          }
        }

        return true
      }
      return false
    } catch (error) {
      console.error('获取套餐基本信息失败:', error)
      ElMessage.error('获取套餐基本信息失败')
      return false
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 获取套餐详细信息
   */
  const fetchSetMealDetail = async (
    processSingleSkuProducts: (setMealDetails: any[]) => void,
    setInitialSelectionState: (setMealIndex: number) => void,
  ) => {
    try {
      isLoading.value = true

      // 先获取基本信息
      const basicInfoSuccess = await fetchSetMealBasicInfo()
      if (!basicInfoSuccess) return

      // 遍历所有套餐ID，获取详细信息
      for (const setMealId of setMealIds.value) {
        const { code, data } = await doGetSetMealDetailPc(shopId, setMealId)
        if (code !== 200 || !data) continue

        // 保存套餐数据
        distributionModeVal.value = data.distributionMode
        setMealData.value.push(data)
        setMealType.value = data.setMealType
        setMealSkus.value = data.setMealProductDetails

        // 处理单规格商品
        processSingleSkuProducts(data.setMealProductDetails)

        // 设置默认选中和初始状态
        setInitialSelectionState(setMealData.value.length - 1)
      }
    } catch (error) {
      console.error('获取套餐详细信息失败:', error)
      ElMessage.error('获取套餐详细信息失败')
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 设置初始选中状态
   */
  const setInitialSelectionState = (setMealIndex: number) => {
    if (setMealIndex >= setMealData.value.length) return

    const currentMeal = setMealData.value[setMealIndex]
    const checkBox = checkBoxList[setMealIndex]

    // 设置商品的初始状态
    for (let j = 0; j < currentMeal.setMealProductDetails.length; j++) {
      const productDetail = currentMeal.setMealProductDetails[j]

      // 主商品默认选中
      if (productDetail.productAttributes === 'MAIN_PRODUCT') {
        checkBox[j].checkBox = true
        checkBox[j].type = 'MAIN_PRODUCT'
      }

      // 设置其他基本信息
      if (productDetail.setMealProductSkuDetails.length > 0) {
        const firstSku = productDetail.setMealProductSkuDetails[0]
        checkBox[j].skuId = firstSku.skuId
        checkBox[j].name = productDetail.productName
        checkBox[j].type = productDetail.productAttributes
      }

      // 固定搭配模式下，所有商品都默认选中
      if (currentMeal.setMealType === 'FIXED_COMBINATION') {
        checkBox[j].checkBox = true
      }
    }
  }

  /**
   * 选择商品
   */
  const handleCheckboxSelect = async (
    item: SetMealProductDetails,
    index: number,
    mealType: string,
    mealIndex: number,
    productAttributes: string,
  ): Promise<number> => {
    // 主商品或固定搭配模式下，不可取消选择
    if (productAttributes === 'MAIN_PRODUCT' || mealType === 'FIXED_COMBINATION') {
      return 0
    }

    const currentItem = checkBoxList[mealIndex][index]

    // 未选中 -> 选中
    if (!currentItem.checkBox) {
      if (currentItem.price === 0) {
        ElMessage.error('请先选规格再选商品')
        return 0
      } else {
        currentItem.checkBox = true
        currentItem.name = item.productName
        return 1 // 增加一个商品
      }
    }
    // 已选中 -> 取消选中
    else {
      currentItem.checkBox = false
      return -1 // 减少一个商品
    }
  }

  // 是否有套餐数据
  const hasMealData = computed(() => setMealData.value.length > 0)

  return {
    isLoading,
    setMealNum,
    setMealIds,
    setMealData,
    setMealType,
    distributionModeVal,
    setMealSkus,
    mealId,
    mainProduct,
    hasMealData,
    checkBoxList,
    createDefaultCheckBoxArray,
    fetchSetMealBasicInfo,
    fetchSetMealDetail,
    setInitialSelectionState,
    handleCheckboxSelect,
  }
}
