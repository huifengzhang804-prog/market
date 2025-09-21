<script lang="ts" setup>
import { onMounted, defineProps, withDefaults, ref } from 'vue'
import useStorageDetails from './hooks/useStorageDetails'
import { InStorageInterface } from './types/instorage'

const $props = withDefaults(defineProps<{ orderNo: string }>(), {
  orderNo: '',
})
const { tableData, initialInstorageData, extraData } = useStorageDetails($props)
onMounted(() => initialInstorageData())
const expandableTable = ref()
const handleRowClick = (row: InStorageInterface['products']) => {
  expandableTable.value.toggleRowExpansion(row)
}
</script>

<template>
  <div class="batch">
    <el-form :show-message="false" class="purchase__remark">
      <el-form-item label="采购备注">
        {{ extraData.remark }}
      </el-form-item>
    </el-form>
    <el-table ref="expandableTable" :data="tableData" row-key="id" :max-height="450" @row-click="handleRowClick">
      <el-table-column type="expand">
        <template #default="{ row }">
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
          </el-table>
        </template>
      </el-table-column>
      <el-table-column label="商品名称" width="250">
        <template #default="{ row }">
          <div class="batch__commodity">
            <img :src="row.image" />
            <span class="batch__commodity--name">{{ row.productName }}</span>
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
    </el-table>
  </div>
</template>

<style lang="scss" scoped>
@include b(batch) {
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
  }
}
</style>
