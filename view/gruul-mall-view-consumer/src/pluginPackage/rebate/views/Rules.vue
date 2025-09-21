<script setup lang="ts">
import { ref } from 'vue'
import { doRebateConf } from '../apis'

const Msg = `     消费返利是指您购买商品可获得一定比例的【返利】，返利金额可用于购买商品、提现。不同会员等级享有不同返利百分比、返利支付百分比、提现门槛。

1、返利百分比：是指平台发放的返利金额的百分比，比值越大返的越多。

2、返利支付百分比：用户在使用消费返利余额支付占订单实付金额的百分之几，百分比越大则其实付现金越小。

3、重复返利：使用消费返利支付的订单，也可获得消费返利。

4、提现门槛：是指消费返利余额需大于该金额才能提现，可提现金额 =  消费返利余额  -  提现门槛，未达到提现门槛时虽不能提现但可用于购买平台上的商品。

5、积分商品：消费返利支付不能购买积分商品，购买积分也无法获得消费返利。`

const rebateUsers = ref<any[]>([])
const initialRebateConf = async () => {
  const { data } = await doRebateConf()
  if (data) {
    if (data?.rebateUsers === 'PAID_MEMBER') {
      rebateUsers.value = data?.payRebateUsers
    } else {
      rebateUsers.value = data?.allRebateUsers
    }
  }
}
initialRebateConf()
const { divHundred, divTenThousand } = useConvert()
</script>

<template>
  <view style="padding: 20rpx">
    <view class="rules">
      消费返利是指您购买商品可获得一定比例的【返利】，返利金额可用于购买商品、提现。不同会员等级享有不同返利百分比、返利支付百分比、提现门槛。
    </view>
    <view class="paragraph"> 1、返利百分比：是指平台发放的返利金额的百分比，比值越大返的越多。</view>
    <view class="paragraph"> 2、返利支付百分比：用户在使用消费返利余额支付占订单实付金额的百分之几，百分比越大则其实付现金越小。</view>
    <view class="paragraph"> 3、重复返利：使用消费返利支付的订单，也可获得消费返利。 </view>
    <view class="paragraph">
      4、提现门槛：是指消费返利余额需大于该金额才能提现，可提现金额 = 消费返利余额 -提现门槛，未达到提现门槛时虽不能提现但可用于购买平台上的商品。
    </view>
    <view class="paragraph"> 5、积分商品：消费返利支付不能购买积分商品，购买积分也无法获得消费返利。</view>

    <u-table :th-style="{ background: '#d9d9d9' }">
      <u-tr>
        <u-th>会员名称</u-th>
        <!-- <u-th width="100rpx">会员等级</u-th> -->
        <u-th width="100rpx">返利百分比</u-th>
        <u-th width="150rpx">返利支付百分比</u-th>
        <u-th width="160rpx">提现门槛</u-th>
      </u-tr>
      <u-tr v-for="rebateUser in rebateUsers" :key="rebateUser.id">
        <u-td
          ><text class="overflow">{{ rebateUser?.memberName }}</text></u-td
        >
        <!-- <u-td width="100rpx">{{ rebateUser?.rankCode }}</u-td> -->
        <u-td width="100rpx">{{ divHundred(rebateUser?.rebatePercentage) }}</u-td>
        <u-td width="150rpx">{{ divHundred(rebateUser?.rebatePaymentPercentage) }}</u-td>
        <u-td width="160rpx">{{ divTenThousand(rebateUser?.withdrawalThreshold) }}</u-td>
      </u-tr>
    </u-table>
  </view>
</template>

<style scoped lang="scss">
/* 设置背景颜色为白色 */
page {
  background-color: #fff;
}
@include b(rules) {
  text-indent: 2em;
}
@include b(paragraph) {
  padding: 20rpx 0;
}
@include b(overflow) {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}
@include b(u-td) {
  overflow: hidden;
}
</style>
