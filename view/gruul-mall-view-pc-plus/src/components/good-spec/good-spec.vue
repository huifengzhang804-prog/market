<script setup lang="ts">
import { PropType, shallowReactive, watch, toRefs, markRaw, toRaw } from 'vue'
import { cloneDeep } from 'lodash-es'
import GoodNormClass from './goodNorm'
import useConvert from '@/composables/useConvert'
import { doGetProduct } from '@/apis/goods'
import { ElMessage } from 'element-plus'
import { ProductSpecsSkusVO, ProductFeaturesValueDTO, StorageSku, ProductSpecVO } from '@/views/detail/types'

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
  params: {
    type: Object,
    default() {
      return {}
    },
  },
  currentSpecs: {
    type: Array as PropType<ProductFeaturesValueDTO[]>,
    default() {
      return []
    },
  },
  charSpec: {
    type: Boolean,
    default: true,
  },
})
const $route = useRoute()
const $state = shallowReactive<StateType>({
  commoditySpecs: [],
  optionSpec: [],
  chooseSpec: [],
  normClass: new GoodNormClass([], []),
})
const $emit = defineEmits(['chooseSpec'])
let firstSpecs = {} as any

let defaultSpecs: any
const specsObj = ref<any>({})
const productAttributes = ref<any>(
  $props.params.productAttributes?.map((item: { id: string | number }) => {
    return {
      ...item,
      featureValues: specsObj.value[item.id] || [],
    }
  }),
)
const handleInit = async () => {
  const attributes = $props.params.productAttributes
  if (!$props.skuGroup.specGroups) return
  const adopt = $props.skuGroup.specGroups.length === 0 ? productAttributes.value : $state.chooseSpec.length
  if (attributes && adopt) {
    attributes.forEach((item: any) => {
      const obj = $props.currentSpecs.find((ite: ProductFeaturesValueDTO) => ite.id === item.id)
      console.log($props.currentSpecs, '$props.currentSpecs')
      if (obj) {
        specsObj.value[item.id] = [...obj.featureValues]
      } else {
        specsObj.value[item.id] = []
      }
      console.log(specsObj.value, 'specsObj.value')
    })
    productAttributes.value = attributes.map((item: any) => {
      return {
        ...item,
        featureValues: specsObj.value[item.id],
      }
    })
    defaultSpecs = await getChooseSpecs()
    $emit('chooseSpec', defaultSpecs)
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
    $emit('chooseSpec', defaultSpecs)
  }
}
const stockMap = ref(new Map())
watch(
  $props.skuGroup,
  (val) => {
    if (val) {
      const e = cloneDeep(val)
      // 整理传参数据
      const filterClassArr = filterClassArrField(e.specGroups)
      const filterList = filterListField(e.skus)
      initStockMap(e.skus)
      // 创建商品规格矩阵
      const newGoodNormClass = new GoodNormClass(filterClassArr, filterList)
      // 初始化变量
      $state.commoditySpecs = filterClassArr
      const firstSpec = $state.commoditySpecs[0]?.list.map((item) => {
        return {
          name: item,
          img: $props.skuGroup.skus.find((sku) => sku.specs[0] === item)?.image,
        }
      })
      firstSpecs = firstSpec
      // this.specCollectList = filterList
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

const changeSkuImage = () => {
  firstSpecs.forEach((item: any) => {
    if (item.name === $state.chooseSpec[0]) {
      $props.skuGroup.skus.forEach((s) => {
        if (s.specs[0] === item.name && s.specs[1] === $state.chooseSpec[1]) {
          item.img = s.image
        }
      })
    }
  })
}

//选择多规格
const chooseSpecHandle = async (flag: boolean, value: string, index: number) => {
  const { commoditySpecs, chooseSpec, normClass, optionSpec } = toRefs($state)
  if (chooseSpec.value[index] === value || !flag) return
  chooseSpec.value[index] = chooseSpec.value[index] === value ? '' : value
  chooseSpec.value = chooseSpec.value.slice()
  optionSpec.value = normClass.value.querySpecsOptions(chooseSpec.value.slice())
  changeSkuImage()
  // 向父级传递选中的规格
  if (chooseSpec.value.filter(Boolean).length === commoditySpecs.value.length) {
    const defaultSpecs = await getChooseSpecs()
    $emit('chooseSpec', defaultSpecs)
  }
}
const flashSaleSku = ref('')
//获取选中规格
async function getChooseSpecs() {
  const tempArr = toRaw($props.skuGroup.skus)
  const arr =
    $props.skuGroup.specGroups.length === 0
      ? tempArr
      : tempArr.filter((item) => {
          if (equar(item.specs, $state.chooseSpec)) {
            return true
          } else {
            return false
          }
        })
  let temp = cloneDeep(arr) as any
  if ($props.charSpec) {
    await getDatil(temp[0].id)
  }
  let count = 0
  for (const item in specsObj.value) {
    count = specsObj.value[item].reduce((pre: number, ite: any) => {
      return pre + Number(ite.secondValue)
    }, count)
  }
  if (!temp[0]) temp = [{ salePrice: '0' }]
  const salePrice = flashSaleSku.value || temp[0].salePrice
  temp[0].salePrice = String(+salePrice + count)
  return temp
}
async function getDatil(id: string) {
  const { data, code, msg } = await doGetProduct({ productId: $route.query.productId, shopId: $route.query.shopId, skuId: id })
  if (code !== 200) {
    ElMessage.error(msg || '商品详情获取失败')
    return Promise.reject()
  }
  flashSaleSku.value = data?.activity?.type === 'SPIKE' && data.activityOpen ? data?.activity.activityPrice : null
  return true
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
function filterClassArrField(specArr: ProductSpecVO[]) {
  return specArr.map((item) => {
    return {
      title: item.name,
      list: item.children.map((item) => {
        return item.name
      }),
    }
  })
}
function filterListField(list: StorageSku[]) {
  return list.map((item) => {
    return {
      id: item.id,
      specs: item.specs,
    }
  })
}
// 选中参数
const handleCheckbox = async (flag: boolean, value: any, key: string, isMultiSelect: boolean) => {
  if (flag) {
    if (isMultiSelect) {
      specsObj.value[key] = [...specsObj.value[key], value]
    } else {
      specsObj.value[key] = [value]
    }
  } else {
    specsObj.value[key] = specsObj.value[key].filter((item: any) => item.featureValueId !== value.featureValueId)
  }
  let count = 0
  for (const item in specsObj.value) {
    count = specsObj.value[item].reduce((pre: typeof count, ite: any) => {
      return pre + divTenThousand(ite.secondValue).toNumber()
    }, count)
  }
  productAttributes.value = $props.params.productAttributes.map((item: ProductFeaturesValueDTO) => {
    return {
      ...item,
      featureValues: specsObj.value[item.id],
    }
  })
  const defaultSpecs = await getChooseSpecs()
  $emit('chooseSpec', defaultSpecs)
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
  <div>
    <div v-for="(item, index) in $state.commoditySpecs" :key="index">
      <div flex c-mt-13>
        <div c-w-50 e-tj c-mr-16 c-mt-6>
          {{ item.title }}
        </div>
        <div v-if="index === 0" flex items-center flex-wrap c-w-630>
          <div
            v-for="(ite, inde) in firstSpecs"
            :key="inde"
            c-lh-42
            b-2
            c-bc-e0e0e0
            flex
            items-center
            c-mr-7
            c-mb-5
            c-pr-5
            e-c-3
            :class="{
              active: $state.chooseSpec.indexOf(ite.name) !== -1,
              disable: $state.optionSpec.indexOf(ite.name) < 0,
              charSpec: !charSpec,
              charSpecActive: !charSpec && $state.chooseSpec.indexOf(ite.name) !== -1,
            }"
            @click="chooseSpecHandle($state.optionSpec.indexOf(ite.name) > -1, ite.name, index)"
          >
            <img v-if="$props.imgBoolean && ite.img" :src="ite.img" c-w-38 c-h-38 c-mr-9 />
            <div c-lh-42 style="min-width: 50px; text-align: center">
              {{ ite.name }}
            </div>
          </div>
        </div>
        <div v-else flex items-center flex-wrap c-w-630>
          <div
            v-for="(ite, inde) in item.list"
            :key="inde"
            c-lh-42
            b-2
            c-bc-e0e0e0
            flex
            items-center
            c-mr-7
            c-mb-5
            c-pr-15
            c-pl-15
            c-c-000
            e-c-3
            :class="{
              active: $state.chooseSpec.indexOf(ite) > -1 && !stockMap.get(sellOutStyle(ite, index)),
              disable: $state.optionSpec.indexOf(ite) <= 0,
              overClass: stockMap.get(sellOutStyle(ite, index)),
              charSpec: !charSpec,
              charSpecActive: !charSpec && $state.chooseSpec.indexOf(ite) > -1 && !stockMap.get(sellOutStyle(ite, index)),
            }"
            @click="chooseSpecHandle($state.optionSpec.indexOf(ite) > -1, ite, index)"
          >
            {{ ite }}
          </div>
        </div>
      </div>
    </div>
    <template v-if="Object.keys(specsObj).length">
      <div v-for="(item, index) in params.productAttributes" :key="index" class="norm">
        <div class="norm__item" flex>
          <div class="norm__title">
            {{ item.featureName }} ({{ item.isRequired ? '必选' : '非必选' }}) ({{ item.isMultiSelect ? '多选' : '单选' }})
          </div>
          <div class="norm__block">
            <div
              v-for="(ite, inde) in item.featureValues"
              :key="inde"
              class="norm__options"
              :class="{
                choosed: specsObj[item.id]?.find((it: any) => it.featureValueId === ite.featureValueId) !== undefined,
                charSpec: !charSpec,
                charSpecActive: !charSpec && specsObj[item.id]?.find((it: any) => it.featureValueId === ite.featureValueId) !== undefined,
              }"
              @click="
                handleCheckbox(
                  specsObj[item.id]?.find((it: any) => it.featureValueId === ite.featureValueId) === undefined,
                  ite,
                  item.id,
                  item.isMultiSelect,
                )
              "
            >
              {{ ite.firstValue }} {{ divTenThousand(ite.secondValue) }}元
            </div>
          </div>
        </div>
      </div>
    </template>
  </div>
</template>

<style lang="scss" scoped>
@include b(norm) {
  margin-top: 13px;
  @include e(title) {
    font-size: 12px;
    margin: 6px 10px 0px 0px;
    // font-weight: 700;
  }
  @include e(block) {
    width: 100%;
    display: flex;
    font-size: 14px;
    flex-wrap: wrap;
  }
  @include e(options) {
    // background: #e0e0e0;
    // min-width: 152px;
    padding: 0 14px;
    height: 42px;
    text-align: center;
    line-height: 39px;
    color: #333;
    margin: 0 7px 5px 0;
    border: 2px solid #e0e0e0;
  }
}
@include b(option) {
  color: #333;
}
@include b(choosed) {
  // color: #fff;
  border: 2px solid #fa3534;
}
@include b(disable) {
  color: #ccc;
  border: none;
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
    right: 0px;
    top: -15px;
    border-radius: 5rpx;
    padding: 0 5rpx;
    line-height: 26rpx;
    transform: scale(0.85);
  }
}
@include b(charSpec) {
  border-radius: 5px;
}
@include b(charSpecActive) {
  border-color: #ff5f16 !important;
  color: #ff5f16 !important;
}
</style>
<style scoped>
.active {
  border: 2px solid red;
}

.disable {
  border: 2px dashed #efefef !important;
  /* color: #efefef; */
}
</style>
