<template>
  <div class="description">
    <div class="description__content">
      &emsp;&emsp;分销订单数据涵盖平台内所有店铺的分销订单，分销佣金基于剔除营销成本后的实际成交金额计算，分销订单上的价格<br />
      即为商品的实际成交价格。内购功能开启后，分销商购买分销商品可获得一级分销佣金。
    </div>
    <el-table
      :data="configData"
      border
      style="width: 600px"
      :header-cell-style="{
        'background-color': '#BEBEBE',
        'font-weight': 'normal',
        color: '#000',
        fontWeight: 'bold',
      }"
      :cell-style="{ color: '#666' }"
    >
      <template #empty> <ElTableEmpty /> </template>
      <el-table-column prop="settlementstatus" label="结算状态" width="100" />
      <el-table-column prop="description" label="说明" />
    </el-table>
    <el-table
      :data="tableData"
      border
      :header-cell-style="{
        'background-color': '#BEBEBE',
        'font-weight': 'normal',
        color: '#000',
        fontWeight: 'bold',
      }"
      :cell-style="{ color: '#666' }"
      :style="{ marginTop: '15px' }"
    >
      <template #empty> <ElTableEmpty /> </template>
      <el-table-column prop="commission" label="佣金试算" width="100" />
      <el-table-column prop="description" label="说明" />
    </el-table>
  </div>
</template>

<script lang="ts" setup>
const configData = [
  {
    settlementstatus: '待结算',
    description: '订单未完结（可能涉及退款退货等），分销佣金暂不计入累计佣金',
  },
  {
    settlementstatus: '已赚',
    description: '商品没有退款(或退款失败) ，对应佣金统计到【累计佣金】中',
  },
  {
    settlementstatus: '已失效',
    description: '商品已退款成功，无法获得佣金',
  },
]
const tableData = [
  {
    commission: '按百分比',
    description: '一级佣金 = （商品A实付金额 * 一级佣金参数 * 商品数）+（商品B实付金额 * 一级佣金参数 * 商品数）',
  },
  {
    commission: '按固定金额',
    description: '一级佣金  =  一级佣金 * 商品数',
  },
  {
    commission: '佣金合计',
    description: '佣金合计  =  一级佣金 + 二级佣金 + 三级佣金 + 内购佣金   ',
  },
]
</script>

<style lang="scss" scoped>
@include b(description) {
  @include e(content) {
    margin-bottom: 15px;
    line-height: 22px;
    color: #101010;
  }
}
</style>
