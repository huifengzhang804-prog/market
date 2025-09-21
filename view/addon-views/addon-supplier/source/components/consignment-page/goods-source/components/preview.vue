<script lang="ts" setup>
import { PropType } from 'vue'
import { ListInterface } from '../../types/list'
import useConvert from '@/composables/useConvert'

const { divTenThousand } = useConvert()
defineProps({
  commodityItem: {
    type: Object as PropType<ListInterface>,
    default: () => ({}),
    required: true,
  },
})
</script>

<template>
  <div class="preview">
    <div class="preview__name">商品名称：{{ commodityItem.productName }}</div>
    <div class="preview__image">
      <span>商品图片：</span>
      <el-image :src="commodityItem.albumPics?.split(',')?.shift()" style="width: 80px; height: 80px" />
    </div>
    <el-table :data="commodityItem.storageSkus">
      <el-table-column label="商品规格">
        <template #default="{ row }">
          {{ commodityItem.storageSkus?.length === 1 ? '单规格' : row?.specs?.join(';') }}
        </template>
      </el-table-column>
      <el-table-column label="供货价">
        <template #default="{ row }">
          {{ divTenThousand(row?.salePrice) }}
        </template>
      </el-table-column>
      <el-table-column label="库存">
        <template #default="{ row }">
          {{ row?.stockType === 'UNLIMITED' ? '无限库存' : row?.stock }}
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style lang="scss" scoped>
@include b(preview) {
  line-height: 1.5rem;
  @include e(image) {
    span {
      vertical-align: top;
    }
  }
}
</style>
