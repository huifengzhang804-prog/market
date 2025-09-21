<script lang="ts" setup>
import { PropType, ref, onMounted, defineProps } from 'vue'
import { ToBeReleaseList } from '../types'
import { doGetCommoditySku } from '../../../../apis'
import { useShopInfoStore } from '@/store/modules/shopInfo'
import useConvert from '@/composables/useConvert'

const $props = defineProps({
  currentProduct: {
    type: Object as PropType<ToBeReleaseList>,
    default: () => ({}),
  },
})
const { divTenThousand } = useConvert()
const skuList = ref<any[]>([])
const specGroups = ref<any[]>([])
const getProductDetails = async () => {
  const { data } = await doGetCommoditySku(useShopInfoStore().shopInfo.id, $props.currentProduct.productId)
  skuList.value = data?.skus || []
  specGroups.value = data?.specGroups || []
}
onMounted(() => getProductDetails())
</script>

<template>
  <div class="good">
    <div class="good__info">店铺名称：{{ $props.currentProduct?.supplierName }}</div>
    <div class="good__info">商品名称：{{ $props.currentProduct?.productName }}</div>
    <div class="good__img">
      <text>商品图片：</text>
      <el-image :preview-src-list="[$props.currentProduct.image]" :src="$props.currentProduct.image" style="width: 100px; height: 100px"></el-image>
    </div>
    <div>规格：</div>
    <el-table :data="skuList" :header-row-style="{ 'font-size': '12px', color: '#000000' }" :cell-style="{ height: '48px' }" height="350" stripe>
      <el-table-column v-if="specGroups.length" align="center" label="规格">
        <template #default="{ row }">
          <span>{{ row?.specs && row?.specs.join('-') }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="skuList[0]?.image" align="center" label="sku图">
        <template #default="{ row }">
          <el-image :src="row?.image" style="width: 50px; height: 50px" />
        </template>
      </el-table-column>
      <el-table-column align="center" label="销售价(元)">
        <template #default="{ row }">
          <span>{{ row.salePrice && divTenThousand(row.salePrice) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="划线价(元)" prop="originalPrice">
        <template #default="{ row }">
          <span>{{ row.price && divTenThousand(row.price) }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" label="重量(kg)" prop="weight">
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
