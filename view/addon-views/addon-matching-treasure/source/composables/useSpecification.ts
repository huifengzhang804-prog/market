import { ref, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { doGetGoodDetail, doGetGoodSku } from '../apis'
import { CheckBoxItem } from './useSetMeal'
import { ApiGoodSkus } from '@/views/detail/types/index'

// 定义ApiGoodSkuCombination接口
export interface ApiGoodSkuCombination {
  specGroups?: any[]
  skus?: any[]
}

// 类型定义
export interface SetMealProductDetails {
  productId: string
  productName: string
  productPic: string
  productAttributes: string
  setMealProductSkuDetails: any[]
}

/**
 * 规格选择逻辑
 */
export default function useSpecification(
  shopId: string,
  saveCoppleteSku: (key: string, sku: any, isMore?: boolean) => void,
  updateSavingPrice: () => void,
) {
  // SKU相关状态
  const eItem = ref<ApiGoodSkus>()
  const onlySkys = ref<Record<string, any>>({})
  const completeSku = ref<Record<string, any>>({})
  const allAttr = ref<Record<string, any>>({})

  // 选择交互状态
  const selectBoxCopyBoolean = ref(false)
  const productIds = ref('')
  const priceid = ref(-1)
  const globalNum = ref(0)
  const myTeamKey = ref(0)
  const productExtra = ref<any>()
  const firstSkuListCopy = ref<ApiGoodSkuCombination>()
  const GoodSpecRef = ref()

  // 选中状态
  const finalStatement = ref<any[]>([])

  // 当前选中的商品ID
  let currentProductId = ''

  /**
   * 查询商品SKU
   */
  const fetchGoodSku = async (productId: string) => {
    try {
      const { data, code } = await doGetGoodSku(shopId, productId)
      return code === 200 ? data : []
    } catch (error) {
      console.error('获取商品SKU失败:', error)
      return []
    }
  }

  /**
   * 处理规格选择返回结果
   */
  const handleChooseSpec = (selectedSkus: ApiGoodSkus[]) => {
    if (!selectedSkus.length) return

    eItem.value = selectedSkus[0]
    finalStatement.value[priceid.value] = selectedSkus[0]

    // 更新选择框中的SKU ID
    if (globalNum.value >= 0 && priceid.value >= 0) {
      return selectedSkus[0]?.id || ''
    }

    return ''
  }

  /**
   * 确认关闭规格选择
   */
  const closeSpecificationSelector = (isVisible: boolean) => {
    selectBoxCopyBoolean.value = isVisible
  }

  /**
   * 处理GoodSpec组件初始化完成
   */
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

  /**
   * 确认规格选择
   */
  const confirmSpecSelection = (checkBoxList: CheckBoxItem[][], getPrice: (id: string) => string, handleParams: (currentSpecs: any) => string[]) => {
    if (!GoodSpecRef.value || !GoodSpecRef.value[0]) {
      console.error('规格选择器组件未找到，可能尚未渲染完成')
      return ElMessage.error('规格选择器未初始化')
    }

    const attributes = GoodSpecRef.value[0].productAttributes || []
    console.log('确认规格选择:', { productId: productIds.value, attributes })

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
      if (!allAttr.value[productIds.value]) {
        allAttr.value[productIds.value] = []
      }

      // 检查每个属性并更新
      attributes.forEach((attr: any) => {
        // 寻找已有的属性
        const existingAttrIndex = allAttr.value[productIds.value].findIndex((a: any) => a.id === attr.id)

        // 如果属性已存在，更新featureValues
        if (existingAttrIndex >= 0) {
          // 确保保存完整的featureValues对象（包含featureValueId、firstValue和secondValue）
          allAttr.value[productIds.value][existingAttrIndex].featureValues = attr.featureValues ? JSON.parse(JSON.stringify(attr.featureValues)) : []
        } else {
          // 如果属性不存在，添加新属性
          allAttr.value[productIds.value].push({
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
    } else if (currentProductId === productIds.value && (values.length === 0 || !sku)) {
      // 主商品没有规格的处理
    }

    // 批量更新选中状态
    const currentItem = checkBoxList[globalNum.value][priceid.value]
    const specsFinal = specsDescribeValue === '' ? '默认' : specsDescribeValue

    // 返回更新对象
    const updateObj = {
      price: finalPrice,
      original: original,
      salePrices: salePrices,
      specs: specsFinal,
      skuId: eItem.value?.id || '', // 确保skuId也被更新
    }

    console.log('规格选择已更新:', {
      productId: productIds.value,
      skuId: eItem.value?.id,
      price: finalPrice,
      specs: specsFinal,
    })

    // 异步保存SKU和属性信息，确保UI更新后再执行
    nextTick(() => {
      try {
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
          const storedAttrs = allAttr.value[productIds.value] || []

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

        // 更新价格
        updateSavingPrice()

        // 更新后关闭选择器
        closeSpecificationSelector(false)

        // 强制更新UI
        myTeamKey.value = Date.now()

        // 调试日志
        console.log('Saved attributes:', allAttr.value[productIds.value])
        console.log('Saved SKU:', completeSku.value[productIds.value])
      } catch (error) {
        console.error('保存规格信息失败:', error)
      }
    })

    return updateObj
  }

  /**
   * 打开规格选择器
   */
  const openSpecificationSelector = async (
    index: number,
    item: SetMealProductDetails,
    mealIndex: number,
    mealData: any,
    specs: any,
    checkBoxList: CheckBoxItem[][],
  ) => {
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
      if (val.matchingStock === '0') return

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
      const res = await doGetGoodDetail(productId, shopId)

      // 处理多选配置
      let productAttributesData = res.data?.extra?.productAttributes || []

      // 如果存在属性数据
      if (productAttributesData.length > 0) {
        console.log('原始属性数据:', JSON.stringify(productAttributesData))

        // 获取已保存的属性数据
        const savedAttrs = allAttr.value[productId] || []
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
            Array.isArray(sku.specs) &&
            sku.specs.length === selectedSpecs.length &&
            sku.specs.every((s: string, i: number) => s === selectedSpecs[i]),
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

  /**
   * 从featureValues中计算并更新属性价格
   */
  const updateAttrPricesFromFeatureValues = (productId: string) => {
    if (!allAttr.value[productId] || !completeSku.value[productId]) return

    // 计算属性价格
    let attrPrice = 0

    // 遍历所有属性
    allAttr.value[productId].forEach((attr: any) => {
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

  /**
   * 保存SKU到onlySkys
   */
  const saveOnlySkus = (sku: any) => {
    onlySkys.value = { ...onlySkys.value, ...sku }
  }

  /**
   * 保存属性到allAttr
   */
  const saveOnlyAttr = (attr: { key: string; value: any }) => {
    const { key, value } = attr
    allAttr.value[key] = value
    if (onlySkys.value[key]) {
      saveCoppleteSku(key, onlySkys.value[key])
    }
  }

  return {
    // 暴露状态
    eItem,
    onlySkys,
    completeSku,
    allAttr,
    selectBoxCopyBoolean,
    productIds,
    priceid,
    globalNum,
    myTeamKey,
    productExtra,
    firstSkuListCopy,
    GoodSpecRef,

    // 暴露方法
    handleChooseSpec,
    closeSpecificationSelector,
    handleInitComplete,
    confirmSpecSelection,
    openSpecificationSelector,
    saveOnlySkus,
    saveOnlyAttr,
  }
}
