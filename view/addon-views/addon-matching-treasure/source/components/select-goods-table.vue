<script lang="ts" setup>
import { defineExpose, ref, watch, toRaw } from 'vue'
import { ElMessage } from 'element-plus'
import { ApiRetrieveComItemType } from '@/apis/good/model'
import type { FlatGood, DoPostAddBargainQuery } from '../index'
import type { PropType } from 'vue'
import type { TableColumnCtx } from 'element-plus/es/components/table/src/table-column/defaults'
import useConvert from '@/composables/useConvert'

interface SpanMethodProps {
  row: any
  column: TableColumnCtx<FlatGood>
  rowIndex: number
  columnIndex: number
}

const $props = defineProps({
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
    type: Array as PropType<FlatGood[]>,
    default() {
      return []
    },
  },
  productAttributes: {
    type: String,
    default: 'MAIN_PRODUCT',
  },
})
const { divTenThousand, mulTenThousand } = useConvert()
const tableList = ref<FlatGood[]>([])
const dialogVisible = ref(false)
watch(
  () => $props.productList,
  (newVal) => {
    console.log('表格变化', newVal)
    const flatGoodList = flatGoodSku(newVal, 'skus')
    console.log('flatGoodList', flatGoodList)
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
function resultStore(item: FlatGood): number {
  if (item.skuItem.stockType === 'LIMITED') {
    return Number(item.skuItem.skuStock)
  } else {
    return 100000
  }
}
function resultPrice(item: FlatGood): number {
  return divTenThousand(item.skuItem.skuPrice).toNumber()
}
/**
 * 扁平化处理商品sku且打上合并单元格标记
 * @param {ApiRetrieveComItemType[]} goods 商品列表
 * @param {string} flatKey 需要扁平化的key
 */
function flatGoodSku(goods: ApiRetrieveComItemType[], flatKey: string) {
  if (!goods.length) return []
  const tempGoods: FlatGood[] = []
  goods.forEach((good) => {
    good.skuIds.forEach((skuId, index) => {
      if ((good.stocks[index] > 0 && good.stockTypes[index] === 'LIMITED') || good.stockTypes[index] === 'UNLIMITED') {
        tempGoods.push({
          productId: good.productId,
          productName: good.productName,
          productPic: good.pic,
          shopId: good.shopId,
          skuItem: {
            // limitType: good.,
            productId: good.productId,
            skuId,
            skuName: good.specs[index],
            skuPrice: good.salePrices[index],
            skuStock: good.stocks[index],
            stockType: good.stockTypes[index],
          },
          rowTag: 0,
          matchingStock: 1,
          isJoin: true,
          matchingPrice: 0.01,
        })
      }
    })
  })
  return tempGoods
}

/**
 * 为行合并单元格打上标记
 * @param {any[]} flatGoodList 扁平化处理后的商品列表
 */
function addTag(flatGoodList: FlatGood[], key: string) {
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
function showProductSkuStock(skuItem: FlatGood['skuItem']) {
  return skuItem.stockType === 'UNLIMITED' ? '无限库存' : `${'库存' + skuItem.skuStock}`
}

/**
 * 获取商品提交数据
 */
function getProduct(): DoPostAddBargainQuery['bargainProducts'] {
  const productList = toRaw(tableList.value)
  return productList
    .filter((item) => item.isJoin)
    .map((item) => {
      const {
        setMealId,
        productId,
        productPic,
        matchingPrice,
        productName,
        matchingStock,
        shopId,
        skuItem: { skuId, skuStock, skuPrice, skuName, stockType },
      } = item
      return {
        setMealId: setMealId || '',
        shopId,
        productId,
        productPic,
        productName,
        productAttributes: $props.productAttributes,
        skuId,
        skuName,
        skuStock: +skuStock,
        skuPrice,
        stockType,
        matchingPrice: mulTenThousand(matchingPrice).toString(),
        matchingStock,
      }
    })
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
      if (!productList[i].matchingStock) {
        ElMessage.warning('商品库存必须大于零')
        result = false
        break
      }
    }
  }
  return result
}
const batchSet = ref({
  stock: 1,
  price: 0.01,
})
const currentBatchSetProductId = ref('0')
const batchSetClick = (row: FlatGood) => {
  currentBatchSetProductId.value = row.productId
  dialogVisible.value = true
}
const handleClose = () => {
  batchSet.value = { stock: 1, price: 0.01 }
  dialogVisible.value = false
}
const submitBatch = () => {
  tableList.value.forEach((item) => {
    if (item.productId === currentBatchSetProductId.value) {
      item.matchingStock = batchSet.value.stock > resultStore(item) ? resultStore(item) : batchSet.value.stock
      item.matchingPrice = batchSet.value.price > resultPrice(item) ? resultPrice(item) : batchSet.value.price
    }
  })
  dialogVisible.value = false
}
defineExpose({
  getProduct,
  validateProduct,
})
</script>

<template>
  <div>
    <el-table :data="tableList" :max-height="500" :span-method="arraySpanMethod">
      <el-table-column label="商品信息" width="215">
        <template #default="{ row }">
          <div class="com">
            <el-image :src="row.productPic" class="com__pic" />
            <div class="com__name">
              <el-button v-if="!$props.isEdit" class="com__batch" type="primary" link @click="batchSetClick(row)"> 批量设置 </el-button>
              <div class="com__name--text" style="margin-top: 5px">{{ row.productName }}</div>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="规格">
        <template #default="{ row }">{{ row.skuItem.skuName }}</template>
      </el-table-column>
      <el-table-column label="套餐库存">
        <template #default="{ row }">
          <div>
            <el-input-number
              :controls="false"
              :disabled="$props.isEdit"
              :max="resultStore(row)"
              :min="0"
              :model-value="+row.matchingStock"
              :precision="0"
              style="width: 80px"
              @update:model-value="(e) => (row.matchingStock = e)"
            />
          </div>
          <div>{{ showProductSkuStock(row.skuItem) }}</div>
        </template>
      </el-table-column>

      <el-table-column label="套餐价（元）">
        <template #default="{ row }">
          <div>
            <el-input-number
              :controls="false"
              :disabled="$props.isEdit"
              :max="resultPrice(row)"
              :min="0.01"
              :model-value="+row.matchingPrice"
              :precision="2"
              style="width: 80px"
              @update:model-value="(e) => (row.matchingPrice = e)"
            />
          </div>
          <div>销售价{{ resultPrice(row) }}</div>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dialogVisible" title="批量设置" width="500" destroy-on-close center top="30vh" @close="handleClose">
      <div class="flex">
        套餐库存
        <el-input-number v-model="batchSet.stock" :precision="0" :controls="false" :min="0" @change="handleChange" /> 套餐价
        <el-input-number v-model="batchSet.price" :precision="2" :controls="false" :min="0.01" @change="handleChange" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBatch"> 确定 </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style lang="scss" scoped>
@include b(com) {
  @include flex;
  @include e(pic) {
    width: 62px;
    height: 62px;
  }
  @include e(name) {
    font-size: 14px;
    @include flex;
    flex-direction: column;
    justify-content: center;
    margin-left: 10px;
    align-items: baseline;
  }
}
.com__name--text {
  width: 113px;
  height: 50px;
  line-height: 24px;
  @include utils-ellipsis(2);
}
.flex {
  display: flex;
  justify-content: space-around;
  align-items: center;
}
:deep.el-table .cell {
  line-height: 20px;
}
</style>
