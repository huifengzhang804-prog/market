<script setup lang="ts">
import { defineProps, defineEmits, PropType } from 'vue'
import QTableColumn from '@/components/qszr-core/packages/q-table/q-table-column.vue'
import QTable from '@/components/qszr-core/packages/q-table/QTable'
import receiverCompanyList from '@/assets/json/data.json'
import type { IntegralOrderItem } from '../types/order'
import type { CompanySelectList } from '../../entries/DeliveryList.vue'

const companySelectList: {
  value: CompanySelectList
  label: string
  companyType: string
}[] = receiverCompanyList.map((item) => ({
  label: item.companyName,
  value: { value: item.companyCode, label: item.companyName, companyType: item.companyType },
  companyType: item.companyType,
}))
const $props = defineProps({
  tableData: { type: Array as PropType<IntegralOrderItem[]>, default: () => [] },
})
const $emit = defineEmits(['filterOrderList', 'express-No', 'express-company-name'])
const handleExpressNoVal = (val: string, row: IntegralOrderItem) => {
  $emit('express-No', val, row)
}
const handleExpressCompanyName = (val: string, row: IntegralOrderItem) => {
  $emit('express-company-name', val, row)
}
</script>

<template>
  <q-table :data="$props.tableData" class="orderIndex-table q-table">
    <template #header="{ row }: { row: IntegralOrderItem }">
      <div style="margin-right: 36px">订单号:{{ row.no }}</div>
      <div>创建时间:{{ row.createTime }}</div>
      <el-row style="flex: 1" justify="end">
        <el-button type="primary" link size="small" @click="$emit('filterOrderList', row)">移除</el-button>
      </el-row>
    </template>
    <q-table-column prop="name" label="商品" align="left">
      <template #default="{ row }: { row: IntegralOrderItem }">
        <div class="orderIndex-table__img-box">
          <el-image fits="cover" style="width: 63px; height: 63px" shape="square" size="large" :src="row.image" :title="row.productName" />
          <div class="orderIndex-table__img-mask">
            <span class="orderIndex-table__img-mask--name">{{ row.productName }}</span>
            <div class="orderIndex-table__img-mask--integral">
              <span>积分{{ row.price }}</span>
              <span style="color: #838383; font-size: 10px">共{{ row.num }}件</span>
            </div>
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column prop="age" label="客户">
      <template #default="{ row }: { row: IntegralOrderItem }">
        <div class="avatar_text avatar_text__bottom money_text">
          <span style="color: #2e99f3; margin-right: 10px">买家昵称 : {{ row.buyerNickname }}</span>
          <div style="padding: 0 10px 0" class="money_text">
            (收货人：{{ row.integralOrderReceiverVO.name }},{{ row.integralOrderReceiverVO.mobile }})
          </div>
        </div>
      </template>
    </q-table-column>
    <q-table-column prop="sex" label="操作" align="right">
      <template #default="{ row }: { row: IntegralOrderItem }">
        <el-row>
          <el-form-item label="物流公司" style="width: 230px">
            <el-select-v2
              v-model="row.expressCompanyName"
              placeholder="请选择物流公司"
              :options="companySelectList"
              value-key="value"
              @change="(val) => handleExpressCompanyName(val, row)"
            >
            </el-select-v2>
          </el-form-item>
          <el-form-item label="运单号码" style="width: 230px">
            <el-input v-model="row.expressNo" style="height: 28px" maxlength="40" @change="(val) => handleExpressNoVal(val, row)" />
          </el-form-item>
        </el-row>
      </template>
    </q-table-column>
  </q-table>
</template>

<style scoped lang="scss">
@include b(orderIndex-table) {
  @include e(img-box) {
    width: 200px;
    display: flex;
    justify-content: flex-start;
  }
  @include e(img) {
    border-radius: 5px;
    position: relative;
  }
  @include e(img-mask) {
    margin-left: 5px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: flex-start;
    font-size: 12px;
    color: #000000;
    @include m(name) {
      width: 120px;
      @include utils-ellipsis;
    }
    @include m(integral) {
      width: 100px;
      display: flex;
      justify-content: space-between;
      & > :nth-child(1) {
        font-size: 12px;
        color: #000000;
      }
    }
  }
}
@include b(is-complete) {
  background: #eef1f6;
}
@include b(header-table) {
  width: 100%;
  @include flex(space-between);
}
@include b(money_text) {
  font-size: 12px;
  color: #000000;
}
</style>
