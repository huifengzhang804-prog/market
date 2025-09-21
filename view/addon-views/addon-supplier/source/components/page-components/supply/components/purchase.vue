<script lang="ts" setup>
import { ref, defineProps, computed, onMounted } from 'vue'
import { ListInterface } from '../types/list'
import usePurchase from './hooks/usePurchase'

const batchPurchaseNum = ref(1)

const $props = withDefaults(defineProps<{ lines: ListInterface }>(), {
  lines: () => ({
    id: '',
    albumPics: '',
    productName: '',
    salePrices: [],
    sellType: 'PURCHASE',
    shopId: '',
    shopName: '',
    shopOwnProductStockNum: 0,
    storageSkus: [],
  }),
})
const $emitFn = defineEmits(['update:lines'])
const tableData = computed({
  get() {
    return $props.lines
  },
  set(newVal) {
    $emitFn('update:lines', newVal)
  },
})
const {
  maxUnlimitedNum,
  totalNum,
  freightTotal,
  totalPrice,
  maxBatchNum,
  totalOrderPrice,
  changeBatchPurchaseNum,
  receiveList,
  fetchReceiveAddress,
  purchaseFormModel,
  getOrderConfig,
  handleRemove,
} = usePurchase(tableData)

onMounted(() => fetchReceiveAddress())
defineExpose({ getOrderConfig })
</script>

<template>
  <div class="purchase">
    <div v-if="tableData.storageSkus && tableData.storageSkus.length > 1" class="purchase__batch">
      <el-form :show-message="false" inline>
        <el-form-item label="采购数(批量)">
          <el-input-number v-model="batchPurchaseNum" :max="+maxBatchNum" :min="0" @change="changeBatchPurchaseNum" />
        </el-form-item>
      </el-form>
    </div>
    <el-table :data="tableData.storageSkus" :max-height="350">
      <el-table-column v-if="tableData.storageSkus?.length === 1" label="商品名称" min-width="250">
        <template #default>
          <div class="commodity-info">
            <img :src="tableData.albumPics?.split(',')?.shift()" />
            <span>{{ tableData.productName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column v-else label="商品规格" min-width="250">
        <template #default="{ row }">{{ row?.specs?.join(',') || '单规格' }}</template>
      </el-table-column>
      <el-table-column align="center" label="供货价" width="150">
        <template #default="{ row }"> ￥{{ row?.salePrice / 10000 }}</template>
      </el-table-column>
      <el-table-column align="center" label="起批数" prop="minimumPurchase" width="80" />
      <el-table-column align="center" label="供应商库存" width="100">
        <template #default="{ row }">
          {{ row?.stockType === 'UNLIMITED' ? '无限库存' : row.stock }}
        </template>
      </el-table-column>
      <el-table-column align="center" label="自有库存" prop="shopOwnProductStockNum" width="80" />
      <el-table-column align="center" label="采购数" width="180">
        <template #default="{ row }">
          <el-input-number
            v-model="row.purchaseNum"
            :max="row?.stockType === 'UNLIMITED' ? +maxUnlimitedNum : +row.stock"
            :min="+row.minimumPurchase"
            placeholder="请输入"
            :precision="0"
          />
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="80">
        <template #default="{ $index }">
          <el-button link size="small" type="danger" @click="handleRemove($index)">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-form :show-message="false" class="purchase__remark">
      <el-form-item label="收货人信息">
        <el-select v-model="purchaseFormModel.receiveId" style="width: 100%">
          <el-option
            v-for="receiver in receiveList"
            :key="receiver.id"
            :label="receiver.contactName + '  ' + receiver.contactPhone + '   ' + receiver.area.join('') + '   ' + receiver.address"
            :value="receiver.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="采购备注">
        <el-input v-model="purchaseFormModel.remark" placeholder="请输入采购备注(100字以内)" maxlength="100" type="textarea" />
      </el-form-item>
    </el-form>
    <div class="purchase__total">
      <div class="purchase__total--title">订单合计</div>
      <div class="purchase__total--line">采购数量：{{ totalNum }}</div>
      <div class="purchase__total--line">商品总价：{{ totalPrice }}</div>
      <div class="purchase__total--line">运费：{{ freightTotal.toFixed(2) }}</div>
      <div class="purchase__total--line">采购金额(应付款)：￥{{ totalOrderPrice }}</div>
    </div>
  </div>
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

@include b(purchase) {
  @include e(batch) {
    display: flex;
    justify-content: flex-end;
  }
  @include e(remark) {
    padding: 15px 0;
  }
  @include e(total) {
    display: flex;
    flex-direction: column;
    align-items: flex-end;
    line-height: 1.5;
    @include m(title) {
      font-size: 1.2em;
      font-weight: 600;
    }
  }
}
</style>
