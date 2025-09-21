<script lang="ts" setup>
import { PropType, computed } from 'vue'
import useHandleBatchDistribution from '../hooks/useHandleBatchDistribution'
import BatchUnifySettings from './batch-unify-settings.vue'
import { ListInterface } from '../../types/list'

const $props = defineProps({
  batchItems: {
    type: Object as PropType<ListInterface[]>,
    required: true,
  },
})
const computedBatchItems = computed({
  get() {
    return $props.batchItems
  },
  set(newVal) {},
})
const { formModel, formRefs, formRules, shopCategoryList, shopCascaderProps, computedSalePrice, computedSuplier, buildData } =
  useHandleBatchDistribution(computedBatchItems)
defineExpose({ buildData })
</script>

<template>
  <el-form ref="formRefs" :model="formModel" :rules="formRules">
    <el-form-item label="店铺类目" prop="shopCategory">
      <el-cascader
        ref="shopCategoryRef"
        v-model="formModel.shopCategory"
        clearable
        style="width: 62.5%"
        class="inputWidth"
        :options="shopCategoryList"
        :props="shopCascaderProps"
        placeholder="请选择店铺分类"
        show-all-levels
      />
    </el-form-item>
    <el-form-item label="价格设置">
      <div class="price-settings">
        <el-radio-group v-model="formModel.consignmentPriceSetting.type">
          <el-radio value="UNIFY">统一设价</el-radio>
          <el-radio value="RATE">按比例设价</el-radio>
          <el-radio value="REGULAR">固定金额设价</el-radio>
        </el-radio-group>
        <template v-if="formModel.consignmentPriceSetting.type === 'UNIFY'">
          <BatchUnifySettings
            :sale="formModel.unifyPriceSetting.sale"
            :scribe="formModel.unifyPriceSetting.scribe"
            :type="formModel.unifyPriceSetting.type"
          />
        </template>
        <template v-else-if="formModel.consignmentPriceSetting.type === 'RATE'">
          <el-form-item prop="consignmentPriceSetting.sale">
            <span>销售价 = 供货价 + （供货价 * &nbsp;</span>
            <el-input-number v-model="formModel.consignmentPriceSetting.sale" :controls="false" :precision="2" />
            <span>&nbsp;% ）</span>
          </el-form-item>
          <el-form-item prop="consignmentPriceSetting.scribe">
            <span>划线价 = 销售价 + （销售价 * &nbsp;</span>
            <el-input-number v-model="formModel.consignmentPriceSetting.scribe" :controls="false" :precision="2" />
            <span>&nbsp;% ）</span>
          </el-form-item>
        </template>
        <template v-else>
          <el-form-item prop="consignmentPriceSetting.sale">
            <span>销售价 = 供货价 + &nbsp;</span>
            <el-input-number v-model="formModel.consignmentPriceSetting.sale" :controls="false" :precision="2" />
            <span>&nbsp;元</span>
          </el-form-item>
          <el-form-item prop="consignmentPriceSetting.scribe">
            <span>划线价 = 销售价 + &nbsp;</span>
            <el-input-number v-model="formModel.consignmentPriceSetting.scribe" :controls="false" :precision="2" />
            <span>&nbsp;元</span>
          </el-form-item>
        </template>
      </div>
    </el-form-item>
  </el-form>
  <el-table :data="computedBatchItems" :max-height="350">
    <el-table-column label="商品名称" align="left">
      <template #default="{ row }">
        <div class="commodity-info">
          <img :src="row.albumPics?.split(',')?.shift()" />
          <span>{{ row.productName }}</span>
        </div>
      </template>
    </el-table-column>
    <el-table-column label="供货价" align="center">
      <template #default="{ row }"> ￥{{ computedSalePrice(row) }} </template>
    </el-table-column>
    <el-table-column label="库存" align="center" width="100">
      <template #default="{ row }">{{ computedSuplier(row?.storageSkus) }}</template>
    </el-table-column>
    <el-table-column label="操作" width="80">
      <template #default="{ $index }">
        <el-link type="danger" @click="() => computedBatchItems.splice($index, 1)">移除</el-link>
      </template>
    </el-table-column>
  </el-table>
</template>

<style lang="scss" scoped>
@include b(commodity-info) {
  display: flex;
  align-items: center;
  img {
    width: 60px;
    height: 60px;
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
:deep(.el-form-item .el-form-item) {
  margin-top: 8px;
}
</style>
