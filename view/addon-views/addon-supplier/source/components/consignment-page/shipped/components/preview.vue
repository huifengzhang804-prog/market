<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import useConvert from '@/composables/useConvert'
import { PropType } from 'vue'
import { ShippedGoodsList } from '../types'
import { doGetCommoditySku } from '../../../../apis'

const $props = defineProps({
  currentProduct: {
    type: Object as PropType<ShippedGoodsList>,
    default: () => ({}),
  },
})
const { divTenThousand } = useConvert()
</script>

<template>
  <div class="good">
    <div class="good__info">店铺名称：{{ $props.currentProduct?.supplierName }}</div>
    <div class="good__info">商品名称：{{ $props.currentProduct?.name }}</div>
    <div class="good__img">
      <text>商品图片：</text>
      <el-image style="width: 100px; height: 100px" :src="$props.currentProduct.pic" :preview-src-list="[$props.currentProduct.pic]"></el-image>
    </div>
    <div>规格：</div>
    <el-table :data="currentProduct.storageSkus" height="350" stripe :header-row-style="{ 'font-size': '12px', color: '#000000' }">
      <el-table-column label="规格" align="center">
        <template #default="{ row }">
          <span>{{ row?.specs ? row?.specs.join('-') : '' }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="currentProduct.storageSkus[0]?.image" label="sku图" align="center">
        <template #default="{ row }">
          <el-image :src="row?.image" style="width: 50px; height: 50px" />
        </template>
      </el-table-column>
      <el-table-column label="销售价(元)" align="center">
        <template #default="{ row }">
          <span>{{ row.salePrice && divTenThousand(row.salePrice) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="originalPrice" label="划线价(元)" align="center">
        <template #default="{ row }">
          <span>{{ row.price && divTenThousand(row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="weight" label="重量(kg)" align="center">
        <template #default="{ row }">
          <span>{{ row.weight }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<style lang="scss" scoped>
@include b(good) {
  padding: 0 30px 0 45px;
  box-sizing: border-box;
  font-size: 12px;
  font-family: Microsoft YaHei, Microsoft YaHei-Normal;
  font-weight: normal;
  color: #000000;
  @include e(info) {
    margin-bottom: 24px;
  }
  @include e(img) {
    @include flex(flex-start, flex-start);
  }
  @include e(spec) {
    width: 758px;
  }
}
.b {
  font-size: 12px;
  color: #000000;
}
</style>
