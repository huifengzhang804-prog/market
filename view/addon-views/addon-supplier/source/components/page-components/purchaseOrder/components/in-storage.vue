<script lang="ts" setup>
import { onMounted, defineProps, withDefaults, ref } from 'vue'
import useInStorage from './hooks/useInStorage'
import { InStorageInterface } from './types/instorage'

const $props = withDefaults(defineProps<{ orderNo: string }>(), {
  orderNo: '',
})
const {
  remark,
  tableData,
  expandRowKeys,
  expandOpen,
  maxRowBatchNum,
  changeRowBatchNum,
  computedActualNum,
  handleRemoveCommodity,
  handleRemoveSku,
  initialInstorageData,
} = useInStorage($props)
onMounted(() => initialInstorageData())
defineExpose({ tableData, remark, computedActualNum })
const expandableTable = ref()
const handleRowClick = (row: InStorageInterface['products']) => {
  expandableTable.value.toggleRowExpansion(row)
}
</script>

<template>
  <div class="oneClickProcurement">
    <el-form :show-message="false" class="oneClickProcurement__remark">
      <el-form-item label="入库备注">
        <el-input v-model="remark" placeholder="请输入入库备注" type="textarea" />
      </el-form-item>
    </el-form>
    <el-table
      ref="expandableTable"
      :max-height="450"
      :data="tableData"
      :expand-row-keys="expandRowKeys"
      row-key="productId"
      @expand-change="expandOpen"
      @row-click="handleRowClick"
    >
      <el-table-column type="expand">
        <template #default="{ row, $index }">
          <div class="oneClickProcurement__form">
            <el-form>
              <el-form-item label="入库数(批量)">
                <el-input-number
                  v-model="row.batchNum"
                  :max="maxRowBatchNum(row)"
                  :min="1"
                  :precision="0"
                  @change="(batchNum: number) => changeRowBatchNum(row.productId, batchNum)"
                />
              </el-form-item>
            </el-form>
          </div>
          <el-table :data="row.skus" row-key="id">
            <el-table-column label="商品规格">
              <template #default="{ row: skuRow }">
                {{ skuRow?.specs?.join(';') }}
              </template>
            </el-table-column>
            <el-table-column label="采购数" prop="num" width="80" />
            <el-table-column label="已入库数" prop="used" width="100" />
            <el-table-column label="剩余入库数" width="100">
              <template #default="{ row: skuRow }">
                {{ skuRow?.num - skuRow?.used }}
              </template>
            </el-table-column>
            <el-table-column label="实际入库数（本次）" prop="inStorageNum" width="180">
              <template #default="{ row: childRow }">
                <el-input-number v-model="childRow.inStorageNum" :max="childRow?.num - childRow?.used || 1" :min="1" :precision="0" />
              </template>
            </el-table-column>
            <el-table-column fixed="right" label="操作" width="80">
              <template #default="{ $index: skuIndex }">
                <el-link type="danger" @click="handleRemoveSku($index, skuIndex)">移出</el-link>
              </template>
            </el-table-column>
          </el-table>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" width="250">
        <template #default="{ row }">
          <div class="oneClickProcurement__commodity">
            <img :src="row.image" />
            <span class="oneClickProcurement__commodity--name">{{ row.productName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="采购数">
        <template #default="{ row }">
          {{ row?.skus?.reduce((pre: number, item: any) => pre + item.num, 0) }}
        </template>
      </el-table-column>
      <el-table-column label="已入库数">
        <template #default="{ row }">
          {{ row?.skus?.reduce((pre: number, item: any) => pre + item.used, 0) }}
        </template>
      </el-table-column>
      <el-table-column label="剩余入库数">
        <template #default="{ row }">
          {{ row?.skus?.reduce((pre: number, item: any) => pre + item.num, 0) - row?.skus?.reduce((pre: number, item: any) => pre + item.used, 0) }}
        </template>
      </el-table-column>
      <el-table-column label="实际入库数（本次）">
        <template #default="{ row }">{{ computedActualNum(row.productId) }}</template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="{ $index }">
          <el-link type="danger" @click="handleRemoveCommodity($index)">移出</el-link>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style lang="scss" scoped>
@include b(oneClickProcurement) {
  @include e(commodity) {
    display: flex;
    align-items: center;
    img {
      width: 60px;
      height: 60px;
      flex-shrink: 0;
    }
    span {
      overflow: hidden;
      text-overflow: ellipsis;
      display: box;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      margin-left: 10px;
    }
  }
  @include e(form) {
    display: flex;
    justify-content: flex-end;
    margin-right: 98px;
  }
}
</style>
