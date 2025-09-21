<script setup lang="ts">
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
})
const { divTenThousand, mulTenThousand } = useConvert()
const tableList = ref<FlatGood[]>([])
watch(
  () => $props.productList,
  (newVal) => {
    const flatGoodList = flatGoodSku(newVal, 'skus')
    console.log('flatGoodList', flatGoodList)
    tableList.value = addTag(flatGoodList, 'skus')
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
    return Infinity
  }
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
      tempGoods.push({
        productId: good.productId,
        productName: good.productName,
        productPic: good.pic,
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
        stock: 0,
        isJoin: true,
        floorPrice: 0.01,
      })
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
  return skuItem.stockType === 'UNLIMITED' ? '不限购' : skuItem.skuStock
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
        productId,
        productPic,
        floorPrice,
        productName,
        stock,
        skuItem: { skuId, skuStock, skuPrice, skuName, stockType },
      } = item
      return {
        activityId: '',
        productId,
        productPic,
        floorPrice: mulTenThousand(floorPrice).toString(),
        productName,
        stock,
        skuId,
        skuStock: +skuStock,
        skuPrice,
        skuName,
        stockType,
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
      if (!productList[i].stock) {
        ElMessage.warning('商品库存必须大于零')
        result = false
        break
      }
    }
  }
  return result
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
          <el-image class="com__pic" :src="row.productPic" />
          <div class="com__name">{{ row.productName }}</div>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="规格">
      <template #default="{ row }">{{ row.skuItem.skuName }} </template>
    </el-table-column>
    <el-table-column label="砍价低价（元）">
      <template #default="{ row }">
        <div>
          <el-input v-if="$props.isEdit" style="width: 80px" disabled :placeholder="divTenThousand(row.floorPrice).toString()" />
          <el-input-number
            v-else
            v-model="row.floorPrice"
            :min="0.01"
            style="width: 80px"
            :disabled="$props.isEdit"
            :precision="2"
            :max="divTenThousand(row.skuItem.skuPrice).toNumber()"
            :controls="false"
          />
        </div>
        <div>销售价{{ divTenThousand(row.skuItem.skuPrice) }}</div>
      </template>
    </el-table-column>
    <el-table-column label="砍价库存">
      <template #default="{ row }">
        <div>
          <el-input-number
            :model-value="+row.stock"
            :min="0"
            style="width: 80px"
            :max="resultStore(row)"
            :disabled="$props.isEdit"
            :precision="0"
            :controls="false"
            @update:model-value="(e:string) => (row.stock = e)"
          />
        </div>
        <div>库存{{ showProductSkuStock(row.skuItem) }}</div>
      </template>
    </el-table-column>

    <!-- <el-table-column label="是否参与">
            <template #default="{ row }">
                <el-switch v-model="row.isJoin" size="large" :disabled="$props.isEdit" />
            </template>
        </el-table-column> -->
  </el-table>
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
    color: #2e99f3;
    @include utils-ellipsis(2);
    margin-left: 12px;
  }
}
</style>
