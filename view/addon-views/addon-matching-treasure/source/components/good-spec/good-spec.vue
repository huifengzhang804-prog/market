<script setup lang="ts">
import { PropType, shallowReactive, watch, toRefs, toRaw, ref, onMounted, nextTick } from 'vue'
import GoodNormClass from './goodNorm'
import type { ApiProductSpecs, ApiGoodSkuCombination, ApiGoodSkus, ProductExtraDTO } from './good-spec-type'
import useConvert from '@/composables/useConvert'
import Decimal from 'decimal.js'
import { cloneDeep, debounce } from 'lodash-es'

const { divTenThousand } = useConvert()

interface CommoditySpecType {
  title: string
  list: string[]
}
interface StateType {
  commoditySpecs: CommoditySpecType[]
  optionSpec: string[]
  chooseSpec: string[]
  normClass: GoodNormClass
}

const $props = defineProps({
  skuGroup: {
    type: Object as PropType<ApiGoodSkuCombination>,
    default() {
      return {}
    },
  },
  currentChoose: {
    type: Array as PropType<string[]>,
    default() {
      return []
    },
  },
  imgBoolean: {
    type: Boolean,
    default: true,
  },
  disableSpec: {
    type: Array as PropType<string[]>,
    default() {
      return []
    },
  },
  params: {
    type: Object as PropType<ProductExtraDTO>,
    default() {
      return {}
    },
  },
})
const $state = shallowReactive<StateType>({
  commoditySpecs: [],
  optionSpec: [],
  chooseSpec: [],
  normClass: new GoodNormClass([], []),
})
let defaultSpecs: ApiGoodSkus[]
// 商品属性
const specsObj = ref<{ [key: string]: any }>({})
const productAttributes = ref<ProductExtraDTO['productAttributes'] | null>(
  $props.params.productAttributes?.map((item) => {
    return {
      ...item,
      featureValues: specsObj.value[item.id] || [],
    }
  }),
)

const $emit = defineEmits(['chooseSpec', 'init-complete'])

const handleInit = () => {
  const attributes = $props.params.productAttributes
  const adopt = $props.skuGroup.specGroups?.length === 0 ? productAttributes.value : $state.chooseSpec.length
  if (attributes && adopt) {
    attributes.forEach((item) => {
      specsObj.value[item.id] = []
    })
    productAttributes.value = attributes.map((item) => {
      return {
        ...item,
        featureValues: specsObj.value[item.id],
      }
    })
    defaultSpecs = getChooseSpecs()
    $emit('chooseSpec', defaultSpecs, false)

    // 初始化后通知父组件
    nextTick(() => {
      $emit('init-complete', {
        specsObj: specsObj,
        handleCheckbox: handleCheckbox,
        productAttributes: productAttributes.value,
      })
    })
  } else {
    setTimeout(() => {
      handleInit()
    }, 500)
  }
}

onMounted(() => {
  handleInit()
})

watch(
  $props.skuGroup,
  (val) => {
    if (val) {
      // const e = cloneDeep(val)
      const e = JSON.parse(JSON.stringify(val))
      // 整理传参数据
      let filterClassArr: any = []
      let filterList: any = []
      if ($props.skuGroup.specGroups && $props.skuGroup.specGroups.length > 0) {
        // 整理传参数据
        filterClassArr = filterClassArrField(e.specGroups)
        filterList = filterListField(e.skus, $props.disableSpec)
      }
      // 初始化变量
      $state.commoditySpecs = filterClassArr
      // 创建商品规格矩阵
      const newGoodNormClass = new GoodNormClass(filterClassArr, filterList)
      // 初始化变量
      $state.commoditySpecs = filterClassArr
      $state.normClass = newGoodNormClass

      // 初始化选择规格数组
      if (!$props.currentChoose.length && e.skus.length > 0) {
        $state.chooseSpec = e.skus[0].specs
      } else if ($props.currentChoose.length && e.skus.length > 0) {
        $state.chooseSpec = JSON.parse(JSON.stringify($props.currentChoose))
      } else {
        $state.chooseSpec = Array.from({ length: filterClassArr.length })
      }
      // 初始化可选规格值数组
      $state.optionSpec = newGoodNormClass.querySpecsOptions($state.chooseSpec)
    }
  },
  {
    immediate: true,
  },
)

//选择多规格
const chooseSpecHandle = debounce(
  (flag: boolean, value: string, index: number) => {
    const { commoditySpecs, chooseSpec, normClass, optionSpec } = toRefs($state)
    if (chooseSpec.value[index] === value || !flag) return
    chooseSpec.value[index] = chooseSpec.value[index] === value ? '' : value
    chooseSpec.value = chooseSpec.value.slice()
    optionSpec.value = normClass.value.querySpecsOptions(chooseSpec.value.slice())
    // 向父级传递选中的规格
    if (chooseSpec.value.filter(Boolean).length === commoditySpecs.value.length) {
      $emit('chooseSpec', getChooseSpecs())
    }
  },
  300,
  {
    leading: true,
    trailing: false,
  },
)

//获取选中规格
function getChooseSpecs() {
  // 所有的颜色分类
  const tempArr = toRaw($props.skuGroup.skus)
  // 选中的颜色分类
  const arr = $props.skuGroup.specGroups?.length === 0 ? tempArr : tempArr.filter((item) => equar(item.specs, $state.chooseSpec))
  const temp = cloneDeep(arr)
  let count = 0

  let item: string
  // specsObj.value选中的特殊规格[](需要加价格)
  for (item in specsObj.value) {
    count += specsObj.value[item].reduce((pre: number, ite: any) => {
      return pre + Number(ite.secondValue)
    }, 0)
  }
  if (temp[0]) {
    temp[0].salePrice = String((+temp[0].salePrice || 0) + count)
    temp[0].attributePirce = String(count)
  }
  return temp
}
function equar(a: string[], b: string[]) {
  // 判断数组的长度
  if (a.length !== b.length) {
    return false
  } else {
    // 循环遍历数组的值进行比较
    for (let i = 0; i < a.length; i++) {
      if (a[i] !== b[i]) {
        return false
      }
    }
    return true
  }
}

function filterClassArrField(specArr: ApiProductSpecs[]) {
  if (specArr) {
    return specArr.map((item) => {
      return {
        title: item.name,
        list: item.children.map((item) => {
          return item.name
        }),
      }
    })
  }

  return []
}

function filterListField(list: ApiGoodSkus[], disableSpecs: string[]) {
  const filterArr: Array<{ id: string; specs: string[] }> = []
  list.forEach((item) => {
    const tag = disableSpecs.includes(item.specs?.join(','))
    if (!tag) {
      filterArr.push({
        id: item.id,
        specs: item.specs,
      })
    }
  })
  return filterArr
}

// 选中参数
const handleCheckbox = (flag: boolean, value: any, key: string, isMultiSelect: boolean) => {
  if (flag) {
    if (isMultiSelect) {
      // 对于多选，添加到数组中
      specsObj.value[key] = [...specsObj.value[key], value]
    } else {
      // 对于单选，替换整个数组
      specsObj.value[key] = [value]
    }
  } else {
    // 移除特定选项
    specsObj.value[key] = specsObj.value[key].filter((item: any) => item.featureValueId !== value.featureValueId)
  }

  // 计算价格
  let count = new Decimal(0)
  for (const item in specsObj.value) {
    count = specsObj.value[item].reduce((pre: typeof count, ite: any) => {
      return pre.add(divTenThousand(ite.secondValue).toNumber())
    }, count)
  }

  // 更新属性值
  productAttributes.value = $props.params.productAttributes
    ? $props.params.productAttributes.map((item) => {
        return {
          ...item,
          featureValues: specsObj.value[item.id],
        }
      })
    : []

  // 通知父组件
  $emit('chooseSpec', getChooseSpecs(), false)
}

defineExpose({
  productAttributes,
  params: $props.params,
  commoditySpecs: $state.commoditySpecs,
})
</script>

<template>
  <div class="box">
    <!-- 单规格默认 -->
    <div v-if="skuGroup.specGroups && skuGroup.specGroups.length <= 0">
      <div class="norm">
        <div flex c-mt-13>
          <div e-tj c-mr-23 c-mt-11 class="title">规格</div>
          <div flex items-center flex-wrap c-w-630>
            <div c-lh-42 style="min-width: 30px; text-align: center">
              <div c-lh-34 b-2 c-bc-e0e0e0 c-mr-7 c-mt-5 e-c-3 c-pl-15 c-pr-15 class="active">默认</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-for="(item, index) in $state.commoditySpecs" :key="index" class="norm">
      <div flex c-mt-13>
        <div e-tj c-mr-23 c-mt-11 class="title">{{ item.title }}</div>
        <div flex items-center flex-wrap c-w-630>
          <div
            v-for="(ite, inde) in item.list"
            :key="inde"
            c-lh-34
            b-2
            c-bc-e0e0e0
            c-mr-7
            c-mt-5
            e-c-3
            c-pl-15
            c-pr-15
            :class="{
              active: $state.chooseSpec.indexOf(ite) !== -1,
              disable: $state.optionSpec.indexOf(ite) === -1,
            }"
            @click="chooseSpecHandle($state.optionSpec.indexOf(ite) > -1, ite, index)"
          >
            <!-- <img v-if="$props.imgBoolean" :src="ite.img" c-w-38 c-h-38 c-mr-9 /> -->
            <div c-lh-42 style="min-width: 30px; text-align: center">{{ ite }}</div>
          </div>
        </div>
      </div>
    </div>
    <!-- 商品属性 -->
    <div v-for="item in params.productAttributes" :key="item.id" class="norm">
      <div flex c-mt-13>
        <div e-tj c-mr-23 c-mt-11 class="title">
          {{ item.featureName }}({{ item.isRequired ? '必选' : '非必选' }})({{ item.isMultiSelect ? '多选' : '单选' }})
        </div>
        <div flex items-center flex-wrap c-w-630>
          <div
            v-for="ite in item.featureValues"
            :key="ite.featureValueId"
            :class="{
                                active: specsObj[item.id]?.find((it: any) => it.featureValueId === ite.featureValueId) !== undefined,
                            }"
            c-lh-34
            b-2
            c-bc-e0e0e0
            c-mr-7
            c-mt-5
            e-c-3
            c-pl-15
            c-pr-15
            @click="
              handleCheckbox(
                specsObj[item.id]?.find((it: any) => it.featureValueId === ite.featureValueId) === undefined,
                ite,
                item.id,
                item.isMultiSelect,
              )
            "
          >
            <div e-c-3 c-lh-42 style="min-width: 30px; text-align: center">
              {{ `${ite.firstValue}${ite.secondValue && new Decimal(ite.secondValue).lte(0) ? '' : '+'}${divTenThousand(ite.secondValue)}` }}元
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.active {
  border: 2px solid red;
}

.disable {
  color: #ccc;
  border: none;
  background: #f6f6f6;
}
.title {
  padding: 0 8px;
  white-space: nowrap;
  display: inline-block;
  margin-top: 15px;
}
.confirmBtn {
  padding-top: 25px;
  width: 100%;
  text-align: right;
}
.el-button {
  background-color: rgb(85, 92, 253);
  color: #fff;
}
.box {
  padding: 20px;
}
</style>
