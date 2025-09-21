<script lang="ts" setup>
import { onMounted, type PropType, ref, shallowReactive, toRaw, toRefs, watch } from 'vue'
import { cloneDeep, debounce } from 'lodash'
import GoodNormClass from '@/components/good-spec/goodNorm'
import type { extra, ProductSpecsSkusVO, StorageSku } from '@/pluginPackage/goods/commodityInfo/types'
import useConvert from '@/composables/useConvert'
import { Decimal } from 'decimal.js-light'
import type { ProductResponse, ProductSpecVO } from '@/apis/good/model'

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
    type: Object as PropType<ProductSpecsSkusVO>,
    default() {
      return {}
    },
  },
  // ['白色,L','红色,m']
  disableOptions: {
    type: Array as PropType<string[]>,
    default() {
      return []
    },
  },
  // 当前选中的属性参数
  currentSpecs: {
    type: Array as PropType<extra['productAttributes']>,
    default() {
      return []
    },
  },
  // 当前选中的规格参数
  currentChoose: {
    type: Array as PropType<string[]>,
    default() {
      return []
    },
  },
  disableSpec: {
    type: Array as PropType<string[]>,
    default() {
      return []
    },
  },
  params: {
    type: Object as PropType<extra>,
    default() {
      return {}
    },
  },
  goodInfo: {
    type: Object as PropType<ProductResponse>,
    default() {
      return {}
    },
  },
  isGroup: {
    type: Boolean,
    default: false,
  },
  // 是否套餐模式
  setMenu: {
    type: Boolean,
    default: false,
  },
})
const $state = shallowReactive<StateType>({
  commoditySpecs: [],
  optionSpec: [],
  chooseSpec: [],
  normClass: new GoodNormClass([], []),
})

const $emit = defineEmits(['chooseSpec'])

let defaultSpecs: StorageSku[]
// 商品属性
const specsObj = ref<{ [key: Long]: any }>({})
const productAttributes = ref<extra['productAttributes'] | null>(
  $props.params.productAttributes?.map((item) => {
    return {
      ...item,
      featureValues: specsObj.value[item.id] || [],
    }
  }),
)
const handleInit = () => {
  const attributes = $props.params.productAttributes
  console.log('================')

  const adopt = $props.skuGroup.specGroups?.length === 0 ? productAttributes.value : $state.chooseSpec.length
  if (attributes && adopt) {
    attributes.forEach((item) => {
      const obj = $props.currentSpecs?.find((ite) => ite.id === item.id)
      if (obj) {
        specsObj.value[item.id] = [...obj.featureValues]
      } else {
        specsObj.value[item.id] = []
      }
    })
    productAttributes.value = attributes.map((item) => {
      return {
        ...item,
        featureValues: specsObj.value[item.id],
      }
    })
    defaultSpecs = getChooseSpecs()
    console.log(1)
    $emit('chooseSpec', defaultSpecs, false)
  } else {
    setTimeout(() => {
      handleInit()
    }, 500)
  }
}

onMounted(() => {
  handleInit()
})

const refresh = () => {
  if (defaultSpecs) {
    console.log(2)
    $emit('chooseSpec', defaultSpecs)
  }
}
const stockMap = ref(new Map())
// TODO: 待优化 GoodNormClass 实例化只需要一次
watch(
  $props,
  (val) => {
    if (val) {
      const e = cloneDeep($props.skuGroup)
      let filterClassArr: any = []
      let filterList: any = []
      initStockMap(e.skus)
      if ($props.skuGroup.specGroups && $props.skuGroup.specGroups.length > 0) {
        // 整理传参数据
        filterClassArr = filterClassArrField(e.specGroups)
        filterList = filterListField(e.skus, val.disableSpec)
      }
      // 创建商品规格矩阵
      const newGoodNormClass = new GoodNormClass(filterClassArr, filterList)
      // 初始化变量
      $state.commoditySpecs = filterClassArr
      $state.normClass = newGoodNormClass
      console.log('已选的', $props.currentChoose)
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

//选择多规格 注意节流
const chooseSpecHandle = debounce(
  (flag: boolean, value: string, index: number) => {
    const { commoditySpecs, chooseSpec, normClass, optionSpec } = toRefs($state)
    if (chooseSpec.value[index] === value || !flag) return
    chooseSpec.value[index] = chooseSpec.value[index] === value ? '' : value
    chooseSpec.value = chooseSpec.value.slice()
    optionSpec.value = normClass.value.querySpecsOptions(chooseSpec.value.slice())
    // 向父级传递选中的规格
    if (chooseSpec.value.filter(Boolean).length === commoditySpecs.value.length) {
      console.log(3)
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
  let activePrice = $props.skuGroup.skus[0].salePrice
  if ($props.goodInfo.activity?.type === 'SPIKE') {
    // 秒杀活动
    activePrice = $props.goodInfo.activity?.activityPrice || 0
  }
  // 所有的颜色分类
  const tempArr = toRaw(
    $props.skuGroup.skus.map((v) => {
      return {
        ...v,
        activePrice: activePrice,
      }
    }),
  )
  // 选中的颜色分类
  const arr = $props.skuGroup.specGroups?.length === 0 ? tempArr : tempArr.filter((item) => equar(item.specs, $state.chooseSpec))
  const temp = cloneDeep(arr).filter((item) => item.stock)
  let count = 0

  let item: string
  // specsObj.value选中的特殊规格[](需要加价格)
  for (item in specsObj.value) {
    count += specsObj.value[item].reduce((pre: number, ite: any) => {
      return pre + Number(ite.secondValue)
    }, 0)
  }
  if (temp[0]) {
    if ($props.goodInfo.activityOpen && $props.goodInfo.skuActivity && ($props.isGroup || $props.goodInfo.activity?.type === 'SPIKE')) {
      temp[0].activePrice = String((+temp[0].activePrice || 0) + count)
      temp[0].attributePirce = String(count)
    } else {
      temp[0].salePrice = String((+temp[0].salePrice || 0) + count)
      temp[0].attributePirce = String(count)
    }
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

function filterClassArrField(specArr?: ProductSpecVO[]) {
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

function filterListField(list: StorageSku[], disableSpecs: string[]) {
  const filterArr: Array<{ id: Long; specs: string[] }> = []
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
const handleCheckbox = (flag: boolean, value: any, key: Long, isMultiSelect: boolean) => {
  if (flag) {
    if (isMultiSelect) {
      specsObj.value[key] = [...specsObj.value[key], value]
    } else {
      specsObj.value[key] = [value]
    }
  } else {
    specsObj.value[key] = specsObj.value[key].filter((item: any) => item.featureValueId !== value.featureValueId)
  }
  let count = new Decimal(0)
  for (const item in specsObj.value) {
    count = specsObj.value[item].reduce((pre: typeof count, ite: any) => {
      return pre.add(divTenThousand(ite.secondValue).toNumber())
    }, count)
  }
  productAttributes.value = $props.params.productAttributes
    ? $props.params.productAttributes.map((item) => {
        return {
          ...item,
          featureValues: specsObj.value[item.id],
        }
      })
    : []
  console.log(4)
  $emit('chooseSpec', getChooseSpecs(), false)
}

// 初始化库存map
function initStockMap(skus: any[]) {
  for (let i = 0; i < skus.length; i++) {
    const item = skus[i]
    stockMap.value.set(item.specs.join(','), item.stockType === 'LIMITED' && Number(item.stock) === 0)
  }
}

// 整理库存map的key
function sellOutStyle(ite: string, index: number) {
  let key = [...$state.chooseSpec]
  key[index] = ite
  return key.join(',')
}

defineExpose({
  refresh,
  productAttributes,
})
</script>
<template>
  <view>
    <template v-if="skuGroup.specGroups && skuGroup.specGroups.length > 0">
      <view v-for="(item, index) in $state.commoditySpecs" :key="index" class="norm">
        <view class="norm__item">
          <view class="norm__title">{{ item.title }}</view>
          <view class="norm__block">
            <view
              v-for="(ite, inde) in item.list"
              :key="inde"
              :class="{
                option: $state.optionSpec.indexOf(ite) > -1,
                choosed: $state.chooseSpec.indexOf(ite) > -1,
                disable: $state.optionSpec.indexOf(ite) === -1 || stockMap.get(sellOutStyle(ite, index)),
                overClass: stockMap.get(sellOutStyle(ite, index)),
              }"
              class="norm__options"
              @click="chooseSpecHandle($state.optionSpec.indexOf(ite) > -1 && !stockMap.get(sellOutStyle(ite, index)), ite, index)"
            >
              {{ ite }}
            </view>
          </view>
        </view>
      </view>
    </template>
    <template v-if="Object.keys(specsObj).length > 0">
      <!-- 商品属性 -->
      <view v-for="item in params.productAttributes" :key="item.id" class="norm">
        <view class="norm__item">
          <view class="norm__title">
            {{ item.featureName }}({{ item.isRequired ? '必选' : '非必选' }})({{ item.isMultiSelect ? '多选' : '单选' }})
          </view>
          <view class="norm__block">
            <view
              v-for="ite in item.featureValues"
              :key="ite.featureValueId"
              :class="{
                choosed: specsObj[item.id]?.find((it: any) => it.featureValueId === ite.featureValueId) !== undefined,
              }"
              class="norm__options"
              @click="
                handleCheckbox(
                  specsObj[item.id]?.find((it: any) => it.featureValueId === ite.featureValueId) === undefined,
                  ite,
                  item.id,
                  item.isMultiSelect,
                )
              "
            >
              {{ `${ite.firstValue}${ite.secondValue && new Decimal(ite.secondValue).lte(0) ? '' : '+'}${divTenThousand(ite.secondValue)}` }}元
            </view>
          </view>
        </view>
      </view>
    </template>
    <template v-if="setMenu && Object.keys(specsObj).length <= 0 && skuGroup.specGroups && skuGroup.specGroups.length <= 0">
      <view class="norm__item" style="display: flex">
        <view class="norm__title">规格</view>
        <view class="norm__block" style="width: auto; margin: 0">
          <view class="norm__options choosed"> 默认规格 </view>
        </view>
      </view>
    </template>
  </view>
</template>

<style lang="scss" scoped>
@include b(norm) {
  @include e(title) {
    margin-bottom: 15rpx;
    font-size: 32rpx;
    font-weight: 700;
  }
  @include e(block) {
    width: 100%;
    display: flex;
    font-size: 28rpx;
    flex-wrap: wrap;
    margin: 0 -20rpx 30rpx;
  }
  @include e(options) {
    background: #f6f6f6;
    min-width: 152rpx;
    padding: 12rpx 20rpx;
    text-align: center;
    line-height: 34rpx;
    color: #333;
    margin: 0 20rpx 20rpx;
    border: 1px solid transparent;
    border-radius: 10rpx;
  }
}

@include b(option) {
  color: #333;
}

@include b(overClass) {
  color: #c7c7c7;
  position: relative;
  &::before {
    content: '缺货';
    font-size: 24rpx;
    color: #c7c7c7;
    display: block;
    position: absolute;
    right: 10rpx;
    top: -15rpx;
    border-radius: 5rpx;
    padding: 0 5rpx;
    line-height: 26rpx;
    transform: scale(0.85);
  }
}

@include b(choosed) {
  color: #333333;
  border-color: rgb(245, 67, 25);
  background-color: rgba(245, 67, 25, 0.04);
  &::before {
    color: #fff;
    background: #fa3534;
  }
}

@include b(disable) {
  color: #ccc;
  border: none;
}
</style>
