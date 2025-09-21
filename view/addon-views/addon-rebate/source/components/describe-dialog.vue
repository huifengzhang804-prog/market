<template>
  <div class="description">
    <div class="description__content">
      1、购买积分商品无法使用返利支付，也无法获得消费返利<br />
      2、开启消费返利即生成返利订单，消费返利金额以商品为单位计算<br />
      3、返利佣金计算：商品返利金额 = 平台服务费 × 会员等级对应的返利百分比
    </div>
    <el-table
      :data="configData"
      border
      :header-cell-style="{
        'background-color': '#BEBEBE',
        'font-weight': 'normal',
        color: '#000',
        fontWeight: 'bold',
      }"
      :cell-style="{ color: '#666' }"
    >
      <template #empty> <ElTableEmpty /> </template>
      <el-table-column prop="settlementStatus" label="结算状态" width="180" />
      <el-table-column prop="description" label="说明" />
    </el-table>
  </div>
</template>

<script lang="ts" setup>
const configData = [
  {
    settlementStatus: '待结算',
    description: '是指订单处于【待发货、待收货、待评价】状态，未进行返利结算',
  },
  {
    settlementStatus: '已返',
    description: '商品未发生退款或退款失败',
  },
  {
    settlementStatus: '已失效',
    description: '因退款 或 支付失败 导致消费返利佣金失效',
  },
]
</script>

<style lang="scss" scoped>
@include b(description) {
  @include e(content) {
    margin-bottom: 15px;
    line-height: 22px;
    color: #000;
  }
}
</style>
