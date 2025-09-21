<script lang="ts" setup>
import { PropType } from 'vue'
import { ListInterface } from '../types/list'
import useConvert from '@/composables/useConvert'

const { divTenThousand } = useConvert()
const $props = defineProps({
  detailsInfo: {
    type: Object as PropType<ListInterface>,
    default: () => ({}),
  },
})
</script>

<template>
  <div class="details">
    <div class="details__line">
      <span class="details__line--label">商品图片：</span>
      <img :src="$props.detailsInfo?.albumPics?.split(',')?.shift()" class="details__img" />
    </div>
    <div class="details__line">商品名称：{{ $props.detailsInfo?.productName }}</div>
    <el-table :data="$props.detailsInfo?.storageSkus || []">
      <el-table-column label="商品规格">
        <template #default="{ row }">
          {{ row?.specs?.length ? row?.specs?.join(',') : '单规格' }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="供货价" width="150">
        <template #default="{ row }"> ￥{{ divTenThousand(row?.salePrice) }}</template>
      </el-table-column>
      <el-table-column align="center" label="起批数" width="150" prop="minimumPurchase" />
      <el-table-column align="center" label="供应商库存" width="150" prop="stock" />
      <el-table-column align="center" label="自有库存" width="150" prop="shopOwnProductStockNum" />
    </el-table>
  </div>
</template>

<style lang="scss" scoped>
.details__img {
  width: 80px;
  height: 80px;
  vertical-align: top;
}
</style>
