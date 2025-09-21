<template>
  <div class="description">
    <div class="description__content">
      &emsp;&emsp; 消费返利属于平台层级的营销活动，通过该营销工具可促进付费会员的购买及续费，同时还可提高用户粘性将用户未来的交易固定在本平台发生的<br />
      营销神器。平台从各店铺交易获得的【平台服务费】拿出小部分用于消费返利的资金支出(订单完成后才返利)，系统可实现不同会员等级获得不同的返<br />
      利权益支持您的灵活营销。<br />
      <div style="margin-top: 10px">
        &emsp;&emsp; 商品实付交易金额 = (商品交易+会员折扣+会员包邮 + 平台优惠券 +返利抵扣) - (店铺优惠券+满减优惠+运费+退款金额+分销佣金+代销货款)
      </div>
    </div>
    <el-table
      :data="configData"
      border
      class="descriptionTable"
      :header-cell-style="{
        backgroundColor: '#BEBEBE',
        'font-weight': 'normal',
        color: '#000',
        fontWeight: 'bold',
      }"
      :cell-style="{ color: '#666' }"
    >
      <template #empty> <ElTableEmpty /> </template>
      <el-table-column prop="field" label="字段" width="180" />
      <el-table-column prop="description" label="说明" />
    </el-table>
    <table class="goods-tables">
      <tr class="headers">
        <th>商品类型</th>
        <th>交易对象</th>
        <th>是否参与返利</th>
        <th>消费返利金额说明</th>
      </tr>
      <tr>
        <td class="type-cell">自有商品</td>
        <td class="trade-cell">消费者 --> 店铺</td>
        <td class="rebate-cell" rowspan="2">是</td>
        <td class="desc-cell" rowspan="2">返利金额 = 商品实付交易金额 * 平台抽佣比例 * 返利百分比=平台服务费 * 返利百分比</td>
      </tr>
      <tr>
        <td class="type-cell" rowspan="2">代销商品</td>
        <td class="trade-cell">消费者 --> 店铺</td>
      </tr>
      <tr>
        <td class="trade-cell">店铺 --> 供应商</td>
        <td class="rebate-cell" rowspan="3">否</td>
        <td class="desc-cell" rowspan="3">返利金额 = 0</td>
      </tr>
      <tr>
        <td class="type-cell">积分商品</td>
        <td class="trade-cell">消费者 --> 平台</td>
      </tr>
      <tr>
        <td class="type-cell" rowspan="2">采购商品</td>
        <td class="trade-cell">店铺 --> 供应商</td>
      </tr>
      <tr>
        <td class="trade-cell">消费者 --&gt; 店铺</td>
        <td class="rebate-cell">是</td>
        <td class="desc-cell">返利金额 = 商品实付交易金额 * 平台抽佣比例 * 返利百分比-平台服务费 * 返利百分比</td>
      </tr>
    </table>
  </div>
</template>

<script lang="ts" setup>
const configData = [
  {
    field: '返利百分比',
    description: '平台将平台服务费按该百分比计算消费返利金额，消费返利按商品为粒度计算',
  },
  {
    field: '返利支付百分比',
    description: '使用消费返利余额支付占订单金额的百分之几，百分比越大其，实付现金越小；返利余额支付的金额叫返利抵扣',
  },
  {
    field: '提现门槛',
    description: '可提现金额 = 消费返利余额 - 提现门槛；未达门槛时，虽不能提现，但可用于购买平台商品',
  },
  {
    field: '重复返利',
    description: '使用消费返利金额购买商品，也可获得消费返利',
  },
]
</script>

<style lang="scss" scoped>
@include b(description) {
  @include e(content) {
    margin-bottom: 15px;
    line-height: 16px;
    color: #000;
  }
}
.goods-tables {
  width: 100%;
  border-collapse: separate; /* 改为separate以更好地控制边框 */
  border-spacing: 0; /* 确保单元格间没有间距 */
  font-size: 14px;
  border: 1px solid #ebeef5;
  border-radius: 2px;
  margin-top: 10px;
  border-right: none;
}

.goods-tables th,
.goods-tables td {
  padding: 8px 12px;
  text-align: left;
  vertical-align: center;
  border-right: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
}

/* 移除最后一行的底部边框 */
.goods-tables tr:last-child td {
  border-bottom: none;
}

/* 其他样式保持不变 */
.headers {
  background-color: #bebebe;
}

.headers th {
  font-weight: bold;
  color: #333;
}

.type-cell {
  width: 100px;
}

.trade-cell {
  width: 140px;
}

.trade-cell div {
  line-height: 1.8;
}

.rebate-cell {
  width: 110px;
  text-align: center !important;
}

.desc-cell {
  min-width: 400px;
}
</style>
