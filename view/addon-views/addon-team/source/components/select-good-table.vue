<script setup lang="ts">
import { defineExpose, ref, watch, toRaw } from 'vue'
import { ElMessage } from 'element-plus'
import { ApiRetrieveComItemType } from '@/apis/good/model'
import type { ApiGroupMode, ApiGroupProducts, SkuItem, FlatGoodRetrieve } from '../index'
import type { PropType } from 'vue'
import type { TableColumnCtx } from 'element-plus/es/components/table/src/table-column/defaults'
import useConvert from '@/composables/useConvert'

interface SpanMethodProps {
  row: FlatGoodRetrieve
  column: TableColumnCtx<FlatGoodRetrieve>
  rowIndex: number
  columnIndex: number
}

const $props = defineProps({
  mode: {
    type: String as PropType<ApiGroupMode>,
    default: 'COMMON',
  },
  users: {
    type: Array,
    default() {
      return []
    },
  },
  productList: {
    type: Array as PropType<ApiRetrieveComItemType[]>,
    default() {
      return []
    },
  },
  isEdit: {
    type: Boolean,
    default: false,
  },
  flatGoodList: {
    type: Array as PropType<FlatGoodRetrieve[]>,
    default() {
      return []
    },
  },
})
const { divTenThousand, mulTenThousand } = useConvert()
const tableList = ref<FlatGoodRetrieve[]>([])
watch(
  () => $props.productList,
  (newVal) => {
    const flatGoodList = flatGoodSku(newVal, 'skus')
    if (flatGoodList?.length === 0) {
      return ElMessage.error('请至少选择一个存在库存的商品')
    }
    tableList.value = addTag(flatGoodList, 'skus')
  },
  {
    deep: true,
  },
)
watch(
  () => $props.flatGoodList,
  (newVal) => {
    tableList.value = addTag(newVal, 'skus')
  },
)
/**
 * 返回库存
 */
function resultStore(item: FlatGoodRetrieve): number {
  if (item.skuItem.stockType === 'LIMITED') {
    return Number(item.skuItem.skuStock)
  } else {
    return 100000
  }
}
function resultPrice(item: FlatGoodRetrieve): number {
  return divTenThousand(item.skuItem.skuPrice).toNumber()
}
/**
 * 扁平化处理商品sku且打上合并单元格标记
 * @param {ApiRetrieveComItemType[]} goods 商品列表
 * @param {string} flatKey 需要扁平化的key
 */
function flatGoodSku(goods: ApiRetrieveComItemType[], flatKey: string) {
  if (!goods.length) return []
  const tempGoods: FlatGoodRetrieve[] = []
  goods.forEach((good) => {
    good.skuIds.forEach((skuId, index) => {
      if ((good.stocks[index] > 0 && good.stockTypes[index] === 'LIMITED') || good.stockTypes[index] === 'UNLIMITED') {
        tempGoods.push({
          productId: good.productId,
          productName: good.productName,
          productPic: good.pic,
          skuItem: {
            // limitType: good.,
            productId: good.productId,
            skuId,
            skuName: good.specs[index] ? good.specs[index] : '',
            skuPrice: good.salePrices[index],
            skuStock: good.stocks[index] ? good.stocks[index] : '',
            stockType: good.stockTypes[index],
          },
          rowTag: 0,
          stock: +good.stocks[index],
          prices: [good.salePrices[index]],
          isJoin: good.stocks[index] > 0,
        })
      }
    })
  })
  return tempGoods
}
/**
 * 为行合并单元格打上标记
 * @param {FlatGoodRetrieve[]} flatGoodList 扁平化处理后的商品列表
 */
function addTag(flatGoodList: FlatGoodRetrieve[], key: string) {
  let pos = 0
  let len = flatGoodList.length
  for (let i = 0; i < len; i++) {
    const item = flatGoodList[i]
    if (i === 0) {
      item.rowTag = 1
      pos = 0
    }
    if (i !== 0) {
      if (item.productId === flatGoodList[i - 1].productId) {
        item.rowTag = 0
        flatGoodList[pos].rowTag = flatGoodList[pos].rowTag + 1
      } else {
        item.rowTag = 1
        pos = i
      }
    }
    item.prices = item.prices.map((ele) => +ele)
    item.stock = +item.stock
  }

  return flatGoodList
}
const arraySpanMethod = ({ row, column, rowIndex, columnIndex }: SpanMethodProps) => {
  if (columnIndex === 0) {
    return {
      rowspan: row.rowTag,
      colspan: row.rowTag ? 1 : 0,
    }
  }
}
/**
 * 展示拼团库存
 */
function showProductSkuStock(skuItem: SkuItem) {
  return skuItem.stockType === 'UNLIMITED' ? '无限库存' : `${'库存' + skuItem.skuStock || 0}`
}
/**
 * 获取商品提交数据
 */
function getProduct(): ApiGroupProducts[] {
  const productList = toRaw(tableList.value)
  const returnArr: ApiGroupProducts[] = []
  const comIndexMap = new Map()
  if (productList.length) {
    for (let i = 0; i < productList.length; i++) {
      if (!productList[i].isJoin) {
        continue
      }
      const currentItem = productList[i]
      const productId = currentItem.productId
      const skuItem = {
        skuId: currentItem.skuItem.skuId,
        stock: +currentItem.stock,
        prices: currentItem.prices.map((item) => {
          return mulTenThousand(item).toNumber()
        }),
      }
      if (!comIndexMap.has(productId)) {
        comIndexMap.set(productId, returnArr.length)
        returnArr.push({
          productId,
          skus: [skuItem],
        })
      } else {
        const index = comIndexMap.get(productId)
        returnArr[index].skus.push(skuItem)
      }
    }
  }
  return returnArr
}
/**
 * 校验规则
 */
function validateProduct(): boolean {
  let result = true
  const productList = tableList.value
  if (!productList.length) {
    ElMessage.warning('请选择商品')
    result = false
  } else {
    for (let i = 0; i < productList.length; i++) {
      if (!productList[i].isJoin) {
        continue
      }
      if (!productList[i].stock) {
        ElMessage.warning('商品库存必须大于零')
        result = false
        break
      }
      if (productList[i].prices.length !== $props.users.length || productList[i].prices.some((item) => item === null)) {
        ElMessage.warning('拼团价格填写完整')
        result = false
        break
      }
    }
  }
  return result
}
const batchSet = ref({
  stock: 1,
  price: [0.01],
})
const dialogVisible = ref(false)
const currentBatchSetProductId = ref('0')
const batchSetClick = (row: any) => {
  currentBatchSetProductId.value = row.productId
  batchSet.value.stock = row.skuItem.skuStock
  dialogVisible.value = true
}
const handleClose = () => {
  batchSet.value = { stock: 1, price: [0.01] }
  dialogVisible.value = false
}
const submitBatch = () => {
  if ($props.mode === 'COMMON') {
    tableList.value.forEach((item) => {
      if (item.productId === currentBatchSetProductId.value) {
        item.stock = batchSet.value.stock > resultStore(item) ? resultStore(item) : batchSet.value.stock
        item.prices[0] = batchSet.value.price[0] > resultPrice(item) ? resultPrice(item) : batchSet.value.price[0]
      }
    })
  } else {
    tableList.value.forEach((item) => {
      if (item.productId === currentBatchSetProductId.value) {
        item.stock = batchSet.value.stock > resultStore(item) ? resultStore(item) : batchSet.value.stock
        for (let index = 0; index < $props.users.length; index++) {
          // const element = $props.users.length[index]
          item.prices[index] = batchSet.value.price[index] > resultPrice(item) ? resultPrice(item) : batchSet.value.price[index]
        }
      }
    })
  }
  dialogVisible.value = false
}
const showSkuName = (skuName: string | string[], index: number) => {
  if (typeof skuName === 'string') {
    return skuName
  } else {
    if (skuName.length === 1) {
      return skuName[0]
    }
    return skuName[index]
  }
}
defineExpose({
  getProduct,
  validateProduct,
})
</script>

<template>
  <el-table :data="tableList" :span-method="arraySpanMethod">
    <el-table-column label="商品信息" width="215">
      <template #default="{ row }">
        <div class="com">
          <el-image :src="row.productPic" class="com__pic" />
          <div class="com__name">
            <el-button v-if="!$props.isEdit" class="com__batch" type="primary" link @click="batchSetClick(row)"> 批量设置 </el-button>
            <span class="com__name--text" style="margin-top: 5px">{{ row.productName }}</span>
          </div>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="规格">
      <template #default="{ row, $index }">
        {{ showSkuName(row.skuItem.skuName, $index) }}
      </template>
    </el-table-column>
    <el-table-column label="库存">
      <template #default="{ row }">
        <div>
          <el-input-number
            v-model="row.stock"
            :min="0"
            style="width: 80px"
            :max="resultStore(row)"
            :disabled="$props.isEdit"
            :precision="0"
            :controls="false"
          />
        </div>
        <div>{{ showProductSkuStock(row.skuItem) }}</div>
      </template>
    </el-table-column>
    <el-table-column v-if="$props.mode === 'COMMON'" label="拼团价">
      <template #default="{ row }">
        <div>
          <el-input v-if="$props.isEdit" style="width: 80px" disabled :placeholder="divTenThousand(row.prices[0]).toString()" />
          <el-input-number
            v-else
            v-model="row.prices[0]"
            :min="0.01"
            style="width: 80px"
            :disabled="$props.isEdit"
            :precision="2"
            :max="resultPrice(row)"
            :controls="false"
          />
        </div>
        <div>销售价{{ divTenThousand(row.skuItem.skuPrice) }}</div>
      </template>
    </el-table-column>
    <el-table-column v-if="$props.mode === 'STAIRS' && $props.users.length > 0" label="第一阶段拼团">
      <template #default="{ row }">
        <div>
          <el-input v-if="$props.isEdit" style="width: 80px" disabled :placeholder="divTenThousand(row.prices[0]).toString()" />
          <el-input-number
            v-else
            v-model="row.prices[0]"
            :min="0.01"
            style="width: 80px"
            :disabled="$props.isEdit"
            :precision="2"
            :max="resultPrice(row)"
            :controls="false"
          />
        </div>
        <div>销售价{{ divTenThousand(row.skuItem.skuPrice) }}</div>
      </template>
    </el-table-column>
    <el-table-column v-if="$props.mode === 'STAIRS' && $props.users.length > 1" label="第二阶段拼团">
      <template #default="{ row }">
        <div>
          <el-input v-if="$props.isEdit" style="width: 80px" disabled :placeholder="divTenThousand(row.prices[1]).toString()" />
          <el-input-number
            v-else
            v-model="row.prices[1]"
            :disabled="$props.isEdit"
            :min="0.01"
            style="width: 80px"
            :max="resultPrice(row)"
            :precision="2"
            :controls="false"
          />
        </div>
        <div>销售价{{ resultPrice(row) }}</div>
      </template>
    </el-table-column>
    <el-table-column v-if="$props.mode === 'STAIRS' && $props.users.length > 2" label="第三阶段拼团">
      <template #default="{ row }">
        <div>
          <el-input v-if="$props.isEdit" style="width: 80px" disabled :placeholder="divTenThousand(row.prices[2]).toString()" />
          <el-input-number
            v-else
            v-model="row.prices[2]"
            :disabled="$props.isEdit"
            :min="0.01"
            style="width: 80px"
            :precision="2"
            :max="resultPrice(row)"
            :controls="false"
          />
        </div>
        <div>销售价{{ resultPrice(row) }}</div>
      </template>
    </el-table-column>
    <!-- <el-table-column label="是否参与">
            <template #default="{ row }">
                <el-switch v-model="row.isJoin" size="large" :disabled="$props.isEdit" />
            </template>
        </el-table-column> -->
  </el-table>
  <el-dialog
    v-model="dialogVisible"
    title="批量设置"
    destroy-on-close
    center
    :width="$props.mode === 'COMMON' ? 500 : '50%'"
    top="30vh"
    @close="handleClose"
  >
    <div class="flex">
      <div>
        库存
        <el-input-number
          v-model="batchSet.stock"
          :controls="false"
          :style="{ width: $props.mode === 'COMMON' ? '150px' : '80px' }"
          :min="0"
          :precision="0"
        />
      </div>
      <div v-if="$props.mode === 'COMMON'">
        拼团价
        <el-input-number v-model="batchSet.price[0]" :precision="2" :controls="false" :min="0.01" />
      </div>
      <template v-else>
        <div v-for="(item, idx) in $props.users" :key="idx">
          第{{ idx + 1 }}阶段拼团
          <el-input-number v-model="batchSet.price[idx]" style="width: 80px" :precision="2" :controls="false" :min="0.01" />
        </div>
      </template>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBatch"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped>
@include b(com) {
  @include flex;
  @include e(pic) {
    width: 62px;
    height: 62px;
  }
  @include e(name) {
    width: 113px;
    font-size: 14px;
    color: #333;
    margin-left: 10px;
    display: flex;
    flex-direction: column;
    align-items: baseline;
  }
}
.com__name--text {
  @include utils-ellipsis(2);
}
.flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
