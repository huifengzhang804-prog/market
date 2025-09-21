<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  doGetSetMealBasicInfo,
  doGetSetMealDetailPc,
  doGetGoodSku,
  doGetGoodDetail,
  doGetShopInfo,
  doGetGproductDelivery,
  doPostBudget,
  doGetDefaultAddress,
} from '../apis'
import { SetMealNum, TResponseGetSetMealDetail, SetMealProductDetails, ApiGoodSkuCombination } from '../index'
import { ApiGoodSkus } from '@/views/detail/types/index'
import useConvert from '@/composables/useConvert'
import GoodSpec from '../components/good-spec/good-spec.vue'
import storage from '@/libs/storage'
import Countdown from '../components/countdown.vue'
import QIcon from '@/components/q-icon/q-icon.vue'

// 类型定义
interface CheckBoxItem {
  checkBox: boolean
  skuId: string
  price: number
  original: number
  salePrices: number
  name: string
  type: string
  specs: string
}

// 组件参数和路由
defineProps({
  properties: {
    type: Object,
    default: () => ({}),
  },
})

const router = useRouter()
const route = useRoute()
const { divTenThousand } = useConvert()

// 基础状态
const isLoading = ref(false)
const shopId = computed(() => route.query.shopId as string)
const productId = computed(() => route.query.productId as string)

// 套餐数据
const setMealNum = ref<SetMealNum[]>([])
const setMealIds = ref<string[]>([])
const setMealData = ref<TResponseGetSetMealDetail[]>([])
const setMealType = ref('')
const distributionModeVal = ref('EXPRESS')
const setMealSkus = ref<SetMealProductDetails[]>()
const mealId = ref('')
const mainProduct = ref('')

// 价格和优惠
const sparePrice = ref(0)

// SKU相关状态
const eItem = ref<ApiGoodSkus>()
const onlySkys = ref<Record<string, any>>({})
const completeSku = ref<Record<string, any>>({})
const allAttr = reactive<Record<string, any>>({})

// 选中的商品状态
const totalNum = ref(1)
const finalStatement = ref<any[]>([])

// 选择交互状态
const selectBoxCopyBoolean = ref(false)
const productIds = ref('')
const priceid = ref(-1)
const globalNum = ref(0)
const myTeamKey = ref(0)
const productExtra = ref<any>()
const firstSkuListCopy = ref<ApiGoodSkuCombination>()
const GoodSpecRef = ref()
// 当前选中的商品ID
let currentProductId = ''

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
 * 基础数据获取部分
 */
// 获取套餐基本信息
const fetchSetMealBasicInfo = async () => {
  try {
    isLoading.value = true
    const { code, data } = await doGetSetMealBasicInfo(shopId.value, productId.value)

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

// 查询商品SKU
const fetchGoodSku = async (productId: string) => {
  try {
    const { data, code } = await doGetGoodSku(shopId.value, productId)
    return code === 200 ? data : []
  } catch (error) {
    console.error('获取商品SKU失败:', error)
    return []
  }
}

// 获取套餐详细信息
const fetchSetMealDetail = async () => {
  try {
    isLoading.value = true

    // 先获取基本信息
    const basicInfoSuccess = await fetchSetMealBasicInfo()
    if (!basicInfoSuccess) return

    // 遍历所有套餐ID，获取详细信息
    for (const setMealId of setMealIds.value) {
      const { code, data } = await doGetSetMealDetailPc(shopId.value, setMealId)
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

// 处理单规格商品
const processSingleSkuProducts = (setMealDetails: SetMealProductDetails[]) => {
  const onlySpacs: Record<string, any> = {}

  setMealDetails.forEach((item) => {
    if (item.setMealProductSkuDetails.length === 1) {
      const onlySpac = item.setMealProductSkuDetails[0]
      const obj = {
        ...onlySpac.storageSku,
        salePrice: onlySpac.matchingPrice,
        stock: onlySpac.matchingStock,
        id: onlySpac.skuId,
        specs: onlySpac.skuName,
        stockType: onlySpac.stockType,
        saveAtLeast: onlySpac.saveAtLeast,
        productName: item.productName,
        productAttributes: item.productAttributes,
      }

      // 主商品或固定搭配模式下，保存到completeSku
      if (item.productAttributes === 'MAIN_PRODUCT' || setMealData.value[0]?.setMealType === 'FIXED_COMBINATION') {
        saveCoppleteSku(item.productId, obj)
      }

      onlySpacs[item.productId] = obj
    }
  })

  saveOnlySkus(onlySpacs)
}

// 设置初始选中状态
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

// 初始化调用
onMounted(() => {
  fetchSetMealDetail()
})

// 处理属性参数回显需要数据
const handleParams = (currentSpecs: any) => {
  if (Array.isArray(currentSpecs)) {
    const allfeatureValues = currentSpecs.flatMap((item: any) => item.featureValues) || []
    return allfeatureValues.map((item: any) => {
      const propertyPrice = divTenThousand(item.secondValue)
      return `${item.firstValue}${propertyPrice.lte(0) ? '' : '+'}${propertyPrice}元`
    })
  }
  return []
}

/**
 * 商品选择和交互部分
 */
// 选择商品
const handleCheckboxSelect = async (item: SetMealProductDetails, index: number, mealType: string, mealIndex: number, productAttributes: string) => {
  // 主商品或固定搭配模式下，不可取消选择
  if (productAttributes === 'MAIN_PRODUCT' || mealType === 'FIXED_COMBINATION') {
    return
  }

  const currentItem = checkBoxList[mealIndex][index]

  // 未选中 -> 选中
  if (!currentItem.checkBox) {
    if (currentItem.price === 0) {
      ElMessage.error('请先选规格再选商品')
    } else {
      currentItem.checkBox = true
      totalNum.value += 1
      currentItem.name = item.productName
    }
  }
  // 已选中 -> 取消选中
  else {
    currentItem.checkBox = false
    totalNum.value -= 1
  }
}

// 打开规格选择
const openSpecificationSelector = async (index: number, item: SetMealProductDetails, mealIndex: number, mealData: any, specs: any) => {
  const { productId, productName, productAttributes, setMealProductSkuDetails } = item

  // 保存当前选择的商品ID
  currentProductId = productId
  productIds.value = productId
  globalNum.value = mealIndex
  priceid.value = index

  // 首先查找当前项目中已保存的规格信息
  const currentCheckBoxItem = checkBoxList[mealIndex][index]
  const savedSpecString = currentCheckBoxItem.specs || ''

  // 构建有效的SKU数组
  const skuArr: any[] = []
  setMealProductSkuDetails.forEach((val) => {
    // 忽略库存为0的SKU
    if (val.matchingStock === 0) return

    // 获取SKU规格数组，避免类型错误
    let skuSpecs: string[] = []

    // 安全地处理skuName
    const skuName = val.skuName as unknown
    if (skuName) {
      if (typeof skuName === 'string') {
        // 字符串类型，进行拆分
        skuSpecs = skuName.split(',')
      } else if (Array.isArray(skuName)) {
        // 数组类型，直接使用
        skuSpecs = skuName as string[]
      }
    }

    skuArr.push({
      ...val.storageSku,
      salePrice: val.matchingPrice,
      stock: val.matchingStock,
      id: val.skuId,
      specs: skuSpecs,
      stockType: val.stockType,
      productName,
      productAttributes,
    })
  })

  if (skuArr.length === 0) {
    ElMessage.warning('该商品暂无库存')
    return
  }

  try {
    // 获取商品详情和多选配置
    const res = await doGetGoodDetail(productId, shopId.value)

    // 处理多选配置
    let productAttributesData = res.data?.extra?.productAttributes || []

    // 如果存在属性数据
    if (productAttributesData.length > 0) {
      console.log('原始属性数据:', JSON.stringify(productAttributesData))

      // 获取已保存的属性数据
      const savedAttrs = allAttr[productId] || []
      console.log('已保存的属性数据:', JSON.stringify(savedAttrs))

      // 确保所有属性都可见，深复制以避免修改原始数据
      productAttributesData = JSON.parse(JSON.stringify(productAttributesData))

      // 更新属性值，确保选择状态被正确保存和恢复
      productAttributesData = productAttributesData.map((attr: any) => {
        // 寻找已保存的属性
        const savedAttr = savedAttrs.find((sa: any) => sa.id === attr.id)

        // 如果找到了匹配的已保存属性
        if (savedAttr && savedAttr.featureValues && savedAttr.featureValues.length > 0) {
          // 保留所有可选项，但标记已选中的
          attr.featureValues = attr.featureValues.map((fv: any) => {
            // 检查该值是否在已保存的选项中
            const isSelected = savedAttr.featureValues.some((sv: any) => sv.featureValueId === fv.featureValueId)
            // 如果是，添加一个标记字段
            if (isSelected) {
              fv._selected = true
            }
            return fv
          })
        }
        // 如果是必选项且没有保存过的选择
        else if (attr.isRequired && attr.featureValues && attr.featureValues.length > 0) {
          // 标记第一个选项为选中
          attr.featureValues[0]._selected = true
        }

        return attr
      })

      // 创建更新后的extra对象
      const updatedExtra = { ...res.data.extra, productAttributes: productAttributesData }
      productExtra.value = updatedExtra
      console.log('更新后的属性数据:', JSON.stringify(productAttributesData))
    } else {
      productExtra.value = res.data.extra
    }

    // 获取SKU分组信息
    const data = await fetchGoodSku(productId)
    const { specGroups } = data as any

    // 准备规格选择器数据
    firstSkuListCopy.value = {
      specGroups,
      skus: skuArr.map((item, idx) => ({
        ...item,
        salePrices: res.data.salePrices[idx],
      })),
    }

    // 确定要使用的规格数组 - 优先级顺序
    let selectedSpecs: string[] = []

    // 1. 首先检查completeSku中是否存在该商品的已保存规格
    if (completeSku.value[productId]?.specs) {
      const savedSpecs = completeSku.value[productId].specs
      selectedSpecs = Array.isArray(savedSpecs) ? savedSpecs : typeof savedSpecs === 'string' ? savedSpecs.split(',') : []
    }
    // 2. 如果completeSku中没有，检查checkBoxList中是否有规格信息
    else if (savedSpecString && savedSpecString !== '请选择规格') {
      selectedSpecs = savedSpecString.split(',')
    }

    // 3. 如果仍然没有找到规格，但是SKU数组中有匹配当前规格的项
    if (selectedSpecs.length === 0 && savedSpecString) {
      // 尝试基于规格字符串查找匹配的SKU
      const matchingSku = skuArr.find((sku) => Array.isArray(sku.specs) && sku.specs.join(',') === savedSpecString)
      if (matchingSku) {
        selectedSpecs = matchingSku.specs
      }
    }

    // 4. 如果还没有找到规格，使用第一个SKU的规格作为默认值
    if (selectedSpecs.length === 0 && skuArr.length > 0) {
      selectedSpecs = skuArr[0].specs || []
    }

    // 确定要使用的SKU
    let selectedSku = skuArr[0]
    if (selectedSpecs.length > 0) {
      // 根据已选规格查找匹配的SKU
      const matchingSku = skuArr.find(
        (sku) =>
          Array.isArray(sku.specs) && sku.specs.length === selectedSpecs.length && sku.specs.every((s: string, i: number) => s === selectedSpecs[i]),
      )
      if (matchingSku) {
        selectedSku = matchingSku
      }
    }

    // 设置选中的SKU到eItem
    eItem.value = {
      ...selectedSku,
      salePrices: res.data.salePrices[skuArr.indexOf(selectedSku)],
      specs: selectedSpecs,
    }

    // 更新UI状态
    currentCheckBoxItem.name = productName
    if (selectedSpecs.length > 0) {
      currentCheckBoxItem.specs = selectedSpecs.join(',')
    }

    // 切换规格选择器显示状态
    if (priceid.value !== index) {
      selectBoxCopyBoolean.value = true
    } else {
      selectBoxCopyBoolean.value = !selectBoxCopyBoolean.value
    }

    // 更新组件key，强制重新渲染规格选择器
    myTeamKey.value = Date.now()

    // 打印信息便于调试
    console.log('Opening spec selector for', productName, 'with selected specs:', selectedSpecs)
  } catch (error) {
    console.error('获取商品规格信息失败:', error)
    ElMessage.error('获取商品规格信息失败')
  }
}

// 处理规格选择返回结果
const handleChooseSpec = (selectedSkus: ApiGoodSkus[]) => {
  if (!selectedSkus.length) return

  eItem.value = selectedSkus[0]
  finalStatement.value[priceid.value] = selectedSkus[0]

  // 更新选择框中的SKU ID
  if (globalNum.value >= 0 && priceid.value >= 0) {
    checkBoxList[globalNum.value][priceid.value].skuId = selectedSkus[0]?.id || ''
  }
}

// 确认关闭规格选择
const closeSpecificationSelector = (isVisible: boolean) => {
  selectBoxCopyBoolean.value = isVisible
}

// 处理GoodSpec组件初始化完成
const handleInitComplete = (component: any) => {
  if (!component || !productExtra.value || !productExtra.value.productAttributes) return

  console.log('GoodSpec初始化完成')

  // 获取预设的已选中状态
  const productAttributesData = productExtra.value.productAttributes
  if (!productAttributesData.length) return

  // 检查是否有需要初始选中的属性
  let hasPreselection = false

  // 对每个属性进行检查
  productAttributesData.forEach((attr: any) => {
    if (!attr.featureValues) return

    // 寻找被标记为选中的值
    const selectedValues = attr.featureValues.filter((fv: any) => fv._selected)
    if (selectedValues.length > 0) {
      hasPreselection = true

      // 如果有已保存的值，手动触发选择
      selectedValues.forEach((value: any) => {
        // 复制这个值，避免直接修改原始数据
        const valueToSelect = { ...value }

        // 手动调用组件的选中方法
        if (component.handleCheckbox && attr.id) {
          // 确保组件中有对应的规格对象
          if (!component.specsObj.value[attr.id]) {
            component.specsObj.value[attr.id] = []
          }

          // 检查是否已经选中
          const isAlreadySelected = component.specsObj.value[attr.id].some((sv: any) => sv.featureValueId === valueToSelect.featureValueId)

          // 如果未选中，则手动选中
          if (!isAlreadySelected) {
            // 模拟选中操作
            component.handleCheckbox(true, valueToSelect, attr.id, attr.isMultiSelect)
          }
        }
      })
    }
  })

  if (hasPreselection) {
    console.log('已预设属性选中状态')
  }
}

// 确认规格选择
const confirmSpecSelection = () => {
  if (!GoodSpecRef.value || !GoodSpecRef.value[0]) {
    return ElMessage.error('规格选择器未初始化')
  }

  const attributes = GoodSpecRef.value[0].productAttributes || []

  // 验证必选属性是否已选择
  if (attributes.length) {
    let errObj: any
    const allRequired = attributes.some((item: { isRequired: boolean; featureValues: any[] }) => {
      if (!item.isRequired) return true
      if (item.isRequired && item.featureValues?.length) return true
      errObj = item
      return false
    })

    if (!allRequired) {
      return ElMessage.error(`${errObj.featureName}为必选项`)
    }
  }

  // 构建SKU对象
  const sku: Record<string, any> = {
    [productIds.value]: eItem.value,
  }

  // 计算价格信息
  const salePrice = eItem.value ? +eItem.value.salePrice : 0
  const priceFromGet = eItem.value ? +getPrice(eItem.value.id) : 0
  const finalPrice = salePrice || priceFromGet
  const original = eItem.value ? Number(eItem.value.attributePirce) : 0
  const salePrices = eItem.value ? Number(eItem.value.salePrices) : 0

  // 处理规格描述
  const allfeatureValues = attributes.flatMap((item: any) => item.featureValues) || []
  const values = allfeatureValues.map((item: any) => item.firstValue + item.secondValue / 10000 + '元')

  // 确保所有选择的属性都被保存
  if (attributes.length > 0) {
    // 如果productId还没有在allAttr中，创建一个新条目
    if (!allAttr[productIds.value]) {
      allAttr[productIds.value] = []
    }

    // 检查每个属性并更新
    attributes.forEach((attr: any) => {
      // 寻找已有的属性
      const existingAttrIndex = allAttr[productIds.value].findIndex((a: any) => a.id === attr.id)

      // 如果属性已存在，更新featureValues
      if (existingAttrIndex >= 0) {
        // 确保保存完整的featureValues对象（包含featureValueId、firstValue和secondValue）
        allAttr[productIds.value][existingAttrIndex].featureValues = attr.featureValues ? JSON.parse(JSON.stringify(attr.featureValues)) : []
      } else {
        // 如果属性不存在，添加新属性
        allAttr[productIds.value].push({
          id: attr.id,
          featureName: attr.featureName,
          isRequired: attr.isRequired,
          isMultiSelect: attr.isMultiSelect,
          featureValues: attr.featureValues ? JSON.parse(JSON.stringify(attr.featureValues)) : [],
        })
      }
    })
  }

  let specsDescribeValue = ''

  if (sku[productIds.value]?.specs?.length) {
    specsDescribeValue = [...sku[productIds.value].specs, ...values].join(',')
  } else if (values.length) {
    specsDescribeValue = [...values].join(',')
  } else if (mainProduct.value === productIds.value && (values.length === 0 || !sku)) {
    setMealSkus.value?.forEach((item: any) => {
      if (item.productId === productIds.value) {
        item.flag = true
      }
    })
  }

  // 批量更新选中状态
  const currentItem = checkBoxList[globalNum.value][priceid.value]
  const specsFinal = specsDescribeValue === '' ? '默认' : specsDescribeValue

  Object.assign(currentItem, {
    price: finalPrice,
    original: original,
    salePrices: salePrices,
    specs: specsFinal,
    skuId: eItem.value?.id || '', // 确保skuId也被更新
  })

  // 异步保存SKU和属性信息，确保UI更新后再执行
  nextTick(() => {
    // 先保存SKU到onlySkys
    saveOnlySkus(sku)

    // 直接保存属性信息到completeSku
    if (sku[productIds.value]) {
      // 确保specs是一个数组
      const specs = Array.isArray(sku[productIds.value].specs)
        ? sku[productIds.value].specs
        : typeof sku[productIds.value].specs === 'string'
        ? sku[productIds.value].specs.split(',')
        : []

      // 获取保存在allAttr中的属性信息
      const storedAttrs = allAttr[productIds.value] || []

      // 更新SKU对象，确保包含最新的规格和属性信息
      const updatedSku = {
        ...sku[productIds.value],
        specs,
        productFeatures: storedAttrs.length > 0 ? JSON.parse(JSON.stringify(storedAttrs)) : [],
        attributeSelections: storedAttrs.reduce((acc: any, attr: any) => {
          // 为每个属性的选择都保存featureValueId
          attr.featureValues.forEach((val: any) => {
            if (val && val.featureValueId) {
              if (!acc[attr.id]) acc[attr.id] = []
              acc[attr.id].push(val.featureValueId)
            }
          })
          return acc
        }, {}),
      }

      // 保存到completeSku
      saveCoppleteSku(productIds.value, updatedSku)
    }

    // 更新属性价格
    updateAttrPricesFromFeatureValues(productIds.value)

    // 更新后关闭选择器
    closeSpecificationSelector(false)

    // 强制更新UI
    myTeamKey.value = Date.now()

    // 调试日志
    console.log('Saved attributes:', allAttr[productIds.value])
    console.log('Saved SKU:', completeSku.value[productIds.value])
  })
}

// 从featureValues中计算并更新属性价格
const updateAttrPricesFromFeatureValues = (productId: string) => {
  if (!allAttr[productId] || !completeSku.value[productId]) return

  // 计算属性价格
  let attrPrice = 0

  // 遍历所有属性
  allAttr[productId].forEach((attr: any) => {
    // 遍历每个属性的选中值
    attr.featureValues?.forEach((value: any) => {
      if (value && typeof value.secondValue !== 'undefined') {
        // 累加价格
        attrPrice += Number(value.secondValue || 0)
      }
    })
  })

  // 更新到SKU中
  if (completeSku.value[productId]) {
    completeSku.value[productId].attributePirce = String(attrPrice)
  }
}

// 计算 选中的总价格
const getTotalPrice = (checkBoxList: any) => {
  let totalPrice = 0
  checkBoxList.forEach((el: any) => {
    if (el.checkBox) {
      totalPrice += Math.max(0, el.price)
    }
  })
  return divTenThousand(totalPrice)
}

// 计算 选中的优惠价格
const getDiscountedPrices = (checkBoxList: any) => {
  let discountedPrice = 0
  checkBoxList.forEach((el: any) => {
    if (el.checkBox) {
      discountedPrice += el.original + el.salePrices - el.price
    }
  })
  return divTenThousand(discountedPrice)
}

/**
 * 购买相关功能
 */
// 验证购买条件
const validatePurchase = (item: TResponseGetSetMealDetail, index: number): boolean => {
  // 1. 检查选中规格的活动库存matchingStock是否为0
  if (eItem.value && Number(eItem.value.stock) === 0) {
    ElMessage.error('该规格商品库存不足')
    return false
  }

  const currentMeal = setMealData.value[index]
  const mealCheckBoxList = checkBoxList[index]

  // 输出调试信息
  console.log('开始验证购买条件:', {
    setMealType: item.setMealType,
    checkBoxList: mealCheckBoxList,
    productDetails: currentMeal.setMealProductDetails.map((p) => ({
      name: p.productName,
      type: p.productAttributes,
      skuCount: p.setMealProductSkuDetails.length,
    })),
  })

  // 3. setMealType为固定套餐时,验证主商品+所有搭配商品已选
  if (item.setMealType === 'FIXED_COMBINATION') {
    // 检查所有商品都已选择规格
    const productsWithPrice = mealCheckBoxList.filter((item) => item.price !== 0).length
    if (item.setMealProductDetails.length !== productsWithPrice) {
      // 找出哪些商品没有选择规格
      const missingProducts = []
      for (let i = 0; i < item.setMealProductDetails.length; i++) {
        if (!mealCheckBoxList[i] || mealCheckBoxList[i].price === 0) {
          missingProducts.push(item.setMealProductDetails[i].productName)
        }
      }
      ElMessage.error(`固定套餐模式下需要选择所有商品的规格，还缺少: ${missingProducts.join(', ')}`)
      return false
    }
    return true
  }

  // 2. setMealType为自选商品套餐时,验证主商品+至少一种搭配商品已选
  // 首先验证主商品是否已选择
  const mainProducts = currentMeal.setMealProductDetails.filter((product) => product.productAttributes === 'MAIN_PRODUCT')

  // 检查主商品是否已选择规格
  for (const mainProduct of mainProducts) {
    const selectedMain = mealCheckBoxList.find((v) => v.name === mainProduct.productName && v.price > 0 && v.checkBox === true)

    if (!selectedMain) {
      ElMessage.error(`请选择主商品"${mainProduct.productName}"的规格`)
      return false
    }
  }

  // 检查搭配商品是否至少选择一个
  // 搭配商品是非主商品
  const additionalProducts = currentMeal.setMealProductDetails.filter((product) => product.productAttributes !== 'MAIN_PRODUCT')

  console.log('搭配商品验证:', {
    additionalProductsCount: additionalProducts.length,
    selectedCheckBoxes: mealCheckBoxList.filter((v) => v.checkBox === true && v.type !== 'MAIN_PRODUCT').map((v) => v.name),
    itemsWithPrice: mealCheckBoxList.filter((v) => v.price > 0 && v.type !== 'MAIN_PRODUCT').map((v) => v.name + ' (勾选状态:' + v.checkBox + ')'),
  })

  // 只有当套餐中有搭配商品时，才要求至少选择一个
  if (additionalProducts.length > 0) {
    // 已选中的搭配商品（必须确保同时满足：非主商品、已勾选、有价格）
    const selectedAdditional = mealCheckBoxList.filter((v) => v.type !== 'MAIN_PRODUCT' && v.checkBox === true && v.price > 0)

    if (selectedAdditional.length === 0) {
      ElMessage.error('请至少选择一种搭配商品')
      return false
    }
  }

  return true
}

// 立即购买
const handleBuy = (item: TResponseGetSetMealDetail, index: number) => {
  // 验证购买条件
  if (!validatePurchase(item, index)) {
    return
  }

  // 调用购买处理
  handleBuyNow(checkBoxList[index], item)
}

// 立即购买处理
const handleBuyNow = async (list?: CheckBoxItem[], currentItem?: TResponseGetSetMealDetail) => {
  try {
    isLoading.value = true

    // 获取店铺信息
    const { code, data, msg } = (await doGetShopInfo(shopId.value)) as any
    if (code !== 200 || !data) {
      return ElMessage.error(msg || '获取店铺基本信息失败')
    }

    // 过滤completeSku，只保留选中的商品
    let completeSkus: Record<string, any> = {}

    // 使用传入的currentItem.setMealProductDetails获取所有可能的productId
    const availableProductIds: string[] = []
    if (currentItem?.setMealProductDetails) {
      currentItem.setMealProductDetails.forEach((detail: any) => {
        availableProductIds.push(detail.productId)
      })
    }

    // 从传入的list中获取已选中的skuId
    const selectedSkuIds: string[] = []
    if (list && Array.isArray(list)) {
      list.forEach((item) => {
        if (item.checkBox && item.skuId) {
          selectedSkuIds.push(item.skuId)
        }
      })
    }

    // 过滤商品SKU
    if (availableProductIds.length > 0) {
      // 根据可用商品ID和已选中SKU过滤
      for (const key in completeSku.value) {
        if (availableProductIds.includes(key)) {
          if (selectedSkuIds.length === 0 || selectedSkuIds.includes(completeSku.value[key].id)) {
            completeSkus[key] = completeSku.value[key]
          }
        }
      }
    } else if (selectedSkuIds.length > 0) {
      // 只根据选中的SKU过滤
      for (const key in completeSku.value) {
        if (selectedSkuIds.includes(completeSku.value[key].id)) {
          completeSkus[key] = completeSku.value[key]
        }
      }
    } else {
      // 使用checkBoxList中选中的项
      for (const checkboxGroup of checkBoxList) {
        for (const item of checkboxGroup) {
          if (item.checkBox && item.skuId) {
            for (const key in completeSku.value) {
              if (completeSku.value[key].id === item.skuId) {
                completeSkus[key] = completeSku.value[key]
              }
            }
          }
        }
      }
    }

    // 检查是否有选中商品
    if (Object.keys(completeSkus).length === 0) {
      return ElMessage.error('请选择至少一个商品')
    }

    // 获取配送信息
    const deliveryResult = await handleDeliveryData(completeSkus)
    if (!deliveryResult) return
    const { deliveryData } = deliveryResult as { mainProduict: any; deliveryData: any[] }

    // 构建商品和套餐数据
    const products = []
    const setMealProducts = []

    for (const key in completeSkus) {
      const gproduct = deliveryData.find((item: { productId: string }) => item.productId === key)
      const { image, price, salePrice, productName, id, productAttributes, skuStock, specs } = completeSkus[key]
      // 构建商品对象
      const productsObj = {
        skuId: id,
        image: image || setMealData.value[0].setMealProductDetails.find((item: any) => item.productId === key)?.productPic,
        price,
        id: key,
        salePrice,
        productId: key,
        distributionMode: setMealData.value[0].distributionMode,
        productName,
        num: 1,
        freightTemplateId: gproduct?.freightTemplateId,
        weight: gproduct?.weight ?? '0',
        productFeaturesValue: allAttr[key] || null,
        specs: [...specs, ...handleParams(allAttr[key] || [])],
      }

      // 构建套餐商品对象
      const setMealProductsObj = {
        activityId: mealId.value,
        productAttributes,
        shopId: shopId.value,
        skuId: id,
        skuStock,
        productId: key,
      }

      products.push(productsObj)
      setMealProducts.push(setMealProductsObj)
    }

    // 构建请求参数
    const param = [
      {
        ...data,
        shopLogo: data.logo,
        shopName: data.name,
        groupBuying: true,
        shopId: data.id,
        products,
        orderType: 'PACKAGE',
        distributionMode: setMealData.value[0].distributionMode,
        activityParam: {
          type: 'PACKAGE',
          activityId: mealId.value,
          extra: {
            setMealProducts,
          },
          stackable: setMealData.value[0]?.stackable,
        },
      },
    ]

    // 获取默认收货地址
    let receiver = {
      name: '',
      mobile: '',
      area: [],
      address: '',
      location: {
        type: 'Point',
        coordinates: [121.489551, 29.936806],
      },
      isDefault: false,
    }

    const address = await doGetDefaultAddress()
    if (address.code === 200 && address.data?.area?.length) {
      receiver = address.data
    } else {
      ElMessage.warning('请添加收货地址')
    }

    // 获取价格预算
    const mandatory = await handleBudget(param[0], receiver)
    if (mandatory.code === 20025) {
      return ElMessage.error(mandatory.msg)
    }

    // 保存商品信息并跳转到结算页
    new storage().setItem('commodityInfo', param, 60 * 60 * 2)
    router.push({
      path: '/settlement',
      query: { source: 'PRODUCT' },
    })
  } catch (error) {
    console.error('处理购买请求失败:', error)
    ElMessage.error('处理购买请求失败')
  } finally {
    isLoading.value = false
  }
}

// 获取价格预算判断必选是否选中
async function handleBudget(submitForm: any, receiver: any) {
  let remarkObj = ref<{ [key: string]: string }>({})
  const remark = ref<any>([])
  const shopPackages = [submitForm].map((shopProducs) => {
    const { shopId, shopName, shopLogo, products } = shopProducs
    for (let i = 0; i < remark.value.length; i++) {
      if (Object.values(remark.value[i])?.[0] === '') continue
      remarkObj.value = { ...remarkObj.value, ...remark.value[i] }
    }
    return {
      id: shopId,
      name: shopName,
      logo: shopLogo,
      remark: remarkObj.value || {},
      products: products.map(({ id, skuId, num, productFeaturesValue }: any) => ({ id, skuId, num, productFeaturesValue })),
    }
  })
  shopPackages.forEach((shopPackages) => {
    shopPackages.products.forEach((product: any) => {
      const valueList = product.productFeaturesValue
      const valueMap: any = {}
      valueList?.forEach((value: any) => {
        valueMap[value.id] = value?.featureValues?.map((item: any) => item.featureValueId)
      })
      product.productFeaturesValue = valueMap
    })
  })
  const params = {
    receiver: receiver,
    shopPackages: shopPackages,
    source: 'PRODUCT',
    orderType: 'PACKAGE',
    activity: {
      activityId: submitForm.activityParam.activityId,
      extra: submitForm.activityParam.extra,
    },
    discounts: {},
    extra: {
      distributionMode: setMealData.value[0].distributionMode,
      shopStoreId: '',
      packUpTime: '',
    },
    distributionMode: setMealData.value[0].distributionMode,
  }
  const res = await doPostBudget({ ...params })
  return res
}

// 通过所有商品sku筛选出需要数据
const handleDeliveryData = async (completeSku: any) => {
  const deliveryParams = Object.keys(completeSku).map((item) => {
    return {
      shopId: route.query.shopId,
      productId: item,
      skuId: completeSku[item].id,
    }
  })
  const res = await doGetGproductDelivery(deliveryParams)
  if (res.code !== 200 || !res.data) return ElMessage.error(`${res.msg || '获取店铺基本信息失败'}`)
  const deliveryData = res.data as any[]
  const mainProduict: any | undefined = deliveryData.find((item) => item.productId === route.query.productId)
  return { mainProduict, deliveryData }
}

// 选择规格获取价格
const getPrice = (id: string) => {
  let price = '' // 普通变量，非响应式
  setMealData.value.forEach((ele) => {
    for (let i = 0; i < ele.setMealProductDetails.length; i++) {
      ele.setMealProductDetails[i].setMealProductSkuDetails.forEach((element) => {
        if (element.skuId === id) price = element.matchingPrice
      })
    }
  })
  return price
}

/**
 * 计算属性
 */
// 是否有套餐数据
const hasMealData = computed(() => setMealData.value.length > 0)

// 保存选择好的SKU到onlySkys
const saveOnlySkus = (sku: any) => {
  onlySkys.value = { ...onlySkys.value, ...sku }
}

// 保存商品属性并更新completeSku
const saveOnlyAttr = async (attr: { key: string; value: any['productAttributes'] }) => {
  mainProduct.value = currentProductId
  const { key, value } = attr
  allAttr[key] = value
  saveCoppleteSku(currentProductId, onlySkys.value[currentProductId])
}

// 保存选择好的SKU到completeSku
const saveCoppleteSku = (key: string, sku: any, isMore = true) => {
  if (!isMore) {
    Reflect.deleteProperty(completeSku.value, key)
  } else {
    completeSku.value[key] = sku
  }

  // 计算节省价格
  updateSavingPrice()
}

// 更新节省价格
const updateSavingPrice = () => {
  let countSpare = 0
  for (const key in completeSku.value) {
    const { salePrices, attributePirce, salePrice } = completeSku.value[key]
    countSpare += Number(salePrices) + Number(attributePirce) - Number(salePrice)
  }
  sparePrice.value = countSpare
}
</script>
<template>
  <div v-if="hasMealData" class="con">
    <div v-if="isLoading" class="loader-container">
      <div class="loader"></div>
    </div>

    <div v-for="(item, index) in setMealData" :key="index" class="content">
      <!-- 套餐商品列表 -->
      <div class="content__products" style="display: flex; max-width: 1020px; flex-wrap: wrap">
        <!-- 加号图标 -->
        <div class="content__plusSign content__plusSign--left" style="order: 1">
          <QIcon name="icon-tianjia" style="width: 26px; height: 26px; font-weight: bold; margin-right: 20px" size="25px" color="#666" />
        </div>

        <!-- 商品卡片列表 -->
        <div
          v-for="(product, productIndex) in item.setMealProductDetails"
          :key="productIndex"
          style="display: flex; justify-content: space-evenly"
          :class="[product?.productAttributes !== 'MATCHING_PRODUCTS' ? 'one' : 'else']"
        >
          <!-- 商品卡片 -->
          <div
            :class="
              item.setMealType === 'FIXED_COMBINATION' || checkBoxList[index]?.[productIndex].checkBox
                ? 'content__box content__center boxBorder'
                : 'content__box content__center'
            "
          >
            <!-- 商品图片 -->
            <div class="content__box--img" @click="handleCheckboxSelect(product, productIndex, item.setMealType, index, product.productAttributes)">
              <img :src="product?.productPic" alt="商品图片" />
            </div>

            <!-- 商品名称 -->
            <div
              class="content__box--tilText"
              @click="handleCheckboxSelect(product, productIndex, item.setMealType, index, product.productAttributes)"
            >
              {{ product?.productName }}
            </div>

            <!-- 商品价格 -->
            <div
              v-if="checkBoxList?.[index]?.[productIndex]?.price"
              class="content__box--price colorRed textFw"
              @click="handleCheckboxSelect(product, productIndex, item.setMealType, index, product.productAttributes)"
            >
              ￥{{ divTenThousand(checkBoxList?.[index]?.[productIndex]?.price).toFixed(2) }}
            </div>

            <!-- 规格选择 -->
            <div class="content__box--select">
              <div
                class="content__box--sel"
                @click="openSpecificationSelector(productIndex, product, index, item, checkBoxList?.[index]?.[productIndex]?.specs)"
              >
                <p>{{ checkBoxList?.[index]?.[productIndex]?.specs || '请选择规格' }}</p>
                <el-icon><ArrowDownBold /></el-icon>
              </div>

              <!-- 规格选择弹窗 -->
              <div v-if="selectBoxCopyBoolean && priceid === productIndex && globalNum === index" class="content__box--specification">
                <GoodSpec
                  ref="GoodSpecRef"
                  :key="myTeamKey"
                  :sku-group="firstSkuListCopy"
                  :current-choose="Array.isArray(eItem?.specs) ? eItem.specs : []"
                  :params="productExtra"
                  :img-boolean="false"
                  @choose-spec="handleChooseSpec"
                  @init-complete="handleInitComplete"
                />
                <div class="confirmBtn">
                  <el-button @click="confirmSpecSelection">确定</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 等号 -->
      <div class="content__plusSign">
        <span class="content__equals" style="font-weight: 400; font-size: 56px; color: #c4c4c4">=</span>
      </div>

      <!-- 套餐信息和购买区 -->
      <div class="content__rightBox">
        <!-- 套餐信息 -->
        <div class="content__rightBox--info">
          <p class="content__selected-count">
            已选择
            <span class="colorRed">{{ item.setMealType === 'FIXED_COMBINATION' ? item.setMealProductDetails.length : totalNum }}</span>
            件商品
          </p>

          <p class="content__rightBox--price">
            优惠金额：<span class="colorRed">￥{{ divTenThousand(sparePrice) }}</span>
          </p>

          <p class="content__total-price">
            套餐价：<span class="colorRed textFw" style="font-size: 18px">￥{{ getTotalPrice(checkBoxList[index]) }}</span>
          </p>
        </div>

        <!-- 购买按钮 -->
        <div class="content__rightBox--btn" @click="handleBuy(item, index)">立即购买</div>

        <!-- 倒计时 -->
        <div class="content__rightBox--over">
          <div class="content__countdown"><text>距离结束：</text><Countdown :startTime="item.endTime" /></div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
$setMealColor: #ff0000; // 套餐模块颜色
$setMealBuyColor: #e31436; // 立即购买

img {
  width: 100%;
  height: 100%;
}
@include b(colorRed) {
  color: $setMealColor;
}
@include b(textFw) {
  font-weight: bold;
}
@include b(boxBorder) {
  border: 2px solid $setMealColor !important;
}

// 加载状态
.loader-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.7);
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
}

.loader {
  border: 5px solid #c7c7c7;
  border-radius: 50%;
  border-top: 5px solid $setMealColor;
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@include b(con) {
  width: 1190px;
  margin: 0 auto;
}

@include b(content) {
  position: relative;
  display: flex;
  padding: 12px 6px;
  @include e(box) {
    position: relative;
    width: 150px;
    height: 220px;
    border-radius: 4px;
    border: 2px solid #c7c7c7;
    @include m(img) {
      text-align: center;
      padding: 12px 0 8px 0;
      img {
        width: 100px;
        height: 100px;
      }
    }
    @include m(tilText) {
      padding: 0 10px;
      margin-bottom: 3px;
      text-align: left;
      word-break: break-all;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
    @include m(price) {
      padding: 0 10px;
      font-size: 16px;
      text-align: left;
    }
    @include m(select) {
      position: relative;
      text-align: left;
      margin: 3px 10px;
      cursor: pointer;
      border: 1px solid #c7c7c7;
      border-radius: 3px;
    }
    @include m(sel) {
      line-height: 24px;
      margin: 1px 10px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      display: flex;
      justify-content: space-between;
      align-items: center;
      p {
        max-width: 90px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
    }
    // 规格
    @include m(specification) {
      position: absolute;
      top: 35px;
      left: 0px;
      max-width: 550px;
      background-color: #fff;
      z-index: 99;
      box-shadow: 2px 2px 5px 1px #999;
    }
    @include m(til) {
      margin: 10px 10px;
    }
    @include m(body) {
      padding: 2px 6px;
      margin: 5px;
      display: inline-block;
      border: 2px solid transparent;
      background-color: #e6e6e6;
      cursor: pointer;
    }
  }
  @include e(plusSign) {
    text-align: center;
    line-height: 40px;
    margin: 90px 0 0 31px;
  }
  @include e(center) {
  }
  @include e(rightBox) {
    position: absolute;
    right: 0;
    display: flex;
    justify-content: center;
    flex-direction: column;
    align-items: end;
    height: 218px;
    font-size: 14px;
    margin: 0 25px 0 0;
    @include m(price) {
      margin: 10px 0;
    }
    @include m(btn) {
      width: 104px;
      height: 32px;
      line-height: 32px;
      color: #fff;
      background-color: $setMealBuyColor;
      margin: 10px 0;
      cursor: pointer;
    }
    @include m(over) {
      margin-right: 20px;
      text-align: right;
      font-size: 12px;
      color: #999;
      display: flex;
      align-items: center;
      margin: 0 auto;
    }
  }
}

.confirmBtn {
  width: 100%;
  text-align: right;
  padding: 10px 30px 30px 0;
}
.el-button {
  background-color: rgb(85, 92, 253);
  color: #fff;
}
.else {
  order: 2;
  margin: 1px 10px;
}
.one {
  order: -1;
}
</style>
